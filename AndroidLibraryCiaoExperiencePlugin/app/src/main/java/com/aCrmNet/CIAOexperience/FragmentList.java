package com.aCrmNet.CIAOexperience;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentList extends Fragment {
    AdapterListView adapter;
    JSONUtil jsonUtil;
    HashMap<String, CategoriesItem> catMap;
    private ListView lv;
    private String json;
    private OnFragmentInteractionListener mListener;


    ////test view
    //TextView btnHome, btnEvent, btnHapp;

    ////*-*-**- test view
    public FragmentList() {
        // Required empty public constructor
    }

    public void setElement(String json, String jsonDash, int status) throws JSONException {
        //check status
        //adapter.setJson(json);
        JSONObject js = new JSONObject(json);
        JSONArray jsonArray;
        switch (status) {
            case Constant.HOME:
                jsonArray = js.getJSONObject(JSONTag.RESULT).getJSONArray(JSONTag.BUSINESS);
                break;
            case Constant.BUSINESS_CATEGORY:
                jsonArray = js.getJSONObject(JSONTag.RESULT).getJSONArray(JSONTag.BUSINESS);
                break;
            case Constant.DEFAULT:
                jsonArray = js.getJSONArray(JSONTag.RESULT);
                break;
            default:
                jsonArray = js.getJSONArray(JSONTag.RESULT);
        }

        catMap.clear();
        //dashboard
        JSONArray jDash = new JSONObject(jsonDash).getJSONObject(JSONTag.RESULT).getJSONArray(JSONTag.CATEGORIES);
        for (int i = 0; i < jDash.length(); i++) {
            String id = jDash.getJSONObject(i).getString(JSONTag.ID);
            String name = jDash.getJSONObject(i).getString(JSONTag.NAME);
            String color = jDash.getJSONObject(i).getString(JSONTag.COLOR);
            catMap.put(id, new CategoriesItem(name, color));
        }
        //simple element
        adapter.clear();
        ArrayList<ItemAdapter> simpleItems = new ArrayList<>();
        if (status != Constant.RECOMMENDATION) {
            simpleItems.addAll(jsonUtil.parseSimpleJSON(jsonArray, status));
            for (int i = 0; i < simpleItems.size(); i++) {
                adapter.add(simpleItems.get(i));
            }
        } else {///RECCOMENDATION LIST STATUS
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                simpleItems.addAll(jsonUtil.parseSimpleJSON(jo.getJSONArray(JSONTag.BUSINESS), status));
            }

            for (int i = 0; i < simpleItems.size(); i++) {
                adapter.add(simpleItems.get(i));
            }
        }
        simpleItems.clear();

        ///horizontal element
        if (status == Constant.HOME) {
            ArrayList<ItemAdapter> categoryItem = new ArrayList<>();
            categoryItem.addAll(jsonUtil.parseCategoryJSON(js, status));
            for (int i = 0; i < categoryItem.size(); i++) {
                adapter.add(categoryItem.get(i));
            }
        }
        new Runnable() {
            @Override
            public void run() {
                adapter.setMap(catMap);
                adapter.notifyDataSetChanged();
                lv.setSelectionAfterHeaderView();
            }
        }.run();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            json = getArguments().getString("json");
            Log.i("json", json);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        adapter = new AdapterListView(getActivity(), R.layout.simple_row_list);
        catMap = new HashMap<String, CategoriesItem>();
        jsonUtil = new JSONUtil();
        String a = "a";
        Log.i("TEST log", a);
        ///---*-*-test view
        ((MainActivity) getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh(String json, String jsonDash, int status) {
                try {
                    setElement(json, jsonDash, status);
                } catch (JSONException e) {
                    Log.i("EXEPTION ", e.toString());
                    e.printStackTrace();
                }
                // Refresh Fragment
            }
        });
        ((MainActivity) getActivity()).setOnSetLikeListener(new MainActivity.OnSetLikeListener() {
            @Override
            public void setLikeListener(String id, int like) {
                likeHandler(id, like);
            }
        });

        ((MainActivity) getActivity()).setOnListInit(new MainActivity.OnListInit() {
            @Override
            public void listInit(int top, int bottom) {
                Log.i("LISTINIT", "init listener");
                // init Fragment list params
                if (lv != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, top, 0, bottom);
                    lv.setLayoutParams(params);
                }
            }
        });
        lv = (ListView) v.findViewById(R.id.myListView);
        lv.setAdapter(adapter);
        adapter.setOnImageLoad(new AdapterListView.OnImageLoad() {
            @Override
            public void ImageLoad() {
                if (lv.getVisibility() != View.VISIBLE)
                    lv.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void likeHandler(String id, int like) {
        Log.i("FAVORITE CHECK","id = "+id);
        Log.i("FAVORITE CHECK","value = "+like);
        for (int i = 0; i < adapter.getCount(); i++) {

            if (adapter.getItem(i).getType() == Constant.SIMPLE) {
                if (adapter.getItem(i).getId().equals(id)) {
                    adapter.getItem(i).setLike(like);
                    JSONObject jsl = adapter.getItem(i).getJs();
                    try {
                        jsl.put(JSONTag.FAVORITE, like);
                        adapter.getItem(i).setJs(jsl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("FAVORITE CHECK","vertical element post value "+ adapter.getItem(i).getLike());
                }
            }else{
                if(adapter.getItem(i).getType()==Constant.HORIZONTAL_LIST){
                    try {
                        JSONArray ja = new JSONArray(adapter.getItem(i).getTitle());
                        for(int j =0;j<ja.length();j++){
                            if(ja.getJSONObject(j).getString(JSONTag.ID).equals(id)){
                                ja.getJSONObject(j).put(JSONTag.FAVORITE,like);
                                adapter.getItem(i).setTitle(ja.toString());
                                Log.i("FAVORITE CHECK","HORIZONTAL element post value (JSOBJ)  "+ adapter.getItem(i).getTitle());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
