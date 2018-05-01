package com.google.android.gms.internal;

import java.util.Map;

public final class zzn
{
  public final byte[] data;
  private int statusCode;
  private long zzA;
  public final Map<String, String> zzy;
  public final boolean zzz;
  
  public zzn(int paramInt, byte[] paramArrayOfByte, Map<String, String> paramMap, boolean paramBoolean, long paramLong)
  {
    this.statusCode = paramInt;
    this.data = paramArrayOfByte;
    this.zzy = paramMap;
    this.zzz = paramBoolean;
    this.zzA = paramLong;
  }
  
  public zzn(byte[] paramArrayOfByte, Map<String, String> paramMap)
  {
    this(200, paramArrayOfByte, paramMap, false, 0L);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */