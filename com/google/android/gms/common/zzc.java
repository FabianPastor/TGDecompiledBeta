package com.google.android.gms.common;

import java.lang.ref.WeakReference;

abstract class zzc
  extends GoogleCertificates.CertData
{
  private static final WeakReference<byte[]> zzbf = new WeakReference(null);
  private WeakReference<byte[]> zzbe = zzbf;
  
  zzc(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  final byte[] getBytes()
  {
    try
    {
      Object localObject1 = (byte[])this.zzbe.get();
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = zzf();
        localObject1 = new java/lang/ref/WeakReference;
        ((WeakReference)localObject1).<init>(localObject2);
        this.zzbe = ((WeakReference)localObject1);
      }
      return (byte[])localObject2;
    }
    finally {}
  }
  
  protected abstract byte[] zzf();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */