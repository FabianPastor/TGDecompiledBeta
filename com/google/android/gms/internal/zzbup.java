package com.google.android.gms.internal;

public final class zzbup
  implements Cloneable
{
  private static final zzbuq zzcrZ = new zzbuq();
  private int mSize;
  private boolean zzcsa = false;
  private int[] zzcsb;
  private zzbuq[] zzcsc;
  
  zzbup()
  {
    this(10);
  }
  
  zzbup(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzcsb = new int[paramInt];
    this.zzcsc = new zzbuq[paramInt];
    this.mSize = 0;
  }
  
  private int idealByteArraySize(int paramInt)
  {
    int i = 4;
    for (;;)
    {
      int j = paramInt;
      if (i < 32)
      {
        if (paramInt <= (1 << i) - 12) {
          j = (1 << i) - 12;
        }
      }
      else {
        return j;
      }
      i += 1;
    }
  }
  
  private int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  private boolean zza(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (paramArrayOfInt1[i] != paramArrayOfInt2[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean zza(zzbuq[] paramArrayOfzzbuq1, zzbuq[] paramArrayOfzzbuq2, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (!paramArrayOfzzbuq1[i].equals(paramArrayOfzzbuq2[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private int zzqz(int paramInt)
  {
    int i = 0;
    int j = this.mSize - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzcsb[k];
      if (m < paramInt) {
        i = k + 1;
      } else if (m > paramInt) {
        j = k - 1;
      } else {
        return k;
      }
    }
    return i ^ 0xFFFFFFFF;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbup)) {
        return false;
      }
      paramObject = (zzbup)paramObject;
      if (size() != ((zzbup)paramObject).size()) {
        return false;
      }
    } while ((zza(this.zzcsb, ((zzbup)paramObject).zzcsb, this.mSize)) && (zza(this.zzcsc, ((zzbup)paramObject).zzcsc, this.mSize)));
    return false;
  }
  
  public int hashCode()
  {
    int j = 17;
    int i = 0;
    while (i < this.mSize)
    {
      j = (j * 31 + this.zzcsb[i]) * 31 + this.zzcsc[i].hashCode();
      i += 1;
    }
    return j;
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  int size()
  {
    return this.mSize;
  }
  
  void zza(int paramInt, zzbuq paramzzbuq)
  {
    int i = zzqz(paramInt);
    if (i >= 0)
    {
      this.zzcsc[i] = paramzzbuq;
      return;
    }
    i ^= 0xFFFFFFFF;
    if ((i < this.mSize) && (this.zzcsc[i] == zzcrZ))
    {
      this.zzcsb[i] = paramInt;
      this.zzcsc[i] = paramzzbuq;
      return;
    }
    if (this.mSize >= this.zzcsb.length)
    {
      int j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      zzbuq[] arrayOfzzbuq = new zzbuq[j];
      System.arraycopy(this.zzcsb, 0, arrayOfInt, 0, this.zzcsb.length);
      System.arraycopy(this.zzcsc, 0, arrayOfzzbuq, 0, this.zzcsc.length);
      this.zzcsb = arrayOfInt;
      this.zzcsc = arrayOfzzbuq;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.zzcsb, i, this.zzcsb, i + 1, this.mSize - i);
      System.arraycopy(this.zzcsc, i, this.zzcsc, i + 1, this.mSize - i);
    }
    this.zzcsb[i] = paramInt;
    this.zzcsc[i] = paramzzbuq;
    this.mSize += 1;
  }
  
  public final zzbup zzacP()
  {
    int j = size();
    zzbup localzzbup = new zzbup(j);
    System.arraycopy(this.zzcsb, 0, localzzbup.zzcsb, 0, j);
    int i = 0;
    while (i < j)
    {
      if (this.zzcsc[i] != null) {
        localzzbup.zzcsc[i] = ((zzbuq)this.zzcsc[i].clone());
      }
      i += 1;
    }
    localzzbup.mSize = j;
    return localzzbup;
  }
  
  zzbuq zzqx(int paramInt)
  {
    paramInt = zzqz(paramInt);
    if ((paramInt < 0) || (this.zzcsc[paramInt] == zzcrZ)) {
      return null;
    }
    return this.zzcsc[paramInt];
  }
  
  zzbuq zzqy(int paramInt)
  {
    return this.zzcsc[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */