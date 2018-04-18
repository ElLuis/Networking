package com.centennial.elluis.networking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    final private int REQUEST_INTERNET = 123;

    //Open HTTP connection
    private InputStream OpenHttpConnection(String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    //Downloading Binary Data
    private Bitmap DownloadImage(String URL)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        return bitmap;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            return DownloadImage(urls[0]);
        }
        protected void onPostExecute(Bitmap result) {
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(result);
        }
    }

    //Download multiple images
    /*private class DownloadImageTask extends AsyncTask
            <String, Bitmap, Long> {
        //---takes in a list of image URLs in String type---
        protected Long doInBackground(String... urls) {
            long imagesCount = 0;
            for (int i = 0; i < urls.length; i++) {
//---download the image---
                Bitmap imageDownloaded = DownloadImage(urls[i]);
                if (imageDownloaded != null) {
//---increment the image count---
                    imagesCount++;
                    try {
//---insert a delay of 3 seconds---
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//---return the image downloaded---
                    publishProgress(imageDownloaded);
                }
            }
//---return the total images downloaded count---
            return imagesCount;
        }
        //---display the image downloaded---
        protected void onProgressUpdate(Bitmap... bitmap) {
            img.setImageBitmap(bitmap[0]);
        }
        //---when all the images have been downloaded---
        protected void onPostExecute(Long imagesDownloaded) {
            Toast.makeText(getBaseContext(),
                    "Total " + imagesDownloaded + " images downloaded" ,
                    Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET);
        } else{
            new DownloadImageTask().execute(
                    "https://3c1703fe8d.site.internapcdn.net/newman/gfx/news/hires/2013/morphobutter.jpg");
        }
    }

    //download a series of images asynchronously in the background
    /* new DownloadImageTask().execute(
"http://www.mayoff.com/5-01cablecarDCP01934.jpg");
*/
    /*img = (ImageView) findViewById(R.id.img);
new DownloadImageTask().execute(
"http://www.mayoff.com/5-01cablecarDCP01934.jpg",
        "http://www.hartiesinfo.net/greybox/Cable_Car_
Hartbeespoort.jpg",
        "http://mcmanuslab.ucsf.edu/sites/default/files/
        imagepicker/m/mmcmanus/
        CaliforniaSanFranciscoPaintedLadiesHz.jpg",
        "http://www.fantom-xp.com/wallpapers/63/San_Francisco
        _-_Sunset.jpg",
        "http://travel.roro44.com/europe/france/
        Paris_France.jpg",
        "http://wwp.greenwichmeantime.com/time-zone/usa/nevada
        /las-vegas/hotel/the-strip/paris-las-vegas/parislas-
        vegas-hotel.jpg",
        "http://designheaven.files.wordpress.com/2010/04/
        eiffel_tower_paris_france.jpg");
}*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_INTERNET:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new DownloadImageTask().execute(
                            "http://www.jfdimarzio.com/butterfly.png");
                } else {
                    Toast.makeText(MainActivity.this,
                            "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,
                        permissions, grantResults);
        }
    }
}
