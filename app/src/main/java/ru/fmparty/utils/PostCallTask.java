package ru.fmparty.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostCallTask extends AsyncTask<HttpObjectPair, Void, JSONObject> {

    private static String TAG = "Flashmob PostCallTask";
    private static String apiUrl = "http://dtigran.ru/fmapi/api.php";

    public AsyncResponse delegate = null;

    public PostCallTask(AsyncResponse asyncResponse){
        delegate = asyncResponse;
    }

    @Override
    protected JSONObject doInBackground(HttpObjectPair... params) {
        JSONObject result = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(apiUrl);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for(HttpObjectPair httpObjectPair : params) {
                nameValuePairs.add(new BasicNameValuePair(httpObjectPair.getKey(), httpObjectPair.getValue()));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);
            String answer = EntityUtils.toString(response.getEntity());
            result = new JSONObject(answer);

        } catch (ClientProtocolException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        } catch (JSONException e){
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(JSONObject jsonObject){
        if(delegate != null)
            delegate.onCompleted(jsonObject);
    }
}
