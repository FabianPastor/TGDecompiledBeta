package com.google.android.gms.common;

import java.util.Arrays;

final class zzb
  extends GoogleCertificates.CertData
{
  private final byte[] zzbd;
  
  zzb(byte[] paramArrayOfByte)
  {
    super(Arrays.copyOfRange(paramArrayOfByte, 0, 25));
    this.zzbd = paramArrayOfByte;
  }
  
  final byte[] getBytes()
  {
    return this.zzbd;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */