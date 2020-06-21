package com.epam.izh.rd.online.autcion.repository;

import com.epam.izh.rd.online.autcion.entity.Bid;
import com.epam.izh.rd.online.autcion.entity.Item;
import com.epam.izh.rd.online.autcion.entity.User;
import com.epam.izh.rd.online.autcion.mappers.BidMapper;
import com.epam.izh.rd.online.autcion.mappers.ItemMapper;
import com.epam.izh.rd.online.autcion.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JdbcTemplatePublicAuction implements PublicAuction {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BidMapper bidMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Bid> getUserBids(long id) {
        return jdbcTemplate.query("SELECT * FROM bids WHERE user_id = ?", bidMapper, id);
    }

    @Override
    public List<Item> getUserItems(long id) {
        return jdbcTemplate.query("SELECT * FROM items WHERE user_id = ?", itemMapper, id);
    }

    @Override
    public Item getItemByName(String name) {
        return jdbcTemplate.queryForObject("SELECT * FROM items WHERE title LIKE ?", itemMapper, name);
    }

    @Override
    public Item getItemByDescription(String name) {
        return jdbcTemplate.queryForObject("SELECT * FROM items WHERE description LIKE ?", itemMapper, name);
    }

    @Override
    public Map<User, Double> getAvgItemCost() {
        List<User> listUsers = jdbcTemplate.query("SELECT * FROM users", userMapper);
        Map<User, Double> avgItemCostForUsers = new HashMap<>();
        Double avgItemCost;

        for (User user : listUsers) {
            avgItemCost = jdbcTemplate.queryForObject("SELECT AVG(start_price) FROM items WHERE user_id = ?",
                    Double.class, user.getUserId());

            if (avgItemCost != null) {
                avgItemCostForUsers.put(user, avgItemCost);
            }
        }

        return avgItemCostForUsers;
    }

    @Override
    public Map<Item, Bid> getMaxBidsForEveryItem() {
        List<Item> listItems = jdbcTemplate.query("SELECT * FROM items", itemMapper);
        Map<Item, Bid> maxBidsForEveryItem = new HashMap<>();
        Bid maxBidForItem;

        for (Item item : listItems) {
            try {
                maxBidForItem = jdbcTemplate.queryForObject("SELECT * FROM bids WHERE " +
                                "bid_value = (SELECT MAX(bid_value) FROM bids WHERE item_id = ?)",
                        bidMapper, item.getItemId());
            } catch (EmptyResultDataAccessException e) {
                continue;
            }

            maxBidsForEveryItem.put(item, maxBidForItem);
        }

        return maxBidsForEveryItem;
    }

    @Override
    public boolean createUser(User user) {
        List<User> listUsers = jdbcTemplate.query("SELECT * FROM users", userMapper);

        if (!listUsers.contains(user)) {
            jdbcTemplate.update("INSERT INTO users VALUES (?, ?, ?, ?, ?)", user.getUserId(), user.getBillingAddress(),
                    user.getFullName(), user.getLogin(), user.getPassword());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean createItem(Item item) {
        List<Item> listItems = jdbcTemplate.query("SELECT * FROM items", itemMapper);

        if (!listItems.contains(item)) {
            jdbcTemplate.update("INSERT INTO items VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", item.getItemId(),
                    item.getBidIncrement(), item.getBuyItNow(), item.getDescription(), item.getStartDate(),
                    item.getStartPrice(), item.getStopDate(), item.getTitle(), item.getUserId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean createBid(Bid bid) {
        List<Bid> listBids = jdbcTemplate.query("SELECT * FROM bids", bidMapper);

        if (!listBids.contains(bid)) {
            jdbcTemplate.update("INSERT INTO bids VALUES (?, ?, ?, ?, ?)", bid.getBidId(), bid.getBidDate(),
                    bid.getBidValue(), bid.getItemId(), bid.getUserId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteUserBids(long id) {
        List<Long> listId = jdbcTemplate.queryForList("SELECT user_id FROM users", Long.class);

        if (listId.contains(id)) {
            jdbcTemplate.update("DELETE FROM bids WHERE user_id = ?", id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean doubleItemsStartPrice(long id) {
        List<Long> listId = jdbcTemplate.queryForList("SELECT user_id FROM users", Long.class);

        if (listId.contains(id)) {
            jdbcTemplate.update("UPDATE items SET start_price = start_price * 2 " +
                    "WHERE user_id = ?", id);
            return true;
        } else {
            return false;
        }
    }
}
