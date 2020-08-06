package com.shop.ItemServiceRest.Controller;

import com.shop.ItemServiceRest.Model.Item;
import com.shop.ItemServiceRest.Service.ItemService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Find item by name")
    @GetMapping("/byName/{name}")
    public ResponseEntity<List<Item>> showItemsByName(@PathVariable("name") String name) {
        logger.info("Called showItemsByName method");
        List<Item> items = itemService.findByName(name);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @ApiOperation(value = "Find item by price")
    @GetMapping("/byPrice/{price}")
    public ResponseEntity<List<Item>> showItemsByPrice(@PathVariable("price") Double price) {
        logger.info("Called showItemsByPrice method");
        List<Item> items = itemService.findByPrice(price);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @ApiOperation(value = "Find item by keyword with pagination")
    @GetMapping(value = "/byKeyword/{keyword}", params = {"page", "size"})
    public ResponseEntity<List<Item>> showItemsByKeyword(@PathVariable("keyword") String keyword,
                                                         @RequestParam("page") int page,
                                                         @RequestParam("size") int size) {
        logger.info("Called showItemsByKeyword method");
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<Item> items = itemService.findBySearch(keyword, pageable).getContent();

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @ApiOperation(value = "Find item by id")
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

    @ApiOperation(value = "Find item by code")
    @GetMapping("/byCode/{code}")
    public ResponseEntity<Item> showItemByCode(@PathVariable("code") String code) {
        logger.info("Called showItemById method");

        Item item = itemService.findByCode(code);
        if (item == null) {
            logger.warn("Item with code " + code + " not found");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @ApiOperation(value = "Update exists item")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad request (invalid item information)")
    })
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

    @ApiOperation(value = "Create new item")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad request (invalid item information)")
    })
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

    @ApiOperation(value = "Delete item", notes = "Don't recommended to use")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Conflict (The item can be located in the basket or in the order list of anyone)")
    })
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
