package com.aCrmNet.CIAOexperience;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrea on 09/02/2017.
 */

class AdapterListView extends ArrayAdapter<ItemAdapter> {
    private Context context;
    private int resource;
    private ArrayList<ItemAdapter> list;
    private HashMap<String, CategoriesItem> catMap;
    private ImageUtil util;
    //private ArrayList<Object> like;

    AdapterListView(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        list = new ArrayList<>();
        this.onItemImageClickListener = (OnSimpleItemClickListener) context;
        this.util = new ImageUtil(context);

    }

    @Override
    public void clear() {
        super.clear();
        list.clear();
    }

    //categories hashpmap, map key is equal horizonal item id, values is a categorie item
    void setMap(HashMap<String, CategoriesItem> catMap) {
        this.catMap = catMap;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ItemAdapter getItem(int position) {
        return list.get(position);
    }

    @Override
    public void add(ItemAdapter object) {
        list.add(object);
    }

    //type 0 = simple, 1 = horizontal
    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }
    ///SIMPLE VIEW HOLDER
    public static class SimpleViewHolder {
        TextView titleText, catText, dateText, promoText, viewBook;
        ImageView img, shareBtn, likeBtn, catIcon, promoKey, mapBtn;
        RelativeLayout labelLayout;
        RelativeLayout footer;
        ImageView catLabel;

        SimpleViewHolder(View convertView) {
            titleText = (TextView) convertView.findViewById(R.id.titleText);
            catText = (TextView) convertView.findViewById(R.id.hor_cat_text);
            dateText = (TextView) convertView.findViewById(R.id.dateText);
            catIcon = (ImageView) convertView.findViewById(R.id.cat_icon);
            img = (ImageView) convertView.findViewById(R.id.imgItem);
            shareBtn = (ImageView) convertView.findViewById(R.id.shareBtn);
            likeBtn = (ImageView) convertView.findViewById(R.id.likeBtn);
            catLabel = (ImageView) convertView.findViewById(R.id.catLabel);
            labelLayout = (RelativeLayout) convertView.findViewById(R.id.labelLayout);
            promoKey = (ImageView) convertView.findViewById(R.id.promoKey);
            mapBtn = (ImageView) convertView.findViewById(R.id.mapBtn);
            footer = (RelativeLayout) convertView.findViewById(R.id.footer);
            promoText = (TextView)convertView.findViewById(R.id.promoText);
            viewBook = (TextView)convertView.findViewById(R.id.viewBook);
        }
    }

    ///HORIZONTAL VIEW HOLDER
    private static class HorizontalViewHolder {
        LinearLayout linear;
        CustomHorizontalScrollView scrollView;
        ImageView horLabel, horIconLabel;
        TextView horTextLabel;

        HorizontalViewHolder(View convertView) {
            linear = (LinearLayout) convertView.findViewById(R.id.linear);
            scrollView = (CustomHorizontalScrollView) convertView.findViewById(R.id.horScroll);
            horIconLabel = (ImageView) convertView.findViewById(R.id.hor_cat_icon);
            horLabel = (ImageView) convertView.findViewById(R.id.horCatLabel);
            horTextLabel = (TextView) convertView.findViewById(R.id.hor_cat_text);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (getItemViewType(position) == Constant.SIMPLE) {////SIMPLE ITEM
            SimpleViewHolder viewHolder;
            ///add more items if listview reached bottom
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(resource, null, true);
                viewHolder = new SimpleViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SimpleViewHolder) convertView.getTag();
            }
            final ItemAdapter item = list.get(position);
            if (item != null) {
                ///set click
                clickOnItem(viewHolder.img, jsonFormat(item.getJs(), Constant.IMAGE, position), position, Constant.SIMPLE);
                clickOnItem(viewHolder.likeBtn, jsonFormat(item.getJs(), Constant.LIKE, position), position, Constant.SIMPLE);
                clickOnItem(viewHolder.shareBtn, jsonFormat(item.getJs(), Constant.SHARE, position), position, Constant.SIMPLE);
                clickOnItem(viewHolder.footer, jsonFormat(item.getJs(), Constant.ITEM, position), position, Constant.SIMPLE);
                clickOnItem(viewHolder.catLabel, jsonFormat(item.getJs(), Constant.CATEGORY, position), position, Constant.SIMPLE);
                clickOnItem(viewHolder.mapBtn, jsonFormat(item.getJs(), Constant.MAP, position), position, Constant.SIMPLE);
                clickOnItem(viewHolder.promoText, jsonFormat(item.getJs(), Constant.PROMO_CODE, position), position, Constant.SIMPLE);


                ///SET ITEM
                if (!item.getIdCategory().equals("none") && Constant.CURRENT_STATUS!=Constant.BUSINESS_CATEGORY && Constant.CURRENT_STATUS!=Constant.EXPERIENCE ) {///check if has category label
                    viewHolder.labelLayout.setVisibility(View.VISIBLE);
                    viewHolder.catLabel.setColorFilter(Color.parseColor(catMap.get(item.getIdCategory()).getColor()));
                    viewHolder.catText.setText(catMap.get(item.getIdCategory()).getName().toUpperCase());
                    util.setCatIcon(viewHolder.catIcon, item.getIdCategory());
                } else {
                    viewHolder.labelLayout.setVisibility(View.GONE);
                }
                //NEW
                if(Constant.IS_LOCALE) {
                    String path = Constant.ROOT_PATH + item.getImageUrl().replace("http://www.interlog.it/ciao", "");
                    File f = new File(path);

                    if (f.exists())
                        util.setImageRatio(item.getImageUrl(), viewHolder.img, Constant.getScreenWidth());
                }else {
                    util.setImageRatio(item.getImageUrl(), viewHolder.img, Constant.getScreenWidth());
                }

                viewHolder.titleText.setText(item.getTitle());
                if (item.isHasLike()) {                            ///check if has like button
                    viewHolder.likeBtn.setVisibility(View.VISIBLE);
                    if (item.getLike() == 1) {
                        //Toast.makeText(context, "LIKE ON", Toast.LENGTH_SHORT).show();
                        viewHolder.likeBtn.setImageResource(R.mipmap.like_fill_mm);
                        viewHolder.likeBtn.setTag(1);
                    } else {
                        viewHolder.likeBtn.setImageResource(R.mipmap.like_mm);
                        viewHolder.likeBtn.setTag(0);
                    }
                } else {
                    viewHolder.likeBtn.setVisibility(View.GONE);
                }
                if (!item.getDate().equals("none")) {
                    viewHolder.dateText.setVisibility(View.VISIBLE);
                    viewHolder.dateText.setText(item.getDate());
                } else if (item.getDate().equals("none")) {
                    viewHolder.dateText.setVisibility(View.GONE);
                }
                if (item.getPromoKey() != 0 && viewHolder.promoKey.getVisibility() != View.VISIBLE && !item.isHasMap()) {///check if ha promokey
                    viewHolder.promoKey.setVisibility(View.VISIBLE);

                } else if (item.getPromoKey() == 0 && viewHolder.promoKey.getVisibility() == View.VISIBLE) {
                    viewHolder.promoKey.setVisibility(View.INVISIBLE);
                }

                if (Constant.CURRENT_STATUS!=Constant.EXPERIENCE) {           ///Hide share button for experiences from profile
                    viewHolder.shareBtn.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.shareBtn.setVisibility(View.GONE);
                    viewHolder.promoKey.setVisibility(View.GONE);
                }

                if (item.isHasMap()) {
                    viewHolder.viewBook.setVisibility(View.VISIBLE);
                    viewHolder.mapBtn.setVisibility(View.VISIBLE);
                    if(item.getPromoKey() != 0){
                        viewHolder.promoText.setVisibility(View.VISIBLE);
                        viewHolder.promoText.setText(item.getPromoCode());
                        //Log.i("GET_PROMO",item.getPromoCode());
                    }else viewHolder.promoText.setVisibility(View.GONE);
                } else {
                    viewHolder.viewBook.setVisibility(View.GONE);
                    viewHolder.mapBtn.setVisibility(View.GONE);
                    viewHolder.promoText.setVisibility(View.GONE);
                }
            }
        } else if (getItemViewType(position) == Constant.HORIZONTAL_LIST) {//// HORIZONTAL ITEM
            HorizontalViewHolder horViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.horizontal_listview, null, true);//inflate scrollview layout
                horViewHolder = new HorizontalViewHolder(convertView);
                convertView.setTag(horViewHolder);
            } else
                horViewHolder = (HorizontalViewHolder) convertView.getTag();

            final ItemAdapter item = list.get(position);
            if (item != null) {
                try {
                    final JSONArray ja = new JSONArray(item.getTitle());
                    horViewHolder.scrollView.initSnap(ja.length() + 1, Constant.get75pScreen());
                    util.setCatIcon(horViewHolder.horIconLabel, item.getIdCategory());
                    horViewHolder.horTextLabel.setText(catMap.get(item.getIdCategory()).getName().toUpperCase());
                    horViewHolder.horLabel.setColorFilter(Color.parseColor(catMap.get(item.getIdCategory()).getColor()));
                    horViewHolder.linear.removeAllViews();
                    clickOnItem(horViewHolder.horLabel, jsonFormat(item.getJs(), Constant.CATEGORY, position), position, Constant.SIMPLE);

                    for (int i = 0; i < ja.length(); i++) {
                        View itemView = LayoutInflater.from(context).inflate(R.layout.horizontal_row_element, horViewHolder.linear, false);
                        itemView.setLayoutParams(new ViewGroup.LayoutParams(Constant.get75pScreen(), ViewGroup.LayoutParams.WRAP_CONTENT));
                        final JSONObject jso = ja.getJSONObject(i);
                        final String name = jso.getString(JSONTag.NAME);
                        final String image = jso.getString(JSONTag.IMAGE);
                        final int promoKey = jso.getInt(JSONTag.PROMO_KEY);
                        final int islike = jso.getInt(JSONTag.FAVORITE);
                        ///item view
                        TextView titleText = (TextView) itemView.findViewById(R.id.titleText);
                        ImageView img = (ImageView) itemView.findViewById(R.id.imgItem);
                        ImageView like = (ImageView) itemView.findViewById(R.id.horLikeBtn);
                        ImageView share = (ImageView) itemView.findViewById(R.id.horShareBtn);
                        ImageView promoLabel = (ImageView) itemView.findViewById(R.id.horPromoKey);
                        //imgOnt
                        LinearLayout catFooter = (LinearLayout) itemView.findViewById(R.id.catFooter);
                        if (islike == 1) {
                            like.setImageResource(R.mipmap.like_fill_mm);
                            like.setTag(1);
                        } else {
                            like.setImageResource(R.mipmap.like_mm);
                            like.setTag(0);
                        }
                        clickOnItem(img, jsonFormat(jso, Constant.IMAGE, position), position, Constant.HORIZONTAL_LIST);
                        clickOnItem(like, jsonFormat(jso, Constant.LIKE, position), position, Constant.HORIZONTAL_LIST);
                        clickOnItem(share, jsonFormat(jso, Constant.SHARE, position), position, Constant.HORIZONTAL_LIST);
                        clickOnItem(titleText, jsonFormat(jso, Constant.ITEM, position), position, Constant.HORIZONTAL_LIST);
                        if (catMap != null && catFooter != null)
                            catFooter.setBackgroundColor(Color.parseColor(catMap.get(item.getIdCategory()).getColor()));

                        if (promoKey == 1 && promoLabel.getVisibility() != View.VISIBLE) {
                            promoLabel.setVisibility(View.VISIBLE);
                        } else if (promoKey == 0 && promoLabel.getVisibility() == View.VISIBLE) {
                            {
                                promoLabel.setVisibility(View.GONE);
                            }
                        }
                        titleText.setText(name);
                        util.setImageRatio(image, img, Constant.get75pScreen());
                        img.setLayoutParams(new RelativeLayout.LayoutParams(Constant.get75pScreen(), RelativeLayout.LayoutParams.WRAP_CONTENT));
                        horViewHolder.linear.addView(itemView);
                    }
                    View itemView2 = LayoutInflater.from(context).inflate(R.layout.category_horizontal_row_element, horViewHolder.linear, false);
                    itemView2.setLayoutParams(new ViewGroup.LayoutParams(Constant.get75pScreen(), ViewGroup.LayoutParams.MATCH_PARENT));
                    RelativeLayout rl = (RelativeLayout) itemView2.findViewById(R.id.imageContainer);
                    clickOnItem(rl, jsonFormat(item.getJs(), Constant.CATEGORY, position), position, Constant.SIMPLE);
                    ImageView iv = (ImageView) itemView2.findViewById(R.id.catIcon);
                    rl.setBackgroundColor(Color.parseColor(catMap.get(item.getIdCategory()).getColor()));
                    util.setCatIcon(iv, item.getIdCategory());
                    horViewHolder.linear.addView(itemView2);
                    horViewHolder.scrollView.scrollTo(0,0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("HOR ADAPTER", e.toString());
                }
            }
        }
        return convertView;
    }
    ////public interfaces

    interface OnSimpleItemClickListener {
        void simpleItemClick(View v, int pos, String function, int click);
    }

    ///notify if is load at least ona image
    interface OnImageLoad {
        void ImageLoad();
    }

    ///interface listener instance
    private OnSimpleItemClickListener onItemImageClickListener;
    private OnImageLoad onImageLoad;

    public void setOnSimpleItemClickListener(OnSimpleItemClickListener onItemImageClickListener) {
        this.onItemImageClickListener = onItemImageClickListener;
    }

    private String jsonFormat(JSONObject js, int click, int index) {
        try {
            js.put("_click_action", click);
            if (click == Constant.LIKE) {
                js.put("_list_index", index);
            }
            return js.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    void setOnImageLoad(OnImageLoad onImageLoad) {
        this.onImageLoad = onImageLoad;
        if (util != null) {
            util.setImageLoadListener(onImageLoad);
        }
    }

    private void setImageFromPath(final ImageView imgv, final String fileName) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                File imgFile = new File("/sdcard/textures/file-upload/events/000584.jpg");
                if (imgFile.exists()) {
                    //return BitmapFactory.decodeFile(imgFile.getAbsolutePath()).compress();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null)
                    imgv.setImageBitmap(bitmap);
            }
        }.execute();
    }

    private void clickOnItem(View v, final String args, final int pos, final int type) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.likeBtn || v.getId() == R.id.horLikeBtn) {
                    if (!Constant.isLogged) {
                        AlertDialog.Builder d = alertDialog();
                        d.show();
                    } else {
                        onItemImageClickListener.simpleItemClick(v, 0, args, Constant.ITEM);
                        if ((int) v.getTag() == 0) {
                            v.setTag(1);
                            ((ImageView) v).setImageResource(R.mipmap.like_fill_mm);
                            likeHandler(pos, args, type, 1);
                        } else {
                            v.setTag(0);
                            ((ImageView) v).setImageResource(R.mipmap.like_mm);
                            likeHandler(pos, args, type, 0);
                        }
                    }
                    Log.i("CHECK_FAVORIT",args);
                } else
                    onItemImageClickListener.simpleItemClick(v, 0, args, Constant.ITEM);
            }
        });
    }


    private void likeHandler(int pos, String args, int type, int active) {
        list.get(pos).setLike(active);
        JSONObject jsl  = list.get(pos).getJs();
        try {
            jsl.put(JSONTag.FAVORITE,active);
            list.get(pos).setJs(jsl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (type == Constant.SIMPLE) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getType() == Constant.HORIZONTAL_LIST) {
                    try {
                        JSONArray ja = new JSONArray(list.get(i).getTitle());
                        for (int j = 0; j < ja.length(); j++) {
                            if (ja.getJSONObject(j).getString(JSONTag.ID).equals(list.get(pos).getId())) {
                                ja.getJSONObject(j).put(JSONTag.FAVORITE, active);
                                list.get(i).setTitle(ja.toString());
                                Log.i("CHECK LIKE HANDLER", ja.toString());
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("CHECK LIKE HANDLER", e.toString());
                    }
                }
            }
        } else {
            try {
                String id = new JSONObject(args).getString(JSONTag.ID);
                JSONArray ja = new JSONArray(list.get(pos).getTitle());
                for (int i = 0; i < ja.length(); i++) {
                    if (ja.getJSONObject(i).getString(JSONTag.ID).equals(id)) {
                        ja.getJSONObject(i).put(JSONTag.FAVORITE, active);
                        list.get(pos).setTitle(ja.toString());
                        break;
                    }
                }
                for(int i =0;i<list.size();i++){
                    if(list.get(i).getType()!=type){
                        if(list.get(i).getId().equals(id)){
                            list.get(i).setLike(active);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i("POS LIKE", pos + "");
        Log.i("ARGS LIKE", args);
        notifyDataSetChanged();
    }

    private AlertDialog.Builder alertDialog() {
        String title = context.getResources().getString(R.string.dialog_title);
        String text = context.getResources().getString(R.string.dialog_text);
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }
}












