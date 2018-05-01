package net.hockeyapp.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpURLConnectionBuilder
{
  private final Map<String, String> mHeaders;
  private SimpleMultipartEntity mMultipartEntity;
  private String mRequestBody;
  private String mRequestMethod;
  private int mTimeout = 120000;
  private final String mUrlString;
  
  public HttpURLConnectionBuilder(String paramString)
  {
    this.mUrlString = paramString;
    this.mHeaders = new HashMap();
    this.mHeaders.put("User-Agent", "HockeySDK/Android 5.0.4");
  }
  
  private static String getFormString(Map<String, String> paramMap, String paramString)
    throws UnsupportedEncodingException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)paramMap.get(str1);
      str1 = URLEncoder.encode(str1, paramString);
      str2 = URLEncoder.encode(str2, paramString);
      localArrayList.add(str1 + "=" + str2);
    }
    return TextUtils.join("&", localArrayList);
  }
  
  public HttpURLConnection build()
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(this.mUrlString).openConnection();
    localHttpURLConnection.setConnectTimeout(this.mTimeout);
    localHttpURLConnection.setReadTimeout(this.mTimeout);
    if (!TextUtils.isEmpty(this.mRequestMethod))
    {
      localHttpURLConnection.setRequestMethod(this.mRequestMethod);
      if ((!TextUtils.isEmpty(this.mRequestBody)) || (this.mRequestMethod.equalsIgnoreCase("POST")) || (this.mRequestMethod.equalsIgnoreCase("PUT"))) {
        localHttpURLConnection.setDoOutput(true);
      }
    }
    Object localObject = this.mHeaders.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      localHttpURLConnection.setRequestProperty(str, (String)this.mHeaders.get(str));
    }
    if (!TextUtils.isEmpty(this.mRequestBody))
    {
      localObject = new BufferedWriter(new OutputStreamWriter(localHttpURLConnection.getOutputStream(), "UTF-8"));
      ((BufferedWriter)localObject).write(this.mRequestBody);
      ((BufferedWriter)localObject).flush();
      ((BufferedWriter)localObject).close();
    }
    if (this.mMultipartEntity != null)
    {
      localHttpURLConnection.setRequestProperty("Content-Length", String.valueOf(this.mMultipartEntity.getContentLength()));
      this.mMultipartEntity.writeTo(localHttpURLConnection.getOutputStream());
    }
    return localHttpURLConnection;
  }
  
  public HttpURLConnectionBuilder setBasicAuthorization(String paramString1, String paramString2)
  {
    setHeader("Authorization", "Basic " + Base64.encodeToString(new StringBuilder().append(paramString1).append(":").append(paramString2).toString().getBytes(), 2));
    return this;
  }
  
  public HttpURLConnectionBuilder setHeader(String paramString1, String paramString2)
  {
    this.mHeaders.put(paramString1, paramString2);
    return this;
  }
  
  public HttpURLConnectionBuilder setRequestBody(String paramString)
  {
    this.mRequestBody = paramString;
    return this;
  }
  
  public HttpURLConnectionBuilder setRequestMethod(String paramString)
  {
    this.mRequestMethod = paramString;
    return this;
  }
  
  public HttpURLConnectionBuilder writeFormFields(Map<String, String> paramMap)
  {
    if (paramMap.size() > 25) {
      throw new IllegalArgumentException("Fields size too large: " + paramMap.size() + " - max allowed: " + 25);
    }
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)paramMap.get(str1);
      if ((str2 != null) && (str2.length() > 4194304L)) {
        throw new IllegalArgumentException("Form field " + str1 + " size too large: " + str2.length() + " - max allowed: " + 4194304L);
      }
    }
    try
    {
      paramMap = getFormString(paramMap, "UTF-8");
      setHeader("Content-Type", "application/x-www-form-urlencoded");
      setRequestBody(paramMap);
      return this;
    }
    catch (UnsupportedEncodingException paramMap)
    {
      throw new RuntimeException(paramMap);
    }
  }
  
  public HttpURLConnectionBuilder writeMultipartData(Map<String, String> paramMap, Context paramContext, List<Uri> paramList)
  {
    Object localObject1;
    Object localObject2;
    try
    {
      localObject1 = File.createTempFile("multipart", null, paramContext.getCacheDir());
      localObject2 = new net/hockeyapp/android/utils/SimpleMultipartEntity;
      ((SimpleMultipartEntity)localObject2).<init>((File)localObject1);
      this.mMultipartEntity = ((SimpleMultipartEntity)localObject2);
      this.mMultipartEntity.writeFirstBoundaryIfNeeds();
      localObject1 = paramMap.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        this.mMultipartEntity.addPart((String)localObject2, (String)paramMap.get(localObject2));
      }
      i = 0;
    }
    catch (IOException paramMap)
    {
      throw new RuntimeException(paramMap);
    }
    int i;
    if (i < paramList.size())
    {
      localObject1 = (Uri)paramList.get(i);
      if (i == paramList.size() - 1) {}
      for (boolean bool = true;; bool = false)
      {
        paramMap = paramContext.getContentResolver().openInputStream((Uri)localObject1);
        localObject1 = ((Uri)localObject1).getLastPathSegment();
        localObject2 = this.mMultipartEntity;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        ((SimpleMultipartEntity)localObject2).addPart("attachment" + i, (String)localObject1, paramMap, bool);
        i++;
        break;
      }
    }
    this.mMultipartEntity.writeLastBoundaryIfNeeds();
    paramMap = new java/lang/StringBuilder;
    paramMap.<init>();
    setHeader("Content-Type", "multipart/form-data; boundary=" + this.mMultipartEntity.getBoundary());
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/HttpURLConnectionBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */