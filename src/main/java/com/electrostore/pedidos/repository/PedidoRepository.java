package com.electrostore.pedidos.repository;

import com.electrostore.pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT DISTINCT p FROM Pedido p JOIN FETCH p.detalles d JOIN FETCH d.producto WHERE p.cliente = :cliente")
    List<Pedido> buscarPorClienteConDetallesJPQL(@Param("cliente") String cliente);
}