package org.telegram.tgnet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import org.telegram.messenger.FileLog;

public class SerializedData
  extends AbstractSerializedData
{
  private DataInputStream in;
  private ByteArrayInputStream inbuf;
  protected boolean isOut = true;
  private boolean justCalc = false;
  private int len;
  private DataOutputStream out;
  private ByteArrayOutputStream outbuf;
  
  public SerializedData()
  {
    this.outbuf = new ByteArrayOutputStream();
    this.out = new DataOutputStream(this.outbuf);
  }
  
  public SerializedData(int paramInt)
  {
    this.outbuf = new ByteArrayOutputStream(paramInt);
    this.out = new DataOutputStream(this.outbuf);
  }
  
  public SerializedData(File paramFile)
    throws Exception
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    paramFile = new byte[(int)paramFile.length()];
    new DataInputStream(localFileInputStream).readFully(paramFile);
    localFileInputStream.close();
    this.isOut = false;
    this.inbuf = new ByteArrayInputStream(paramFile);
    this.in = new DataInputStream(this.inbuf);
  }
  
  public SerializedData(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.outbuf = new ByteArrayOutputStream();
      this.out = new DataOutputStream(this.outbuf);
    }
    this.justCalc = paramBoolean;
    this.len = 0;
  }
  
  public SerializedData(byte[] paramArrayOfByte)
  {
    this.isOut = false;
    this.inbuf = new ByteArrayInputStream(paramArrayOfByte);
    this.in = new DataInputStream(this.inbuf);
    this.len = 0;
  }
  
  private void writeInt32(int paramInt, DataOutputStream paramDataOutputStream)
  {
    int i = 0;
    for (;;)
    {
      if (i < 4) {
        try
        {
          paramDataOutputStream.write(paramInt >> i * 8);
          i += 1;
        }
        catch (Exception paramDataOutputStream)
        {
          FileLog.e("tmessages", "write int32 error");
        }
      }
    }
  }
  
  private void writeInt64(long paramLong, DataOutputStream paramDataOutputStream)
  {
    int i = 0;
    for (;;)
    {
      if (i < 8)
      {
        int j = (int)(paramLong >> i * 8);
        try
        {
          paramDataOutputStream.write(j);
          i += 1;
        }
        catch (Exception paramDataOutputStream)
        {
          FileLog.e("tmessages", "write int64 error");
        }
      }
    }
  }
  
  public void cleanup()
  {
    try
    {
      if (this.inbuf != null)
      {
        this.inbuf.close();
        this.inbuf = null;
      }
    }
    catch (Exception localException2)
    {
      try
      {
        if (this.in != null)
        {
          this.in.close();
          this.in = null;
        }
      }
      catch (Exception localException2)
      {
        try
        {
          for (;;)
          {
            if (this.outbuf != null)
            {
              this.outbuf.close();
              this.outbuf = null;
            }
            try
            {
              if (this.out != null)
              {
                this.out.close();
                this.out = null;
              }
              return;
            }
            catch (Exception localException4)
            {
              FileLog.e("tmessages", localException4);
            }
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
            continue;
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
          }
        }
        catch (Exception localException3)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException3);
          }
        }
      }
    }
  }
  
  public int getPosition()
  {
    return this.len;
  }
  
  public int length()
  {
    if (!this.justCalc)
    {
      if (this.isOut) {
        return this.outbuf.size();
      }
      return this.inbuf.available();
    }
    return this.len;
  }
  
  public boolean readBool(boolean paramBoolean)
  {
    boolean bool = false;
    int i = readInt32(paramBoolean);
    if (i == -1720552011) {
      bool = true;
    }
    while (i == -1132882121) {
      return bool;
    }
    if (paramBoolean) {
      throw new RuntimeException("Not bool value!");
    }
    FileLog.e("tmessages", "Not bool value!");
    return false;
  }
  
  /* Error */
  public byte[] readByteArray(boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   6: invokevirtual 133	java/io/DataInputStream:read	()I
    //   9: istore 4
    //   11: aload_0
    //   12: aload_0
    //   13: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   16: iconst_1
    //   17: iadd
    //   18: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   21: iload 4
    //   23: istore_3
    //   24: iload 4
    //   26: sipush 254
    //   29: if_icmplt +45 -> 74
    //   32: aload_0
    //   33: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   36: invokevirtual 133	java/io/DataInputStream:read	()I
    //   39: aload_0
    //   40: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   43: invokevirtual 133	java/io/DataInputStream:read	()I
    //   46: bipush 8
    //   48: ishl
    //   49: ior
    //   50: aload_0
    //   51: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   54: invokevirtual 133	java/io/DataInputStream:read	()I
    //   57: bipush 16
    //   59: ishl
    //   60: ior
    //   61: istore_3
    //   62: aload_0
    //   63: aload_0
    //   64: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   67: iconst_3
    //   68: iadd
    //   69: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   72: iconst_4
    //   73: istore_2
    //   74: iload_3
    //   75: newarray <illegal type>
    //   77: astore 6
    //   79: aload_0
    //   80: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   83: aload 6
    //   85: invokevirtual 136	java/io/DataInputStream:read	([B)I
    //   88: pop
    //   89: aload_0
    //   90: aload_0
    //   91: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   94: iconst_1
    //   95: iadd
    //   96: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   99: aload 6
    //   101: astore 5
    //   103: iload_3
    //   104: iload_2
    //   105: iadd
    //   106: iconst_4
    //   107: irem
    //   108: ifeq +56 -> 164
    //   111: aload_0
    //   112: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   115: invokevirtual 133	java/io/DataInputStream:read	()I
    //   118: pop
    //   119: aload_0
    //   120: aload_0
    //   121: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   124: iconst_1
    //   125: iadd
    //   126: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   129: iload_2
    //   130: iconst_1
    //   131: iadd
    //   132: istore_2
    //   133: goto -34 -> 99
    //   136: astore 5
    //   138: iload_1
    //   139: ifeq +15 -> 154
    //   142: new 123	java/lang/RuntimeException
    //   145: dup
    //   146: ldc -118
    //   148: aload 5
    //   150: invokespecial 140	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   153: athrow
    //   154: ldc 85
    //   156: ldc -118
    //   158: invokestatic 93	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   161: aconst_null
    //   162: astore 5
    //   164: aload 5
    //   166: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	167	0	this	SerializedData
    //   0	167	1	paramBoolean	boolean
    //   1	132	2	i	int
    //   23	83	3	j	int
    //   9	21	4	k	int
    //   101	1	5	arrayOfByte1	byte[]
    //   136	13	5	localException	Exception
    //   162	3	5	arrayOfByte2	byte[]
    //   77	23	6	arrayOfByte3	byte[]
    // Exception table:
    //   from	to	target	type
    //   2	21	136	java/lang/Exception
    //   32	72	136	java/lang/Exception
    //   74	99	136	java/lang/Exception
    //   111	129	136	java/lang/Exception
  }
  
  public NativeByteBuffer readByteBuffer(boolean paramBoolean)
  {
    return null;
  }
  
  public void readBytes(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    try
    {
      this.in.read(paramArrayOfByte);
      this.len += paramArrayOfByte.length;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      if (paramBoolean) {
        throw new RuntimeException("read bytes error", paramArrayOfByte);
      }
      FileLog.e("tmessages", "read bytes error");
    }
  }
  
  public byte[] readData(int paramInt, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[paramInt];
    readBytes(arrayOfByte, paramBoolean);
    return arrayOfByte;
  }
  
  public double readDouble(boolean paramBoolean)
  {
    try
    {
      double d = Double.longBitsToDouble(readInt64(paramBoolean));
      return d;
    }
    catch (Exception localException)
    {
      if (paramBoolean) {
        throw new RuntimeException("read double error", localException);
      }
      FileLog.e("tmessages", "read double error");
    }
    return 0.0D;
  }
  
  public int readInt32(boolean paramBoolean)
  {
    int i = 0;
    int j = 0;
    int k;
    for (;;)
    {
      k = i;
      if (j < 4) {
        try
        {
          i |= this.in.read() << j * 8;
          this.len += 1;
          j += 1;
        }
        catch (Exception localException)
        {
          if (paramBoolean) {
            throw new RuntimeException("read int32 error", localException);
          }
          FileLog.e("tmessages", "read int32 error");
          k = 0;
        }
      }
    }
    return k;
  }
  
  public long readInt64(boolean paramBoolean)
  {
    long l1 = 0L;
    int i = 0;
    long l2;
    for (;;)
    {
      l2 = l1;
      if (i < 8) {
        try
        {
          l1 |= this.in.read() << i * 8;
          this.len += 1;
          i += 1;
        }
        catch (Exception localException)
        {
          if (paramBoolean) {
            throw new RuntimeException("read int64 error", localException);
          }
          FileLog.e("tmessages", "read int64 error");
          l2 = 0L;
        }
      }
    }
    return l2;
  }
  
  public String readString(boolean paramBoolean)
  {
    int i = 1;
    try
    {
      int k = this.in.read();
      this.len += 1;
      int j = k;
      if (k >= 254)
      {
        j = this.in.read() | this.in.read() << 8 | this.in.read() << 16;
        this.len += 3;
        i = 4;
      }
      Object localObject = new byte[j];
      this.in.read((byte[])localObject);
      this.len += 1;
      while ((j + i) % 4 != 0)
      {
        this.in.read();
        this.len += 1;
        i += 1;
      }
      localObject = new String((byte[])localObject, "UTF-8");
      return (String)localObject;
    }
    catch (Exception localException)
    {
      if (paramBoolean) {
        throw new RuntimeException("read string error", localException);
      }
      FileLog.e("tmessages", "read string error");
    }
    return null;
  }
  
  protected void set(byte[] paramArrayOfByte)
  {
    this.isOut = false;
    this.inbuf = new ByteArrayInputStream(paramArrayOfByte);
    this.in = new DataInputStream(this.inbuf);
  }
  
  public void skip(int paramInt)
  {
    if (paramInt == 0) {}
    do
    {
      return;
      if (this.justCalc) {
        break;
      }
    } while (this.in == null);
    try
    {
      this.in.skipBytes(paramInt);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
      return;
    }
    this.len += paramInt;
  }
  
  public byte[] toByteArray()
  {
    return this.outbuf.toByteArray();
  }
  
  public void writeBool(boolean paramBoolean)
  {
    if (!this.justCalc)
    {
      if (paramBoolean)
      {
        writeInt32(-1720552011);
        return;
      }
      writeInt32(-1132882121);
      return;
    }
    this.len += 4;
  }
  
  public void writeByte(byte paramByte)
  {
    try
    {
      if (!this.justCalc)
      {
        this.out.writeByte(paramByte);
        return;
      }
      this.len += 1;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", "write byte error");
    }
  }
  
  public void writeByte(int paramInt)
  {
    try
    {
      if (!this.justCalc)
      {
        this.out.writeByte((byte)paramInt);
        return;
      }
      this.len += 1;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", "write byte error");
    }
  }
  
  public void writeByteArray(byte[] paramArrayOfByte)
  {
    for (;;)
    {
      int i;
      try
      {
        if (paramArrayOfByte.length <= 253)
        {
          if (!this.justCalc)
          {
            this.out.write(paramArrayOfByte.length);
            if (this.justCalc) {
              break label164;
            }
            this.out.write(paramArrayOfByte);
            if (paramArrayOfByte.length > 253) {
              break label195;
            }
            i = 1;
            if ((paramArrayOfByte.length + i) % 4 != 0)
            {
              if (this.justCalc) {
                break label178;
              }
              this.out.write(0);
              break label188;
            }
          }
          else
          {
            this.len += 1;
            continue;
          }
          return;
        }
      }
      catch (Exception paramArrayOfByte)
      {
        FileLog.e("tmessages", "write byte array error");
      }
      if (!this.justCalc)
      {
        this.out.write(254);
        this.out.write(paramArrayOfByte.length);
        this.out.write(paramArrayOfByte.length >> 8);
        this.out.write(paramArrayOfByte.length >> 16);
      }
      else
      {
        this.len += 4;
        continue;
        label164:
        this.len += paramArrayOfByte.length;
        continue;
        label178:
        this.len += 1;
        label188:
        i += 1;
        continue;
        label195:
        i = 4;
      }
    }
  }
  
  public void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 253) {}
    for (;;)
    {
      try
      {
        if (!this.justCalc)
        {
          this.out.write(paramInt2);
          if (this.justCalc) {
            break label153;
          }
          this.out.write(paramArrayOfByte, paramInt1, paramInt2);
          break label179;
          if ((paramInt2 + paramInt1) % 4 != 0)
          {
            if (this.justCalc) {
              break label166;
            }
            this.out.write(0);
            break label191;
          }
        }
        else
        {
          this.len += 1;
          continue;
        }
        return;
      }
      catch (Exception paramArrayOfByte)
      {
        FileLog.e("tmessages", "write byte array error");
      }
      if (!this.justCalc)
      {
        this.out.write(254);
        this.out.write(paramInt2);
        this.out.write(paramInt2 >> 8);
        this.out.write(paramInt2 >> 16);
      }
      else
      {
        this.len += 4;
        continue;
        label153:
        this.len += paramInt2;
        break label179;
        label166:
        this.len += 1;
        label179:
        if (paramInt2 <= 253)
        {
          paramInt1 = 1;
          continue;
          label191:
          paramInt1 += 1;
        }
        else
        {
          paramInt1 = 4;
        }
      }
    }
  }
  
  public void writeByteBuffer(NativeByteBuffer paramNativeByteBuffer) {}
  
  public void writeBytes(byte[] paramArrayOfByte)
  {
    try
    {
      if (!this.justCalc)
      {
        this.out.write(paramArrayOfByte);
        return;
      }
      this.len += paramArrayOfByte.length;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      FileLog.e("tmessages", "write raw error");
    }
  }
  
  public void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      if (!this.justCalc)
      {
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      this.len += paramInt2;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      FileLog.e("tmessages", "write bytes error");
    }
  }
  
  public void writeDouble(double paramDouble)
  {
    try
    {
      writeInt64(Double.doubleToRawLongBits(paramDouble));
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", "write double error");
    }
  }
  
  public void writeInt32(int paramInt)
  {
    if (!this.justCalc)
    {
      writeInt32(paramInt, this.out);
      return;
    }
    this.len += 4;
  }
  
  public void writeInt64(long paramLong)
  {
    if (!this.justCalc)
    {
      writeInt64(paramLong, this.out);
      return;
    }
    this.len += 8;
  }
  
  public void writeString(String paramString)
  {
    try
    {
      writeByteArray(paramString.getBytes("UTF-8"));
      return;
    }
    catch (Exception paramString)
    {
      FileLog.e("tmessages", "write string error");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/SerializedData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */