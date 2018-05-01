package org.telegram.tgnet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import org.telegram.messenger.BuildVars;
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
          i++;
        }
        catch (Exception paramDataOutputStream)
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("write int32 error");
          }
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
          i++;
        }
        catch (Exception paramDataOutputStream)
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("write int64 error");
          }
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
    catch (Exception localException3)
    {
      try
      {
        if (this.in != null)
        {
          this.in.close();
          this.in = null;
        }
      }
      catch (Exception localException3)
      {
        try
        {
          if (this.outbuf != null)
          {
            this.outbuf.close();
            this.outbuf = null;
          }
        }
        catch (Exception localException3)
        {
          try
          {
            for (;;)
            {
              if (this.out != null)
              {
                this.out.close();
                this.out = null;
              }
              return;
              localException1 = localException1;
              FileLog.e(localException1);
              continue;
              localException2 = localException2;
              FileLog.e(localException2);
              continue;
              localException3 = localException3;
              FileLog.e(localException3);
            }
          }
          catch (Exception localException4)
          {
            for (;;)
            {
              FileLog.e(localException4);
            }
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
    int i;
    if (!this.justCalc) {
      if (this.isOut) {
        i = this.outbuf.size();
      }
    }
    for (;;)
    {
      return i;
      i = this.inbuf.available();
      continue;
      i = this.len;
    }
  }
  
  public boolean readBool(boolean paramBoolean)
  {
    boolean bool1 = false;
    int i = readInt32(paramBoolean);
    boolean bool2;
    if (i == -NUM) {
      bool2 = true;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (i != -NUM)
      {
        if (paramBoolean) {
          throw new RuntimeException("Not bool value!");
        }
        bool2 = bool1;
        if (BuildVars.LOGS_ENABLED)
        {
          FileLog.e("Not bool value!");
          bool2 = bool1;
        }
      }
    }
  }
  
  /* Error */
  public byte[] readByteArray(boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   6: invokevirtual 135	java/io/DataInputStream:read	()I
    //   9: istore_3
    //   10: aload_0
    //   11: aload_0
    //   12: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   15: iconst_1
    //   16: iadd
    //   17: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   20: iload_3
    //   21: istore 4
    //   23: iload_3
    //   24: sipush 254
    //   27: if_icmplt +46 -> 73
    //   30: aload_0
    //   31: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   34: invokevirtual 135	java/io/DataInputStream:read	()I
    //   37: aload_0
    //   38: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   41: invokevirtual 135	java/io/DataInputStream:read	()I
    //   44: bipush 8
    //   46: ishl
    //   47: ior
    //   48: aload_0
    //   49: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   52: invokevirtual 135	java/io/DataInputStream:read	()I
    //   55: bipush 16
    //   57: ishl
    //   58: ior
    //   59: istore 4
    //   61: aload_0
    //   62: aload_0
    //   63: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   66: iconst_3
    //   67: iadd
    //   68: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   71: iconst_4
    //   72: istore_2
    //   73: iload 4
    //   75: newarray <illegal type>
    //   77: astore 5
    //   79: aload_0
    //   80: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   83: aload 5
    //   85: invokevirtual 138	java/io/DataInputStream:read	([B)I
    //   88: pop
    //   89: aload_0
    //   90: aload_0
    //   91: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   94: iconst_1
    //   95: iadd
    //   96: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   99: aload 5
    //   101: astore 6
    //   103: iload 4
    //   105: iload_2
    //   106: iadd
    //   107: iconst_4
    //   108: irem
    //   109: ifeq +59 -> 168
    //   112: aload_0
    //   113: getfield 74	org/telegram/tgnet/SerializedData:in	Ljava/io/DataInputStream;
    //   116: invokevirtual 135	java/io/DataInputStream:read	()I
    //   119: pop
    //   120: aload_0
    //   121: aload_0
    //   122: getfield 78	org/telegram/tgnet/SerializedData:len	I
    //   125: iconst_1
    //   126: iadd
    //   127: putfield 78	org/telegram/tgnet/SerializedData:len	I
    //   130: iinc 2 1
    //   133: goto -34 -> 99
    //   136: astore 6
    //   138: iload_1
    //   139: ifeq +15 -> 154
    //   142: new 126	java/lang/RuntimeException
    //   145: dup
    //   146: ldc -116
    //   148: aload 6
    //   150: invokespecial 143	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   153: athrow
    //   154: getstatic 88	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   157: ifeq +8 -> 165
    //   160: ldc -116
    //   162: invokestatic 96	org/telegram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   165: aconst_null
    //   166: astore 6
    //   168: aload 6
    //   170: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	this	SerializedData
    //   0	171	1	paramBoolean	boolean
    //   1	130	2	i	int
    //   9	19	3	j	int
    //   21	86	4	k	int
    //   77	23	5	arrayOfByte1	byte[]
    //   101	1	6	arrayOfByte2	byte[]
    //   136	13	6	localException	Exception
    //   166	3	6	arrayOfByte3	byte[]
    // Exception table:
    //   from	to	target	type
    //   2	20	136	java/lang/Exception
    //   30	71	136	java/lang/Exception
    //   73	99	136	java/lang/Exception
    //   112	130	136	java/lang/Exception
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
      for (;;)
      {
        if (paramBoolean) {
          throw new RuntimeException("read bytes error", paramArrayOfByte);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read bytes error");
        }
      }
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
      d = Double.longBitsToDouble(readInt64(paramBoolean));
      return d;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (paramBoolean) {
          throw new RuntimeException("read double error", localException);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read double error");
        }
        double d = 0.0D;
      }
    }
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
          j++;
        }
        catch (Exception localException)
        {
          if (paramBoolean) {
            throw new RuntimeException("read int32 error", localException);
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("read int32 error");
          }
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
          i++;
        }
        catch (Exception localException)
        {
          if (paramBoolean) {
            throw new RuntimeException("read int64 error", localException);
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("read int64 error");
          }
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
      int j = this.in.read();
      this.len += 1;
      int k = j;
      if (j >= 254)
      {
        k = this.in.read() | this.in.read() << 8 | this.in.read() << 16;
        this.len += 3;
        i = 4;
      }
      localObject1 = new byte[k];
      this.in.read((byte[])localObject1);
      this.len += 1;
      while ((k + i) % 4 != 0)
      {
        this.in.read();
        this.len += 1;
        i++;
      }
      localObject1 = new String((byte[])localObject1, "UTF-8");
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject1;
        if (paramBoolean) {
          throw new RuntimeException("read string error", localException);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read string error");
        }
        Object localObject2 = null;
      }
    }
    return (String)localObject1;
  }
  
  public int remaining()
  {
    try
    {
      i = this.in.available();
      return i;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        int i = Integer.MAX_VALUE;
      }
    }
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
    for (;;)
    {
      return;
      if (!this.justCalc)
      {
        if (this.in != null) {
          try
          {
            this.in.skipBytes(paramInt);
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      }
      else {
        this.len += paramInt;
      }
    }
  }
  
  public byte[] toByteArray()
  {
    return this.outbuf.toByteArray();
  }
  
  public void writeBool(boolean paramBoolean)
  {
    if (!this.justCalc) {
      if (paramBoolean) {
        writeInt32(-NUM);
      }
    }
    for (;;)
    {
      return;
      writeInt32(-NUM);
      continue;
      this.len += 4;
    }
  }
  
  public void writeByte(byte paramByte)
  {
    try
    {
      if (!this.justCalc) {
        this.out.writeByte(paramByte);
      }
      for (;;)
      {
        return;
        this.len += 1;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write byte error");
        }
      }
    }
  }
  
  public void writeByte(int paramInt)
  {
    try
    {
      if (!this.justCalc) {
        this.out.writeByte((byte)paramInt);
      }
      for (;;)
      {
        return;
        this.len += 1;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write byte error");
        }
      }
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
              break label171;
            }
            this.out.write(paramArrayOfByte);
            if (paramArrayOfByte.length > 253) {
              break label185;
            }
            i = 1;
            if ((paramArrayOfByte.length + i) % 4 != 0)
            {
              if (this.justCalc) {
                break label190;
              }
              this.out.write(0);
              i++;
              continue;
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
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write byte array error");
        }
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
        label171:
        this.len += paramArrayOfByte.length;
        continue;
        label185:
        i = 4;
        continue;
        label190:
        this.len += 1;
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
            break label166;
          }
          this.out.write(paramArrayOfByte, paramInt1, paramInt2);
          if (paramInt2 > 253) {
            break label179;
          }
          paramInt1 = 1;
          if ((paramInt2 + paramInt1) % 4 != 0)
          {
            if (this.justCalc) {
              break label184;
            }
            this.out.write(0);
            paramInt1++;
            continue;
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
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write byte array error");
        }
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
        label166:
        this.len += paramInt2;
        continue;
        label179:
        paramInt1 = 4;
        continue;
        label184:
        this.len += 1;
      }
    }
  }
  
  public void writeByteBuffer(NativeByteBuffer paramNativeByteBuffer) {}
  
  public void writeBytes(byte[] paramArrayOfByte)
  {
    try
    {
      if (!this.justCalc) {
        this.out.write(paramArrayOfByte);
      }
      for (;;)
      {
        return;
        this.len += paramArrayOfByte.length;
      }
    }
    catch (Exception paramArrayOfByte)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write raw error");
        }
      }
    }
  }
  
  public void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      if (!this.justCalc) {
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
      }
      for (;;)
      {
        return;
        this.len += paramInt2;
      }
    }
    catch (Exception paramArrayOfByte)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write bytes error");
        }
      }
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
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write double error");
        }
      }
    }
  }
  
  public void writeInt32(int paramInt)
  {
    if (!this.justCalc) {
      writeInt32(paramInt, this.out);
    }
    for (;;)
    {
      return;
      this.len += 4;
    }
  }
  
  public void writeInt64(long paramLong)
  {
    if (!this.justCalc) {
      writeInt64(paramLong, this.out);
    }
    for (;;)
    {
      return;
      this.len += 8;
    }
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
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write string error");
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/SerializedData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */