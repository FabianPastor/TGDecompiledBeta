package com.google.android.gms.common;

import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzat;
import com.google.android.gms.common.internal.zzau;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzl;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

abstract class zzh
  extends zzau
{
  private int zzflb;
  
  protected zzh(byte[] paramArrayOfByte)
  {
    Object localObject = paramArrayOfByte;
    if (paramArrayOfByte.length != 25)
    {
      int i = paramArrayOfByte.length;
      localObject = zzl.zza(paramArrayOfByte, 0, paramArrayOfByte.length, false);
      Log.wtf("GoogleCertificates", String.valueOf(localObject).length() + 51 + "Cert hash data has incorrect length (" + i + "):\n" + (String)localObject, new Exception());
      localObject = Arrays.copyOfRange(paramArrayOfByte, 0, 25);
      if (localObject.length == 25) {
        bool = true;
      }
      i = localObject.length;
      zzbq.checkArgument(bool, 55 + "cert hash data has incorrect length. length=" + i);
    }
    this.zzflb = Arrays.hashCode((byte[])localObject);
  }
  
  protected static byte[] zzfx(String paramString)
  {
    try
    {
      paramString = paramString.getBytes("ISO-8859-1");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new AssertionError(paramString);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof zzat))) {
      return false;
    }
    try
    {
      paramObject = (zzat)paramObject;
      if (((zzat)paramObject).zzagb() != hashCode()) {
        return false;
      }
      paramObject = ((zzat)paramObject).zzaga();
      if (paramObject == null) {
        return false;
      }
      paramObject = (byte[])zzn.zzx((IObjectWrapper)paramObject);
      boolean bool = Arrays.equals(getBytes(), (byte[])paramObject);
      return bool;
    }
    catch (RemoteException paramObject)
    {
      Log.e("GoogleCertificates", "Failed to get Google certificates from remote", (Throwable)paramObject);
    }
    return false;
  }
  
  abstract byte[] getBytes();
  
  public int hashCode()
  {
    return this.zzflb;
  }
  
  public final IObjectWrapper zzaga()
  {
    return zzn.zzz(getBytes());
  }
  
  public final int zzagb()
  {
    return hashCode();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */