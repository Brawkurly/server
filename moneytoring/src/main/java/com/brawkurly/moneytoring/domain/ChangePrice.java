package com.brawkurly.moneytoring.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "change_price")
public class ChangePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="change_price_id")
    private Long id;

    private int price;

    @Column(name="create_at")
    private Date createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;
}
