package com.brawkurly.moneytoring.repository;

import com.brawkurly.moneytoring.domain.ConsumerPrice;
import com.brawkurly.moneytoring.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumerPriceRepository extends JpaRepository<ConsumerPrice, Long> {

//    @Query("select c from ConsumerPrice c where c.item =:item order by c.reservationTime DESC limit :needSize")
//    List<ConsumerPrice> findRecentReserve(Item item, int needSize);

    List<ConsumerPrice> findTop20ByItemOrderByReservationTimeDesc(Item item);
}
