package com.brawkurly.moneytoring.controller;

import com.brawkurly.moneytoring.domain.ConsumerRecentReserveDto;
import com.brawkurly.moneytoring.service.OrderService;
import com.brawkurly.moneytoring.service.ProductService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ResponseDto> getProductData(@RequestParam("id") Long id){
        ResponseDto responseDto = null;

        try{
            responseDto = productService.getProductData(id);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.ACCEPTED);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ResponseDto{
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
        private List<ConsumerPopularityDto> consumerPopularityPurchase = new ArrayList<>();
        private List<ConsumerPopularityDto> consumerPopularityReserve = new ArrayList<>();
        private List<CompetitorPriceDto> competitorPrice = new ArrayList<>();
        private List<ChangePriceDto> changePrice = new ArrayList<>();   // 가격변동
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePriceDto{
        private int price;
        private Date createAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumerPopularityDto {
        private String productName;
        private Long cnt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitorPriceDto {
        private String competitor;
        private Long price;
    }
}
