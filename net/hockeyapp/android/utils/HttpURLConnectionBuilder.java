package net.hockeyapp.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
  public static final String DEFAULT_CHARSET = "UTF-8";
  private static final int DEFAULT_TIMEOUT = 120000;
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
  }
  
  private static String getFormString(Map<String, String> paramMap, String paramString)
    throws UnsupportedEncodingException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      String str1 = (String)paramMap.get(str2);
      str2 = URLEncoder.encode(str2, paramString);
      str1 = URLEncoder.encode(str1, paramString);
      localArrayList.add(str2 + "=" + str1);
    }
    return TextUtils.join("&", localArrayList);
  }
  
  public HttpURLConnection build()
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(this.mUrlString).openConnection();
    localHttpURLConnection.setConnectTimeout(this.mTimeout);
    localHttpURLConnection.setReadTimeout(this.mTimeout);
    if (Build.VERSION.SDK_INT <= 9) {
      localHttpURLConnection.setRequestProperty("Connection", "close");
    }
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
      localObject = new BufferedOutputStream(localHttpURLConnection.getOutputStream());
      ((BufferedOutputStream)localObject).write(this.mMultipartEntity.getOutputStream().toByteArray());
      ((BufferedOutputStream)localObject).flush();
      ((BufferedOutputStream)localObject).close();
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
  
  public HttpURLConnectionBuilder setTimeout(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Timeout has to be positive.");
    }
    this.mTimeout = paramInt;
    return this;
  }
  
  public HttpURLConnectionBuilder writeFormFields(Map<String, String> paramMap)
  {
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
    Object localObject;
    try
    {
      this.mMultipartEntity = new SimpleMultipartEntity();
      this.mMultipartEntity.writeFirstBoundaryIfNeeds();
      localObject = paramMap.keySet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str = (String)((Iterator)localObject).next();
        this.mMultipartEntity.addPart(str, (String)paramMap.get(str));
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
      localObject = (Uri)paramList.get(i);
      if (i != paramList.size() - 1) {
        break label231;
      }
    }
    label231:
    for (boolean bool = true;; bool = false)
    {
      paramMap = paramContext.getContentResolver().openInputStream((Uri)localObject);
      localObject = ((Uri)localObject).getLastPathSegment();
      this.mMultipartEntity.addPart("attachment" + i, (String)localObject, paramMap, bool);
      i += 1;
      break;
      this.mMultipartEntity.writeLastBoundaryIfNeeds();
      setHeader("Content-Type", "multipart/form-data; boundary=" + this.mMultipartEntity.getBoundary());
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/HttpURLConnectionBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */