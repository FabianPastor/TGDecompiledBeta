package com.googlecode.mp4parser.util;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class Matrix
{
  public static final Matrix ROTATE_0 = new Matrix(1.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
  public static final Matrix ROTATE_180 = new Matrix(-1.0D, 0.0D, 0.0D, -1.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
  public static final Matrix ROTATE_270 = new Matrix(0.0D, -1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
  public static final Matrix ROTATE_90 = new Matrix(0.0D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
  double a;
  double b;
  double c;
  double d;
  double tx;
  double ty;
  double u;
  double v;
  double w;
  
  public Matrix(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9)
  {
    this.u = paramDouble5;
    this.v = paramDouble6;
    this.w = paramDouble7;
    this.a = paramDouble1;
    this.b = paramDouble2;
    this.c = paramDouble3;
    this.d = paramDouble4;
    this.tx = paramDouble8;
    this.ty = paramDouble9;
  }
  
  public static Matrix fromByteBuffer(ByteBuffer paramByteBuffer)
  {
    return fromFileOrder(IsoTypeReader.readFixedPoint1616(paramByteBuffer), IsoTypeReader.readFixedPoint1616(paramByteBuffer), IsoTypeReader.readFixedPoint0230(paramByteBuffer), IsoTypeReader.readFixedPoint1616(paramByteBuffer), IsoTypeReader.readFixedPoint1616(paramByteBuffer), IsoTypeReader.readFixedPoint0230(paramByteBuffer), IsoTypeReader.readFixedPoint1616(paramByteBuffer), IsoTypeReader.readFixedPoint1616(paramByteBuffer), IsoTypeReader.readFixedPoint0230(paramByteBuffer));
  }
  
  public static Matrix fromFileOrder(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9)
  {
    return new Matrix(paramDouble1, paramDouble2, paramDouble4, paramDouble5, paramDouble3, paramDouble6, paramDouble9, paramDouble7, paramDouble8);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (Matrix)paramObject;
        if (Double.compare(((Matrix)paramObject).a, this.a) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).b, this.b) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).c, this.c) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).d, this.d) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).tx, this.tx) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).ty, this.ty) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).u, this.u) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).v, this.v) != 0) {
          bool = false;
        } else if (Double.compare(((Matrix)paramObject).w, this.w) != 0) {
          bool = false;
        }
      }
    }
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.a);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.b);
    IsoTypeWriter.writeFixedPoint0230(paramByteBuffer, this.u);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.c);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.d);
    IsoTypeWriter.writeFixedPoint0230(paramByteBuffer, this.v);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.tx);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.ty);
    IsoTypeWriter.writeFixedPoint0230(paramByteBuffer, this.w);
  }
  
  public int hashCode()
  {
    long l = Double.doubleToLongBits(this.u);
    int i = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.v);
    int j = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.w);
    int k = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.a);
    int m = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.b);
    int n = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.c);
    int i1 = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.d);
    int i2 = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.tx);
    int i3 = (int)(l >>> 32 ^ l);
    l = Double.doubleToLongBits(this.ty);
    return (((((((i * 31 + j) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + (int)(l >>> 32 ^ l);
  }
  
  public String toString()
  {
    String str;
    if (equals(ROTATE_0)) {
      str = "Rotate 0°";
    }
    for (;;)
    {
      return str;
      if (equals(ROTATE_90)) {
        str = "Rotate 90°";
      } else if (equals(ROTATE_180)) {
        str = "Rotate 180°";
      } else if (equals(ROTATE_270)) {
        str = "Rotate 270°";
      } else {
        str = "Matrix{u=" + this.u + ", v=" + this.v + ", w=" + this.w + ", a=" + this.a + ", b=" + this.b + ", c=" + this.c + ", d=" + this.d + ", tx=" + this.tx + ", ty=" + this.ty + '}';
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/Matrix.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */