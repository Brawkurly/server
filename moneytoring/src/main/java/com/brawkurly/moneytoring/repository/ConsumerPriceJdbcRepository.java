package com.brawkurly.moneytoring.repository;

import com.brawkurly.moneytoring.domain.ConsumerPrice;
import com.brawkurly.moneytoring.domain.ConsumerPriceDto;
import com.brawkurly.moneytoring.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConsumerPriceJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAllPurChase(List<ConsumerPriceDto> consumerPriceList) {
        jdbcTemplate.batchUpdate("insert into consumer_price(price, purchase_time, item_id) " +
                        "values(?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, consumerPriceList.get(i).getPrice());
                        ps.setString(2, String.valueOf(consumerPriceList.get(i).getPurchaseTime()));
                        ps.setLong(3, consumerPriceList.get(i).getProductId());
                    }

                    @Override
                    public int getBatchSize() {
                        return consumerPriceList.size();
                    }
                });
    }

    public void saveAllReserve(List<ConsumerPriceDto> consumerPriceList) {
        jdbcTemplate.batchUpdate("insert into consumer_price(price, reservation_time, item_id) " +
                        "values(?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, consumerPriceList.get(i).getPrice());
                        ps.setString(2, String.valueOf(consumerPriceList.get(i).getReservationTime()));
                        ps.setLong(3, consumerPriceList.get(i).getProductId());
                    }

                    @Override
                    public int getBatchSize() {
                        return consumerPriceList.size();
                    }
                });
    }
}
