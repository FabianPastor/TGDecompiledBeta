package com.google.android.gms.internal;

public final class zzarg
  implements Cloneable
{
  private static final zzarh bqx = new zzarh();
  private zzarh[] bqA;
  private boolean bqy = false;
  private int[] bqz;
  private int mSize;
  
  zzarg()
  {
    this(10);
  }
  
  zzarg(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.bqz = new int[paramInt];
    this.bqA = new zzarh[paramInt];
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
  
  private boolean zza(zzarh[] paramArrayOfzzarh1, zzarh[] paramArrayOfzzarh2, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (!paramArrayOfzzarh1[i].equals(paramArrayOfzzarh2[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private int zzahs(int paramInt)
  {
    int i = 0;
    int j = this.mSize - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.bqz[k];
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
  
  public final zzarg cR()
  {
    int j = size();
    zzarg localzzarg = new zzarg(j);
    System.arraycopy(this.bqz, 0, localzzarg.bqz, 0, j);
    int i = 0;
    while (i < j)
    {
      if (this.bqA[i] != null) {
        localzzarg.bqA[i] = ((zzarh)this.bqA[i].clone());
      }
      i += 1;
    }
    localzzarg.mSize = j;
    return localzzarg;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzarg)) {
        return false;
      }
      paramObject = (zzarg)paramObject;
      if (size() != ((zzarg)paramObject).size()) {
        return false;
      }
    } while ((zza(this.bqz, ((zzarg)paramObject).bqz, this.mSize)) && (zza(this.bqA, ((zzarg)paramObject).bqA, this.mSize)));
    return false;
  }
  
  public int hashCode()
  {
    int j = 17;
    int i = 0;
    while (i < this.mSize)
    {
      j = (j * 31 + this.bqz[i]) * 31 + this.bqA[i].hashCode();
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
  
  void zza(int paramInt, zzarh paramzzarh)
  {
    int i = zzahs(paramInt);
    if (i >= 0)
    {
      this.bqA[i] = paramzzarh;
      return;
    }
    i ^= 0xFFFFFFFF;
    if ((i < this.mSize) && (this.bqA[i] == bqx))
    {
      this.bqz[i] = paramInt;
      this.bqA[i] = paramzzarh;
      return;
    }
    if (this.mSize >= this.bqz.length)
    {
      int j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      zzarh[] arrayOfzzarh = new zzarh[j];
      System.arraycopy(this.bqz, 0, arrayOfInt, 0, this.bqz.length);
      System.arraycopy(this.bqA, 0, arrayOfzzarh, 0, this.bqA.length);
      this.bqz = arrayOfInt;
      this.bqA = arrayOfzzarh;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.bqz, i, this.bqz, i + 1, this.mSize - i);
      System.arraycopy(this.bqA, i, this.bqA, i + 1, this.mSize - i);
    }
    this.bqz[i] = paramInt;
    this.bqA[i] = paramzzarh;
    this.mSize += 1;
  }
  
  zzarh zzahq(int paramInt)
  {
    paramInt = zzahs(paramInt);
    if ((paramInt < 0) || (this.bqA[paramInt] == bqx)) {
      return null;
    }
    return this.bqA[paramInt];
  }
  
  zzarh zzahr(int paramInt)
  {
    return this.bqA[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzarg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */