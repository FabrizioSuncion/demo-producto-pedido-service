package com.electrostore.pedidos.repository;

import com.electrostore.pedidos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p WHERE p.precio <= :precioMax ORDER BY p.precio DESC")
    List<Producto> buscarPorPrecioMaximoJPQL(@Param("precioMax") BigDecimal precioMax);

    List<Producto> findByCategoriaNamed(@Param("categoria") String categoria);
}