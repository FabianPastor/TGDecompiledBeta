package com.google.android.gms.internal;

public final class zzbyf
  implements Cloneable
{
  private static final zzbyg zzcwE = new zzbyg();
  private int mSize;
  private boolean zzcwF = false;
  private int[] zzcwG;
  private zzbyg[] zzcwH;
  
  zzbyf()
  {
    this(10);
  }
  
  zzbyf(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzcwG = new int[paramInt];
    this.zzcwH = new zzbyg[paramInt];
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
  
  private boolean zza(zzbyg[] paramArrayOfzzbyg1, zzbyg[] paramArrayOfzzbyg2, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (!paramArrayOfzzbyg1[i].equals(paramArrayOfzzbyg2[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private int zzrv(int paramInt)
  {
    int i = 0;
    int j = this.mSize - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzcwG[k];
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
      if (!(paramObject instanceof zzbyf)) {
        return false;
      }
      paramObject = (zzbyf)paramObject;
      if (size() != ((zzbyf)paramObject).size()) {
        return false;
      }
    } while ((zza(this.zzcwG, ((zzbyf)paramObject).zzcwG, this.mSize)) && (zza(this.zzcwH, ((zzbyf)paramObject).zzcwH, this.mSize)));
    return false;
  }
  
  public int hashCode()
  {
    int j = 17;
    int i = 0;
    while (i < this.mSize)
    {
      j = (j * 31 + this.zzcwG[i]) * 31 + this.zzcwH[i].hashCode();
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
  
  void zza(int paramInt, zzbyg paramzzbyg)
  {
    int i = zzrv(paramInt);
    if (i >= 0)
    {
      this.zzcwH[i] = paramzzbyg;
      return;
    }
    i ^= 0xFFFFFFFF;
    if ((i < this.mSize) && (this.zzcwH[i] == zzcwE))
    {
      this.zzcwG[i] = paramInt;
      this.zzcwH[i] = paramzzbyg;
      return;
    }
    if (this.mSize >= this.zzcwG.length)
    {
      int j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      zzbyg[] arrayOfzzbyg = new zzbyg[j];
      System.arraycopy(this.zzcwG, 0, arrayOfInt, 0, this.zzcwG.length);
      System.arraycopy(this.zzcwH, 0, arrayOfzzbyg, 0, this.zzcwH.length);
      this.zzcwG = arrayOfInt;
      this.zzcwH = arrayOfzzbyg;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.zzcwG, i, this.zzcwG, i + 1, this.mSize - i);
      System.arraycopy(this.zzcwH, i, this.zzcwH, i + 1, this.mSize - i);
    }
    this.zzcwG[i] = paramInt;
    this.zzcwH[i] = paramzzbyg;
    this.mSize += 1;
  }
  
  public final zzbyf zzafr()
  {
    int j = size();
    zzbyf localzzbyf = new zzbyf(j);
    System.arraycopy(this.zzcwG, 0, localzzbyf.zzcwG, 0, j);
    int i = 0;
    while (i < j)
    {
      if (this.zzcwH[i] != null) {
        localzzbyf.zzcwH[i] = ((zzbyg)this.zzcwH[i].clone());
      }
      i += 1;
    }
    localzzbyf.mSize = j;
    return localzzbyf;
  }
  
  zzbyg zzrt(int paramInt)
  {
    paramInt = zzrv(paramInt);
    if ((paramInt < 0) || (this.zzcwH[paramInt] == zzcwE)) {
      return null;
    }
    return this.zzcwH[paramInt];
  }
  
  zzbyg zzru(int paramInt)
  {
    return this.zzcwH[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */