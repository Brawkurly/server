package com.brawkurly.moneytoring.service;

import com.brawkurly.moneytoring.domain.ConsumerPriceDto;
import com.brawkurly.moneytoring.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RedisTemplate redisTemplate;
    private final ItemRepository itemRepository;

    /*
    * 소비자 예약 가격 저장
    * key : consumer_price(테이블 명)::productName(상품 명)
    * */
    @Transactional
    public void addReserve(String productName, int price) {
        Long productId = itemRepository.findByName(productName).getId();
        System.out.println("productId = " + productId);
        ConsumerPriceDto consumerPriceDto = new ConsumerPriceDto(productId, productName, price, LocalDateTime.now(), null);
        String key = "consumer_price::"+productId;
        redisTemplate.opsForList().leftPush(key, consumerPriceDto);
        redisTemplate.expireAt(key, Date.from(ZonedDateTime.now().plusDays(1).toInstant())); // 유효기간 TTL 1일 설정
    }
}
