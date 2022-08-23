package com.brawkurly.moneytoring.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumer_price")
@Getter
public class ConsumerPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="consumer_price_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int price;

    @Column(name="reservation_time")
    private LocalDateTime reservationTime;

    @Column(name="purchase_time")
    private LocalDateTime purchaseTime;

    public void ChangePrice(int price) {
        this.price = price;
        this.purchaseTime = LocalDateTime.now();
    }
}
