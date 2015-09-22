package ru.fmparty.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.ResultObject;

public class PostCallTask extends AsyncTask<HttpObjectPair, Integer, ResultObject> {

    private static String TAG = "FlashMob PostCallTask";

    private AsyncResponse delegate;
    private ProgressBar progressBar;

    public PostCallTask(){}
    public PostCallTask(AsyncResponse asyncResponse){ delegate = asyncResponse; }

    public PostCallTask(AsyncResponse asyncResponse, ProgressBar progressBar){
        delegate = asyncResponse;
        this.progressBar = progressBar;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(progressBar != null)
            progressBar.setProgress(values[0]);
    }

    @Override
    protected ResultObject doInBackground(HttpObjectPair... params) {
        ResultObject result = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Consts.ApiPHP.get() + "api.php");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for(HttpObjectPair httpObjectPair : params) {
                nameValuePairs.add(new BasicNameValuePair(httpObjectPair.getKey(), httpObjectPair.getValue()));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);
            String answer = EntityUtils.toString(response.getEntity());
            Log.d(TAG, "answer = " + answer);
            JSONObject jsonObject = new JSONObject(answer);

            int resultCode = jsonObject.getInt("resultCode");
            Log.d(TAG, "onTaskCompleted resultCode =" + resultCode);

            Object resultObject = jsonObject.get("resultObject");
            Log.d(TAG, "resultObject = " + resultObject);

            if(resultObject instanceof JSONArray) {
                JSONArray resultObjectJsonArray = (JSONArray) resultObject;
                result = new ResultObject(resultCode, resultObjectJsonArray);
            }
            else if(resultObject instanceof JSONObject) {
                JSONObject resultObjectJsonObject = (JSONObject) resultObject;
                result = new ResultObject(resultCode, resultObjectJsonObject);
            }
            else{
                result = new ResultObject(resultCode);
            }

            return result;

        } catch (ClientProtocolException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        } catch (IOException | JSONException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
            result = new ResultObject(0);
        }

        return result;
    }

    protected void onPostExecute(ResultObject resultObject){
        if(delegate != null)
            delegate.onCompleted(resultObject);

        if(progressBar != null)
            progressBar.setVisibility(ProgressBar.GONE);
    }
}
