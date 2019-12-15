package com.aCrmNet.CIAOexperience;

import org.json.JSONObject;

/**
 * Created by Andrea on 08/02/2017.
 */

class ItemAdapter {
    private String imageUrl, title,id,idCategory,date;
    private int type, promoKey, like;
    private boolean hasLike, hasMap;
    private JSONObject js;

    private String promoCode;
    ItemAdapter(String id, String imageUrl, int type,
                String title, String idCategory, int promoKey, boolean hasLike, JSONObject js, int like, String date,
                boolean hasMap, String promoCode) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.type = type;
        this.title = title;
        this.idCategory = idCategory;
        this.promoKey = promoKey;
        this.hasLike = hasLike;
        this.js = js;
        this.like=like;
        this.date = date;
        this.hasMap = hasMap;
        this.promoCode = promoCode;
    }
    ///get methods
    int getType() {return type;}
    JSONObject getJs() {
        return js;
    }
    boolean isHasLike() {
        return hasLike;
    }
    int getPromoKey() {
        return promoKey;
    }
    String getIdCategory() {
        return idCategory;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    String getImageUrl() {
        return imageUrl;
    }
    String getTitle() {
        return title;
    }
    String getDate() {return date;}
    public boolean isHasMap() {return hasMap;}
    public String getPromoCode() {
        return promoCode;
    }


    //setMethod
    public void setJs(JSONObject js){
        this.js = js;
    }
    public int getLike() {return like;}
    public void setLike(int like){
        this.like = like;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
