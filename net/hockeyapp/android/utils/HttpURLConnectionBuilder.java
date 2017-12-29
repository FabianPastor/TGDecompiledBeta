package net.hockeyapp.android.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.C;

public class HttpURLConnectionBuilder {
    private final Map<String, String> mHeaders;
    private SimpleMultipartEntity mMultipartEntity;
    private String mRequestBody;
    private String mRequestMethod;
    private int mTimeout = 120000;
    private final String mUrlString;

    public HttpURLConnectionBuilder(String urlString) {
        this.mUrlString = urlString;
        this.mHeaders = new HashMap();
        this.mHeaders.put("User-Agent", "HockeySDK/Android 4.1.3");
    }

    public HttpURLConnectionBuilder setRequestMethod(String requestMethod) {
        this.mRequestMethod = requestMethod;
        return this;
    }

    public HttpURLConnectionBuilder setRequestBody(String requestBody) {
        this.mRequestBody = requestBody;
        return this;
    }

    public HttpURLConnectionBuilder writeFormFields(Map<String, String> fields) {
        for (String key : fields.keySet()) {
            String value = (String) fields.get(key);
            if (((long) value.length()) > 4194304) {
                throw new IllegalArgumentException("Form field " + key + " size too large: " + value.length() + " - max allowed: " + 4194304);
            }
        }
        try {
            String formString = getFormString(fields, C.UTF8_NAME);
            setHeader("Content-Type", "application/x-www-form-urlencoded");
            setRequestBody(formString);
            return this;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpURLConnectionBuilder writeMultipartData(Map<String, String> fields, Context context, List<Uri> attachmentUris) {
        try {
            this.mMultipartEntity = new SimpleMultipartEntity();
            this.mMultipartEntity.writeFirstBoundaryIfNeeds();
            for (String key : fields.keySet()) {
                this.mMultipartEntity.addPart(key, (String) fields.get(key));
            }
            int i = 0;
            while (i < attachmentUris.size()) {
                Uri attachmentUri = (Uri) attachmentUris.get(i);
                boolean lastFile = i == attachmentUris.size() + -1;
                this.mMultipartEntity.addPart("attachment" + i, attachmentUri.getLastPathSegment(), context.getContentResolver().openInputStream(attachmentUri), lastFile);
                i++;
            }
            this.mMultipartEntity.writeLastBoundaryIfNeeds();
            setHeader("Content-Type", "multipart/form-data; boundary=" + this.mMultipartEntity.getBoundary());
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpURLConnectionBuilder setHeader(String name, String value) {
        this.mHeaders.put(name, value);
        return this;
    }

    public HttpURLConnectionBuilder setBasicAuthorization(String username, String password) {
        setHeader("Authorization", "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), 2));
        return this;
    }

    public HttpURLConnection build() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(this.mUrlString).openConnection();
        connection.setConnectTimeout(this.mTimeout);
        connection.setReadTimeout(this.mTimeout);
        if (VERSION.SDK_INT <= 9) {
            connection.setRequestProperty("Connection", "close");
        }
        if (!TextUtils.isEmpty(this.mRequestMethod)) {
            connection.setRequestMethod(this.mRequestMethod);
            if (!TextUtils.isEmpty(this.mRequestBody) || this.mRequestMethod.equalsIgnoreCase("POST") || this.mRequestMethod.equalsIgnoreCase("PUT")) {
                connection.setDoOutput(true);
            }
        }
        for (String name : this.mHeaders.keySet()) {
            connection.setRequestProperty(name, (String) this.mHeaders.get(name));
        }
        if (!TextUtils.isEmpty(this.mRequestBody)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), C.UTF8_NAME));
            writer.write(this.mRequestBody);
            writer.flush();
            writer.close();
        }
        if (this.mMultipartEntity != null) {
            connection.setRequestProperty("Content-Length", String.valueOf(this.mMultipartEntity.getContentLength()));
            BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
            outputStream.write(this.mMultipartEntity.getOutputStream().toByteArray());
            outputStream.flush();
            outputStream.close();
        }
        return connection;
    }

    private static String getFormString(Map<String, String> params, String charset) throws UnsupportedEncodingException {
        List<String> protoList = new ArrayList();
        for (String key : params.keySet()) {
            String value = (String) params.get(key);
            String key2 = URLEncoder.encode(key2, charset);
            protoList.add(key2 + "=" + URLEncoder.encode(value, charset));
        }
        return TextUtils.join("&", protoList);
    }
}
