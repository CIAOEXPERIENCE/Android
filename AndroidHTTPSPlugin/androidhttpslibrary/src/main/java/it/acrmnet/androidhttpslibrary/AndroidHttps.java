package it.acrmnet.androidhttpslibrary;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.unity3d.player.UnityPlayerActivity;
//import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by Luz on 7/06/2015.
 */
public class AndroidHttps extends UnityPlayerActivity
{
    private static final String TAG = "AndroidHttpsLibrary";

    private static KeyStore sKeyStore = null;
    private static CertificateValidator validator;
    private static String subjectCN_CIAOexperience="CN=*.ciaoexperience.com";
    public static final String responseErrorMagicPrefix             = "#@!!!@#";
    public static final String responseExceptionMagicPrefix         = "#@[!]@#";
    public static final String responseSocketTimeoutException       = responseExceptionMagicPrefix+"SocketTimeoutException";
    public static final String responseProtocolException            = responseExceptionMagicPrefix+"ProtocolException";
    public static final String responseIOException                  = responseExceptionMagicPrefix+"IOException";
    public static final String responseMalformedURLException        = responseExceptionMagicPrefix+"MalformedURLException";
    private static boolean debugPrint = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public static void listCertifcates() {
        //validator = (CertificateValidator) this;
        findInstalledCertificate(validator);
    }

    private interface CertificateValidator {
        boolean valid(X509Certificate cert);
    }
    private static boolean findInstalledCertificate(final CertificateValidator validator) {
        try {
            final KeyStore ks = KeyStore.getInstance("AndroidCAStore");
            if (ks != null) {
                ks.load(null, null);
                final Enumeration<String> aliases = ks.aliases();
                while (aliases.hasMoreElements()) {
                    final String alias = aliases.nextElement();
                    final X509Certificate cert = (java.security.cert.X509Certificate) ks.getCertificate(alias);
                    boolean v = validator.valid(cert);
                    if (debugPrint)
                        Log.d(TAG, "Signature: " + v + " " + cert.getSignature().toString());
                    if (v) return true;
                }
            }
        } catch (IOException
                | KeyStoreException
                | CertificateException
                | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Turns off security and accepts all certificates.
     * Only for debugging!
     */
    public static void ignoreCertifcates()
    {
        //Create a trust manager that trusts everyone
        X509TrustManager trustEveryone = new TrustEveryoneTrustManager();
        try
        {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{trustEveryone}, new java.security.SecureRandom());
            //this is important: unity will use the default ssl socket factory we just created
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

        }catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }catch(KeyManagementException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Turns off the default security checks and only trusts the single given certificate.
     * @param crtFileContent
     */
    public static void trustOnly(byte[] crtFileContent)
    {
        try
        {
            Certificate ca = readCertificate(crtFileContent);
            if (debugPrint)
                Log.d(TAG, "Create a KeyStore containing our trusted CAs");
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            if (debugPrint)
                Log.d(TAG, "keyStoreType: " + keyStoreType);
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            try
            {
                if (debugPrint)
                    Log.d(TAG, "Create an SSLContext that uses our TrustManager");
                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);

                //this is important: unity will use the default ssl socket factory we just created
                if (debugPrint) {
                    SSLSocketFactory sslSF = HttpsURLConnection.getDefaultSSLSocketFactory();
                    Log.d(TAG, "Old DefaultHostnameVerifier: " + sslSF.toString());
                }
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
                if (debugPrint) {
                    SSLSocketFactory sslSF = HttpsURLConnection.getDefaultSSLSocketFactory();
                    Log.d(TAG, "New DefaultHostnameVerifier: " + sslSF.toString());
                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    Log.d(TAG, "DefaultHostnameVerifier: " + hv.toString());
                    Log.d(TAG, "Default SSL Socket set.");
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private static Certificate readCertificate(byte[] cert)
            throws CertificateException {
        Certificate ca = null;
        try {
            InputStream input = new BufferedInputStream(new ByteArrayInputStream(cert));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(input);
            if (debugPrint)
                if (debugPrint)
                    Log.d(TAG, "certificate lenght " + ca.toString().length());
            String[] caRow = ca.toString().split("\n");
            String[] subjectDN = ((X509Certificate) ca).getSubjectDN().getName().split(",");
            boolean fPrint = false;
            for (int i = 0; i < subjectDN.length; i++) {
                if (subjectDN[i].equals(subjectCN_CIAOexperience)) {
                    fPrint = true;
                    break;
                }
            }
            if (debugPrint && fPrint) {
                //Log.d(TAG, "Certificate:");
                for (int i = 0; i < caRow.length; i++) {
                    Log.d(TAG, caRow[i]);
                }
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ca;
    }

    /**
     * Allows the given certificate + all default android certificates.
     *
     * @param crtFileContent
     */
    public static void addCertificate(byte[] crtFileContent)
    {
        addCertificates(new byte[][]{crtFileContent});
    }

    public static void addCertificates(byte[][] certificates)
    {
        try
        {
            if(sKeyStore == null)
            {
                if (debugPrint)
                    Log.d(TAG, "Create a KeyStore containing our trusted CAs");
                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                sKeyStore = keyStore;
            }
            for(int i = 0; i < certificates.length; i++)
            {
                Certificate ca = readCertificate(certificates[i]);
                sKeyStore.setCertificateEntry("ca" + sKeyStore.size(), ca);
            }

            if (debugPrint)
                Log.d(TAG, "Create a TrustManager that trusts the CAs in our KeyStore");
            // Create a TrustManager that trusts the CAs in our KeyStore

            TrustManagerFactory defaultTMF = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            defaultTMF.init((KeyStore)null);

            TrustManagerFactory additionalTMF = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            additionalTMF.init(sKeyStore);

            TrustManager[] defaultTMs = defaultTMF.getTrustManagers();
            TrustManager[] additionalTMs = additionalTMF.getTrustManagers();
            if (debugPrint)
                Log.d(TAG, "CombinedTrustManager");
            CombinedTrustManager combinedTrustManager = new CombinedTrustManager();
            for(TrustManager tm : defaultTMs)
            {
                if(tm instanceof X509TrustManager)
                    combinedTrustManager.addTrustManager((X509TrustManager)tm);
            }
            for(TrustManager tm : additionalTMs)
            {
                if(tm instanceof X509TrustManager)
                    combinedTrustManager.addTrustManager((X509TrustManager)tm);
            }
            if (debugPrint)
                Log.d(TAG, "AcceptedIssuers number " + combinedTrustManager.getAcceptedIssuers().length);
//            for (int c=0; c<combinedTrustManager.getAcceptedIssuers().length; c++) {
//                if (debugPrint)
//            Log.d(TAG, "SigAlgName: " + combinedTrustManager.getAcceptedIssuers()[c].getSigAlgName());
//                if (debugPrint)
//            Log.d(TAG, "hashCode: " + combinedTrustManager.getAcceptedIssuers()[c].hashCode());
//            }
            // create a new SSLContext that uses the combined TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{combinedTrustManager}, null);

            //this is important: unity will use the default ssl socket factory we just created
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            if (debugPrint)
                Log.d(TAG, "Default SSL Socket set.");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            if (debugPrint)
                Log.d(TAG, "NoSuchAlgorithmException");
            throw new RuntimeException(e);
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
            if (debugPrint)
                Log.d(TAG, "KeyManagementException");
            throw new RuntimeException(e);
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            if (debugPrint)
                Log.d(TAG, "KeyStoreException");
            throw new RuntimeException(e);
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
            if (debugPrint)
                Log.d(TAG, "CertificateException");
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            if (debugPrint)
                Log.d(TAG, "IOException");
            throw new RuntimeException(e);
        }
    }
    public static void jsonRequest(String endpointIndex, String server, String data, String user, String password, String type) {
        AsyncJsonRequest async = new AsyncJsonRequest(); //Outer class
//        GetSearchResults task = outerClass.new GetSearchResults(requestSearch);
        async.execute("false", endpointIndex, server, data, user, password, type);
    }
    public static void urlDownloadRequest(String itemIndex, String url, String filePath) {
        AsyncUrlDownloadRequest async = new AsyncUrlDownloadRequest(); //Outer class
        async.execute("false", itemIndex, url, filePath);
    }
//    public void _jsonRequest(String endpointIndex, String server, String data, String user, String password, String type) {
//        AsyncJsonRequest asyncJsonRequest = new AsyncJsonRequest();
//        asyncJsonRequest.execute(endpointIndex, server, data, user, password, type);
////        new AsyncTask<Void,Void,Void>() {
////            @Override
////            protected Void doInBackground(Void... params) {
////                return null;
////            }
////
////            @Override
////            protected void onPostExecute(Void aVoid) {
////                super.onPostExecute(aVoid);
////                AsyncJsonRequest asyncJsonRequest = new AsyncJsonRequest();
////                asyncJsonRequest.execute(server, data, user, password, type);
////            }
////        }.execute();
//    }

    public void jsonRequestTest(String server, String data, String user, String password, String type)
    {
        AsyncJsonRequest async = new AsyncJsonRequest();
        async.execute("true", "", server, data, user, password, type);
    }
    public void urlDownloadRequestTest(String itemIndex, String url, String filePath)
    {
//        String filePath=null;
//        filePath=getApplicationContext().getFilesDir()+"/";
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            filePath=getExternalFilesDir(null)+"/";
//        } else {
//            filePath=getFilesDir()+"/";
//        }
        AsyncUrlDownloadRequest async = new AsyncUrlDownloadRequest(); //Outer class
        async.execute("true", itemIndex, url, filePath);
    }

//    class AsyncJsonRequest extends AsyncTask<String,String,String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String data="";
////            try {
//                //URL url = new URL(params[0]); //Server URL
//                data = makeConnection("POST", params[0], params[1], params[2], params[3], params[4]);
//            if (data.startsWith(responseErrorMagicPrefix) ||
//                data.startsWith(responseExceptionMagicPrefix)) {
//                UnityPlayer.UnitySendMessage(unityObject, unityFunctionError, data);
//            } else {
//                UnityPlayer.UnitySendMessage(unityObject, unityFunctionSuccess, data);
//            }
//
//
////                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
////                httpURLConnection.setDoInput(true);
////                httpURLConnection.setDoOutput(true);
////                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
////                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
////                httpURLConnection.connect();
////
//////                JSONObject jsonObject = new JSONObject();
//////                jsonObject.put("para_1", "arg_1");
////
////                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//////                wr.writeBytes(jsonObject.toString());
////                wr.writeBytes(params[1]);
////
////                wr.flush();
////                wr.close();
////
////                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
////                StringBuilder sb = new StringBuilder();
////
////                String line;
////                while ((line = br.readLine()) != null) {
////                    sb.append(line + "\n");
////                }
////                br.close();
//
//
//
//
////            } catch (MalformedURLException e) {
////                e.printStackTrace();
////            } catch (IOException e) {
////                e.printStackTrace();
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//
//            return data;
//        }
//
//
//    }
//
//    public String makeConnection(String requestMethod, String urlServer, String urlParameters,
//                                 String urlUser, String urlPassword, String requestType) {
//        String data="", urlConn="";
//        int i,retry=0;
//        HttpURLConnection urlConnection;
//        URL url;
//        int maxConnections = 2;
//        System.out.println("maxConnections : "+maxConnections);
//        for (retry = 0; retry < maxConnections; retry++) {
//            try {
//                urlConn=urlServer;
//                if (requestMethod.equals("GET")) {
//                    if (urlConn.endsWith("/")) urlConn=urlConn.substring(0,urlConn.length()-1);
//                    if (urlParameters.length()>0) urlConn += "?" + urlParameters;
//                }
//                url = new URL(urlConn);
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
//                urlConnection.setRequestProperty("Content-Type", "application/json");
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
//                    data = response.toString();
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
//    public static String stackTraceToString(Throwable e) {
//        int i;
//        e.printStackTrace();
//        String msg = "";
//        StackTraceElement[] ste = e.getStackTrace();
//
//        if (e.getMessage() != null && msg.equals(""))
//            msg = e.getMessage();
//        if (e.getCause() != null && msg.equals(""))
//            msg = e.getCause().toString();
//        if (msg.equals(""))
//            for (i=0;i<ste.length;i++) {
//                msg+=(ste[i].toString()+"\n");
//            }
////            msg = "File:" + ste[0].getFileName() + "\nClass:"
////                    + ste[0].getClassName() + "\nMethod:"
////                    + ste[0].getMethodName() + "\nline:"
////                    + ste[0].getLineNumber() + " ";
//        return msg;
//    }
}

