package com.google.android.gms.internal.measurement;

public final class zzabf
  implements Cloneable
{
  private static final zzabg zzbzl = new zzabg();
  private int mSize;
  private boolean zzbzm = false;
  private int[] zzbzn;
  private zzabg[] zzbzo;
  
  zzabf()
  {
    this(10);
  }
  
  private zzabf(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzbzn = new int[paramInt];
    this.zzbzo = new zzabg[paramInt];
    this.mSize = 0;
  }
  
  private static int idealIntArraySize(int paramInt)
  {
    int i = paramInt << 2;
    for (paramInt = 4;; paramInt++)
    {
      int j = i;
      if (paramInt < 32)
      {
        if (i <= (1 << paramInt) - 12) {
          j = (1 << paramInt) - 12;
        }
      }
      else {
        return j / 4;
      }
    }
  }
  
  private final int zzax(int paramInt)
  {
    int i = 0;
    int j = this.mSize - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzbzn[k];
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
    boolean bool = true;
    if (paramObject == this) {}
    label62:
    label84:
    label108:
    label154:
    label160:
    label164:
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzabf))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzabf)paramObject;
        if (this.mSize != ((zzabf)paramObject).mSize)
        {
          bool = false;
        }
        else
        {
          Object localObject = this.zzbzn;
          int[] arrayOfInt = ((zzabf)paramObject).zzbzn;
          int i = this.mSize;
          int j = 0;
          if (j < i) {
            if (localObject[j] != arrayOfInt[j])
            {
              j = 0;
              if (j != 0)
              {
                localObject = this.zzbzo;
                paramObject = ((zzabf)paramObject).zzbzo;
                i = this.mSize;
                j = 0;
                if (j >= i) {
                  break label160;
                }
                if (localObject[j].equals(paramObject[j])) {
                  break label154;
                }
              }
            }
          }
          for (j = 0;; j = 1)
          {
            if (j != 0) {
              break label164;
            }
            bool = false;
            break;
            j++;
            break label62;
            j = 1;
            break label84;
            j++;
            break label108;
          }
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 17;
    for (int j = 0; j < this.mSize; j++) {
      i = (i * 31 + this.zzbzn[j]) * 31 + this.zzbzo[j].hashCode();
    }
    return i;
  }
  
  public final boolean isEmpty()
  {
    if (this.mSize == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  final int size()
  {
    return this.mSize;
  }
  
  final void zza(int paramInt, zzabg paramzzabg)
  {
    int i = zzax(paramInt);
    if (i >= 0) {
      this.zzbzo[i] = paramzzabg;
    }
    for (;;)
    {
      return;
      int j = i ^ 0xFFFFFFFF;
      if ((j < this.mSize) && (this.zzbzo[j] == zzbzl))
      {
        this.zzbzn[j] = paramInt;
        this.zzbzo[j] = paramzzabg;
      }
      else
      {
        if (this.mSize >= this.zzbzn.length)
        {
          i = idealIntArraySize(this.mSize + 1);
          int[] arrayOfInt = new int[i];
          zzabg[] arrayOfzzabg = new zzabg[i];
          System.arraycopy(this.zzbzn, 0, arrayOfInt, 0, this.zzbzn.length);
          System.arraycopy(this.zzbzo, 0, arrayOfzzabg, 0, this.zzbzo.length);
          this.zzbzn = arrayOfInt;
          this.zzbzo = arrayOfzzabg;
        }
        if (this.mSize - j != 0)
        {
          System.arraycopy(this.zzbzn, j, this.zzbzn, j + 1, this.mSize - j);
          System.arraycopy(this.zzbzo, j, this.zzbzo, j + 1, this.mSize - j);
        }
        this.zzbzn[j] = paramInt;
        this.zzbzo[j] = paramzzabg;
        this.mSize += 1;
      }
    }
  }
  
  final zzabg zzav(int paramInt)
  {
    paramInt = zzax(paramInt);
    if ((paramInt < 0) || (this.zzbzo[paramInt] == zzbzl)) {}
    for (zzabg localzzabg = null;; localzzabg = this.zzbzo[paramInt]) {
      return localzzabg;
    }
  }
  
  final zzabg zzaw(int paramInt)
  {
    return this.zzbzo[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */