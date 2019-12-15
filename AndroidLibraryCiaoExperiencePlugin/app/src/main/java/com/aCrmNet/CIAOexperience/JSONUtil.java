package com.aCrmNet.CIAOexperience;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrea on 12/03/2017.
 */

public class JSONUtil {
    private ArrayList<ItemAdapter> adapterListView;
    private HashMap<String, Object> argList;

    JSONUtil() {
        this.adapterListView = new ArrayList<>();
        this.argList = new HashMap<>();
    }

    public ArrayList<ItemAdapter> parseSimpleJSON(JSONArray ja, int status) throws JSONException {
        ///empty jsonobject(lenght = 0)
        return parser(new JSONObject(), ja, status);
    }

    public ArrayList<ItemAdapter> parseCategoryJSON(JSONObject jo, int status) throws JSONException {
        return parser(jo, new JSONArray(), status);
    }

    private ArrayList<ItemAdapter> parser(JSONObject jo, JSONArray ja, int status) throws JSONException {
        int type;
        if (jo.length() > 0) {
            ja = jo.getJSONObject(JSONTag.RESULT).getJSONArray(JSONTag.CATEGORIES);
            type = Constant.HORIZONTAL_LIST;
        } else {
            type = Constant.SIMPLE;
        }
        if (ja.length() > 0) {
            adapterListView.clear();
            for (int i = 0; i < ja.length(); i++) {
                String id = "", name = "", image = "", idCategory = "none";
                String date = "none";
                String promoCode = "";
                String bus_type = "none";
                boolean hasMap;
                int promoKey = 0, like = 0;
                if (type == Constant.HORIZONTAL_LIST) {
                    id = i + "";
                    name = ja.getJSONObject(i).getString(JSONTag.BUSINESS);
                    if (ja.getJSONObject(i).getInt(JSONTag.COUNT) == 0)
                        continue;
                }
                if (status == Constant.ACCOMMODATION) {
                    hasMap = true;
                } else {
                    hasMap = false;
                }
                argList.clear();
                argList.put(JSONTag.ID, id);
                argList.put(JSONTag.NAME, name);
                argList.put(JSONTag.IMAGE, image);
                argList.put(JSONTag.ID_CATEGORY, idCategory);
                argList.put(JSONTag.PROMO_KEY, promoKey);
                argList.put(JSONTag.FAVORITE, like);
                argList.put(JSONTag.DATE, date);
                argList.put(JSONTag.PROMO_CODE, promoCode);
                argList.put(JSONTag.BUS_TYPE, bus_type);
                boolean hasLike = ja.getJSONObject(i).has(JSONTag.FAVORITE);
                for (HashMap.Entry<String, Object> entry : argList.entrySet()) {
                    if (checkParse(entry)) {
                        parseObj(ja.getJSONObject(i), entry);
                    }
                }
                if (!argList.get(JSONTag.BUS_TYPE).equals("none")) {
                    if (argList.get(JSONTag.BUS_TYPE).equals(JSONTag.BUS_EVENT)) {
                        argList.put(JSONTag.ID_CATEGORY, "10");
                    } else if (argList.get(JSONTag.BUS_TYPE).equals(JSONTag.BUS_HAPPENIG)) {
                        argList.put(JSONTag.ID_CATEGORY, "none");
                    }
                }

                adapterListView.add(new ItemAdapter(argList.get(JSONTag.ID).toString(),
                        argList.get(JSONTag.IMAGE).toString(), type, argList.get(JSONTag.NAME).toString(),
                        argList.get(JSONTag.ID_CATEGORY).toString(), (int) argList.get(JSONTag.PROMO_KEY),
                        hasLike, ja.getJSONObject(i), (int) argList.get(JSONTag.FAVORITE),
                        argList.get(JSONTag.DATE).toString(), hasMap, argList.get(JSONTag.PROMO_CODE).toString()));
            }


            return adapterListView;
        }
        return new ArrayList<>();
    }

    private void parseObj(JSONObject jo, HashMap.Entry<String, Object> entry) throws JSONException {
        {

            if (entry.getKey().equals(JSONTag.IMAGE)) {
                if (jo.has(entry.getKey())) {
                    String s = jo.getString(entry.getKey());
                    argList.put(entry.getKey(), s);
                } else if (jo.has(JSONTag.IMG)) {
                    String s = jo.getString(JSONTag.IMG);
                    argList.put(entry.getKey(), s);
                }
            } else if (entry.getKey().equals(JSONTag.PROMO_CODE)) {
                if (jo.has(JSONTag.PROMO_CODE)) {
                    JSONArray ja = jo.getJSONArray(JSONTag.PROMO_CODE);
                    if (ja.length() > 0) {
                        String p = ja.getString(0);
                        argList.put(entry.getKey(), p);
                    } else {
                        argList.put(entry.getKey(), "");
                    }
                }
            } else if (jo.has(entry.getKey())) {
                if (entry.getKey().equals(JSONTag.BUS_TYPE)) {
                    String s = jo.getString(entry.getKey());
                    argList.put(entry.getKey(), s);
                } else {
                    if (entry.getValue() instanceof String) {
                        String s = jo.getString(entry.getKey());
                        argList.put(entry.getKey(), s);
                    } else if (entry.getValue() instanceof Integer) {
                        int i = jo.getInt(entry.getKey());
                        argList.put(entry.getKey(), i);
                    }
                }
            }
        }
    }

    private boolean checkParse(HashMap.Entry<String, Object> entry) {
        boolean check = false;
        if (entry.getValue() instanceof String) {
            if (entry.getValue().equals("") || entry.getValue().equals("none"))
                check = true;
        } else if (entry.getValue() instanceof Integer) {
            if ((int) entry.getValue() == 0) {
                check = true;
            }
        }
        return check;
    }

}

