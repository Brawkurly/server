package com.brawkurly.moneytoring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ConsumerPriceDto implements Serializable {

    private Long productId;
    private String productName;
    private int price;
    private LocalDateTime reservationTime;
    private LocalDateTime purchaseTime;
}
