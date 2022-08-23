package com.brawkurly.moneytoring.domain;

import com.brawkurly.moneytoring.controller.ProductController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDto{
    private Long productId;
    private String productName;
    private int currentPrice;
    private int supplyPrice;
    private int fairPrice;
    private List<ConsumerRecentReserveDto> consumerRecentReserve = new ArrayList<>(); // 상품별 최근 소비자 예약 현황 20개
    private List<Map<String, Long>> consumerReserveCnt; // 해당 상품의 가격대별 예약 수
    private Long totalPrice; // 총 판매 금액
    private Long totalSalesCnt; // 총 판매 수
    private Long consumerReservationCnt; // 해당 상품의 예약 수
    private float dayToDay; // 전일 대비 판매 비율
    private List<ProductController.ConsumerPopularityDto> consumerPopularityPurchase = new ArrayList<>();
    private List<ProductController.ConsumerPopularityDto> consumerPopularityReserve = new ArrayList<>();
    private List<ProductController.CompetitorPriceDto> competitorPrice = new ArrayList<>();
    private List<ProductController.ChangePriceDto> changePrice = new ArrayList<>();   // 가격변동
}