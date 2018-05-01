package org.telegram.messenger.audioinfo.m4a;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigDecimal;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class MP4Atom
  extends MP4Box<RangeInputStream>
{
  public MP4Atom(RangeInputStream paramRangeInputStream, MP4Box<?> paramMP4Box, String paramString)
  {
    super(paramRangeInputStream, paramMP4Box, paramString);
  }
  
  private StringBuffer appendPath(StringBuffer paramStringBuffer, MP4Box<?> paramMP4Box)
  {
    if (paramMP4Box.getParent() != null)
    {
      appendPath(paramStringBuffer, paramMP4Box.getParent());
      paramStringBuffer.append("/");
    }
    return paramStringBuffer.append(paramMP4Box.getType());
  }
  
  public long getLength()
  {
    long l = ((RangeInputStream)getInput()).getPosition();
    return ((RangeInputStream)getInput()).getRemainingLength() + l;
  }
  
  public long getOffset()
  {
    return getParent().getPosition() - getPosition();
  }
  
  public String getPath()
  {
    return appendPath(new StringBuffer(), this).toString();
  }
  
  public long getRemaining()
  {
    return ((RangeInputStream)getInput()).getRemainingLength();
  }
  
  public boolean hasMoreChildren()
  {
    long l;
    if (getChild() != null)
    {
      l = getChild().getRemaining();
      if (l >= getRemaining()) {
        break label33;
      }
    }
    label33:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      l = 0L;
      break;
    }
  }
  
  public MP4Atom nextChildUpTo(String paramString)
    throws IOException
  {
    while (getRemaining() > 0L)
    {
      MP4Atom localMP4Atom = nextChild();
      if (localMP4Atom.getType().matches(paramString)) {
        return localMP4Atom;
      }
    }
    throw new IOException("atom type mismatch, not found: " + paramString);
  }
  
  public boolean readBoolean()
    throws IOException
  {
    return this.data.readBoolean();
  }
  
  public byte readByte()
    throws IOException
  {
    return this.data.readByte();
  }
  
  public byte[] readBytes()
    throws IOException
  {
    return readBytes((int)getRemaining());
  }
  
  public byte[] readBytes(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    this.data.readFully(arrayOfByte);
    return arrayOfByte;
  }
  
  public int readInt()
    throws IOException
  {
    return this.data.readInt();
  }
  
  public BigDecimal readIntegerFixedPoint()
    throws IOException
  {
    int i = this.data.readShort();
    int j = this.data.readUnsignedShort();
    return new BigDecimal(String.valueOf(i) + "" + String.valueOf(j));
  }
  
  public long readLong()
    throws IOException
  {
    return this.data.readLong();
  }
  
  public short readShort()
    throws IOException
  {
    return this.data.readShort();
  }
  
  public BigDecimal readShortFixedPoint()
    throws IOException
  {
    int i = this.data.readByte();
    int j = this.data.readUnsignedByte();
    return new BigDecimal(String.valueOf(i) + "" + String.valueOf(j));
  }
  
  public String readString(int paramInt, String paramString)
    throws IOException
  {
    paramString = new String(readBytes(paramInt), paramString);
    paramInt = paramString.indexOf(0);
    if (paramInt < 0) {}
    for (;;)
    {
      return paramString;
      paramString = paramString.substring(0, paramInt);
    }
  }
  
  public String readString(String paramString)
    throws IOException
  {
    return readString((int)getRemaining(), paramString);
  }
  
  public void skip()
    throws IOException
  {
    while (getRemaining() > 0L) {
      if (((RangeInputStream)getInput()).skip(getRemaining()) == 0L) {
        throw new EOFException("Cannot skip atom");
      }
    }
  }
  
  public void skip(int paramInt)
    throws IOException
  {
    int i = 0;
    while (i < paramInt)
    {
      int j = this.data.skipBytes(paramInt - i);
      if (j > 0) {
        i += j;
      } else {
        throw new EOFException();
      }
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    appendPath(localStringBuffer, this);
    localStringBuffer.append("[off=");
    localStringBuffer.append(getOffset());
    localStringBuffer.append(",pos=");
    localStringBuffer.append(getPosition());
    localStringBuffer.append(",len=");
    localStringBuffer.append(getLength());
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/m4a/MP4Atom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */