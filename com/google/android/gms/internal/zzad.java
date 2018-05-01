package com.google.android.gms.internal;

import android.os.SystemClock;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;

public final class zzad
  implements zzk
{
  private static boolean DEBUG = zzab.DEBUG;
  private static int zzam = 3000;
  private static int zzan = 4096;
  private zzan zzao;
  private zzae zzap;
  
  public zzad(zzan paramzzan)
  {
    this(paramzzan, new zzae(zzan));
  }
  
  private zzad(zzan paramzzan, zzae paramzzae)
  {
    this.zzao = paramzzan;
    this.zzap = paramzzae;
  }
  
  private static Map<String, String> zza(Header[] paramArrayOfHeader)
  {
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    int i = 0;
    while (i < paramArrayOfHeader.length)
    {
      localTreeMap.put(paramArrayOfHeader[i].getName(), paramArrayOfHeader[i].getValue());
      i += 1;
    }
    return localTreeMap;
  }
  
  private static void zza(String paramString, zzp<?> paramzzp, zzaa paramzzaa)
    throws zzaa
  {
    zzx localzzx = paramzzp.zzj();
    int i = paramzzp.zzi();
    try
    {
      localzzx.zza(paramzzaa);
      paramzzp.zzb(String.format("%s-retry [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      return;
    }
    catch (zzaa paramzzaa)
    {
      paramzzp.zzb(String.format("%s-timeout-giveup [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      throw paramzzaa;
    }
  }
  
  private final byte[] zza(HttpEntity paramHttpEntity)
    throws IOException, zzy
  {
    zzaq localzzaq = new zzaq(this.zzap, (int)paramHttpEntity.getContentLength());
    Object localObject2 = null;
    Object localObject1 = localObject2;
    Object localObject4;
    try
    {
      localObject4 = paramHttpEntity.getContent();
      if (localObject4 == null)
      {
        localObject1 = localObject2;
        throw new zzy();
      }
    }
    finally {}
    try
    {
      paramHttpEntity.consumeContent();
      this.zzap.zza((byte[])localObject1);
      localzzaq.close();
      throw ((Throwable)localObject3);
      localObject1 = localObject3;
      byte[] arrayOfByte = this.zzap.zzb(1024);
      for (;;)
      {
        localObject1 = arrayOfByte;
        int i = ((InputStream)localObject4).read(arrayOfByte);
        if (i == -1) {
          break;
        }
        localObject1 = arrayOfByte;
        localzzaq.write(arrayOfByte, 0, i);
      }
      localObject1 = arrayOfByte;
      localObject4 = localzzaq.toByteArray();
      try
      {
        paramHttpEntity.consumeContent();
        this.zzap.zza(arrayOfByte);
        localzzaq.close();
        return (byte[])localObject4;
      }
      catch (IOException paramHttpEntity)
      {
        for (;;)
        {
          zzab.zza("Error occured when calling consumingContent", new Object[0]);
        }
      }
    }
    catch (IOException paramHttpEntity)
    {
      for (;;)
      {
        zzab.zza("Error occured when calling consumingContent", new Object[0]);
      }
    }
  }
  
  public final zzn zza(zzp<?> paramzzp)
    throws zzaa
  {
    long l1 = SystemClock.elapsedRealtime();
    for (;;)
    {
      Map localMap = null;
      localObject6 = Collections.emptyMap();
      try
      {
        Object localObject1 = new HashMap();
        Object localObject4 = paramzzp.zze();
        if (localObject4 != null)
        {
          if (((zzc)localObject4).zza != null) {
            ((Map)localObject1).put("If-None-Match", ((zzc)localObject4).zza);
          }
          if (((zzc)localObject4).zzc > 0L) {
            ((Map)localObject1).put("If-Modified-Since", DateUtils.formatDate(new Date(((zzc)localObject4).zzc)));
          }
        }
        localObject4 = this.zzao.zza(paramzzp, (Map)localObject1);
        localObject1 = localObject6;
        for (;;)
        {
          int i;
          Object localObject3;
          Object localObject5;
          try
          {
            localStatusLine = ((HttpResponse)localObject4).getStatusLine();
            localObject1 = localObject6;
            i = localStatusLine.getStatusCode();
            localObject1 = localObject6;
            localMap = zza(((HttpResponse)localObject4).getAllHeaders());
            if (i == 304)
            {
              localObject1 = localMap;
              localObject6 = paramzzp.zze();
              if (localObject6 == null)
              {
                localObject1 = localMap;
                return new zzn(304, null, localMap, true, SystemClock.elapsedRealtime() - l1);
              }
              localObject1 = localMap;
              ((zzc)localObject6).zzf.putAll(localMap);
              localObject1 = localMap;
              return new zzn(304, ((zzc)localObject6).data, ((zzc)localObject6).zzf, true, SystemClock.elapsedRealtime() - l1);
            }
            localObject1 = localMap;
            if (((HttpResponse)localObject4).getEntity() != null)
            {
              localObject1 = localMap;
              localObject6 = zza(((HttpResponse)localObject4).getEntity());
              localObject1 = localObject6;
            }
          }
          catch (IOException localIOException3)
          {
            long l2;
            Object localObject2;
            StatusLine localStatusLine = null;
            localObject6 = localIOException1;
            localObject5 = localIOException3;
            continue;
          }
          try
          {
            l2 = SystemClock.elapsedRealtime() - l1;
            if ((!DEBUG) && (l2 <= zzam)) {
              break label745;
            }
            if (localObject1 == null) {
              continue;
            }
            localObject6 = Integer.valueOf(localObject1.length);
            zzab.zzb("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", new Object[] { paramzzp, Long.valueOf(l2), localObject6, Integer.valueOf(localStatusLine.getStatusCode()), Integer.valueOf(paramzzp.zzj().zzb()) });
          }
          catch (IOException localIOException2)
          {
            localObject6 = localObject5;
            localObject5 = localIOException2;
            Object localObject7 = localObject3;
            localObject3 = localIOException3;
            continue;
            if (i < 200) {
              continue;
            }
            if (i <= 299) {
              continue;
            }
          }
        }
        throw new IOException();
      }
      catch (SocketTimeoutException localSocketTimeoutException)
      {
        for (;;)
        {
          zza("socket", paramzzp, new zzz());
          break;
          localObject2 = localMap;
          localObject6 = new byte[0];
          localObject2 = localObject6;
          continue;
          localObject6 = "null";
        }
        localObject6 = new zzn(i, (byte[])localObject2, localMap, false, SystemClock.elapsedRealtime() - l1);
        return (zzn)localObject6;
      }
      catch (ConnectTimeoutException localConnectTimeoutException)
      {
        zza("connection", paramzzp, new zzz());
      }
      catch (MalformedURLException localMalformedURLException)
      {
        paramzzp = String.valueOf(paramzzp.getUrl());
        if (paramzzp.length() != 0) {}
        for (paramzzp = "Bad URL ".concat(paramzzp);; paramzzp = new String("Bad URL ")) {
          throw new RuntimeException(paramzzp, localMalformedURLException);
        }
      }
      catch (IOException localIOException1)
      {
        localStatusLine = null;
        localObject3 = localObject6;
        localObject6 = localMap;
        if (localObject6 != null)
        {
          i = ((HttpResponse)localObject6).getStatusLine().getStatusCode();
          zzab.zzc("Unexpected response code %d for %s", new Object[] { Integer.valueOf(i), paramzzp.getUrl() });
          if (localStatusLine == null) {
            break label691;
          }
          localObject3 = new zzn(i, localStatusLine, (Map)localObject3, false, SystemClock.elapsedRealtime() - l1);
          if ((i == 401) || (i == 403)) {
            zza("auth", paramzzp, new zza((zzn)localObject3));
          }
        }
        else
        {
          throw new zzo(localIOException1);
        }
      }
      if ((i >= 400) && (i <= 499)) {
        throw new zzf((zzn)localObject3);
      }
      if ((i >= 500) && (i <= 599)) {
        throw new zzy((zzn)localObject3);
      }
      throw new zzy((zzn)localObject3);
      label691:
      zza("network", paramzzp, new zzm());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */