package com.electrostore.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PedidoRequestDTO {

    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    private String cliente;

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<ItemPedidoRequestDTO> items;

    public PedidoRequestDTO() {}

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public List<ItemPedidoRequestDTO> getItems() { return items; }
    public void setItems(List<ItemPedidoRequestDTO> items) { this.items = items; }
}