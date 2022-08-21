package com.brawkurly.moneytoring.repository;

import com.brawkurly.moneytoring.domain.ChangePrice;
import com.brawkurly.moneytoring.domain.Item;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangePriceRepository extends JpaRepository<ChangePrice, Long> {

    @Query("select c from ChangePrice c where c.item=:item")
    List<ChangePrice> changePriceList(@Param("item") Item item, PageRequest pageRequest);
}
