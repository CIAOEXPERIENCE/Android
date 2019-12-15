package it.acrmnet.androidhttpslibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Riccardo on 10/08/2017.
 */

class AsyncUrlDownloadRequest extends AsyncTask<String,String,String> {
    private static final String TAG = "<Trust>:Android ";
//    private String unityObject="HttpsTrust";
    private String unityObject="_Manager";
    private String unityFunctionSuccess="TrustUrlDownloadData";
    private String unityFunctionError="TrustUrlDownloadError";

    public static final String responseErrorMagicPrefix             = "#@!!!@#";
    public static final String responseExceptionMagicPrefix         = "#@[!]@#";
    public static final String responseSocketTimeoutException       = responseExceptionMagicPrefix+"SocketTimeoutException";
    public static final String responseProtocolException            = responseExceptionMagicPrefix+"ProtocolException";
    public static final String responseIOException                  = responseExceptionMagicPrefix+"IOException";
    public static final String responseMalformedURLException        = responseExceptionMagicPrefix+"MalformedURLException";

    @Override
    protected String doInBackground(String... params) {
        int iAppTest=0;
        int iItem=1;
        int iServer=2;
        int iFilePath=3;
        String result="";
        String data="", encoded="";
        boolean bAppTest = params[iAppTest].toLowerCase().equals("true");
        Log.i(TAG, "Receive request from unity " + (bAppTest?"test":"") + " " + params[iItem] + ": " + params[iServer] + ": " + params[iFilePath]);
        data = makeConnection("GET", params[iServer], "", "", "", "", params[iFilePath]);
        result=params[iItem]+"\n"+data;


//        try {
//            URLConnection connection = null;
//            URL url = new URL(params[iServer]);
//            connection = url.openConnection();
//            connection.connect();
//            // getting file length
////            int lenghtOfFile = connection.getContentLength();
//            // input stream to read file - with 8k buffer
//            InputStream is = new BufferedInputStream(url.openStream(), 8192);
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//            if (bitmap!=null) {
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream.toByteArray();
//                writeFile(byteArray, params[iFilePath]);
//                result = params[iItem] + "\n" + params[iFilePath]; //encoded;
//            }
//
////            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
////            String line;
////            StringBuilder response = new StringBuilder();
////            while ((line = rd.readLine()) != null) {
////                response.append(line);
////                response.append('\r');
////            }
////            rd.close();
////            String imageString = String.valueOf(response);
////            Log.i(TAG, "InputStream: (len: " + imageString.length() + ") " + imageString);
////            encoded = Base64.encodeToString(imageString.getBytes(), Base64.DEFAULT);
////            Log.i(TAG, "Base64 Encoded: (len: " + encoded.length() + ") " + encoded);
////            Bitmap bitmapByte = BitmapFactory.decodeByteArray(imageString.getBytes(), 0, imageString.getBytes().length);
//
//
//////            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//////            StringBuilder stringInput = new StringBuilder();
//////            String line;
//////            while ((line = r.readLine()) != null) {
//////                stringInput.append(line).append('\n');
//////            }
//////            Bitmap bitmap = BitmapFactory.decodeStream(input);
////            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
////            byte[] byteArray = byteArrayOutputStream .toByteArray();
////            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


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

    public void writeFile(byte[] data, String fileName) throws IOException{
        Log.i(TAG, "Write file " + fileName + " lenght: " + data.length);
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }

    public String normalizeUrl(String url) {
        String nurl = url;
        int p = nurl.lastIndexOf('?');
        if (p>0)
        {
            nurl = url.substring(0, p);
        }
        return nurl;

    }

    public String makeConnection(String requestMethod, String urlServer, String urlParameters,
                                 String urlUser, String urlPassword, String requestType,
                                 String filePath) {
        String data="", urlConn="";
        int i,retry=0;
        HttpURLConnection urlConnection;
        URL url;
        int maxConnections = 2;
        Log.i(TAG, "AsyncUrlDownloadRequest:");
        for (retry = 0; retry < maxConnections; retry++) {
            try {
                //URLConnection connection = null;
                url = new URL(urlServer);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {   //200

                    InputStream is = new BufferedInputStream(url.openStream(), 8192);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    if (bitmap != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        String normalizedFilePath = normalizeUrl(filePath);
                        if (filePath.toLowerCase().endsWith("png"))
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        else
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        writeFile(byteArray, filePath);
                        data = filePath;
                        Log.i(TAG, "Store File: " + filePath);
                    }
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



//                urlConn=urlServer;
//                if (requestMethod.equals("GET")) {
//                    if (urlConn.endsWith("/")) urlConn=urlConn.substring(0,urlConn.length()-1);
//                    if (urlParameters.length()>0) urlConn += "?" + urlParameters;
//                }
//                url = new URL(urlConn);
//                Log.i(TAG, "Request " + requestMethod + " connection to: " + url);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoInput(true);
//                if (urlParameters.length() > 0 && requestMethod.equals("POST")) {
//                    urlConnection.setDoOutput(true);
//                } else {
//                    urlConnection.setDoOutput(false);
//                }
//                if (urlUser.length() > 0 && urlPassword.length() > 0) {
//                    String auth = urlUser + ":" + urlPassword;
//                    String basicAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
//                    urlConnection.setRequestProperty("Authorization", basicAuth);
//                }
//
//                urlConnection.setRequestMethod(requestMethod);
//                urlConnection.setChunkedStreamingMode(0);
//                urlConnection.setInstanceFollowRedirects(false);
//                //urlConnection.setRequestProperty("Accept-Encoding", "");
//                int p = urlServer.lastIndexOf(".");
//                String contentType = urlServer.substring(p+1);
////                String contentType = "application/jpg";
////                if (urlServer.toLowerCase().endsWith("png"))
////                    contentType = "application/png";
//                urlConnection.setRequestProperty("Content-Type", contentType);
//                urlConnection.setRequestProperty("charset", "utf-8");
//                //urlConnection.setUseCaches(false);
//                urlConnection.setConnectTimeout(5000);
//                urlConnection.setReadTimeout(5000);
//                if (Build.VERSION.SDK_INT > 13)
//                    urlConnection.setRequestProperty("Connection", "close");
//
//                if (urlParameters.length() > 0 && requestMethod.equals("POST")) {
//                    //urlConnection.setDoOutput(true);
//                    byte[] postDataBytes = urlParameters.getBytes("UTF-8");
//                    //urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
//                    urlConnection.setRequestProperty("Content-Length", "" + postDataBytes.length);
//                    OutputStream wr = new BufferedOutputStream(urlConnection.getOutputStream());
//                    wr.write(postDataBytes, 0, postDataBytes.length);
//                    wr.flush();
//                    wr.close();
//                } else {
//                    urlConnection.setRequestProperty("Content-Length", "0");
//                }
//                urlConnection.connect();
//
//                Log.i(TAG, " - Connection to: " + urlConn);
//                Log.i(TAG, " - RequestMethod: " + urlConnection.getRequestMethod());
//                Log.i(TAG, " - ContentEncoding: " + urlConnection.getContentEncoding());
//                Log.i(TAG, " - InstanceFollowRedirects: " + urlConnection.getInstanceFollowRedirects());
//                Log.i(TAG, " - Permission: " + urlConnection.getPermission());
//                if (urlConnection.getHeaderFields() != null) {
//                    for (Map.Entry<String, List<String>> entry : urlConnection.getHeaderFields().entrySet()) {
//                        Log.i(TAG, " - Key: " + entry.getKey() + " Value: " + entry.getValue());
//                    }
//                }
//                Log.i(TAG, " - ResponseCode: " + urlConnection.getResponseCode());
//                Log.i(TAG, " - ResponseMessage: " + urlConnection.getResponseMessage());
//
//                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {   //200
//                    //Get Response
//                    InputStream is = urlConnection.getInputStream();
//                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//                    String line;
//                    StringBuilder response = new StringBuilder();
//                    while ((line = rd.readLine()) != null) {
//                        response.append(line);
//                        response.append('\r');
//                    }
//                    rd.close();
//                    String encodedImage = Base64.encodeToString(response.toString().getBytes(), Base64.DEFAULT);
//                    data = encodedImage;
////                    data = response.toString();
//                    retry=maxConnections;
//                } else {
//                    InputStream is = urlConnection.getErrorStream();
//                    if (is != null) {
//                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//                        String line;
//                        StringBuilder response = new StringBuilder();
//                        while ((line = rd.readLine()) != null) {
//                            response.append(line);
//                            response.append('\r');
//                        }
//                        rd.close();
//                        data = responseErrorMagicPrefix + response.toString();
//                        Log.i(TAG, "Connection error: " + response.toString());
//                    } else {
//                        Log.i(TAG, "Connection response code: " + urlConnection.getResponseCode());
//                        Log.i(TAG, "Connection response message: " + urlConnection.getResponseMessage());
//                        data = responseErrorMagicPrefix + "Connect error " + urlConnection.getResponseCode() + "\n" + urlConnection.getResponseMessage();
//                    }
//                }
                Log.i(TAG, " - Data: " + data);

            }catch(ProtocolException e){
                e.printStackTrace();
                data = responseProtocolException + stackTraceToString(e);
            }catch(MalformedURLException e){
                e.printStackTrace();
                data = responseMalformedURLException + stackTraceToString(e);
            }catch(IOException e){
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
//    public String makeConnection(String requestMethod, String urlServer, String urlParameters,
//                                 String urlUser, String urlPassword, String requestType) {
//        String data="", urlConn="";
//        int i,retry=0;
//        HttpURLConnection urlConnection;
//        URL url;
//        int maxConnections = 2;
//        Log.i(TAG, "AsyncUrlDownloadRequest:");
//        for (retry = 0; retry < maxConnections; retry++) {
//            try {
//                urlConn=urlServer;
//                if (requestMethod.equals("GET")) {
//                    if (urlConn.endsWith("/")) urlConn=urlConn.substring(0,urlConn.length()-1);
//                    if (urlParameters.length()>0) urlConn += "?" + urlParameters;
//                }
//                url = new URL(urlConn);
//                Log.i(TAG, "Request " + requestMethod + " connection to: " + url);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoInput(true);
//                if (urlParameters.length() > 0 && requestMethod.equals("POST")) {
//                    urlConnection.setDoOutput(true);
//                } else {
//                    urlConnection.setDoOutput(false);
//                }
//                if (urlUser.length() > 0 && urlPassword.length() > 0) {
//                    String auth = urlUser + ":" + urlPassword;
//                    String basicAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
//                    urlConnection.setRequestProperty("Authorization", basicAuth);
//                }
//
//                urlConnection.setRequestMethod(requestMethod);
//                urlConnection.setChunkedStreamingMode(0);
//                urlConnection.setInstanceFollowRedirects(false);
//                //urlConnection.setRequestProperty("Accept-Encoding", "");
//                int p = urlServer.lastIndexOf(".");
//                String contentType = urlServer.substring(p+1);
////                String contentType = "application/jpg";
////                if (urlServer.toLowerCase().endsWith("png"))
////                    contentType = "application/png";
//                urlConnection.setRequestProperty("Content-Type", contentType);
//                urlConnection.setRequestProperty("charset", "utf-8");
//                //urlConnection.setUseCaches(false);
//                urlConnection.setConnectTimeout(5000);
//                urlConnection.setReadTimeout(5000);
//                if (Build.VERSION.SDK_INT > 13)
//                    urlConnection.setRequestProperty("Connection", "close");
//
//                if (urlParameters.length() > 0 && requestMethod.equals("POST")) {
//                    //urlConnection.setDoOutput(true);
//                    byte[] postDataBytes = urlParameters.getBytes("UTF-8");
//                    //urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
//                    urlConnection.setRequestProperty("Content-Length", "" + postDataBytes.length);
//                    OutputStream wr = new BufferedOutputStream(urlConnection.getOutputStream());
//                    wr.write(postDataBytes, 0, postDataBytes.length);
//                    wr.flush();
//                    wr.close();
//                } else {
//                    urlConnection.setRequestProperty("Content-Length", "0");
//                }
//                urlConnection.connect();
//
//                Log.i(TAG, " - Connection to: " + urlConn);
//                Log.i(TAG, " - RequestMethod: " + urlConnection.getRequestMethod());
//                Log.i(TAG, " - ContentEncoding: " + urlConnection.getContentEncoding());
//                Log.i(TAG, " - InstanceFollowRedirects: " + urlConnection.getInstanceFollowRedirects());
//                Log.i(TAG, " - Permission: " + urlConnection.getPermission());
//                if (urlConnection.getHeaderFields() != null) {
//                    for (Map.Entry<String, List<String>> entry : urlConnection.getHeaderFields().entrySet()) {
//                        Log.i(TAG, " - Key: " + entry.getKey() + " Value: " + entry.getValue());
//                    }
//                }
//                Log.i(TAG, " - ResponseCode: " + urlConnection.getResponseCode());
//                Log.i(TAG, " - ResponseMessage: " + urlConnection.getResponseMessage());
//
//                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {   //200
//                    //Get Response
//                    InputStream is = urlConnection.getInputStream();
//                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//                    String line;
//                    StringBuilder response = new StringBuilder();
//                    while ((line = rd.readLine()) != null) {
//                        response.append(line);
//                        response.append('\r');
//                    }
//                    rd.close();
//                    String encodedImage = Base64.encodeToString(response.toString().getBytes(), Base64.DEFAULT);
//                    data = encodedImage;
////                    data = response.toString();
//                    retry=maxConnections;
//                } else {
//                    InputStream is = urlConnection.getErrorStream();
//                    if (is != null) {
//                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//                        String line;
//                        StringBuilder response = new StringBuilder();
//                        while ((line = rd.readLine()) != null) {
//                            response.append(line);
//                            response.append('\r');
//                        }
//                        rd.close();
//                        data = responseErrorMagicPrefix + response.toString();
//                        Log.i(TAG, "Connection error: " + response.toString());
//                    } else {
//                        Log.i(TAG, "Connection response code: " + urlConnection.getResponseCode());
//                        Log.i(TAG, "Connection response message: " + urlConnection.getResponseMessage());
//                        data = responseErrorMagicPrefix + "Connect error " + urlConnection.getResponseCode() + "\n" + urlConnection.getResponseMessage();
//                    }
//                }
//                Log.i(TAG, " - Data: " + data);
//
//            }catch(ProtocolException e){
//                e.printStackTrace();
//                data = responseProtocolException + stackTraceToString(e);
//            }catch(MalformedURLException e){
//                e.printStackTrace();
//                data = responseMalformedURLException + stackTraceToString(e);
//            }catch(IOException e){
//                e.printStackTrace();
//                data = responseIOException + stackTraceToString(e);
//            }
//        }
//        return data;
//    }

}