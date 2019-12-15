package it.acrmnet.androidhttpslibrary;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Riccardo on 10/08/2017.
 */

class AsyncJsonRequest extends AsyncTask<String,String,String> {
    private static final String TAG = "<Trust>:Android ";
//    private String unityObject="HttpsTrust";
    private String unityObject="_Manager";
    private String unityFunctionSuccess="TrustJsonData";
    private String unityFunctionError="TrustJsonError";

    public static final String responseErrorMagicPrefix             = "#@!!!@#";
    public static final String responseExceptionMagicPrefix         = "#@[!]@#";
    public static final String responseSocketTimeoutException       = responseExceptionMagicPrefix+"SocketTimeoutException";
    public static final String responseProtocolException            = responseExceptionMagicPrefix+"ProtocolException";
    public static final String responseIOException                  = responseExceptionMagicPrefix+"IOException";
    public static final String responseMalformedURLException        = responseExceptionMagicPrefix+"MalformedURLException";

    @Override
    protected String doInBackground(String... params) {
        int iAppTest=0;
        int iEndpoint=1;
        int iServer=2;
        int iParameter=3;
        int iUser=4;
        int iPassword=5;
        int iType=6;
        String data="";
        boolean bAppTest = params[iAppTest].toLowerCase().equals("true");
        Log.i(TAG, "Receive request from unity " + (bAppTest?"test":"") + " " +
                params[iEndpoint] + ": " + params[iServer] + ": " + params[iParameter]
                + ": " + params[iUser] + ": " + params[iPassword] + ": " + params[iType]);
        data = makeConnection("POST", params[iServer], params[iParameter], params[iUser], params[iPassword], params[iType]);
        String result=params[iEndpoint]+"\n"+data;
        if (!bAppTest) {
            if (data.startsWith(responseErrorMagicPrefix) ||
                    data.startsWith(responseExceptionMagicPrefix)) {
                Log.i(TAG, "Send to unity " + unityObject + " " + unityFunctionError + ": " + result);
                UnityPlayer.UnitySendMessage(unityObject, unityFunctionError, result);
            } else {
                Log.i(TAG, "Send to unity " + unityObject + " " + unityFunctionSuccess + ": " + result);
                UnityPlayer.UnitySendMessage(unityObject, unityFunctionSuccess, result);
            }
        }
        return data;
    }
    public String makeConnection(String requestMethod, String urlServer, String urlParameters,
                                 String urlUser, String urlPassword, String requestType) {
        String data="", urlConn="";
        int i,retry=0;
        HttpURLConnection urlConnection;
        URL url;
        int maxConnections = 2;
        Log.i(TAG, "AsyncJsonRequest:");
        //System.out.println("maxConnections : "+maxConnections);
        for (retry = 0; retry < maxConnections; retry++) {
            try {
                urlConn = urlServer;
                if (requestMethod.equals("GET")) {
                    if (urlConn.endsWith("/")) urlConn = urlConn.substring(0, urlConn.length() - 1);
                    if (urlParameters.length() > 0) urlConn += "?" + urlParameters;
                }
                url = new URL(urlConn);
                Log.i(TAG, "Request " + requestMethod + " connection to: " + url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                if (urlParameters.length() > 0 && requestMethod.equals("POST")) {
                    urlConnection.setDoOutput(true);
                } else {
                    urlConnection.setDoOutput(false);
                }
                if (urlUser.length() > 0 && urlPassword.length() > 0) {
                    String auth = urlUser + ":" + urlPassword;
                    String basicAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
                    urlConnection.setRequestProperty("Authorization", basicAuth);
                }

                urlConnection.setRequestMethod(requestMethod);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setInstanceFollowRedirects(false);
                //urlConnection.setRequestProperty("Accept-Encoding", "");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("charset", "utf-8");
                //urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                if (Build.VERSION.SDK_INT > 13)
                    urlConnection.setRequestProperty("Connection", "close");

                if (urlParameters.length() > 0 && requestMethod.equals("POST")) {
                    //urlConnection.setDoOutput(true);
                    byte[] postDataBytes = urlParameters.getBytes("UTF-8");
                    //urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                    urlConnection.setRequestProperty("Content-Length", "" + postDataBytes.length);
                    OutputStream wr = new BufferedOutputStream(urlConnection.getOutputStream());
                    wr.write(postDataBytes, 0, postDataBytes.length);
                    wr.flush();
                    wr.close();
                } else {
                    urlConnection.setRequestProperty("Content-Length", "0");
                }
                urlConnection.connect();

                Log.i(TAG, " - Connection to: " + urlConn);
                Log.i(TAG, " - RequestMethod: " + urlConnection.getRequestMethod());
                Log.i(TAG, " - ContentEncoding: " + urlConnection.getContentEncoding());
                Log.i(TAG, " - InstanceFollowRedirects: " + urlConnection.getInstanceFollowRedirects());
                Log.i(TAG, " - Permission: " + urlConnection.getPermission());
                if (urlConnection.getHeaderFields() != null) {
                    for (Map.Entry<String, List<String>> entry : urlConnection.getHeaderFields().entrySet()) {
                        Log.i(TAG, " - Key: " + entry.getKey() + " Value: " + entry.getValue());
                    }
                }
                Log.i(TAG, " - ResponseCode: " + urlConnection.getResponseCode());
                Log.i(TAG, " - ResponseMessage: " + urlConnection.getResponseMessage());

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {   //200
                    //Get Response
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    data = response.toString();
                    retry = maxConnections;
                } else {
                    InputStream is = urlConnection.getErrorStream();
                    if (is != null) {
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        data = responseErrorMagicPrefix + response.toString();
                        Log.i(TAG, "Connection error: " + response.toString());
                    } else {
                        Log.i(TAG, "Connection response code: " + urlConnection.getResponseCode());
                        Log.i(TAG, "Connection response message: " + urlConnection.getResponseMessage());
                        data = responseErrorMagicPrefix + "Connect error " + urlConnection.getResponseCode() + "\n" + urlConnection.getResponseMessage();
                    }
                }
                Log.i(TAG, " - Data: " + data);

            } catch (ProtocolException e) {
                e.printStackTrace();
                data = responseProtocolException + stackTraceToString(e);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                data = responseMalformedURLException + stackTraceToString(e);
            } catch (IOException e) {
                e.printStackTrace();
                data = responseIOException + stackTraceToString(e);
            }
        }
        return data;
    }
    public static String stackTraceToString(Throwable e) {
        int i;
        e.printStackTrace();
        String msg = "";
        StackTraceElement[] ste = e.getStackTrace();

        if (e.getMessage() != null && msg.equals(""))
            msg = e.getMessage();
        if (e.getCause() != null && msg.equals(""))
            msg = e.getCause().toString();
        if (msg.equals(""))
            for (i=0;i<ste.length;i++) {
                msg+=(ste[i].toString()+"\n");
            }
//            msg = "File:" + ste[0].getFileName() + "\nClass:"
//                    + ste[0].getClassName() + "\nMethod:"
//                    + ste[0].getMethodName() + "\nline:"
//                    + ste[0].getLineNumber() + " ";
        return msg;
    }

}