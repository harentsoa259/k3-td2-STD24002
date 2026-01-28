package org.example;

import java.util.ArrayList;
import java.util.List;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    // Ajout de la liste des mouvements
    private List<StockMovement> stockMovementList;

    public Ingredient() {
        this.stockMovementList = new ArrayList<>();
    }

    public Ingredient(Integer id, String name, Double price, CategoryEnum category) {
        this();
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }
}