package com.google.android.gms.common;

import java.lang.ref.WeakReference;

abstract class zzj
  extends zzh
{
  private static final WeakReference<byte[]> zzfle = new WeakReference(null);
  private WeakReference<byte[]> zzfld = zzfle;
  
  zzj(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  final byte[] getBytes()
  {
    try
    {
      byte[] arrayOfByte2 = (byte[])this.zzfld.get();
      byte[] arrayOfByte1 = arrayOfByte2;
      if (arrayOfByte2 == null)
      {
        arrayOfByte1 = zzagc();
        this.zzfld = new WeakReference(arrayOfByte1);
      }
      return arrayOfByte1;
    }
    finally {}
  }
  
  protected abstract byte[] zzagc();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */