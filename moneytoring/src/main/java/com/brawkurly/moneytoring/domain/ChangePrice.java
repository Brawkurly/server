package com.brawkurly.moneytoring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
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

    public ChangePrice(int price, Date createAt, Item item) {
        this.price = price;
        this.createAt = createAt;
        this.item = item;
    }
}
