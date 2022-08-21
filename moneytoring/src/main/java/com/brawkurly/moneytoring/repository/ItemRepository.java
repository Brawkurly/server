package com.brawkurly.moneytoring.repository;

import com.brawkurly.moneytoring.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByName(String name);
}
