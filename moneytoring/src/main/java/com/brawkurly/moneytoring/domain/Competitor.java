package com.brawkurly.moneytoring.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "competitor")
public class Competitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="competitor_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int price;

    @Column(name="create_at")
    private Date createAt;
}
