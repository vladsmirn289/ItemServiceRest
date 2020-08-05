package com.shop.ItemServiceRest.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemValidationTest {
    private Validator validator;
    private Item item;
    private Category books;

    @BeforeEach
    void init() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        this.validator = localValidatorFactoryBean;

        this.books = new Category("Books");
        Item item = new Item("Spring 5", 50L, 1.592D, 3300D, "1234567");
        item.setDescription("..........");
        item.setCharacteristics("....");
        item.setCategory(books);

        Image image = new Image();
        image.setImage("1234".getBytes());
        item.setId(1L);
        item.setImage("123");
        item.setAdditionalImages(Collections.singleton(image));
        item.setCreatedOn(LocalDateTime.of(2020, Month.JUNE, 24, 1, 1));
        this.item = item;
    }

    @Test
    void shouldValidateWithCorrectData() {
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(0);
    }

    @Test
    void shouldNotValidateWithBlankName() {
        item.setName("  ");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Название не может быть пустым");
    }

    @Test
    void shouldNotValidateWithSmallDescription() {
        item.setDescription("...");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание должно быть от 10 символов");
    }

    @Test
    void shouldNotValidateWithBlankCharacteristics() {
        item.setCharacteristics("   ");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("characteristics");
        assertThat(violation.getMessage()).isEqualTo("Характеристики должны быть заданы");
    }

    @Test
    void shouldNotValidateWithBlankCode() {
        item.setCode("  ");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("code");
        assertThat(violation.getMessage()).isEqualTo("Код товара должен быть задан");
    }

    @Test
    void shouldNotValidateWithNullCategory() {
        item.setCategory(null);
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("category");
        assertThat(violation.getMessage()).isEqualTo("Категория должна существовать");
    }

    @Test
    void shouldNotValidateWithNegativeCount() {
        item.setCount(-1L);
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("count");
        assertThat(violation.getMessage()).isEqualTo("Количество должно быть положительным числом, или равно нулю");
    }

    @Test
    void shouldNotValidateWithNegativeWeight() {
        item.setWeight(-1D);
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("weight");
        assertThat(violation.getMessage()).isEqualTo("Вес должен быть положительным");
    }

    @Test
    void shouldNotValidateWithZeroWeight() {
        item.setWeight(0D);
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("weight");
        assertThat(violation.getMessage()).isEqualTo("Вес должен быть положительным");
    }

    @Test
    void shouldNotValidateWithNegativePrice() {
        item.setPrice(-1D);
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("price");
        assertThat(violation.getMessage()).isEqualTo("Цена должна быть положительной");
    }

    @Test
    void shouldNotValidateWithZeroPrice() {
        item.setPrice(0D);
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Item> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("price");
        assertThat(violation.getMessage()).isEqualTo("Цена должна быть положительной");
    }

    @Test
    public void getterTests() {
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Spring 5");
        assertThat(item.getCount()).isEqualTo(50L);
        assertThat(item.getWeight()).isEqualTo(1.592D);
        assertThat(item.getPrice()).isEqualTo(3300D);
        assertThat(item.getDescription()).isEqualTo("..........");
        assertThat(item.getCharacteristics()).isEqualTo("....");
        assertThat(item.getImage()).isEqualTo("123");
        assertThat(item.getAdditionalImages().iterator().next().getImage())
                .isEqualTo("1234".getBytes());
        assertThat(item.getCode()).isEqualTo("1234567");
        assertThat(item.getCategory()).isEqualTo(books);
        assertThat(item.getCreatedOn()).isEqualTo(LocalDateTime.of(2020, Month.JUNE, 24, 1, 1));
    }
}
