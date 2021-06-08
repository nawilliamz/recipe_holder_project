package com.example.recipeholder;

import java.util.UUID;

public class Ingredient {

    private String mIngredient_id;
    private UUID mId;
    private String mName;
    private String mAmount;



    public Ingredient(UUID mId, String mIngredient_id, String name, String amount) {
        this.mIngredient_id = mIngredient_id;
        this.mId = mId;
        this.mName = name;
        this.mAmount = amount;
    }


    public String getIngredient_id() {
        return mIngredient_id;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setIngredient_id(String ingredient_id) {
        mIngredient_id = ingredient_id;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    @Override
    public String toString() {
        return this.mName + "  " + this.mAmount;
//        return this.mIngredient_id + " " + this.mAmount;
    }
}
