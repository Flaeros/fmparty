package ru.fmparty.utils;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

public interface AsyncResponse {
        void onTaskCompleted(JSONObject jsonObject);
}
