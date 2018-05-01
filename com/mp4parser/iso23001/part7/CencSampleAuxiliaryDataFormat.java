package com.mp4parser.iso23001.part7;

import com.coremedia.iso.Hex;
import java.math.BigInteger;
import java.util.Arrays;

public class CencSampleAuxiliaryDataFormat
{
  public byte[] iv = new byte[0];
  public Pair[] pairs = null;
  
  public Pair createPair(int paramInt, long paramLong)
  {
    if (paramInt <= 127)
    {
      if (paramLong <= 127L) {
        return new ByteBytePair(paramInt, paramLong);
      }
      if (paramLong <= 32767L) {
        return new ByteShortPair(paramInt, paramLong);
      }
      if (paramLong <= 2147483647L) {
        return new ByteIntPair(paramInt, paramLong);
      }
      return new ByteLongPair(paramInt, paramLong);
    }
    if (paramInt <= 32767)
    {
      if (paramLong <= 127L) {
        return new ShortBytePair(paramInt, paramLong);
      }
      if (paramLong <= 32767L) {
        return new ShortShortPair(paramInt, paramLong);
      }
      if (paramLong <= 2147483647L) {
        return new ShortIntPair(paramInt, paramLong);
      }
      return new ShortLongPair(paramInt, paramLong);
    }
    if (paramLong <= 127L) {
      return new IntBytePair(paramInt, paramLong);
    }
    if (paramLong <= 32767L) {
      return new IntShortPair(paramInt, paramLong);
    }
    if (paramLong <= 2147483647L) {
      return new IntIntPair(paramInt, paramLong);
    }
    return new IntLongPair(paramInt, paramLong);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (CencSampleAuxiliaryDataFormat)paramObject;
      if (!new BigInteger(this.iv).equals(new BigInteger(((CencSampleAuxiliaryDataFormat)paramObject).iv))) {
        return false;
      }
      if (this.pairs == null) {
        break;
      }
    } while (Arrays.equals(this.pairs, ((CencSampleAuxiliaryDataFormat)paramObject).pairs));
    for (;;)
    {
      return false;
      if (((CencSampleAuxiliaryDataFormat)paramObject).pairs == null) {
        break;
      }
    }
  }
  
  public int getSize()
  {
    int j = this.iv.length;
    int i = j;
    if (this.pairs != null)
    {
      i = j;
      if (this.pairs.length > 0) {
        i = j + 2 + this.pairs.length * 6;
      }
    }
    return i;
  }
  
  public int hashCode()
  {
    int j = 0;
    if (this.iv != null) {}
    for (int i = Arrays.hashCode(this.iv);; i = 0)
    {
      if (this.pairs != null) {
        j = Arrays.hashCode(this.pairs);
      }
      return i * 31 + j;
    }
  }
  
  public String toString()
  {
    return "Entry{iv=" + Hex.encodeHex(this.iv) + ", pairs=" + Arrays.toString(this.pairs) + '}';
  }
  
  private abstract class AbstractPair
    implements CencSampleAuxiliaryDataFormat.Pair
  {
    private AbstractPair() {}
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (CencSampleAuxiliaryDataFormat.Pair)paramObject;
        if (clear() != ((CencSampleAuxiliaryDataFormat.Pair)paramObject).clear()) {
          return false;
        }
      } while (encrypted() == ((CencSampleAuxiliaryDataFormat.Pair)paramObject).encrypted());
      return false;
    }
    
    public String toString()
    {
      return "P(" + clear() + "|" + encrypted() + ")";
    }
  }
  
  private class ByteBytePair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private byte clear;
    private byte encrypted;
    
    public ByteBytePair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((byte)paramInt);
      this.encrypted = ((byte)(int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class ByteIntPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private byte clear;
    private int encrypted;
    
    public ByteIntPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((byte)paramInt);
      this.encrypted = ((int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class ByteLongPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private byte clear;
    private long encrypted;
    
    public ByteLongPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((byte)paramInt);
      this.encrypted = paramLong;
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class ByteShortPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private byte clear;
    private short encrypted;
    
    public ByteShortPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((byte)paramInt);
      this.encrypted = ((short)(int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class IntBytePair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private int clear;
    private byte encrypted;
    
    public IntBytePair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = paramInt;
      this.encrypted = ((byte)(int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class IntIntPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private int clear;
    private int encrypted;
    
    public IntIntPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = paramInt;
      this.encrypted = ((int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class IntLongPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private int clear;
    private long encrypted;
    
    public IntLongPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = paramInt;
      this.encrypted = paramLong;
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class IntShortPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private int clear;
    private short encrypted;
    
    public IntShortPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = paramInt;
      this.encrypted = ((short)(int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  public static abstract interface Pair
  {
    public abstract int clear();
    
    public abstract long encrypted();
  }
  
  private class ShortBytePair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private short clear;
    private byte encrypted;
    
    public ShortBytePair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((short)paramInt);
      this.encrypted = ((byte)(int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class ShortIntPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private short clear;
    private int encrypted;
    
    public ShortIntPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((short)paramInt);
      this.encrypted = ((int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class ShortLongPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private short clear;
    private long encrypted;
    
    public ShortLongPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((short)paramInt);
      this.encrypted = paramLong;
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
  
  private class ShortShortPair
    extends CencSampleAuxiliaryDataFormat.AbstractPair
  {
    private short clear;
    private short encrypted;
    
    public ShortShortPair(int paramInt, long paramLong)
    {
      super(null);
      this.clear = ((short)paramInt);
      this.encrypted = ((short)(int)paramLong);
    }
    
    public int clear()
    {
      return this.clear;
    }
    
    public long encrypted()
    {
      return this.encrypted;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso23001/part7/CencSampleAuxiliaryDataFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */