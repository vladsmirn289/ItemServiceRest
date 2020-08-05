package com.shop.ItemServiceRest.Controller;

import com.shop.ItemServiceRest.Model.Category;
import com.shop.ItemServiceRest.Model.Item;
import com.shop.ItemServiceRest.Service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final static Logger logger = LoggerFactory.getLogger(ItemController.class);

    private ItemService itemService;

    @Autowired
    public void setItemService(ItemService itemService) {
        logger.debug("Setting itemService");
        this.itemService = itemService;
    }

    @GetMapping(params = {"name"})
    public ResponseEntity<List<Item>> showItemsByName(@RequestParam("name") String name) {
        logger.info("Called showItemsByName method");
        List<Item> items = itemService.findByName(name);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping(params = {"price"})
    public ResponseEntity<List<Item>> showItemsByPrice(@RequestParam("price") Double price) {
        logger.info("Called showItemsByPrice method");
        List<Item> items = itemService.findByPrice(price);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<List<Item>> showItemsByCategory(@RequestBody @Valid Category category,
                                                          @RequestParam("page") int page,
                                                          @RequestParam("size") int size,
                                                          BindingResult bindingResult) {
        logger.info("Called showItemsByCategory method");

        if (bindingResult.hasErrors()) {
            logger.info("Category is not valid");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<Item> items = itemService.findByCategory(category, pageable).getContent();

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping(params = {"keyword", "page", "size"})
    public ResponseEntity<List<Item>> showItemsByKeyword(@RequestParam("keyword") String keyword,
                                                         @RequestParam("page") int page,
                                                         @RequestParam("size") int size) {
        logger.info("Called showItemsByKeyword method");
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<Item> items = itemService.findBySearch(keyword, pageable).getContent();

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> showItemById(@PathVariable("id") Long id) {
        logger.info("Called showItemById method");

        try {
            Item items = itemService.findById(id);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            logger.warn("Item with id " + id + " not found");
            logger.error(ex.toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(params = {"code"})
    public ResponseEntity<Item> showItemByCode(@RequestParam("code") String code) {
        logger.info("Called showItemById method");

        Item item = itemService.findByCode(code);
        if (item == null) {
            logger.warn("Item with code " + code + " not found");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable("id") Long id,
                                           @RequestBody @Valid Item item,
                                           BindingResult bindingResult) {
        logger.info("Called updateItem method");

        if (bindingResult.hasErrors()) {
            logger.info("Bad request on update item information");
            return new ResponseEntity<>(item, HttpStatus.BAD_REQUEST);
        }

        try {
            Item persistentItem = itemService.findById(id);

            BeanUtils.copyProperties(item, persistentItem, "id");
            itemService.save(persistentItem);
            return new ResponseEntity<>(persistentItem, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            logger.warn("Item with id - " + id + " not found");
            logger.error(ex.toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Item> createNewItem(@RequestBody @Valid Item item,
                                              BindingResult bindingResult) {
        logger.info("Called createNewItem method");

        if (bindingResult.hasErrors()) {
            logger.info("Bad request on create item information");
            return new ResponseEntity<>(item, HttpStatus.BAD_REQUEST);
        }

        itemService.save(item);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Long id) {
        logger.info("Called deleteItem method");

        try {
            Item item = itemService.findById(id);
            itemService.delete(item);
        } catch (NoSuchElementException ex) {
            logger.warn("Item with id - " + id + " not found");
            logger.error(ex.toString());
        }
    }
}
