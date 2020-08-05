package com.shop.ItemServiceRest.Service;

import com.shop.ItemServiceRest.Model.Category;
import com.shop.ItemServiceRest.Model.Item;
import com.shop.ItemServiceRest.Repository.ItemRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private ItemRepo itemRepo;

    private Category book;
    private Item item;

    @BeforeEach
    public void init() {
        cacheManager.getCache("items").clear();
        cacheManager.getCache("pagination").clear();

        Category books = new Category("Books");
        this.book = new Category("Book", books);
        this.item = new Item("item", 30L, 3D
                , 600D, "123");
        item.setDescription("description...");
        item.setCharacteristics("characteristics...");
        item.setCategory(book);
        item.setId(1L);
    }

    @Test
    public void shouldFindByName() {
        Mockito
                .doReturn(Collections.singletonList(item))
                .when(itemRepo)
                .findByName("item");

        Item item = itemService.findByName("item").get(0);

        assertThat(item.getName()).isEqualTo("item");
        Mockito.verify(itemRepo, Mockito.times(1))
                .findByName("item");
    }

    @Test
    public void shouldFindByPrice() {
        Mockito
                .doReturn(Collections.singletonList(item))
                .when(itemRepo)
                .findByPrice(600D);

        Item item = itemService.findByPrice(600D).get(0);

        assertThat(item.getPrice()).isEqualTo(600D);
        Mockito.verify(itemRepo, Mockito.times(1))
                .findByPrice(600D);
    }

    @Test
    public void shouldFindBySearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = new PageImpl<>(Collections.singletonList(item));
        Mockito
                .doReturn(page)
                .when(itemRepo)
                .findBySearch("tE", pageable);

        Item item = itemService.findBySearch("tE", pageable).getContent().get(0);

        assertThat(item.getName()).isEqualTo("item");
        Mockito.verify(itemRepo, Mockito.times(1))
                .findBySearch("tE", pageable);
    }

    @Test
    public void shouldFindById() {
        Mockito
                .doReturn(Optional.of(item))
                .when(itemRepo)
                .findById(1L);

        Item item = itemService.findById(1L);

        assertThat(item.getId()).isEqualTo(1L);
        Mockito.verify(itemRepo, Mockito.times(1))
                .findById(1L);
    }

    @Test
    public void shouldRaiseExceptionWhenFindByUnknownId() {
        assertThrows(NoSuchElementException.class,
                () -> itemService.findById(1L));

        Mockito.verify(itemRepo, Mockito.times(1))
                .findById(1L);
    }

    @Test
    public void shouldFindByCode() {
        Mockito
                .doReturn(item)
                .when(itemRepo)
                .findByCode("123");

        Item item = itemService.findByCode("123");

        assertThat(item.getCode()).isEqualTo("123");
        Mockito.verify(itemRepo, Mockito.times(1))
                .findByCode("123");
    }

    @Test
    public void shouldSaveItem() {
        itemService.save(item);

        Mockito.verify(itemRepo, Mockito.times(1))
                .save(item);
    }

    @Test
    public void shouldDeleteItem() {
        itemService.delete(item);

        Mockito.verify(itemRepo, Mockito.times(1))
                .delete(item);
    }
}
