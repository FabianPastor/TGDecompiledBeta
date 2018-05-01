package com.google.android.gms.internal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public final class zzao
  implements zzan
{
  private final zzap zzaB = null;
  private final SSLSocketFactory zzaC = null;
  
  public zzao()
  {
    this(null);
  }
  
  private zzao(zzap paramzzap)
  {
    this(null, null);
  }
  
  private zzao(zzap paramzzap, SSLSocketFactory paramSSLSocketFactory) {}
  
  private static HttpEntity zza(HttpURLConnection paramHttpURLConnection)
  {
    BasicHttpEntity localBasicHttpEntity = new BasicHttpEntity();
    try
    {
      InputStream localInputStream1 = paramHttpURLConnection.getInputStream();
      localBasicHttpEntity.setContent(localInputStream1);
      localBasicHttpEntity.setContentLength(paramHttpURLConnection.getContentLength());
      localBasicHttpEntity.setContentEncoding(paramHttpURLConnection.getContentEncoding());
      localBasicHttpEntity.setContentType(paramHttpURLConnection.getContentType());
      return localBasicHttpEntity;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        InputStream localInputStream2 = paramHttpURLConnection.getErrorStream();
      }
    }
  }
  
  private static void zza(HttpURLConnection paramHttpURLConnection, zzp<?> paramzzp)
    throws IOException, zza
  {
    paramzzp = paramzzp.zzg();
    if (paramzzp != null)
    {
      paramHttpURLConnection.setDoOutput(true);
      paramHttpURLConnection.addRequestProperty("Content-Type", zzp.zzf());
      paramHttpURLConnection = new DataOutputStream(paramHttpURLConnection.getOutputStream());
      paramHttpURLConnection.write(paramzzp);
      paramHttpURLConnection.close();
    }
  }
  
  public final HttpResponse zza(zzp<?> paramzzp, Map<String, String> paramMap)
    throws IOException, zza
  {
    Object localObject1 = paramzzp.getUrl();
    HashMap localHashMap = new HashMap();
    localHashMap.putAll(paramzzp.getHeaders());
    localHashMap.putAll(paramMap);
    if (this.zzaB != null)
    {
      localObject2 = this.zzaB.zzg((String)localObject1);
      paramMap = (Map<String, String>)localObject2;
      if (localObject2 == null)
      {
        paramzzp = String.valueOf(localObject1);
        if (paramzzp.length() != 0) {}
        for (paramzzp = "URL blocked by rewriter: ".concat(paramzzp);; paramzzp = new String("URL blocked by rewriter: ")) {
          throw new IOException(paramzzp);
        }
      }
    }
    else
    {
      paramMap = (Map<String, String>)localObject1;
    }
    localObject1 = new URL(paramMap);
    paramMap = (HttpURLConnection)((URL)localObject1).openConnection();
    paramMap.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
    int i = paramzzp.zzi();
    paramMap.setConnectTimeout(i);
    paramMap.setReadTimeout(i);
    paramMap.setUseCaches(false);
    paramMap.setDoInput(true);
    "https".equals(((URL)localObject1).getProtocol());
    localObject1 = localHashMap.keySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      paramMap.addRequestProperty((String)localObject2, (String)localHashMap.get(localObject2));
    }
    switch (paramzzp.getMethod())
    {
    default: 
      throw new IllegalStateException("Unknown method type.");
    case 0: 
      paramMap.setRequestMethod("GET");
    }
    for (;;)
    {
      localObject1 = new ProtocolVersion("HTTP", 1, 1);
      if (paramMap.getResponseCode() != -1) {
        break;
      }
      throw new IOException("Could not retrieve response code from HttpUrlConnection.");
      paramMap.setRequestMethod("DELETE");
      continue;
      paramMap.setRequestMethod("POST");
      zza(paramMap, paramzzp);
      continue;
      paramMap.setRequestMethod("PUT");
      zza(paramMap, paramzzp);
      continue;
      paramMap.setRequestMethod("HEAD");
      continue;
      paramMap.setRequestMethod("OPTIONS");
      continue;
      paramMap.setRequestMethod("TRACE");
      continue;
      paramMap.setRequestMethod("PATCH");
      zza(paramMap, paramzzp);
    }
    Object localObject2 = new BasicStatusLine((ProtocolVersion)localObject1, paramMap.getResponseCode(), paramMap.getResponseMessage());
    localObject1 = new BasicHttpResponse((StatusLine)localObject2);
    i = paramzzp.getMethod();
    int j = ((StatusLine)localObject2).getStatusCode();
    if ((i != 4) && ((100 > j) || (j >= 200)) && (j != 204) && (j != 304)) {}
    for (i = 1;; i = 0)
    {
      if (i != 0) {
        ((BasicHttpResponse)localObject1).setEntity(zza(paramMap));
      }
      paramzzp = paramMap.getHeaderFields().entrySet().iterator();
      while (paramzzp.hasNext())
      {
        paramMap = (Map.Entry)paramzzp.next();
        if (paramMap.getKey() != null) {
          ((BasicHttpResponse)localObject1).addHeader(new BasicHeader((String)paramMap.getKey(), (String)((List)paramMap.getValue()).get(0)));
        }
      }
    }
    return (HttpResponse)localObject1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzao.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */