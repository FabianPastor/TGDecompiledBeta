package com.google.android.gms.internal.firebase_abt;

public final class zzf
  implements Cloneable
{
  private static final zzg zzu = new zzg();
  private int mSize;
  private boolean zzv = false;
  private int[] zzw;
  private zzg[] zzx;
  
  zzf()
  {
    this(10);
  }
  
  private zzf(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzw = new int[paramInt];
    this.zzx = new zzg[paramInt];
    this.mSize = 0;
  }
  
  private static int idealIntArraySize(int paramInt)
  {
    int i = paramInt << 2;
    for (int j = 4;; j++)
    {
      paramInt = i;
      if (j < 32)
      {
        if (i <= (1 << j) - 12) {
          paramInt = (1 << j) - 12;
        }
      }
      else {
        return paramInt / 4;
      }
    }
  }
  
  private final int zzh(int paramInt)
  {
    int i = 0;
    int j = this.mSize - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzw[k];
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
    label109:
    label156:
    label162:
    label166:
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzf))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzf)paramObject;
        if (this.mSize != ((zzf)paramObject).mSize)
        {
          bool = false;
        }
        else
        {
          int[] arrayOfInt = this.zzw;
          Object localObject = ((zzf)paramObject).zzw;
          int i = this.mSize;
          int j = 0;
          if (j < i) {
            if (arrayOfInt[j] != localObject[j])
            {
              j = 0;
              if (j != 0)
              {
                localObject = this.zzx;
                paramObject = ((zzf)paramObject).zzx;
                i = this.mSize;
                j = 0;
                if (j >= i) {
                  break label162;
                }
                if (localObject[j].equals(paramObject[j])) {
                  break label156;
                }
              }
            }
          }
          for (j = 0;; j = 1)
          {
            if (j != 0) {
              break label166;
            }
            bool = false;
            break;
            j++;
            break label62;
            j = 1;
            break label84;
            j++;
            break label109;
          }
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 17;
    for (int j = 0; j < this.mSize; j++) {
      i = (i * 31 + this.zzw[j]) * 31 + this.zzx[j].hashCode();
    }
    return i;
  }
  
  final void zza(int paramInt, zzg paramzzg)
  {
    int i = zzh(paramInt);
    if (i >= 0) {
      this.zzx[i] = paramzzg;
    }
    for (;;)
    {
      return;
      i ^= 0xFFFFFFFF;
      if ((i < this.mSize) && (this.zzx[i] == zzu))
      {
        this.zzw[i] = paramInt;
        this.zzx[i] = paramzzg;
      }
      else
      {
        if (this.mSize >= this.zzw.length)
        {
          int j = idealIntArraySize(this.mSize + 1);
          int[] arrayOfInt = new int[j];
          zzg[] arrayOfzzg = new zzg[j];
          System.arraycopy(this.zzw, 0, arrayOfInt, 0, this.zzw.length);
          System.arraycopy(this.zzx, 0, arrayOfzzg, 0, this.zzx.length);
          this.zzw = arrayOfInt;
          this.zzx = arrayOfzzg;
        }
        if (this.mSize - i != 0)
        {
          System.arraycopy(this.zzw, i, this.zzw, i + 1, this.mSize - i);
          System.arraycopy(this.zzx, i, this.zzx, i + 1, this.mSize - i);
        }
        this.zzw[i] = paramInt;
        this.zzx[i] = paramzzg;
        this.mSize += 1;
      }
    }
  }
  
  final zzg zzg(int paramInt)
  {
    paramInt = zzh(paramInt);
    if ((paramInt < 0) || (this.zzx[paramInt] == zzu)) {}
    for (zzg localzzg = null;; localzzg = this.zzx[paramInt]) {
      return localzzg;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */