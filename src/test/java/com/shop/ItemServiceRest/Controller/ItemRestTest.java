package com.shop.ItemServiceRest.Controller;

import com.shop.ItemServiceRest.DTO.PageResponse;
import com.shop.ItemServiceRest.Model.Item;
import com.shop.ItemServiceRest.Repository.CategoryRepo;
import com.shop.ItemServiceRest.Service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@PropertySource(value = "classpath:application.properties")
@Sql(value = {
        "classpath:db/PostgreSQL/after-test.sql",
        "classpath:db/PostgreSQL/category-test.sql",
        "classpath:db/PostgreSQL/item-test.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemRestTest {
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryRepo categoryRepo;

    @Test
    public void shouldShowItemsByName() {
        ResponseEntity<List<Item>> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/byName/Spring 5 для профессионалов",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Item>>(){});

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseItems.getBody()).isNotNull();

        List<Item> items = responseItems.getBody();
        assertThat(items.size()).isEqualTo(1);

        Item first = items.get(0);
        assertThat(first.getId()).isEqualTo(6);
        assertThat(first.getName()).isEqualTo("Spring 5 для профессионалов");
        assertThat(first.getCount()).isEqualTo(300);
        assertThat(first.getWeight()).isEqualTo(1.592);
        assertThat(first.getPrice()).isEqualTo(4000);
        assertThat(first.getDescription()).contains("обновлено по новой версии Spring Framework 5");
        assertThat(first.getCharacteristics()).contains("ISBN.....................978-5-907114-07-4, 978-1-4842-2807-4");
        assertThat(first.getImage()).isEqualTo("images/spring5ForProfessionals.jpg");
        assertThat(first.getCode()).isEqualTo("e43a71c7");
        assertThat(first.getCategory().getId()).isEqualTo(3);
    }

    @Test
    public void shouldShowItemsByPrice() {
        ResponseEntity<List<Item>> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/byPrice/4000",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Item>>(){});

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseItems.getBody()).isNotNull();

        List<Item> items = responseItems.getBody();
        assertThat(items.size()).isEqualTo(1);

        Item first = items.get(0);
        assertThat(first.getId()).isEqualTo(6);
        assertThat(first.getName()).isEqualTo("Spring 5 для профессионалов");
        assertThat(first.getCount()).isEqualTo(300);
        assertThat(first.getWeight()).isEqualTo(1.592);
        assertThat(first.getPrice()).isEqualTo(4000);
        assertThat(first.getDescription()).contains("обновлено по новой версии Spring Framework 5");
        assertThat(first.getCharacteristics()).contains("ISBN.....................978-5-907114-07-4, 978-1-4842-2807-4");
        assertThat(first.getImage()).isEqualTo("images/spring5ForProfessionals.jpg");
        assertThat(first.getCode()).isEqualTo("e43a71c7");
        assertThat(first.getCategory().getId()).isEqualTo(3);
    }

    @Test
    public void shouldShowItemsByKeyword() {
        ResponseEntity<PageResponse<Item>> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/byKeyword/для?page=0&size=10",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PageResponse<Item>>(){});

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseItems.getBody()).isNotNull();

        List<Item> items = responseItems.getBody().toPageImpl().getContent();
        assertThat(items.size()).isEqualTo(2);

        Item first = items.get(0);
        assertThat(first.getName()).isEqualTo("Spring 5 для профессионалов");

        Item second = items.get(1);
        assertThat(second.getName()).isEqualTo("Git для профессионального программиста");
    }

    @Test
    @Sql(value = {
            "classpath:db/PostgreSQL/after-test.sql",
            "classpath:db/PostgreSQL/category-test.sql",
            "classpath:db/PostgreSQL/item-test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void shouldShowItemById() {
        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/6",
                        HttpMethod.GET,
                        null,
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseItems.getBody()).isNotNull();

        Item item = responseItems.getBody();
        assertThat(item).isNotNull();

        assertThat(item.getId()).isEqualTo(6);
        assertThat(item.getName()).isEqualTo("Spring 5 для профессионалов");
        assertThat(item.getCount()).isEqualTo(300);
        assertThat(item.getWeight()).isEqualTo(1.592);
        assertThat(item.getPrice()).isEqualTo(4000);
        assertThat(item.getDescription()).contains("обновлено по новой версии Spring Framework 5");
        assertThat(item.getCharacteristics()).contains("ISBN.....................978-5-907114-07-4, 978-1-4842-2807-4");
        assertThat(item.getImage()).isEqualTo("images/spring5ForProfessionals.jpg");
        assertThat(item.getCode()).isEqualTo("e43a71c7");
        assertThat(item.getCategory().getId()).isEqualTo(3);
    }

    @Test
    public void shouldNotFoundWhenFindItemByIncorrectId() {
        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/100",
                        HttpMethod.GET,
                        null,
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseItems.getBody()).isNull();
    }

    @Test
    public void shouldShowItemByCode() {
        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/byCode/e43a71c7",
                        HttpMethod.GET,
                        null,
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseItems.getBody()).isNotNull();

        Item item = responseItems.getBody();
        assertThat(item).isNotNull();

        assertThat(item.getId()).isEqualTo(6);
        assertThat(item.getName()).isEqualTo("Spring 5 для профессионалов");
        assertThat(item.getCount()).isEqualTo(300);
        assertThat(item.getWeight()).isEqualTo(1.592);
        assertThat(item.getPrice()).isEqualTo(4000);
        assertThat(item.getDescription()).contains("обновлено по новой версии Spring Framework 5");
        assertThat(item.getCharacteristics()).contains("ISBN.....................978-5-907114-07-4, 978-1-4842-2807-4");
        assertThat(item.getImage()).isEqualTo("images/spring5ForProfessionals.jpg");
        assertThat(item.getCode()).isEqualTo("e43a71c7");
        assertThat(item.getCategory().getId()).isEqualTo(3);
    }

    @Test
    public void shouldNotFoundWhenFindItemByIncorrectCode() {
        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/byCode/1234567890",
                        HttpMethod.GET,
                        null,
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseItems.getBody()).isNull();
    }

    @Test
    public void shouldSuccessfulUpdateItem() {
        Item item = itemService.findById(6L);
        item.setName("Hello world");
        item.setPrice(1D);
        item.setCount(5L);

        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/6",
                        HttpMethod.PUT,
                        new HttpEntity<>(item),
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseItems.getBody()).isNotNull();

        Item first = responseItems.getBody();
        assertThat(first.getId()).isEqualTo(6);
        assertThat(first.getName()).isEqualTo("Hello world");
        assertThat(first.getCount()).isEqualTo(5);
        assertThat(first.getWeight()).isEqualTo(1.592);
        assertThat(first.getPrice()).isEqualTo(1);
        assertThat(first.getDescription()).contains("обновлено по новой версии Spring Framework 5");
        assertThat(first.getCharacteristics()).contains("ISBN.....................978-5-907114-07-4, 978-1-4842-2807-4");
        assertThat(first.getImage()).isEqualTo("images/spring5ForProfessionals.jpg");
        assertThat(first.getCode()).isEqualTo("e43a71c7");
        assertThat(first.getCategory().getId()).isEqualTo(3);
    }

    @Test
    public void shouldBadRequestWhenTryToUpdateItemWithInvalidData() {
        Item item = itemService.findById(6L);
        item.setName("");
        item.setPrice(-1D);
        item.setCount(-2L);

        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items/6",
                        HttpMethod.PUT,
                        new HttpEntity<>(item),
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseItems.getBody()).isNotNull();

        Item first = responseItems.getBody();
        assertThat(first.getId()).isEqualTo(6);
        assertThat(first.getName()).isEqualTo("");
        assertThat(first.getCount()).isEqualTo(-2);
        assertThat(first.getWeight()).isEqualTo(1.592);
        assertThat(first.getPrice()).isEqualTo(-1);
        assertThat(first.getDescription()).contains("обновлено по новой версии Spring Framework 5");
        assertThat(first.getCharacteristics()).contains("ISBN.....................978-5-907114-07-4, 978-1-4842-2807-4");
        assertThat(first.getImage()).isEqualTo("images/spring5ForProfessionals.jpg");
        assertThat(first.getCode()).isEqualTo("e43a71c7");
        assertThat(first.getCategory().getId()).isEqualTo(3);
    }

    @Test
    public void shouldSuccessCreateNewItem() {
        Item item = new Item("item_name", 20L, 0.5D, 51D, "1234567");
        item.setDescription("item_desc...");
        item.setCharacteristics("item_characteristics...");
        item.setCategory(categoryRepo.findById(3L).get());

        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items",
                        HttpMethod.POST,
                        new HttpEntity<>(item),
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseItems.getBody()).isNotNull();

        Item first = responseItems.getBody();
        assertThat(first.getId()).isEqualTo(100);
        assertThat(first.getName()).isEqualTo("item_name");
        assertThat(first.getCount()).isEqualTo(20);
        assertThat(first.getWeight()).isEqualTo(0.5);
        assertThat(first.getPrice()).isEqualTo(51);
        assertThat(first.getDescription()).isEqualTo("item_desc...");
        assertThat(first.getCharacteristics()).isEqualTo("item_characteristics...");
        assertThat(first.getCode()).isEqualTo("1234567");
        assertThat(first.getCategory().getId()).isEqualTo(3);
    }

    @Test
    public void shouldBadRequestWhenTryToCreateNewItemWithInvalidData() {
        Item item = new Item("item_name", 20L, 0.5D, 51D, "1234567");
        item.setDescription("");
        item.setCharacteristics("");

        ResponseEntity<Item> responseItems =
                restTemplate.exchange(
                        "http://localhost:9003/items-rest-swagger/api/items",
                        HttpMethod.POST,
                        new HttpEntity<>(item),
                        Item.class);

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseItems.getBody()).isNotNull();

        Item first = responseItems.getBody();
        assertThat(first.getId()).isNull();
        assertThat(first.getName()).isEqualTo("item_name");
        assertThat(first.getCount()).isEqualTo(20);
        assertThat(first.getWeight()).isEqualTo(0.5);
        assertThat(first.getPrice()).isEqualTo(51);
        assertThat(first.getDescription()).isEqualTo("");
        assertThat(first.getCharacteristics()).isEqualTo("");
        assertThat(first.getCode()).isEqualTo("1234567");
        assertThat(first.getCategory()).isNull();
    }

    @Test
    public void shouldSuccessfulDeleteItem() {
        restTemplate.exchange(
                "http://localhost:9003/items-rest-swagger/api/items/6",
                HttpMethod.DELETE,
                null,
                Object.class);

        assertThrows(NoSuchElementException.class, () -> itemService.findById(6L));
    }
}
