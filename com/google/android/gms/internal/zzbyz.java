package com.google.android.gms.internal;

import java.util.Arrays;

final class zzbyz
  extends zzbyu
{
  final transient byte[][] zzcyf;
  final transient int[] zzcyg;
  
  zzbyz(zzbyr paramzzbyr, int paramInt)
  {
    super(null);
    zzbzd.checkOffsetAndCount(paramzzbyr.zzaA, 0L, paramInt);
    zzbyx localzzbyx = paramzzbyr.zzcxU;
    int i = 0;
    int j = 0;
    while (j < paramInt)
    {
      if (localzzbyx.limit == localzzbyx.pos) {
        throw new AssertionError("s.limit == s.pos");
      }
      j += localzzbyx.limit - localzzbyx.pos;
      i += 1;
      localzzbyx = localzzbyx.zzcyc;
    }
    this.zzcyf = new byte[i][];
    this.zzcyg = new int[i * 2];
    paramzzbyr = paramzzbyr.zzcxU;
    j = 0;
    i = k;
    while (i < paramInt)
    {
      this.zzcyf[j] = paramzzbyr.data;
      k = paramzzbyr.limit - paramzzbyr.pos + i;
      i = k;
      if (k > paramInt) {
        i = paramInt;
      }
      this.zzcyg[j] = i;
      this.zzcyg[(this.zzcyf.length + j)] = paramzzbyr.pos;
      paramzzbyr.zzcya = true;
      j += 1;
      paramzzbyr = paramzzbyr.zzcyc;
    }
  }
  
  private zzbyu zzaga()
  {
    return new zzbyu(toByteArray());
  }
  
  private int zzrA(int paramInt)
  {
    paramInt = Arrays.binarySearch(this.zzcyg, 0, this.zzcyf.length, paramInt + 1);
    if (paramInt >= 0) {
      return paramInt;
    }
    return paramInt ^ 0xFFFFFFFF;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (((paramObject instanceof zzbyu)) && (((zzbyu)paramObject).size() == size()) && (zza(0, (zzbyu)paramObject, 0, size()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public byte getByte(int paramInt)
  {
    zzbzd.checkOffsetAndCount(this.zzcyg[(this.zzcyf.length - 1)], paramInt, 1L);
    int j = zzrA(paramInt);
    if (j == 0) {}
    for (int i = 0;; i = this.zzcyg[(j - 1)])
    {
      int k = this.zzcyg[(this.zzcyf.length + j)];
      return this.zzcyf[j][(paramInt - i + k)];
    }
  }
  
  public int hashCode()
  {
    int i = this.zzcug;
    if (i != 0) {
      return i;
    }
    i = 1;
    int i2 = this.zzcyf.length;
    int j = 0;
    int n;
    for (int k = 0; j < i2; k = n)
    {
      byte[] arrayOfByte = this.zzcyf[j];
      int i1 = this.zzcyg[(i2 + j)];
      n = this.zzcyg[j];
      int m = i1;
      while (m < n - k + i1)
      {
        i = i * 31 + arrayOfByte[m];
        m += 1;
      }
      j += 1;
    }
    this.zzcug = i;
    return i;
  }
  
  public int size()
  {
    return this.zzcyg[(this.zzcyf.length - 1)];
  }
  
  public byte[] toByteArray()
  {
    int i = 0;
    byte[] arrayOfByte = new byte[this.zzcyg[(this.zzcyf.length - 1)]];
    int m = this.zzcyf.length;
    int k;
    for (int j = 0; i < m; j = k)
    {
      int n = this.zzcyg[(m + i)];
      k = this.zzcyg[i];
      System.arraycopy(this.zzcyf[i], n, arrayOfByte, j, k - j);
      i += 1;
    }
    return arrayOfByte;
  }
  
  public String toString()
  {
    return zzaga().toString();
  }
  
  public zzbyu zzP(int paramInt1, int paramInt2)
  {
    return zzaga().zzP(paramInt1, paramInt2);
  }
  
  public boolean zza(int paramInt1, zzbyu paramzzbyu, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt1 > size() - paramInt3)) {
      return false;
    }
    int j = zzrA(paramInt1);
    int i = paramInt1;
    paramInt1 = j;
    label30:
    if (paramInt3 > 0)
    {
      if (paramInt1 == 0) {}
      for (j = 0;; j = this.zzcyg[(paramInt1 - 1)])
      {
        int k = Math.min(paramInt3, this.zzcyg[paramInt1] - j + j - i);
        int m = this.zzcyg[(this.zzcyf.length + paramInt1)];
        if (!paramzzbyu.zza(paramInt2, this.zzcyf[paramInt1], i - j + m, k)) {
          break;
        }
        i += k;
        paramInt2 += k;
        paramInt3 -= k;
        paramInt1 += 1;
        break label30;
      }
    }
    return true;
  }
  
  public boolean zza(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt1 > size() - paramInt3) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt3)) {
      return false;
    }
    int j = zzrA(paramInt1);
    int i = paramInt1;
    paramInt1 = j;
    label43:
    if (paramInt3 > 0)
    {
      if (paramInt1 == 0) {}
      for (j = 0;; j = this.zzcyg[(paramInt1 - 1)])
      {
        int k = Math.min(paramInt3, this.zzcyg[paramInt1] - j + j - i);
        int m = this.zzcyg[(this.zzcyf.length + paramInt1)];
        if (!zzbzd.zza(this.zzcyf[paramInt1], i - j + m, paramArrayOfByte, paramInt2, k)) {
          break;
        }
        i += k;
        paramInt2 += k;
        paramInt3 -= k;
        paramInt1 += 1;
        break label43;
      }
    }
    return true;
  }
  
  public String zzafV()
  {
    return zzaga().zzafV();
  }
  
  public String zzafW()
  {
    return zzaga().zzafW();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */