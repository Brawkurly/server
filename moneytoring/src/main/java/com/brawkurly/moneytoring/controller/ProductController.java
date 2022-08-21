package com.brawkurly.moneytoring.controller;

import com.brawkurly.moneytoring.service.ProductService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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
        private List<ConsumerPriceDto> consumerPrice = new ArrayList<>();
        private Long totalPrice;
        private int totalSalesCnt;
        private int consumerReservationCnt;
        private List<ChangePriceDto> changePrice = new ArrayList<>();
        private List<ConsumerPopularityDto> consumerPopularityPurchase = new ArrayList<>();
        private List<ConsumerPopularityDto> consumerPopularityReserve = new ArrayList<>();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumerPriceDto{
        private int price;
        private LocalDateTime reservationTime;
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
}
