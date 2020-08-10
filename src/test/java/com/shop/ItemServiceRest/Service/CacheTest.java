package com.shop.ItemServiceRest.Service;

import com.shop.ItemServiceRest.Model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@PropertySource(value = "classpath:application.properties")
@Sql(value = {
        "classpath:db/PostgreSQL/after-test.sql",
        "classpath:db/PostgreSQL/category-test.sql",
        "classpath:db/PostgreSQL/item-test.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CacheTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void findByNameCachingTest() {
        itemService.findByName("Spring 5 для профессионалов");
        itemService.findByName("Git для профессионального программиста");

        Cache cache = cacheManager.getCache("items");
        assertThat(cache).isNotNull();

        List<Item> cached1 = cache.get("Spring 5 для профессионалов", ArrayList.class);
        List<Item> cached2 = cache.get("Git для профессионального программиста", ArrayList.class);
        assertThat(cached1).isNotNull();
        assertThat(cached2).isNotNull();
    }

    @Test
    public void findByPriceCachingTest() {
        itemService.findByPrice(4000D);
        itemService.findByPrice(1075D);

        Cache cache = cacheManager.getCache("items");
        assertThat(cache).isNotNull();

        List<Item> cached1 = cache.get(4000D, ArrayList.class);
        List<Item> cached2 = cache.get(1075D, ArrayList.class);
        assertThat(cached1).isNotNull();
        assertThat(cached2).isNotNull();
    }

    @Test
    public void findByIdCachingTest() {
        itemService.findById(6L);
        itemService.findById(7L);

        Cache cache = cacheManager.getCache("items");
        assertThat(cache).isNotNull();

        Item cached1 = cache.get(6L, Item.class);
        Item cached2 = cache.get(7L, Item.class);
        System.out.println(cached1);
        System.out.println(cached2);
        assertThat(cached1).isNotNull();
        assertThat(cached2).isNotNull();
    }

    @Test
    public void findByCodeCachingTest() {
        itemService.findByCode("e43a71c7");
        itemService.findByCode("f6e7cd7d");

        Cache cache = cacheManager.getCache("items");
        assertThat(cache).isNotNull();

        Item cached1 = cache.get("e43a71c7", Item.class);
        Item cached2 = cache.get("f6e7cd7d", Item.class);
        assertThat(cached1).isNotNull();
        assertThat(cached2).isNotNull();
    }

    @Test
    public void deleteCachingTest() {
        Cache cache = cacheManager.getCache("items");
        assertThat(cache).isNotNull();

        itemService.findByName("Spring 5 для профессионалов");
        Item toDelete = itemService.findById(6L);
        assertThat(cache.get("Spring 5 для профессионалов", ArrayList.class)).isNotNull();
        assertThat(cache.get(6L, Item.class)).isNotNull();

        itemService.delete(toDelete);

        assertThat(cache.get("Spring 5 для профессионалов", Item.class)).isNull();
        assertThat(cache.get(6L, Item.class)).isNull();

        ///////////////////////////////////////////////

        Item toDelete2 = itemService.findById(7L);
        itemService.findByPrice(1075D);
        itemService.findByCode("f6e7cd7d");
        assertThat(cache.get(1075D, ArrayList.class)).isNotNull();
        assertThat(cache.get("f6e7cd7d", Item.class)).isNotNull();

        itemService.delete(toDelete2);

        assertThat(cache.get(1075D, ArrayList.class)).isNull();
        assertThat(cache.get("f6e7cd7d", Item.class)).isNull();
    }
}
