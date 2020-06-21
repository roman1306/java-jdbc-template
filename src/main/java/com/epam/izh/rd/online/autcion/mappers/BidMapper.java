package com.epam.izh.rd.online.autcion.mappers;

import com.epam.izh.rd.online.autcion.entity.Bid;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BidMapper implements RowMapper<Bid> {

    @Override
    public Bid mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Bid(
                resultSet.getLong("bid_id"),
                resultSet.getDate("bid_date").toLocalDate(),
                resultSet.getDouble("bid_value"),
                resultSet.getLong("item_id"),
                resultSet.getLong("user_id")
        );
    }
}
