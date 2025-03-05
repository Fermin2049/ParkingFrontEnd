package com.fermin2049.parking.data.models;

import java.util.ArrayList;
import java.util.List;

public class PaymentRequest {
    private List<Item> items;
    private Payer payer;

    public PaymentRequest(double totalAmount) {
        items = new ArrayList<>();
        // Se crea un ítem con los datos obligatorios.
        items.add(new Item(
                "1234",                                  // id
                "Reserva de estacionamiento",            // title
                "Reserva de estacionamiento",            // description
                "https://dummyimage.com/300x300/cccccc/000000.png&text=Imagen+de+Prueba", // picture_url
                "reserva",                               // category_id
                1,                                       // quantity
                "ARS",                                   // currency_id
                totalAmount                              // unit_price
        ));
        // Define el payer con un email válido.
        payer = new Payer("fermin2049@gmail.com");
    }

    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }
    public Payer getPayer() {
        return payer;
    }
    public void setPayer(Payer payer) {
        this.payer = payer;
    }
}
