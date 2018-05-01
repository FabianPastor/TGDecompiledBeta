package com.google.android.gms.internal;

public final class adc
  implements Cloneable
{
  private static final add zzcsb = new add();
  private int mSize;
  private boolean zzcsc = false;
  private int[] zzcsd;
  private add[] zzcse;
  
  adc()
  {
    this(10);
  }
  
  private adc(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzcsd = new int[paramInt];
    this.zzcse = new add[paramInt];
    this.mSize = 0;
  }
  
  private static int idealIntArraySize(int paramInt)
  {
    int j = paramInt << 2;
    paramInt = 4;
    for (;;)
    {
      int i = j;
      if (paramInt < 32)
      {
        if (j <= (1 << paramInt) - 12) {
          i = (1 << paramInt) - 12;
        }
      }
      else {
        return i / 4;
      }
      paramInt += 1;
    }
  }
  
  private final int zzcz(int paramInt)
  {
    int j = this.mSize;
    int i = 0;
    j -= 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzcsd[k];
      if (m < paramInt)
      {
        i = k + 1;
      }
      else
      {
        j = k;
        if (m <= paramInt) {
          return j;
        }
        j = k - 1;
      }
    }
    j = i ^ 0xFFFFFFFF;
    return j;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    label71:
    label93:
    label131:
    label138:
    label141:
    for (;;)
    {
      return true;
      if (!(paramObject instanceof adc)) {
        return false;
      }
      paramObject = (adc)paramObject;
      if (this.mSize != ((adc)paramObject).mSize) {
        return false;
      }
      Object localObject = this.zzcsd;
      int[] arrayOfInt = ((adc)paramObject).zzcsd;
      int j = this.mSize;
      int i = 0;
      if (i < j) {
        if (localObject[i] != arrayOfInt[i])
        {
          i = 0;
          if (i != 0)
          {
            localObject = this.zzcse;
            paramObject = ((adc)paramObject).zzcse;
            j = this.mSize;
            i = 0;
            if (i >= j) {
              break label138;
            }
            if (localObject[i].equals(paramObject[i])) {
              break label131;
            }
          }
        }
      }
      for (i = 0;; i = 1)
      {
        if (i != 0) {
          break label141;
        }
        return false;
        i += 1;
        break;
        i = 1;
        break label71;
        i += 1;
        break label93;
      }
    }
  }
  
  public final int hashCode()
  {
    int j = 17;
    int i = 0;
    while (i < this.mSize)
    {
      j = (j * 31 + this.zzcsd[i]) * 31 + this.zzcse[i].hashCode();
      i += 1;
    }
    return j;
  }
  
  public final boolean isEmpty()
  {
    return this.mSize == 0;
  }
  
  final int size()
  {
    return this.mSize;
  }
  
  final void zza(int paramInt, add paramadd)
  {
    int i = zzcz(paramInt);
    if (i >= 0)
    {
      this.zzcse[i] = paramadd;
      return;
    }
    i ^= 0xFFFFFFFF;
    if ((i < this.mSize) && (this.zzcse[i] == zzcsb))
    {
      this.zzcsd[i] = paramInt;
      this.zzcse[i] = paramadd;
      return;
    }
    if (this.mSize >= this.zzcsd.length)
    {
      int j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      add[] arrayOfadd = new add[j];
      System.arraycopy(this.zzcsd, 0, arrayOfInt, 0, this.zzcsd.length);
      System.arraycopy(this.zzcse, 0, arrayOfadd, 0, this.zzcse.length);
      this.zzcsd = arrayOfInt;
      this.zzcse = arrayOfadd;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.zzcsd, i, this.zzcsd, i + 1, this.mSize - i);
      System.arraycopy(this.zzcse, i, this.zzcse, i + 1, this.mSize - i);
    }
    this.zzcsd[i] = paramInt;
    this.zzcse[i] = paramadd;
    this.mSize += 1;
  }
  
  final add zzcx(int paramInt)
  {
    paramInt = zzcz(paramInt);
    if ((paramInt < 0) || (this.zzcse[paramInt] == zzcsb)) {
      return null;
    }
    return this.zzcse[paramInt];
  }
  
  final add zzcy(int paramInt)
  {
    return this.zzcse[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */