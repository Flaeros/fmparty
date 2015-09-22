package ru.fmparty.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.ResultObject;

public class UploadImageTask extends AsyncTask<String, Integer, ResultObject> {

    private static String TAG = "FlashMob UploadImageTask";
    private ProgressBar progressBar;

    public UploadImageTask(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(progressBar != null)
            progressBar.setProgress(values[0]);
    }

    @Override
    protected ResultObject doInBackground(String... params) {
        ResultObject result = null;

        String filePath = params[0];
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String existingFileName = filePath;
        Log.d(TAG, "existingFileName" + existingFileName);
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";

        try {
            Log.d(TAG, "try");

            //------------------ CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(existingFileName));
            // open a URL connection to the Servlet
            URL url = new URL(Consts.ApiPHP.get() + "upload.php");
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // close streams
            Log.e("Debug", "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            Log.e(TAG, "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(TAG, "error: " + ioe.getMessage(), ioe);
        }

        //------------------ read the SERVER RESPONSE
        try {

            inStream = new DataInputStream(conn.getInputStream());
            String filename = null;

            while ((filename = inStream.readLine()) != null) {

                Log.d(TAG, "Server Response " + filename);
                DbApi.updateChatImage(params[1], filename);
            }

            inStream.close();

        } catch (IOException ioex) {
            Log.e(TAG, "error: " + ioex.getMessage(), ioex);
        }

        return result;
    }

    @Override
    protected void onPostExecute(ResultObject resultObject) {
        Log.d(TAG, "resultObject " + resultObject);

        if(progressBar != null)
            progressBar.setVisibility(ProgressBar.GONE);
    }
}
