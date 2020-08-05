package com.shop.ItemServiceRest.Repository;

import com.shop.ItemServiceRest.Model.Category;
import com.shop.ItemServiceRest.Model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepoTest {
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void init() {
        Category books = new Category("Books");
        Category book = new Category("Book", books);
        Item item = new Item("item", 30L, 3D
                , 600D, "123");
        item.setDescription("description...");
        item.setCharacteristics("characteristics...");
        item.setCategory(book);

        categoryRepo.save(books);
        categoryRepo.save(book);
        itemRepo.save(item);
    }

    @AfterEach
    public void resetSequence() {
        entityManager.getEntityManager()
                .createNativeQuery("alter sequence hibernate_sequence restart 1")
                .executeUpdate();
    }

    @Test
    public void shouldFindItemById() {
        Item item = itemRepo.findById(3L).orElse(null);

        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("item");
        assertThat(item.getCount()).isEqualTo(30L);
        assertThat(item.getWeight()).isEqualTo(3D);
        assertThat(item.getPrice()).isEqualTo(600D);
        assertThat(item.getDescription()).isEqualTo("description...");
        assertThat(item.getCharacteristics()).isEqualTo("characteristics...");
        assertThat(item.getCode()).isEqualTo("123");
        assertThat(item.getCategory().getName()).isEqualTo("Book");
    }

    @Test
    public void shouldFindItemsByName() {
        List<Item> items = itemRepo.findByName("item");

        assertThat(items.size()).isEqualTo(1);
        Item item = items.get(0);
        assertThat(item.getCount()).isEqualTo(30L);
        assertThat(item.getWeight()).isEqualTo(3D);
        assertThat(item.getPrice()).isEqualTo(600D);
        assertThat(item.getDescription()).isEqualTo("description...");
        assertThat(item.getCharacteristics()).isEqualTo("characteristics...");
        assertThat(item.getCode()).isEqualTo("123");
        assertThat(item.getCategory().getName()).isEqualTo("Book");
    }

    @Test
    public void shouldFindItemsByPrice() {
        List<Item> items = itemRepo.findByPrice(600D);

        assertThat(items.size()).isEqualTo(1);
        Item item = items.get(0);
        assertThat(item.getName()).isEqualTo("item");
        assertThat(item.getCount()).isEqualTo(30L);
        assertThat(item.getWeight()).isEqualTo(3D);
        assertThat(item.getDescription()).isEqualTo("description...");
        assertThat(item.getCharacteristics()).isEqualTo("characteristics...");
        assertThat(item.getCode()).isEqualTo("123");
        assertThat(item.getCategory().getName()).isEqualTo("Book");
    }

    @Test
    public void shouldFindItemsByCategory() {
        Category category = categoryRepo.findById(2L).orElse(null);
        assertThat(category).isNotNull();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> itemsPage = itemRepo.findByCategory(category, pageable);
        List<Item> items = itemsPage.getContent();

        assertThat(items.size()).isEqualTo(1);
        Item item = items.get(0);
        assertThat(item.getName()).isEqualTo("item");
        assertThat(item.getCount()).isEqualTo(30L);
        assertThat(item.getWeight()).isEqualTo(3D);
        assertThat(item.getPrice()).isEqualTo(600D);
        assertThat(item.getDescription()).isEqualTo("description...");
        assertThat(item.getCharacteristics()).isEqualTo("characteristics...");
        assertThat(item.getCode()).isEqualTo("123");
    }

    @Test
    public void shouldFindItemByCode() {
        Item item = itemRepo.findByCode("123");

        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("item");
        assertThat(item.getCount()).isEqualTo(30L);
        assertThat(item.getWeight()).isEqualTo(3D);
        assertThat(item.getPrice()).isEqualTo(600D);
        assertThat(item.getDescription()).isEqualTo("description...");
        assertThat(item.getCharacteristics()).isEqualTo("characteristics...");
        assertThat(item.getCode()).isEqualTo("123");
    }

    @Test
    public void shouldSaveItem() {
        Category laptops = new Category("Laptops");
        Category laptop = new Category("Laptop", laptops);
        Item item = new Item("laptop", 40L, 2D
                , 56000D, "567");
        item.setDescription("LaptopDescription...");
        item.setCharacteristics("LaptopCharacteristics...");
        item.setCategory(laptop);

        categoryRepo.save(laptops);
        categoryRepo.save(laptop);
        itemRepo.save(item);

        assertThat(itemRepo.findAll().size()).isEqualTo(2);
        Item item1 = itemRepo.findById(6L).orElse(null);

        assertThat(item1).isNotNull();
        assertThat(item1.getName()).isEqualTo("laptop");
        assertThat(item1.getCount()).isEqualTo(40L);
        assertThat(item1.getWeight()).isEqualTo(2D);
        assertThat(item1.getPrice()).isEqualTo(56000D);
        assertThat(item1.getDescription()).isEqualTo("LaptopDescription...");
        assertThat(item1.getCharacteristics()).isEqualTo("LaptopCharacteristics...");
        assertThat(item1.getCode()).isEqualTo("567");
        assertThat(item1.getCategory().getName()).isEqualTo("Laptop");
    }

    @Test
    public void shouldDeleteItem() {
        Item item = itemRepo.findById(3L).orElse(null);
        assertThat(item).isNotNull();
        itemRepo.delete(item);

        assertThat(itemRepo.findAll().size()).isEqualTo(0);
    }

    @Test
    public void shouldDeleteItemById() {
        itemRepo.deleteById(3L);

        assertThat(itemRepo.findAll().size()).isEqualTo(0);
    }

    @Test
    public void shouldFindBySearch() {
        Pageable pageable = PageRequest.of(0, 10).first();
        Page<Item> itemsPage = itemRepo.findBySearch("tE", pageable);
        List<Item> items = itemsPage.getContent();

        assertThat(items.size()).isEqualTo(1);
    }
}
