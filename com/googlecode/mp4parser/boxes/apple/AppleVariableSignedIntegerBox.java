package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public abstract class AppleVariableSignedIntegerBox
  extends AppleDataBox
{
  int intLength = 1;
  long value;
  
  static {}
  
  protected AppleVariableSignedIntegerBox(String paramString)
  {
    super(paramString, 15);
  }
  
  protected int getDataLength()
  {
    return this.intLength;
  }
  
  public int getIntLength()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.intLength;
  }
  
  public long getValue()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (!isParsed()) {
      parseDetails();
    }
    return this.value;
  }
  
  protected void parseData(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.remaining();
    this.value = IsoTypeReaderVariable.read(paramByteBuffer, i);
    this.intLength = i;
  }
  
  public void setIntLength(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.intLength = paramInt;
  }
  
  public void setValue(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if ((paramLong <= 127L) && (paramLong > -128L)) {
      this.intLength = 1;
    }
    for (;;)
    {
      this.value = paramLong;
      return;
      if ((paramLong <= 32767L) && (paramLong > -32768L) && (this.intLength < 2)) {
        this.intLength = 2;
      } else if ((paramLong <= 8388607L) && (paramLong > -8388608L) && (this.intLength < 3)) {
        this.intLength = 3;
      } else {
        this.intLength = 4;
      }
    }
  }
  
  protected byte[] writeData()
  {
    int i = getDataLength();
    ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[i]);
    IsoTypeWriterVariable.write(this.value, localByteBuffer, i);
    return localByteBuffer.array();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/AppleVariableSignedIntegerBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */