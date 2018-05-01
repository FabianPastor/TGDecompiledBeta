package com.google.android.gms.common;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.ICertData;
import com.google.android.gms.common.internal.ICertData.Stub;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
final class GoogleCertificates
{
  private static final Object zzay = new Object();
  private static Context zzaz;
  
  /* Error */
  static void init(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 24	com/google/android/gms/common/GoogleCertificates:zzaz	Landroid/content/Context;
    //   6: ifnonnull +18 -> 24
    //   9: aload_0
    //   10: ifnull +10 -> 20
    //   13: aload_0
    //   14: invokevirtual 30	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   17: putstatic 24	com/google/android/gms/common/GoogleCertificates:zzaz	Landroid/content/Context;
    //   20: ldc 2
    //   22: monitorexit
    //   23: return
    //   24: ldc 32
    //   26: ldc 34
    //   28: invokestatic 40	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: goto -12 -> 20
    //   35: astore_0
    //   36: ldc 2
    //   38: monitorexit
    //   39: aload_0
    //   40: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	9	35	finally
    //   13	20	35	finally
    //   24	32	35	finally
  }
  
  static abstract class CertData
    extends ICertData.Stub
  {
    private int zzbc;
    
    protected CertData(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length == 25) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkArgument(bool);
        this.zzbc = Arrays.hashCode(paramArrayOfByte);
        return;
      }
    }
    
    protected static byte[] zzd(String paramString)
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
      boolean bool;
      if ((paramObject == null) || (!(paramObject instanceof ICertData))) {
        bool = false;
      }
      for (;;)
      {
        return bool;
        try
        {
          paramObject = (ICertData)paramObject;
          if (((ICertData)paramObject).getHashCode() != hashCode())
          {
            bool = false;
          }
          else
          {
            paramObject = ((ICertData)paramObject).getBytesWrapped();
            if (paramObject == null)
            {
              bool = false;
            }
            else
            {
              paramObject = (byte[])ObjectWrapper.unwrap((IObjectWrapper)paramObject);
              bool = Arrays.equals(getBytes(), (byte[])paramObject);
            }
          }
        }
        catch (RemoteException paramObject)
        {
          Log.e("GoogleCertificates", "Failed to get Google certificates from remote", (Throwable)paramObject);
          bool = false;
        }
      }
    }
    
    abstract byte[] getBytes();
    
    public IObjectWrapper getBytesWrapped()
    {
      return ObjectWrapper.wrap(getBytes());
    }
    
    public int getHashCode()
    {
      return hashCode();
    }
    
    public int hashCode()
    {
      return this.zzbc;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GoogleCertificates.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */