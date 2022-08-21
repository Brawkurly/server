package com.brawkurly.moneytoring.controller;

import com.brawkurly.moneytoring.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/reservation")
    public ResponseEntity reserve(@RequestBody Map<String, ?> body){
        String productName = (String) body.get("productName");
        int price = (int) body.get("price");
        orderService.addReserve(productName, price);
        return ResponseEntity.ok().body("");
    }

    @PostMapping("/purchase")
    public ResponseEntity buy(@RequestBody Map<String, ?> body){
        String productName = (String) body.get("productName");
        int price = (int) body.get("price");
        orderService.addBuy(productName, price);
        return ResponseEntity.ok().body("");
    }
}
