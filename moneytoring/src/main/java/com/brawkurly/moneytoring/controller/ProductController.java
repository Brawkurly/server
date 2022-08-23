package com.brawkurly.moneytoring.controller;

import com.brawkurly.moneytoring.domain.ConsumerRecentReserveDto;
import com.brawkurly.moneytoring.domain.ResponseDto;
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
