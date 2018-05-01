package com.google.android.gms.internal;

import java.io.Serializable;
import java.util.Arrays;

public class zzbyu
  implements Serializable, Comparable<zzbyu>
{
  static final char[] zzcxV = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  public static final zzbyu zzcxW = zzam(new byte[0]);
  final byte[] data;
  transient int zzcug;
  transient String zzcxX;
  
  zzbyu(byte[] paramArrayOfByte)
  {
    this.data = paramArrayOfByte;
  }
  
  static int zzH(String paramString, int paramInt)
  {
    int i = 0;
    int k = paramString.length();
    int j = 0;
    while (i < k)
    {
      if (j == paramInt) {
        return i;
      }
      int m = paramString.codePointAt(i);
      if (((Character.isISOControl(m)) && (m != 10) && (m != 13)) || (m == 65533)) {
        return -1;
      }
      j += 1;
      i += Character.charCount(m);
    }
    return paramString.length();
  }
  
  public static zzbyu zzam(byte... paramVarArgs)
  {
    if (paramVarArgs == null) {
      throw new IllegalArgumentException("data == null");
    }
    return new zzbyu((byte[])paramVarArgs.clone());
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (((paramObject instanceof zzbyu)) && (((zzbyu)paramObject).size() == this.data.length) && (((zzbyu)paramObject).zza(0, this.data, 0, this.data.length))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public byte getByte(int paramInt)
  {
    return this.data[paramInt];
  }
  
  public int hashCode()
  {
    int i = this.zzcug;
    if (i != 0) {
      return i;
    }
    i = Arrays.hashCode(this.data);
    this.zzcug = i;
    return i;
  }
  
  public int size()
  {
    return this.data.length;
  }
  
  public byte[] toByteArray()
  {
    return (byte[])this.data.clone();
  }
  
  public String toString()
  {
    if (this.data.length == 0) {
      return "[size=0]";
    }
    String str1 = zzafV();
    int i = zzH(str1, 64);
    if (i == -1)
    {
      if (this.data.length <= 64) {
        return "[hex=" + zzafW() + "]";
      }
      return "[size=" + this.data.length + " hex=" + zzP(0, 64).zzafW() + "…]";
    }
    String str2 = str1.substring(0, i).replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r");
    if (i < str1.length()) {
      return "[size=" + this.data.length + " text=" + str2 + "…]";
    }
    return "[text=" + str2 + "]";
  }
  
  public zzbyu zzP(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("beginIndex < 0");
    }
    if (paramInt2 > this.data.length) {
      throw new IllegalArgumentException("endIndex > length(" + this.data.length + ")");
    }
    int i = paramInt2 - paramInt1;
    if (i < 0) {
      throw new IllegalArgumentException("endIndex < beginIndex");
    }
    if ((paramInt1 == 0) && (paramInt2 == this.data.length)) {
      return this;
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.data, paramInt1, arrayOfByte, 0, i);
    return new zzbyu(arrayOfByte);
  }
  
  public int zza(zzbyu paramzzbyu)
  {
    int j = size();
    int k = paramzzbyu.size();
    int m = Math.min(j, k);
    int i = 0;
    for (;;)
    {
      if (i < m)
      {
        int n = getByte(i) & 0xFF;
        int i1 = paramzzbyu.getByte(i) & 0xFF;
        if (n == i1) {
          i += 1;
        } else {
          if (n >= i1) {
            break;
          }
        }
      }
    }
    do
    {
      return -1;
      return 1;
      if (j == k) {
        return 0;
      }
    } while (j < k);
    return 1;
  }
  
  public boolean zza(int paramInt1, zzbyu paramzzbyu, int paramInt2, int paramInt3)
  {
    return paramzzbyu.zza(paramInt2, this.data, paramInt1, paramInt3);
  }
  
  public boolean zza(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    return (paramInt1 >= 0) && (paramInt1 <= this.data.length - paramInt3) && (paramInt2 >= 0) && (paramInt2 <= paramArrayOfByte.length - paramInt3) && (zzbzd.zza(this.data, paramInt1, paramArrayOfByte, paramInt2, paramInt3));
  }
  
  public String zzafV()
  {
    String str = this.zzcxX;
    if (str != null) {
      return str;
    }
    str = new String(this.data, zzbzd.UTF_8);
    this.zzcxX = str;
    return str;
  }
  
  public String zzafW()
  {
    int i = 0;
    char[] arrayOfChar = new char[this.data.length * 2];
    byte[] arrayOfByte = this.data;
    int k = arrayOfByte.length;
    int j = 0;
    while (i < k)
    {
      int m = arrayOfByte[i];
      int n = j + 1;
      arrayOfChar[j] = zzcxV[(m >> 4 & 0xF)];
      j = n + 1;
      arrayOfChar[n] = zzcxV[(m & 0xF)];
      i += 1;
    }
    return new String(arrayOfChar);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */