package com.google.android.gms.internal.config;

public final class zzbd
  implements Cloneable
{
  private static final zzbe zzck = new zzbe();
  private int mSize;
  private boolean zzcl = false;
  private int[] zzcm;
  private zzbe[] zzcn;
  
  zzbd()
  {
    this(10);
  }
  
  private zzbd(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzcm = new int[paramInt];
    this.zzcn = new zzbe[paramInt];
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
  
  private final int zzq(int paramInt)
  {
    int i = 0;
    int j = this.mSize - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzcm[k];
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
      if (!(paramObject instanceof zzbd))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzbd)paramObject;
        if (this.mSize != ((zzbd)paramObject).mSize)
        {
          bool = false;
        }
        else
        {
          Object localObject = this.zzcm;
          int[] arrayOfInt = ((zzbd)paramObject).zzcm;
          int i = this.mSize;
          int j = 0;
          if (j < i) {
            if (localObject[j] != arrayOfInt[j])
            {
              j = 0;
              if (j != 0)
              {
                localObject = this.zzcn;
                paramObject = ((zzbd)paramObject).zzcn;
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
      i = (i * 31 + this.zzcm[j]) * 31 + this.zzcn[j].hashCode();
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
  
  final void zza(int paramInt, zzbe paramzzbe)
  {
    int i = zzq(paramInt);
    if (i >= 0) {
      this.zzcn[i] = paramzzbe;
    }
    for (;;)
    {
      return;
      i ^= 0xFFFFFFFF;
      if ((i < this.mSize) && (this.zzcn[i] == zzck))
      {
        this.zzcm[i] = paramInt;
        this.zzcn[i] = paramzzbe;
      }
      else
      {
        if (this.mSize >= this.zzcm.length)
        {
          int j = idealIntArraySize(this.mSize + 1);
          int[] arrayOfInt = new int[j];
          zzbe[] arrayOfzzbe = new zzbe[j];
          System.arraycopy(this.zzcm, 0, arrayOfInt, 0, this.zzcm.length);
          System.arraycopy(this.zzcn, 0, arrayOfzzbe, 0, this.zzcn.length);
          this.zzcm = arrayOfInt;
          this.zzcn = arrayOfzzbe;
        }
        if (this.mSize - i != 0)
        {
          System.arraycopy(this.zzcm, i, this.zzcm, i + 1, this.mSize - i);
          System.arraycopy(this.zzcn, i, this.zzcn, i + 1, this.mSize - i);
        }
        this.zzcm[i] = paramInt;
        this.zzcn[i] = paramzzbe;
        this.mSize += 1;
      }
    }
  }
  
  final zzbe zzo(int paramInt)
  {
    paramInt = zzq(paramInt);
    if ((paramInt < 0) || (this.zzcn[paramInt] == zzck)) {}
    for (zzbe localzzbe = null;; localzzbe = this.zzcn[paramInt]) {
      return localzzbe;
    }
  }
  
  final zzbe zzp(int paramInt)
  {
    return this.zzcn[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzbd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */