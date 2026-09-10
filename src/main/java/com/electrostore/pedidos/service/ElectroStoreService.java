package com.electrostore.pedidos.service;

import com.electrostore.pedidos.dto.ItemPedidoRequestDTO;
import com.electrostore.pedidos.dto.PedidoRequestDTO;
import com.electrostore.pedidos.entity.DetallePedido;
import com.electrostore.pedidos.entity.Pedido;
import com.electrostore.pedidos.entity.Producto;
import com.electrostore.pedidos.repository.PedidoRepository;
import com.electrostore.pedidos.repository.ProductoCustomRepositoryImpl;
import com.electrostore.pedidos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ElectroStoreService {

    private static final BigDecimal UMBRAL_DESCUENTO = new BigDecimal("1000.00");
    private static final BigDecimal PORCENTAJE_DESCUENTO = new BigDecimal("0.10");

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoCustomRepositoryImpl productoCustomRepository;

    // --- CRUD PRODUCTOS ---
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> buscarProductosPorCategoriaNamed(String categoria) {
        return productoRepository.findByCategoriaNamed(categoria);
    }

    public List<Producto> buscarPorPrecioMaximo(BigDecimal precioMax) {
        return productoRepository.buscarPorPrecioMaximoJPQL(precioMax);
    }

    public List<Producto> buscarPorFiltroAvanzadoEM(String keyword, Integer stockMin) {
        return productoCustomRepository.buscarPorFiltrosAvanzados(keyword, stockMin);
    }
    public List<Producto> buscarProductosConStockBajo(Integer umbral) {
        return productoCustomRepository.buscarProductosConStockBajo(umbral);
    }

    // --- GESTION DE PEDIDOS Y CALCULO DE MONTO TOTAL ---
    @Transactional
    public Pedido crearPedido(PedidoRequestDTO request) {
        Pedido pedido = new Pedido();
        pedido.setCliente(request.getCliente());

        BigDecimal montoTotalAcumulado = BigDecimal.ZERO;

        for (ItemPedidoRequestDTO item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre()
                        + " (Disponible: " + producto.getStock() + ", Solicitado: " + item.getCantidad() + ")");
            }

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            montoTotalAcumulado = montoTotalAcumulado.add(subtotal);

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            pedido.addDetalle(detalle);
        }

        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal montoFinal = montoTotalAcumulado;
        if (montoTotalAcumulado.compareTo(UMBRAL_DESCUENTO) > 0) {
            descuento = montoTotalAcumulado.multiply(PORCENTAJE_DESCUENTO).setScale(2, java.math.RoundingMode.HALF_UP);
            montoFinal = montoTotalAcumulado.subtract(descuento);
        }

        pedido.setDescuento(descuento);
        pedido.setMontoTotal(montoFinal);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }
}