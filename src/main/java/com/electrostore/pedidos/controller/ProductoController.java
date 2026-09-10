package com.electrostore.pedidos.controller;

import com.electrostore.pedidos.entity.Producto;
import com.electrostore.pedidos.service.ElectroStoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ElectroStoreService service;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(service.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerProductoPorId(id));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return new ResponseEntity<>(service.guardarProducto(producto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        Producto existente = service.obtenerProductoPorId(id);
        existente.setNombre(producto.getNombre());
        existente.setCategoria(producto.getCategoria());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());
        return ResponseEntity.ok(service.guardarProducto(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> listarPorCategoriaNamed(@PathVariable String categoria) {
        return ResponseEntity.ok(service.buscarProductosPorCategoriaNamed(categoria));
    }

    @GetMapping("/precio-max")
    public ResponseEntity<List<Producto>> listarPorPrecioMaximo(@RequestParam BigDecimal max) {
        return ResponseEntity.ok(service.buscarPorPrecioMaximo(max));
    }

    @GetMapping("/buscar-avanzado")
    public ResponseEntity<List<Producto>> buscarAvanzado(
            @RequestParam String keyword,
            @RequestParam Integer stockMin) {
        return ResponseEntity.ok(service.buscarPorFiltroAvanzadoEM(keyword, stockMin));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> listarStockBajo(@RequestParam Integer umbral) {
        return ResponseEntity.ok(service.buscarProductosConStockBajo(umbral));
    }
}