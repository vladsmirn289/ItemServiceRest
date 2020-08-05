package com.shop.ItemServiceRest.Service;

import com.shop.ItemServiceRest.Model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    List<Item> findByName(String name);
    List<Item> findByPrice(Double price);
    Page<Item> findBySearch(String keyword, Pageable pageable);
    Item findById(Long id);
    Item findByCode(String code);

    void save(Item item);

    void delete(Item item);
}
