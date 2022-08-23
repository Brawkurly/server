package com.brawkurly.moneytoring.repository;

import com.brawkurly.moneytoring.domain.ConsumerPrice;
import com.brawkurly.moneytoring.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ConsumerPriceRepository extends JpaRepository<ConsumerPrice, Long> {

    List<ConsumerPrice> findTop20ByItemOrderByReservationTimeDesc(Item item);

    @Query("select c.price as price, count(c.id) as cnt from ConsumerPrice c where c.item = :item and c.purchaseTime is null group by c.price")
    List<Map<String, Long>> findGroupByReserveCnt(@Param("item") Item item);

    @Query("select sum(c.price) from ConsumerPrice c where c.item=:item and c.purchaseTime between :startDatetime and :endDatetime")
    Long findAllByPurchaseTimeBetween(@Param("item") Item item, @Param("startDatetime") LocalDateTime startDatetime, @Param("endDatetime") LocalDateTime endDatetime);
}
