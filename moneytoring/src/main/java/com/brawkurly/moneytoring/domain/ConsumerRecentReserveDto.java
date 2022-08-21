package com.brawkurly.moneytoring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ConsumerRecentReserveDto {
    int price;
    LocalDateTime reservationTime;
}
