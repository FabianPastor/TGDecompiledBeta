package com.google.android.gms.internal;

public final class adl
  implements Cloneable
{
  private static final adm zzcsq = new adm();
  private int mSize;
  private boolean zzcsr = false;
  private int[] zzcss;
  private adm[] zzcst;
  
  adl()
  {
    this(10);
  }
  
  private adl(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzcss = new int[paramInt];
    this.zzcst = new adm[paramInt];
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
      int m = this.zzcss[k];
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
      if (!(paramObject instanceof adl)) {
        return false;
      }
      paramObject = (adl)paramObject;
      if (this.mSize != ((adl)paramObject).mSize) {
        return false;
      }
      Object localObject = this.zzcss;
      int[] arrayOfInt = ((adl)paramObject).zzcss;
      int j = this.mSize;
      int i = 0;
      if (i < j) {
        if (localObject[i] != arrayOfInt[i])
        {
          i = 0;
          if (i != 0)
          {
            localObject = this.zzcst;
            paramObject = ((adl)paramObject).zzcst;
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
      j = (j * 31 + this.zzcss[i]) * 31 + this.zzcst[i].hashCode();
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
  
  final void zza(int paramInt, adm paramadm)
  {
    int i = zzcz(paramInt);
    if (i >= 0)
    {
      this.zzcst[i] = paramadm;
      return;
    }
    i ^= 0xFFFFFFFF;
    if ((i < this.mSize) && (this.zzcst[i] == zzcsq))
    {
      this.zzcss[i] = paramInt;
      this.zzcst[i] = paramadm;
      return;
    }
    if (this.mSize >= this.zzcss.length)
    {
      int j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      adm[] arrayOfadm = new adm[j];
      System.arraycopy(this.zzcss, 0, arrayOfInt, 0, this.zzcss.length);
      System.arraycopy(this.zzcst, 0, arrayOfadm, 0, this.zzcst.length);
      this.zzcss = arrayOfInt;
      this.zzcst = arrayOfadm;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.zzcss, i, this.zzcss, i + 1, this.mSize - i);
      System.arraycopy(this.zzcst, i, this.zzcst, i + 1, this.mSize - i);
    }
    this.zzcss[i] = paramInt;
    this.zzcst[i] = paramadm;
    this.mSize += 1;
  }
  
  final adm zzcx(int paramInt)
  {
    paramInt = zzcz(paramInt);
    if ((paramInt < 0) || (this.zzcst[paramInt] == zzcsq)) {
      return null;
    }
    return this.zzcst[paramInt];
  }
  
  final adm zzcy(int paramInt)
  {
    return this.zzcst[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */