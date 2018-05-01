package com.google.android.gms.internal;

final class zzbyx
{
  final byte[] data;
  int limit;
  int pos;
  boolean zzcya;
  boolean zzcyb;
  zzbyx zzcyc;
  zzbyx zzcyd;
  
  zzbyx()
  {
    this.data = new byte[' '];
    this.zzcyb = true;
    this.zzcya = false;
  }
  
  zzbyx(zzbyx paramzzbyx)
  {
    this(paramzzbyx.data, paramzzbyx.pos, paramzzbyx.limit);
    paramzzbyx.zzcya = true;
  }
  
  zzbyx(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.data = paramArrayOfByte;
    this.pos = paramInt1;
    this.limit = paramInt2;
    this.zzcyb = false;
    this.zzcya = true;
  }
  
  public zzbyx zza(zzbyx paramzzbyx)
  {
    paramzzbyx.zzcyd = this;
    paramzzbyx.zzcyc = this.zzcyc;
    this.zzcyc.zzcyd = paramzzbyx;
    this.zzcyc = paramzzbyx;
    return paramzzbyx;
  }
  
  public void zza(zzbyx paramzzbyx, int paramInt)
  {
    if (!paramzzbyx.zzcyb) {
      throw new IllegalArgumentException();
    }
    if (paramzzbyx.limit + paramInt > 8192)
    {
      if (paramzzbyx.zzcya) {
        throw new IllegalArgumentException();
      }
      if (paramzzbyx.limit + paramInt - paramzzbyx.pos > 8192) {
        throw new IllegalArgumentException();
      }
      System.arraycopy(paramzzbyx.data, paramzzbyx.pos, paramzzbyx.data, 0, paramzzbyx.limit - paramzzbyx.pos);
      paramzzbyx.limit -= paramzzbyx.pos;
      paramzzbyx.pos = 0;
    }
    System.arraycopy(this.data, this.pos, paramzzbyx.data, paramzzbyx.limit, paramInt);
    paramzzbyx.limit += paramInt;
    this.pos += paramInt;
  }
  
  public zzbyx zzafX()
  {
    if (this.zzcyc != this) {}
    for (zzbyx localzzbyx = this.zzcyc;; localzzbyx = null)
    {
      this.zzcyd.zzcyc = this.zzcyc;
      this.zzcyc.zzcyd = this.zzcyd;
      this.zzcyc = null;
      this.zzcyd = null;
      return localzzbyx;
    }
  }
  
  public void zzafY()
  {
    if (this.zzcyd == this) {
      throw new IllegalStateException();
    }
    if (!this.zzcyd.zzcyb) {}
    for (;;)
    {
      return;
      int j = this.limit - this.pos;
      int k = this.zzcyd.limit;
      if (this.zzcyd.zzcya) {}
      for (int i = 0; j <= i + (8192 - k); i = this.zzcyd.pos)
      {
        zza(this.zzcyd, j);
        zzafX();
        zzbyy.zzb(this);
        return;
      }
    }
  }
  
  public zzbyx zzrz(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > this.limit - this.pos)) {
      throw new IllegalArgumentException();
    }
    zzbyx localzzbyx;
    if (paramInt >= 1024) {
      localzzbyx = new zzbyx(this);
    }
    for (;;)
    {
      localzzbyx.limit = (localzzbyx.pos + paramInt);
      this.pos += paramInt;
      this.zzcyd.zza(localzzbyx);
      return localzzbyx;
      localzzbyx = zzbyy.zzafZ();
      System.arraycopy(this.data, this.pos, localzzbyx.data, 0, paramInt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */