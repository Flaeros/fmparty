package ru.fmparty.apiaccess;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultObject {
    private int resultCode;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    public ResultObject(int resultCode){
        this.resultCode = resultCode;
    }

    public ResultObject(int resultCode, JSONObject jsonObject){
        this.resultCode = resultCode;
        this.jsonObject = jsonObject;
    }

    public ResultObject(int resultCode, JSONArray jsonArray){
        this.resultCode = resultCode;
        this.jsonArray = jsonArray;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public String toString(){
        return "answer: " + resultCode + "; jsonObject: " + jsonObject + "; jsonArray: " + jsonArray;
    }
}
