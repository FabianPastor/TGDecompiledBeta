package com.google.android.gms.internal;

public final class zzbyr
  implements zzbys, zzbyt, Cloneable
{
  private static final byte[] zzcxT = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  long zzaA;
  zzbyx zzcxU;
  
  public void close() {}
  
  public boolean equals(Object paramObject)
  {
    long l1 = 0L;
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof zzbyr)) {
      return false;
    }
    paramObject = (zzbyr)paramObject;
    if (this.zzaA != ((zzbyr)paramObject).zzaA) {
      return false;
    }
    if (this.zzaA == 0L) {
      return true;
    }
    Object localObject2 = this.zzcxU;
    paramObject = ((zzbyr)paramObject).zzcxU;
    int j = ((zzbyx)localObject2).pos;
    int i = ((zzbyx)paramObject).pos;
    while (l1 < this.zzaA)
    {
      long l2 = Math.min(((zzbyx)localObject2).limit - j, ((zzbyx)paramObject).limit - i);
      int k = 0;
      while (k < l2)
      {
        if (localObject2.data[j] != paramObject.data[i]) {
          return false;
        }
        k += 1;
        i += 1;
        j += 1;
      }
      k = j;
      Object localObject1 = localObject2;
      if (j == ((zzbyx)localObject2).limit)
      {
        localObject1 = ((zzbyx)localObject2).zzcyc;
        k = ((zzbyx)localObject1).pos;
      }
      j = i;
      localObject2 = paramObject;
      if (i == ((zzbyx)paramObject).limit)
      {
        localObject2 = ((zzbyx)paramObject).zzcyc;
        j = ((zzbyx)localObject2).pos;
      }
      l1 += l2;
      i = j;
      j = k;
      paramObject = localObject2;
      localObject2 = localObject1;
    }
    return true;
  }
  
  public void flush() {}
  
  public int hashCode()
  {
    Object localObject = this.zzcxU;
    if (localObject == null) {
      return 0;
    }
    int j = 1;
    int i;
    zzbyx localzzbyx;
    do
    {
      int k = ((zzbyx)localObject).pos;
      int m = ((zzbyx)localObject).limit;
      for (i = j; k < m; i = j + i * 31)
      {
        j = localObject.data[k];
        k += 1;
      }
      localzzbyx = ((zzbyx)localObject).zzcyc;
      j = i;
      localObject = localzzbyx;
    } while (localzzbyx != this.zzcxU);
    return i;
  }
  
  public long read(zzbyr paramzzbyr, long paramLong)
  {
    if (paramzzbyr == null) {
      throw new IllegalArgumentException("sink == null");
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("byteCount < 0: " + paramLong);
    }
    if (this.zzaA == 0L) {
      return -1L;
    }
    long l = paramLong;
    if (paramLong > this.zzaA) {
      l = this.zzaA;
    }
    paramzzbyr.write(this, l);
    return l;
  }
  
  public String toString()
  {
    return zzafU().toString();
  }
  
  public void write(zzbyr paramzzbyr, long paramLong)
  {
    if (paramzzbyr == null) {
      throw new IllegalArgumentException("source == null");
    }
    if (paramzzbyr == this) {
      throw new IllegalArgumentException("source == this");
    }
    zzbzd.checkOffsetAndCount(paramzzbyr.zzaA, 0L, paramLong);
    if (paramLong > 0L)
    {
      if (paramLong >= paramzzbyr.zzcxU.limit - paramzzbyr.zzcxU.pos) {
        break label189;
      }
      if (this.zzcxU == null) {
        break label160;
      }
      localzzbyx1 = this.zzcxU.zzcyd;
      if ((localzzbyx1 == null) || (!localzzbyx1.zzcyb)) {
        break label176;
      }
      l = localzzbyx1.limit;
      if (!localzzbyx1.zzcya) {
        break label166;
      }
    }
    label160:
    label166:
    for (int i = 0;; i = localzzbyx1.pos)
    {
      if (l + paramLong - i > 8192L) {
        break label176;
      }
      paramzzbyr.zzcxU.zza(localzzbyx1, (int)paramLong);
      paramzzbyr.zzaA -= paramLong;
      this.zzaA += paramLong;
      return;
      localzzbyx1 = null;
      break;
    }
    label176:
    paramzzbyr.zzcxU = paramzzbyr.zzcxU.zzrz((int)paramLong);
    label189:
    zzbyx localzzbyx1 = paramzzbyr.zzcxU;
    long l = localzzbyx1.limit - localzzbyx1.pos;
    paramzzbyr.zzcxU = localzzbyx1.zzafX();
    if (this.zzcxU == null)
    {
      this.zzcxU = localzzbyx1;
      localzzbyx1 = this.zzcxU;
      zzbyx localzzbyx2 = this.zzcxU;
      zzbyx localzzbyx3 = this.zzcxU;
      localzzbyx2.zzcyd = localzzbyx3;
      localzzbyx1.zzcyc = localzzbyx3;
    }
    for (;;)
    {
      paramzzbyr.zzaA -= l;
      this.zzaA += l;
      paramLong -= l;
      break;
      this.zzcxU.zzcyd.zza(localzzbyx1).zzafY();
    }
  }
  
  public zzbyr zzafT()
  {
    zzbyr localzzbyr = new zzbyr();
    if (this.zzaA == 0L) {
      return localzzbyr;
    }
    localzzbyr.zzcxU = new zzbyx(this.zzcxU);
    zzbyx localzzbyx1 = localzzbyr.zzcxU;
    zzbyx localzzbyx2 = localzzbyr.zzcxU;
    zzbyx localzzbyx3 = localzzbyr.zzcxU;
    localzzbyx2.zzcyd = localzzbyx3;
    localzzbyx1.zzcyc = localzzbyx3;
    for (localzzbyx1 = this.zzcxU.zzcyc; localzzbyx1 != this.zzcxU; localzzbyx1 = localzzbyx1.zzcyc) {
      localzzbyr.zzcxU.zzcyd.zza(new zzbyx(localzzbyx1));
    }
    localzzbyr.zzaA = this.zzaA;
    return localzzbyr;
  }
  
  public zzbyu zzafU()
  {
    if (this.zzaA > 2147483647L) {
      throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.zzaA);
    }
    return zzry((int)this.zzaA);
  }
  
  public zzbyu zzry(int paramInt)
  {
    if (paramInt == 0) {
      return zzbyu.zzcxW;
    }
    return new zzbyz(this, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */