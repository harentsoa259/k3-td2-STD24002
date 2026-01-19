package org.example;

import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<DishIngredient> dishIngredients;

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType, Double price, List<DishIngredient> dishIngredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.dishIngredients = dishIngredients;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<DishIngredient> getDishIngredients() { return dishIngredients; }
    public void setDishIngredients(List<DishIngredient> dishIngredients) { this.dishIngredients = dishIngredients; }

    public Double getDishCost(DataRetriever dr) {
        if (dishIngredients == null || dishIngredients.isEmpty()) return 0.0;

        double cost = 0.0;
        for (DishIngredient di : dishIngredients) {
            Ingredient ing = dr.findIngredientById(di.getIngredientId());
            cost += ing.getPrice() * di.getRequiredQuantity();
        }
        return cost;
    }

    public Double getGrossMargin(DataRetriever dr) {
        if (price == null) {
            throw new RuntimeException("Price is null");
        }
        return price - getDishCost(dr);
    }
}
