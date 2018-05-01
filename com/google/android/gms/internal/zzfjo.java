package com.google.android.gms.internal;

public final class zzfjo
  implements Cloneable
{
  private static final zzfjp zzpne = new zzfjp();
  private int mSize;
  private boolean zzpnf = false;
  private int[] zzpng;
  private zzfjp[] zzpnh;
  
  zzfjo()
  {
    this(10);
  }
  
  private zzfjo(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzpng = new int[paramInt];
    this.zzpnh = new zzfjp[paramInt];
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
  
  private final int zzml(int paramInt)
  {
    int j = this.mSize;
    int i = 0;
    j -= 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzpng[k];
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
      if (!(paramObject instanceof zzfjo)) {
        return false;
      }
      paramObject = (zzfjo)paramObject;
      if (this.mSize != ((zzfjo)paramObject).mSize) {
        return false;
      }
      Object localObject = this.zzpng;
      int[] arrayOfInt = ((zzfjo)paramObject).zzpng;
      int j = this.mSize;
      int i = 0;
      if (i < j) {
        if (localObject[i] != arrayOfInt[i])
        {
          i = 0;
          if (i != 0)
          {
            localObject = this.zzpnh;
            paramObject = ((zzfjo)paramObject).zzpnh;
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
      j = (j * 31 + this.zzpng[i]) * 31 + this.zzpnh[i].hashCode();
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
  
  final void zza(int paramInt, zzfjp paramzzfjp)
  {
    int i = zzml(paramInt);
    if (i >= 0)
    {
      this.zzpnh[i] = paramzzfjp;
      return;
    }
    i ^= 0xFFFFFFFF;
    if ((i < this.mSize) && (this.zzpnh[i] == zzpne))
    {
      this.zzpng[i] = paramInt;
      this.zzpnh[i] = paramzzfjp;
      return;
    }
    if (this.mSize >= this.zzpng.length)
    {
      int j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      zzfjp[] arrayOfzzfjp = new zzfjp[j];
      System.arraycopy(this.zzpng, 0, arrayOfInt, 0, this.zzpng.length);
      System.arraycopy(this.zzpnh, 0, arrayOfzzfjp, 0, this.zzpnh.length);
      this.zzpng = arrayOfInt;
      this.zzpnh = arrayOfzzfjp;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.zzpng, i, this.zzpng, i + 1, this.mSize - i);
      System.arraycopy(this.zzpnh, i, this.zzpnh, i + 1, this.mSize - i);
    }
    this.zzpng[i] = paramInt;
    this.zzpnh[i] = paramzzfjp;
    this.mSize += 1;
  }
  
  final zzfjp zzmj(int paramInt)
  {
    paramInt = zzml(paramInt);
    if ((paramInt < 0) || (this.zzpnh[paramInt] == zzpne)) {
      return null;
    }
    return this.zzpnh[paramInt];
  }
  
  final zzfjp zzmk(int paramInt)
  {
    return this.zzpnh[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */