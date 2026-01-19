package org.example;

public class DishIngredient {
    private Integer dishId;
    private Integer ingredientId;
    private Double requiredQuantity;
    private String unit;

    public DishIngredient() {}

    public DishIngredient(Integer dishId, Integer ingredientId, Double requiredQuantity, String unit) {
        this.dishId = dishId;
        this.ingredientId = ingredientId;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    public Integer getDishId() { return dishId; }
    public void setDishId(Integer dishId) { this.dishId = dishId; }

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
