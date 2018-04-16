package com.commonfeaturelib.Apis;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.commonfeaturelib.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by janarthananr on 21/3/18.
 */

public class BackGroundTask extends AsyncTask<String, String, String> {
    Context mContext;
    GetValuesFromApi getValuesFromApi;
    String apitype = "", outputfromapi = "", apiname = "", ApiUrl = "", ApiParametersRequest = "",
            ApiHeadersRequest = "", ApiMultipartRequest = "", filepath = "", ApiUploadfiles = "", uploadnodename = "";
    MediaType mediatype;
    boolean flagloading = false;
    ProgressDialog dialog;

    public BackGroundTask(Context context, boolean flagloading, GetValuesFromApi getValuesFromApi,
                          String type, String apiname, String ApiUploadfiles,
                          String ApiMultipartRequest) {
        this.mContext = context; // Activity Context
        this.flagloading = flagloading; // Loading dialogue
        this.getValuesFromApi = getValuesFromApi; // Output InterFace
        this.apitype = type; // Api type like (Get or Post)
        this.apiname = apiname; // Where we called
        this.ApiMultipartRequest = ApiMultipartRequest; // its required only for files upload
        this.ApiUploadfiles = ApiUploadfiles; // No.of files list to upload
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (flagloading) {
            dialog = new ProgressDialog(mContext, R.style.FullscreenDialog);
            dialog.setContentView(R.layout.progressbar);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        CustomApiClient Chttpclient = new CustomApiClient();
        ApiUrl = strings[0];
        ApiParametersRequest = strings[1];
        ApiHeadersRequest = strings[2];
        Headers.Builder headerbuilder = null;
        okhttp3.Request request = null;
        FormBody.Builder postparamsbuilder = null;
        MultipartBody.Builder multibuilder = null;
        try {
            if (ApiHeadersRequest != null && !ApiHeadersRequest.equals("")) {
                headerbuilder = new Headers.Builder();
                JSONObject headerjson = new JSONObject(ApiParametersRequest);
                Iterator<String> IterforHeader = headerjson.keys();
                while (IterforHeader.hasNext()) {
                    String key = IterforHeader.next();
                    String paraStr = headerjson.getString(key);
                    headerbuilder.add(key, paraStr);
                }
            }
            if (ApiParametersRequest != null && !ApiParametersRequest.equals("")) {
                postparamsbuilder = new FormBody.Builder();
                JSONObject jsonParm = new JSONObject(ApiParametersRequest);
                Iterator<String> IterforApiparameters = jsonParm.keys();
                while (IterforApiparameters.hasNext()) {
                    String key = IterforApiparameters.next();
                    String paraStr = jsonParm.getString(key);
                    postparamsbuilder.add(key, paraStr);
                }
            }
            if (ApiUploadfiles != null && !ApiUploadfiles.equals("")) {
                multibuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                JSONObject jsonObject = new JSONObject(ApiUploadfiles);
                JSONArray jsonArray = jsonObject.getJSONArray("uploadfiles");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jslist = jsonArray.getJSONObject(i);
                    mediatype = MediaType.parse(jslist.getString("mediatype"));
                    filepath = jslist.getString("filepath");
                    uploadnodename = jslist.getString("uploadnodename");
                    multibuilder.addFormDataPart(uploadnodename, new File(filepath).getName(),
                            RequestBody.create(mediatype, new File(filepath)));
                }
            }
            if (ApiMultipartRequest != null && !ApiMultipartRequest.equals("")) {
                JSONObject jsonMultipart = new JSONObject(ApiMultipartRequest);
                Iterator<String> IterforApiparameters = jsonMultipart.keys();
                while (IterforApiparameters.hasNext()) {
                    String key = IterforApiparameters.next();
                    String paraStr = jsonMultipart.getString(key);
                    multibuilder.addFormDataPart(key, paraStr);
                }
            }
            if (apitype.equalsIgnoreCase("POST")) {
                if (headerbuilder != null) {
                    request = new okhttp3.Request.Builder()
                            .url(ApiUrl)
                            .headers(headerbuilder.build())
                            .post(postparamsbuilder.build()).build();
                } else {
                    request = new okhttp3.Request.Builder()
                            .url(ApiUrl)
                            .post(postparamsbuilder.build()).build();
                }
            } else if (apitype.equalsIgnoreCase("Multipart")) {
                if (headerbuilder != null) {
                    request = new okhttp3.Request.Builder()
                            .url(ApiUrl)
                            .headers(headerbuilder.build())
                            .post(multibuilder.build()).build();
                } else {
                    request = new okhttp3.Request.Builder()
                            .url(ApiUrl)
                            .post(multibuilder.build()).build();
                }
            } else if (apitype.equalsIgnoreCase("Delete")) {
                if (headerbuilder != null)
                    request = new okhttp3.Request.Builder()
                            .url(ApiUrl).delete()
                            .headers(headerbuilder.build())
                            .build();
                else request = new okhttp3.Request.Builder()
                        .url(ApiUrl).delete()
                        .build();
            } else if (apitype.equalsIgnoreCase("Get")) {
                if (headerbuilder != null)
                    request = new okhttp3.Request.Builder()
                            .url(ApiUrl)
                            .headers(headerbuilder.build())
                            .build();
                else request = new okhttp3.Request.Builder()
                        .url(ApiUrl)
                        .build();
            } else if (apitype.equalsIgnoreCase("Put")) {

            }
            outputfromapi = Chttpclient.get(request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outputfromapi;
    }

    @Override
    protected void onPostExecute(String output) {
        super.onPostExecute(output);
        if(flagloading&&dialog!=null&&dialog.isShowing()){
            dialog.cancel();
        }
        this.getValuesFromApi.valuesfromServer(output, apiname);
    }
}
