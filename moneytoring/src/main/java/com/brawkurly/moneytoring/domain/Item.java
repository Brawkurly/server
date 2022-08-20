package com.brawkurly.moneytoring.domain;

import lombok.Data;
import org.hibernate.annotations.Columns;

import javax.persistence.*;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_id")
    private Long id;

    private String name;

    @Column(name="current_price")
    private int currentPrice;

    @Column(name="supply_price")
    private int supplyPrice;

    @Column(name="fair_price")
    private int fairPrice;
}
