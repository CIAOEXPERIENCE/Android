package it.acrmnet.httpstrust;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import it.acrmnet.androidhttpslibrary.AndroidHttps;

/*
    Per generare le librerie androidhttpslibrary-debug.aar androidhttpslibrary-release.aar
    sulla tool window 'Gradle' apriere :androidhttpslibrary e doppio click su build
    la libreria è in
    AndroidHTTPSPlugin\androidhttpslibrary\build\outputs\aar
    all'interno della libreria *.aar eliminare in libs classes.jar
*/


public class MainActivity extends AppCompatActivity {

    private String urlServer = "https://www.ciaoexperience.com/file-upload/business/";
    private String urlFile = "000157.jpg";
    private String STAR_ciaoexperience_com =
            "-----BEGIN CERTIFICATE-----\n"+
                    "MIIFZDCCBEygAwIBAgIRAMEVsHl6MUPeWAyNkwt1VxswDQYJKoZIhvcNAQELBQAw\n"+
                    "gZAxCzAJBgNVBAYTAkdCMRswGQYDVQQIExJHcmVhdGVyIE1hbmNoZXN0ZXIxEDAO\n"+
                    "BgNVBAcTB1NhbGZvcmQxGjAYBgNVBAoTEUNPTU9ETyBDQSBMaW1pdGVkMTYwNAYD\n"+
                    "VQQDEy1DT01PRE8gUlNBIERvbWFpbiBWYWxpZGF0aW9uIFNlY3VyZSBTZXJ2ZXIg\n"+
                    "Q0EwHhcNMTcwNjA3MDAwMDAwWhcNMTgwNjA3MjM1OTU5WjBhMSEwHwYDVQQLExhE\n"+
                    "b21haW4gQ29udHJvbCBWYWxpZGF0ZWQxHTAbBgNVBAsTFFBvc2l0aXZlU1NMIFdp\n"+
                    "bGRjYXJkMR0wGwYDVQQDDBQqLmNpYW9leHBlcmllbmNlLmNvbTCCASIwDQYJKoZI\n"+
                    "hvcNAQEBBQADggEPADCCAQoCggEBALK31VAYiuD6pVKQW9/zFXmksXwE7pC/pdmh\n"+
                    "RqwQwVmlgcAnpajWGCTypg2NhJsi/1T29LcXmHY9heLw2nAqPcoZOFZRSWvYf9Wk\n"+
                    "kzrAT/t693/GhWxS+OhBQdyuKtnRohRz49rvtDrWFzLTsFF//m4+VqSqa8Mwbtpr\n"+
                    "L0Crj9GpfdL/el2FA6DQqQCz0ETsDTNqh/5apsfagmVDHJ13KGlpAXtzGMUcCzMT\n"+
                    "yH2+NJSS80tPx+nXxqyT0MX4mH/GIgMR7GmCZZaA21CSctvsXZYBebZFkAvt1Kd3\n"+
                    "fRxzZ2WYwot01fvSakRyiTDZrtMT1tQbj0y/RVJZekH5wDitrB0CAwEAAaOCAeUw\n"+
                    "ggHhMB8GA1UdIwQYMBaAFJCvajqUWgvYkOoSVnPfQ7Q6KNrnMB0GA1UdDgQWBBTE\n"+
                    "uWSTwYNuF/jLVPlqXFYQZUCiJTAOBgNVHQ8BAf8EBAMCBaAwDAYDVR0TAQH/BAIw\n"+
                    "ADAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwTwYDVR0gBEgwRjA6Bgsr\n"+
                    "BgEEAbIxAQICBzArMCkGCCsGAQUFBwIBFh1odHRwczovL3NlY3VyZS5jb21vZG8u\n"+
                    "Y29tL0NQUzAIBgZngQwBAgEwVAYDVR0fBE0wSzBJoEegRYZDaHR0cDovL2NybC5j\n"+
                    "b21vZG9jYS5jb20vQ09NT0RPUlNBRG9tYWluVmFsaWRhdGlvblNlY3VyZVNlcnZl\n"+
                    "ckNBLmNybDCBhQYIKwYBBQUHAQEEeTB3ME8GCCsGAQUFBzAChkNodHRwOi8vY3J0\n"+
                    "LmNvbW9kb2NhLmNvbS9DT01PRE9SU0FEb21haW5WYWxpZGF0aW9uU2VjdXJlU2Vy\n"+
                    "dmVyQ0EuY3J0MCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5jb21vZG9jYS5jb20w\n"+
                    "MwYDVR0RBCwwKoIUKi5jaWFvZXhwZXJpZW5jZS5jb22CEmNpYW9leHBlcmllbmNl\n"+
                    "LmNvbTANBgkqhkiG9w0BAQsFAAOCAQEAE5dOXnj5EdlXXJdOYPQIDACc+9k+oVSJ\n"+
                    "I7/fTsg+R/qq17sojxzh6X5DM0/eMp/cTkGS47yNiSUB1N785nWTJk6qJM56IWOP\n"+
                    "P0SlLWmNfHnP495Z0wWHz7d+GuDCeqXbY5gYivQNBv2V+1yPiFVc4hjZVP3vYScW\n"+
                    "ISjnHl+JyrB9obRqYuR9u0hz9G1kRfNZMVWX4Gys14vcKCO0jaQahmx7eQpTH7yA\n"+
                    "7FCWhdAbOZReJCXNieYJyFYJdhQsuYc/1o3WtMuQHfeWUlYnP+VpWMisMvWkDNUA\n"+
                    "n80cFXhulxmbC6/rWkYIivbGqo/1b7RzJAueXL1yzTqBbW74kLw/jw==\n"+
                    "-----END CERTIFICATE-----\n";
    String ciaoServer = "https://www.ciaoexperience.com/api.php";
    String ciaoJson = "{\"m\":\"getDashboard\",\"id_utente\":996,\"id_citta\":4,\"lang\":\"it\",\"lat\":\"41.8988907\",\"lng\":\"12.4621531\",\"imei_code\":\"947e53d47fa5b7751a1f4b3bcd73ccf1859a7d29\",\"time\":\"2017-07-03 16:11:36\"}";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DownloadFileFromUrl download = new DownloadFileFromUrl();
        download.execute(urlServer, urlFile);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button = (Button) findViewById(R.id.downloadWithCertificate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.downloadMessage);
                textView.setText("Adding certificate");
//                AndroidHttps.addCertificate(STAR_ciaoexperience_com.getBytes());
                AndroidHttps.trustOnly(STAR_ciaoexperience_com.getBytes());
                DownloadFileFromUrl download = new DownloadFileFromUrl();
                textView.setText("Downloading image");
                download.execute(urlServer, urlFile);
                view.setVisibility(View.GONE);
            }
        });
        Button buttonJson = (Button) findViewById(R.id.downloadJson);
        buttonJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.downloadMessage);
                textView.setText("Downloading json");
                AndroidHttps trust = new AndroidHttps();
                trust.jsonRequestTest(ciaoServer, ciaoJson, "", "", "");
                view.setVisibility(View.GONE);
            }
        });
        Button buttonUrlDownload = (Button) findViewById(R.id.downloadUrlDownload);
        buttonUrlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.downloadMessage);
                textView.setText("Downloading Image");
                AndroidHttps trust = new AndroidHttps();
                String fileName = getFilesDir()+"/"+urlFile;

                trust.urlDownloadRequestTest(null, urlServer + urlFile, fileName);
                view.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadFileFromUrl extends AsyncTask<String, String, String> {

        Bitmap myBitmap = null;
        /**
         * Before starting background thread
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... param) {
            int count;
            String msg="";
            try {
                String urlServer = param[0];
                String urlFile = param[1];

//                String root = Environment.getExternalStorageDirectory().toString()+"/HttpsTrust";
                String root = getApplication().getFilesDir().getAbsolutePath();

                System.out.println("Downloading");
                URL url = new URL(urlServer+urlFile);

                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lenghtOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                myBitmap = BitmapFactory.decodeStream(input);

                File path=new File(root);
                if (!path.exists()) path.mkdir();
                File file=new File(root+"/"+urlFile);
                if (!file.exists()) file.createNewFile();

                // Output stream to write file
                OutputStream output = new FileOutputStream(root+"/"+urlFile);
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                msg=e.getMessage();
                Log.e("Error: ", e.getMessage());
            }
            return msg;

        }



        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String result) {
            ImageView imageView = (ImageView) findViewById(R.id.downloadedImage);
            TextView textView = (TextView) findViewById(R.id.downloadMessage);
            if (result.isEmpty()) {
                System.out.println("Downloaded");
                imageView.setImageBitmap(myBitmap);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            } else {
                textView.setText(result);
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);

            }
        }
    }

}
