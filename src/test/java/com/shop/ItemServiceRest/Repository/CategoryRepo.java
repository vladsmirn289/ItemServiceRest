package com.shop.ItemServiceRest.Repository;

import com.shop.ItemServiceRest.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    List<Category> findByParent(Category parent);
    List<Category> findByParentIsNull();
    Category findByName(String name);
}
