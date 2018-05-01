package com.google.android.gms.internal;

public final class zzbxp
  implements Cloneable
{
  private static final zzbxq zzcuK = new zzbxq();
  private int mSize;
  private boolean zzcuL = false;
  private int[] zzcuM;
  private zzbxq[] zzcuN;
  
  zzbxp()
  {
    this(10);
  }
  
  zzbxp(int paramInt)
  {
    paramInt = idealIntArraySize(paramInt);
    this.zzcuM = new int[paramInt];
    this.zzcuN = new zzbxq[paramInt];
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
  
  private boolean zza(zzbxq[] paramArrayOfzzbxq1, zzbxq[] paramArrayOfzzbxq2, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (!paramArrayOfzzbxq1[i].equals(paramArrayOfzzbxq2[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private int zzrq(int paramInt)
  {
    int i = 0;
    int j = this.mSize - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = this.zzcuM[k];
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
      if (!(paramObject instanceof zzbxp)) {
        return false;
      }
      paramObject = (zzbxp)paramObject;
      if (size() != ((zzbxp)paramObject).size()) {
        return false;
      }
    } while ((zza(this.zzcuM, ((zzbxp)paramObject).zzcuM, this.mSize)) && (zza(this.zzcuN, ((zzbxp)paramObject).zzcuN, this.mSize)));
    return false;
  }
  
  public int hashCode()
  {
    int j = 17;
    int i = 0;
    while (i < this.mSize)
    {
      j = (j * 31 + this.zzcuM[i]) * 31 + this.zzcuN[i].hashCode();
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
  
  void zza(int paramInt, zzbxq paramzzbxq)
  {
    int i = zzrq(paramInt);
    if (i >= 0)
    {
      this.zzcuN[i] = paramzzbxq;
      return;
    }
    i ^= 0xFFFFFFFF;
    if ((i < this.mSize) && (this.zzcuN[i] == zzcuK))
    {
      this.zzcuM[i] = paramInt;
      this.zzcuN[i] = paramzzbxq;
      return;
    }
    if (this.mSize >= this.zzcuM.length)
    {
      int j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      zzbxq[] arrayOfzzbxq = new zzbxq[j];
      System.arraycopy(this.zzcuM, 0, arrayOfInt, 0, this.zzcuM.length);
      System.arraycopy(this.zzcuN, 0, arrayOfzzbxq, 0, this.zzcuN.length);
      this.zzcuM = arrayOfInt;
      this.zzcuN = arrayOfzzbxq;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.zzcuM, i, this.zzcuM, i + 1, this.mSize - i);
      System.arraycopy(this.zzcuN, i, this.zzcuN, i + 1, this.mSize - i);
    }
    this.zzcuM[i] = paramInt;
    this.zzcuN[i] = paramzzbxq;
    this.mSize += 1;
  }
  
  public final zzbxp zzaeJ()
  {
    int j = size();
    zzbxp localzzbxp = new zzbxp(j);
    System.arraycopy(this.zzcuM, 0, localzzbxp.zzcuM, 0, j);
    int i = 0;
    while (i < j)
    {
      if (this.zzcuN[i] != null) {
        localzzbxp.zzcuN[i] = ((zzbxq)this.zzcuN[i].clone());
      }
      i += 1;
    }
    localzzbxp.mSize = j;
    return localzzbxp;
  }
  
  zzbxq zzro(int paramInt)
  {
    paramInt = zzrq(paramInt);
    if ((paramInt < 0) || (this.zzcuN[paramInt] == zzcuK)) {
      return null;
    }
    return this.zzcuN[paramInt];
  }
  
  zzbxq zzrp(int paramInt)
  {
    return this.zzcuN[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */