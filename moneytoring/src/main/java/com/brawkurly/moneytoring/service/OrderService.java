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
    * 소비자 예약 저장
    * key : consumer_price(테이블 명):status(상태-reserve):productName(상품 명)
    * */
    @Transactional
    public void addReserve(String productName, int price) {
        Long productId = findProductId(productName);
        ConsumerPriceDto consumerPriceDto = new ConsumerPriceDto(productId, productName, price, LocalDateTime.now(), null);
        insertRedis("reserve",consumerPriceDto);
    }

    /*
     * 소비자 구매 저장
     * key : consumer_price(테이블 명):status(상태-purchase):productName(상품 명)
     * */
    public void addBuy(String productName, int price) {
        Long productId = findProductId(productName);
        ConsumerPriceDto consumerPriceDto = new ConsumerPriceDto(productId, productName, price, null, LocalDateTime.now());
        insertRedis("purchase",consumerPriceDto);
    }

    private Long findProductId(String productName) {
        return itemRepository.findByName(productName).getId();
    }

    private void insertRedis(String status, ConsumerPriceDto consumerPriceDto) {
        String key = "consumer_price:" + status + ":" + consumerPriceDto.getProductId();
        redisTemplate.opsForList().leftPush(key, consumerPriceDto);
        redisTemplate.expireAt(key, Date.from(ZonedDateTime.now().plusDays(1).toInstant())); // 유효기간 TTL 1일 설정
    }
}
