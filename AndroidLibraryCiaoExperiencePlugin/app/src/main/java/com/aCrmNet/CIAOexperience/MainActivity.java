package com.aCrmNet.CIAOexperience;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends UnityPlayerActivity implements FragmentList.OnFragmentInteractionListener,
        AdapterListView.OnSimpleItemClickListener {
    private static final int TAKE_PICTURE = 1, TAKE_GALLERY = 2;
    //getHappenings
    String test1 = "{\"response\":\"ok\",\"notification\":-1,\"result\":[{\"id\":\"117\",\"name\":\"Jazz live\",\"location\":\"Etabl\\u00ec\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/happenings\\/000415.jpg\",\"promo_key\":0,\"order\":1,\"date\":\"09 Mar 2017\"},{\"id\":\"116\",\"name\":\"Donna e salute\",\"location\":\"Prince fitness & Spa\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/happenings\\/000473.jpg\",\"promo_key\":0,\"order\":2,\"date\":\"10 Mar 2017\"}]}";
    //getHome
    String test2 = "{\"response\":\"ok\",\"notification\":-1,\"result\":{\"business\":[{\"id\":12,\"name\":\"PanDivino\",\"idCategory\":1,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000159.jpg\",\"promo_key\":0,\"order\":1,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8965569\",\"lng\":\"12.4735966\"},{\"id\":13,\"name\":\"Taverna & Vineria\",\"idCategory\":1,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000163.jpg\",\"promo_key\":0,\"order\":2,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8974446\",\"lng\":\"12.4712249\"},{\"id\":14,\"name\":\"Bar del Fico\",\"idCategory\":3,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000167.jpg\",\"promo_key\":0,\"order\":3,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8994619\",\"lng\":\"12.4706126\"},{\"id\":16,\"name\":\"Pickle 1\",\"idCategory\":4,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000578.jpg\",\"promo_key\":1,\"order\":4,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8991800\",\"lng\":\"12.4715400\"},{\"id\":18,\"name\":\"Etabl\\u00ec\",\"idCategory\":1,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000251.jpg\",\"promo_key\":0,\"order\":5,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8996243\",\"lng\":\"12.4705097\"},{\"id\":19,\"name\":\"Chiostro del Bramante\",\"idCategory\":6,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000263.jpg\",\"promo_key\":1,\"order\":6,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.9000255\",\"lng\":\"12.4716926\"},{\"id\":20,\"name\":\"Prince fitness & Spa\",\"idCategory\":7,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000267.jpg\",\"promo_key\":0,\"order\":7,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.9177630\",\"lng\":\"12.4915149\"}],\"categories\":[{\"idCategory\":\"1\",\"count\":3,\"business\":[{\"id\":12,\"name\":\"PanDivino\",\"idCategory\":1,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000159.jpg\",\"promo_key\":0,\"order\":1,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8965569\",\"lng\":\"12.4735966\"},{\"id\":13,\"name\":\"Taverna & Vineria\",\"idCategory\":1,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000163.jpg\",\"promo_key\":0,\"order\":2,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8974446\",\"lng\":\"12.4712249\"},{\"id\":18,\"name\":\"Etabl\\u00ec\",\"idCategory\":1,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000251.jpg\",\"promo_key\":0,\"order\":3,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8996243\",\"lng\":\"12.4705097\"}]},{\"idCategory\":\"3\",\"count\":1,\"business\":[{\"id\":14,\"name\":\"Bar del Fico\",\"idCategory\":3,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000167.jpg\",\"promo_key\":0,\"order\":1,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8994619\",\"lng\":\"12.4706126\"}]},{\"idCategory\":\"4\",\"count\":1,\"business\":[{\"id\":16,\"name\":\"Pickle 1\",\"idCategory\":4,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000578.jpg\",\"promo_key\":1,\"order\":1,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.8991800\",\"lng\":\"12.4715400\"}]},{\"idCategory\":\"5\",\"count\":0,\"business\":[]},{\"idCategory\":\"6\",\"count\":1,\"business\":[{\"id\":19,\"name\":\"Chiostro del Bramante\",\"idCategory\":6,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000263.jpg\",\"promo_key\":1,\"order\":1,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.9000255\",\"lng\":\"12.4716926\"}]},{\"idCategory\":\"7\",\"count\":1,\"business\":[{\"id\":20,\"name\":\"Prince fitness & Spa\",\"idCategory\":7,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000267.jpg\",\"promo_key\":0,\"order\":1,\"show_anyway\":false,\"favorite\":0,\"lat\":\"41.9177630\",\"lng\":\"12.4915149\"}]},{\"idCategory\":\"8\",\"count\":0,\"business\":[]},{\"idCategory\":\"11\",\"count\":0,\"business\":[]}]}}";
    //getEvents
    String test3 = "{\"response\":\"ok\",\"notification\":-1,\"result\":[{\"id\":17,\"name\":\"Luca Carboni \",\"location\":\"Casa\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/events\\/000576.jpg\",\"promo_key\":0,\"order\":1,\"date\":\"07 Mar 2017; 09 Mar 2017\"},{\"id\":20,\"name\":\"Boris\",\"location\":\"Palazzo Panico\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/events\\/000581.jpg\",\"promo_key\":1,\"order\":2,\"date\":\"16 Mar 2017\"},{\"id\":22,\"name\":\"Pie\",\"location\":\"Palazzo Gucci\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/events\\/000584.jpg\",\"promo_key\":0,\"order\":3,\"date\":\"16 Mar 2017\"},{\"id\":18,\"name\":\"La Mostra\",\"location\":\"Piazza\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/events\\/000579.jpg\",\"promo_key\":0,\"order\":4,\"date\":\"17 Mar 2017\"},{\"id\":21,\"name\":\"Slash\",\"location\":\"Casa tua\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/events\\/000583.jpg\",\"promo_key\":0,\"order\":5,\"date\":\"22 Mar 2017\"}]}";
    //reccomendation
    String test4 = "{\"response\":\"ok\",\"notification\":-1,\"result\":[{\"id\":0,\"name\":\"CIAOexperience\",\"order\":1,\"business\":[{\"id\":18,\"name\":\"Etabl\\u00ec\",\"idCategory\":1,\"types\":\"\",\"promo_key\":0,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000251.jpg\",\"order\":1},{\"id\":12,\"name\":\"PanDivino\",\"idCategory\":1,\"types\":\"\",\"promo_key\":0,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000159.jpg\",\"order\":2},{\"id\":14,\"name\":\"Bar del Fico\",\"idCategory\":3,\"types\":\"\",\"promo_key\":0,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000167.jpg\",\"order\":3},{\"id\":16,\"name\":\"Pickle 1\",\"idCategory\":4,\"types\":\"\",\"promo_key\":0,\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/file-upload\\/business\\/000578.jpg\",\"order\":4}]}]}";
    public FragmentList myOverlayFragment;
    public FragmentTransaction ft;
    private final String getDashboard = "{\"response\":\"ok\",\"notification\":-1,\"result\":{\"cities\":[{\"id\":1,\"name\":\"Las Vegas\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/images\\/LasVegas.jpg\",\"lat\":\"36.1699412\",\"lng\":\"-115.1398296\",\"currency\":2,\"status\":0},{\"id\":2,\"name\":\"Miami\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/images\\/Miami.jpg\",\"lat\":\"25.7616798\",\"lng\":\"-80.1917902\",\"currency\":2,\"status\":0},{\"id\":3,\"name\":\"Panama\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/images\\/Panama.jpg\",\"lat\":\"8.9833330\",\"lng\":\"-79.5166670\",\"currency\":2,\"status\":0},{\"id\":4,\"name\":\"Roma\",\"image\":\"http:\\/\\/www.interlog.it\\/ciao\\/images\\/Roma.jpg\",\"lat\":\"41.9027835\",\"lng\":\"12.4963655\",\"currency\":1,\"status\":1}],\"menu\":[{\"id\":13,\"name\":\"All cities\",\"logged_user\":false,\"order\":1},{\"id\":1,\"name\":\"What to do\",\"logged_user\":false,\"order\":2},{\"id\":2,\"name\":\"Events & Happenings\",\"logged_user\":false,\"order\":3},{\"id\":3,\"name\":\"Maps\",\"logged_user\":false,\"order\":4},{\"id\":0,\"name\":\"\",\"logged_user\":false,\"order\":5},{\"id\":5,\"name\":\"Suggestions\",\"logged_user\":false,\"order\":6},{\"id\":6,\"name\":\"Favorites\",\"logged_user\":true,\"order\":7},{\"id\":7,\"name\":\"Day planning\",\"logged_user\":true,\"order\":8},{\"id\":0,\"name\":\"\",\"logged_user\":false,\"order\":9},{\"id\":9,\"name\":\"Get your promo\",\"logged_user\":true,\"order\":10},{\"id\":0,\"name\":\"\",\"logged_user\":false,\"order\":11},{\"id\":11,\"name\":\"XperiENses\",\"logged_user\":false,\"order\":12},{\"id\":12,\"name\":\"Accommodations\",\"logged_user\":false,\"order\":13}],\"categories\":[{\"id\":1,\"name\":\"Food & Drinks\",\"whattodo\":true,\"promo\":true,\"map\":true,\"active\":1,\"color\":\"#94154f\",\"order\":1},{\"id\":2,\"name\":\"Drinks\",\"whattodo\":false,\"promo\":false,\"map\":false,\"active\":0,\"color\":\"#000000\",\"order\":2},{\"id\":3,\"name\":\"Clubs\",\"whattodo\":true,\"promo\":true,\"map\":true,\"active\":1,\"color\":\"#106db3\",\"order\":3},{\"id\":4,\"name\":\"Shopping\",\"whattodo\":true,\"promo\":true,\"map\":true,\"active\":1,\"color\":\"#f29f35\",\"order\":4},{\"id\":5,\"name\":\"Attractions\",\"whattodo\":true,\"promo\":false,\"map\":true,\"active\":0,\"color\":\"#cc5f9e\",\"order\":5},{\"id\":6,\"name\":\"Art & Culture\",\"whattodo\":true,\"promo\":true,\"map\":true,\"active\":1,\"color\":\"#a1919b\",\"order\":6},{\"id\":7,\"name\":\"Wellness & Spa\",\"whattodo\":true,\"promo\":true,\"map\":true,\"active\":1,\"color\":\"#a0b842\",\"order\":7},{\"id\":8,\"name\":\"Extracity\",\"whattodo\":true,\"promo\":false,\"map\":false,\"active\":0,\"color\":\"#000000\",\"order\":8},{\"id\":9,\"name\":\"Hotel\",\"whattodo\":false,\"promo\":true,\"map\":false,\"active\":0,\"color\":\"#87468f\",\"order\":9},{\"id\":10,\"name\":\"Eventi\",\"whattodo\":false,\"promo\":true,\"map\":true,\"active\":0,\"color\":\"#000000\",\"order\":10},{\"id\":11,\"name\":\"Services\",\"whattodo\":true,\"promo\":true,\"map\":true,\"active\":0,\"color\":\"#84969c\",\"order\":11}],\"planning\":[{\"id\":1,\"name\":\"Morning\"},{\"id\":2,\"name\":\"Afternoon\"},{\"id\":3,\"name\":\"Evening\"},{\"id\":4,\"name\":\"Late night\"}]}}";

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Constant.setScreenWidth(getDisplay().x);
        myOverlayFragment = new FragmentList();
        ft = getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, myOverlayFragment).commit();
        listVisible(false);
        ///test call
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
        } catch (Exception e) {
            Log.i("CRASH", e.toString());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    ///unity functions
    public void listInit(final String dashboard, final int top, final int bottom, final boolean isLogged) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getOnListInit().listInit(top, bottom);
                Constant.DASHBOARD = dashboard;
                Constant.isLogged = isLogged;
            }
        }.execute();
    }

    public void setList(final String json, final int status) {
        Constant.CURRENT_STATUS = status;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getFragmentRefreshListener().onRefresh(json, Constant.DASHBOARD, status);
            }
        }.execute();
    }

    public void listVisible(boolean visible) {
        if (!visible) {
            hideFragment();
        } else {
            showFragment();
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void setFavorite(final String id, final String favorite) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getOnSetLikeListener().setLikeListener(id, Integer.parseInt(favorite));
            }
        }.execute();
    }


    ///click on native list item, override interface
    @Override
    public void simpleItemClick(View v, int pos, String args, int click) {
       /* if(click == Constant.IMAGE || click == Constant.ITEM){
           //showHideFragment(myOverlayFragment);
        }*/
        Log.i("JS LIST CLICK", args);
        UnityPlayer.UnitySendMessage("_Manager", "androidClickReceiver", args);
    }


    public interface OnSetLikeListener {
        void setLikeListener(String id, int like);
    }

    public interface FragmentRefreshListener {
        void onRefresh(String json, String jsonDash, int status);
    }


    public OnSetLikeListener getOnSetLikeListener() {
        return onSetLikeListener;
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setOnSetLikeListener(OnSetLikeListener onSetLikeListener) {
        this.onSetLikeListener = onSetLikeListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private OnSetLikeListener onSetLikeListener;
    private FragmentRefreshListener fragmentRefreshListener;

    ///return device display size
    private Point getDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
    //unused method..
    // convert view to bitmap

    ///**called from unity
    //


    public void hideFragment() {
        if (!myOverlayFragment.isHidden()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(myOverlayFragment);
            ft.commit();
        }
    }

    public void showFragment() {
        if (myOverlayFragment.isHidden()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.show(myOverlayFragment);
            ft.commit();
        }
    }

    private OnListInit onListInit;

    public interface OnListInit {
        void listInit(int top, int bottom);
    }

    public OnListInit getOnListInit() {
        return onListInit;
    }

    public void setOnListInit(OnListInit onListInit) {
        this.onListInit = onListInit;
    }
}