package com.gnnsnowszerro.psngiftcardsgenerator;

/**
 * Created by Alexandr on 16/06/2017.
 */

public class Coupon {

    private int cost;
    private String text;
    private int image_id;

    public Coupon(int cost, String text, int image_id) {
        this.cost = cost;
        this.text = text;
        this.image_id = image_id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }
}
