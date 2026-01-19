package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    public Dish findDishById(Integer id) {
        String sql = "SELECT id, name, dish_type, price FROM dish WHERE id = ?";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));

                dish.setDishIngredients(findDishIngredientByDishId(id));
                return dish;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DishIngredient> findDishIngredientByDishId(Integer dishId) {
        String sql = "SELECT dish_id, ingredient_id, required_quantity, unit FROM dish_ingredient WHERE dish_id = ?";

        List<DishIngredient> list = new ArrayList<>();
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DishIngredient di = new DishIngredient();
                    di.setDishId(rs.getInt("dish_id"));
                    di.setIngredientId(rs.getInt("ingredient_id"));
                    di.setRequiredQuantity(rs.getDouble("required_quantity"));
                    di.setUnit(rs.getString("unit"));
                    list.add(di);
                }
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Ingredient findIngredientById(Integer id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("Ingredient not found " + id);

                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getDouble("price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                return ing;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDish(Dish toSave) {
        String sql = """
            INSERT INTO dish (id, name, dish_type, price)
            VALUES (?, ?, ?::dish_type, ?)
            ON CONFLICT (id) DO UPDATE
            SET name = EXCLUDED.name,
                dish_type = EXCLUDED.dish_type,
                price = EXCLUDED.price
            RETURNING id;
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                if (toSave.getId() != null) ps.setInt(1, toSave.getId());
                else ps.setInt(1, getNextSerialValue(conn, "dish", "id"));

                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getDishType().name());
                ps.setObject(4, toSave.getPrice());

                ResultSet rs = ps.executeQuery();
                rs.next();
                dishId = rs.getInt(1);
            }

            deleteDishIngredients(conn, dishId);
            insertDishIngredients(conn, dishId, toSave.getDishIngredients());

            conn.commit();
            findDishById(dishId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dish_ingredient WHERE dish_id = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void insertDishIngredients(Connection conn, Integer dishId, List<DishIngredient> list) throws SQLException {
        if (list == null || list.isEmpty()) return;

        String sql = """
            INSERT INTO dish_ingredient (dish_id, ingredient_id, required_quantity, unit)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (dish_id, ingredient_id) DO UPDATE
            SET required_quantity = EXCLUDED.required_quantity,
                unit = EXCLUDED.unit;
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : list) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIngredientId());
                ps.setObject(3, di.getRequiredQuantity());
                ps.setString(4, di.getUnit());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void saveIngredient(Ingredient ingredient) {
        String sql = """
        INSERT INTO ingredient (id, name, price, category)
        VALUES (?, ?, ?, ?::ingredient_category)
        ON CONFLICT (id) DO UPDATE
        SET name = EXCLUDED.name,
            price = EXCLUDED.price,
            category = EXCLUDED.category
        """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (ingredient.getId() == null) {
                int nextId = getNextSerialValue(conn, "ingredient", "id");
                ps.setInt(1, nextId);
                ingredient.setId(nextId);
            } else {
                ps.setInt(1, ingredient.getId());
            }

            ps.setString(2, ingredient.getName());
            ps.setDouble(3, ingredient.getPrice());
            ps.setString(4, ingredient.getCategory().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double getDishCost(Dish dish) {
        double cost = 0.0;
        for (DishIngredient di : dish.getDishIngredients()) {
            Ingredient ing = findIngredientById(di.getIngredientId());
            cost += ing.getPrice() * di.getRequiredQuantity();
        }
        return cost;
    }

    public double getGrossMargin(Dish dish) {
        if (dish.getPrice() == null) {
            throw new RuntimeException("Selling price is null for dish " + dish.getName());
        }
        double cost = getDishCost(dish);
        return dish.getPrice() - cost;
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT pg_get_serial_sequence(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) throw new IllegalArgumentException("No sequence found");

        String setValSql = String.format(
                "SELECT setval('%s', COALESCE((SELECT MAX(%s) FROM %s), 1), false)",
                sequenceName, columnName, tableName
        );
        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }

        String nextValSql = "SELECT nextval(?)";
        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
