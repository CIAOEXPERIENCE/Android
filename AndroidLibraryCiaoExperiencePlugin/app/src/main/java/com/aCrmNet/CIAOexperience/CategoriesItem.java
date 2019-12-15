package com.aCrmNet.CIAOexperience;

/**
 * Created by Andrea on 03/03/2017.
 */

public class CategoriesItem
{
    private String name,color,icon;
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    private int promoKey;
    public CategoriesItem(String name, String color) {

        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }


}
