package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;

public final class zzak
  implements zzan
{
  private HttpClient zzaA;
  
  public zzak(HttpClient paramHttpClient)
  {
    this.zzaA = paramHttpClient;
  }
  
  private static void zza(HttpEntityEnclosingRequestBase paramHttpEntityEnclosingRequestBase, zzp<?> paramzzp)
    throws zza
  {
    paramzzp = paramzzp.zzg();
    if (paramzzp != null) {
      paramHttpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(paramzzp));
    }
  }
  
  private static void zza(HttpUriRequest paramHttpUriRequest, Map<String, String> paramMap)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramHttpUriRequest.setHeader(str, (String)paramMap.get(str));
    }
  }
  
  public final HttpResponse zza(zzp<?> paramzzp, Map<String, String> paramMap)
    throws IOException, zza
  {
    Object localObject;
    switch (paramzzp.getMethod())
    {
    default: 
      throw new IllegalStateException("Unknown request method.");
    case -1: 
      localObject = new HttpGet(paramzzp.getUrl());
    }
    for (;;)
    {
      zza((HttpUriRequest)localObject, paramMap);
      zza((HttpUriRequest)localObject, paramzzp.getHeaders());
      paramMap = ((HttpUriRequest)localObject).getParams();
      int i = paramzzp.zzi();
      HttpConnectionParams.setConnectionTimeout(paramMap, 5000);
      HttpConnectionParams.setSoTimeout(paramMap, i);
      return this.zzaA.execute((HttpUriRequest)localObject);
      localObject = new HttpGet(paramzzp.getUrl());
      continue;
      localObject = new HttpDelete(paramzzp.getUrl());
      continue;
      localObject = new HttpPost(paramzzp.getUrl());
      ((HttpPost)localObject).addHeader("Content-Type", zzp.zzf());
      zza((HttpEntityEnclosingRequestBase)localObject, paramzzp);
      continue;
      localObject = new HttpPut(paramzzp.getUrl());
      ((HttpPut)localObject).addHeader("Content-Type", zzp.zzf());
      zza((HttpEntityEnclosingRequestBase)localObject, paramzzp);
      continue;
      localObject = new HttpHead(paramzzp.getUrl());
      continue;
      localObject = new HttpOptions(paramzzp.getUrl());
      continue;
      localObject = new HttpTrace(paramzzp.getUrl());
      continue;
      localObject = new zzal(paramzzp.getUrl());
      ((zzal)localObject).addHeader("Content-Type", zzp.zzf());
      zza((HttpEntityEnclosingRequestBase)localObject, paramzzp);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzak.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */