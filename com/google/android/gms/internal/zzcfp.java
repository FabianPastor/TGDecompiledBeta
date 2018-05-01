package com.google.android.gms.internal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public final class zzcfp
  extends zzchj
{
  public zzcfp(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  @WorkerThread
  private static byte[] zzc(HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    byte[] arrayOfByte = null;
    Object localObject = arrayOfByte;
    ByteArrayOutputStream localByteArrayOutputStream;
    try
    {
      localByteArrayOutputStream = new ByteArrayOutputStream();
      localObject = arrayOfByte;
      paramHttpURLConnection = paramHttpURLConnection.getInputStream();
      localObject = paramHttpURLConnection;
      arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        localObject = paramHttpURLConnection;
        int i = paramHttpURLConnection.read(arrayOfByte);
        if (i <= 0) {
          break;
        }
        localObject = paramHttpURLConnection;
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      localObject = paramHttpURLConnection;
    }
    finally
    {
      if (localObject != null) {
        ((InputStream)localObject).close();
      }
    }
    arrayOfByte = localByteArrayOutputStream.toByteArray();
    if (paramHttpURLConnection != null) {
      paramHttpURLConnection.close();
    }
    return arrayOfByte;
  }
  
  @WorkerThread
  public final void zza(String paramString, URL paramURL, Map<String, String> paramMap, zzcfr paramzzcfr)
  {
    super.zzjC();
    zzkD();
    zzbo.zzu(paramURL);
    zzbo.zzu(paramzzcfr);
    super.zzwE().zzk(new zzcft(this, paramString, paramURL, null, paramMap, paramzzcfr));
  }
  
  @WorkerThread
  public final void zza(String paramString, URL paramURL, byte[] paramArrayOfByte, Map<String, String> paramMap, zzcfr paramzzcfr)
  {
    super.zzjC();
    zzkD();
    zzbo.zzu(paramURL);
    zzbo.zzu(paramArrayOfByte);
    zzbo.zzu(paramzzcfr);
    super.zzwE().zzk(new zzcft(this, paramString, paramURL, paramArrayOfByte, null, paramzzcfr));
  }
  
  protected final void zzjD() {}
  
  public final boolean zzlQ()
  {
    zzkD();
    Object localObject1 = (ConnectivityManager)super.getContext().getSystemService("connectivity");
    try
    {
      localObject1 = ((ConnectivityManager)localObject1).getActiveNetworkInfo();
      if ((localObject1 != null) && (((NetworkInfo)localObject1).isConnected())) {
        return true;
      }
    }
    catch (SecurityException localSecurityException)
    {
      for (;;)
      {
        Object localObject2 = null;
      }
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */