package com.brawkurly.moneytoring.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumer_price")
public class ConsumerPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="consumer_price_id")
    private Long id;

    private int price;

    @Column(name="create_at")
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('예약', '구매')")
    private ConsumerPriceStatus status;
}
