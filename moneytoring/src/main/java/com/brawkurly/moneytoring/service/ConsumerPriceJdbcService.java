package com.brawkurly.moneytoring.service;

import com.brawkurly.moneytoring.domain.ConsumerPrice;
import com.brawkurly.moneytoring.domain.ConsumerPriceDto;
import com.brawkurly.moneytoring.domain.Item;
import com.brawkurly.moneytoring.repository.ConsumerPriceJdbcRepository;
import com.brawkurly.moneytoring.repository.ItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerPriceJdbcService {

    private final ConsumerPriceJdbcRepository consumerPriceJdbcRepository;
    private  final ItemRepository itemRepository;

    private final RedisTemplate redisTemplate;

    public void post() {
        for(int i=1; i<=8; i++){
            String purchaseKey = "consumer_price:" + "purchase:" + i;
            ListOperations<String, ConsumerPriceDto> purchaseListOperations = redisTemplate.opsForList();
            List<ConsumerPriceDto> purchaseConsumerPriceDtoList = purchaseListOperations.range(purchaseKey, 0, -1);
            List<ConsumerPriceDto> purchaseMappedList = objectMapper().convertValue(purchaseConsumerPriceDtoList, new TypeReference<List<ConsumerPriceDto>>() {});
            consumerPriceJdbcRepository.saveAllPurChase(purchaseMappedList);
            redisTemplate.delete(purchaseKey);

            String reserveKey = "consumer_price:" + "reserve:" + i;
            ListOperations<String, ConsumerPriceDto> reserveListOperations = redisTemplate.opsForList();
            List<ConsumerPriceDto> reserveConsumerPriceDtoList = reserveListOperations.range(reserveKey, 0, -1);
            List<ConsumerPriceDto> reservePurchaseMappedList = objectMapper().convertValue(reserveConsumerPriceDtoList, new TypeReference<List<ConsumerPriceDto>>() {});
            consumerPriceJdbcRepository.saveAllReserve(reservePurchaseMappedList);
            redisTemplate.delete(reserveKey);
        }
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp 형식 안따르도록 설정
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module()); // LocalDateTime 매핑을 위해 모듈 활성화
        return mapper;
    }
}
