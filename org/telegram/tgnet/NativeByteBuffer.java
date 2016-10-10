package org.telegram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.FileLog;

public class NativeByteBuffer
  extends AbstractSerializedData
{
  private static final ThreadLocal<NativeByteBuffer> addressWrapper = new ThreadLocal()
  {
    protected NativeByteBuffer initialValue()
    {
      return new NativeByteBuffer(0, true, null);
    }
  };
  protected int address;
  public ByteBuffer buffer;
  private boolean justCalc;
  private int len;
  public boolean reused = true;
  
  public NativeByteBuffer(int paramInt)
    throws Exception
  {
    if (paramInt >= 0)
    {
      this.address = native_getFreeBuffer(paramInt);
      if (this.address != 0)
      {
        this.buffer = native_getJavaByteBuffer(this.address);
        this.buffer.position(0);
        this.buffer.limit(paramInt);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
      }
      return;
    }
    throw new Exception("invalid NativeByteBuffer size");
  }
  
  private NativeByteBuffer(int paramInt, boolean paramBoolean) {}
  
  public NativeByteBuffer(boolean paramBoolean)
  {
    this.justCalc = paramBoolean;
  }
  
  public static native int native_getFreeBuffer(int paramInt);
  
  public static native ByteBuffer native_getJavaByteBuffer(int paramInt);
  
  public static native int native_limit(int paramInt);
  
  public static native int native_position(int paramInt);
  
  public static native void native_reuse(int paramInt);
  
  public static NativeByteBuffer wrap(int paramInt)
  {
    NativeByteBuffer localNativeByteBuffer = (NativeByteBuffer)addressWrapper.get();
    if (paramInt != 0)
    {
      if (!localNativeByteBuffer.reused) {
        FileLog.e("tmessages", "forgot to reuse?");
      }
      localNativeByteBuffer.address = paramInt;
      localNativeByteBuffer.reused = false;
      localNativeByteBuffer.buffer = native_getJavaByteBuffer(paramInt);
      localNativeByteBuffer.buffer.limit(native_limit(paramInt));
      paramInt = native_position(paramInt);
      if (paramInt <= localNativeByteBuffer.buffer.limit()) {
        localNativeByteBuffer.buffer.position(paramInt);
      }
      localNativeByteBuffer.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    return localNativeByteBuffer;
  }
  
  public int capacity()
  {
    return this.buffer.capacity();
  }
  
  public void compact()
  {
    this.buffer.compact();
  }
  
  public int getIntFromByte(byte paramByte)
  {
    if (paramByte >= 0) {
      return paramByte;
    }
    return paramByte + 256;
  }
  
  public int getPosition()
  {
    return this.buffer.position();
  }
  
  public boolean hasRemaining()
  {
    return this.buffer.hasRemaining();
  }
  
  public int length()
  {
    if (!this.justCalc) {
      return this.buffer.position();
    }
    return this.len;
  }
  
  public int limit()
  {
    return this.buffer.limit();
  }
  
  public void limit(int paramInt)
  {
    this.buffer.limit(paramInt);
  }
  
  public int position()
  {
    return this.buffer.position();
  }
  
  public void position(int paramInt)
  {
    this.buffer.position(paramInt);
  }
  
  public void put(ByteBuffer paramByteBuffer)
  {
    this.buffer.put(paramByteBuffer);
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
    //   3: aload_0
    //   4: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   7: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   10: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   13: istore 4
    //   15: iload 4
    //   17: istore_3
    //   18: iload 4
    //   20: sipush 254
    //   23: if_icmplt +47 -> 70
    //   26: aload_0
    //   27: aload_0
    //   28: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   31: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   34: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   42: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   45: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   48: bipush 8
    //   50: ishl
    //   51: ior
    //   52: aload_0
    //   53: aload_0
    //   54: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   57: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   60: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   63: bipush 16
    //   65: ishl
    //   66: ior
    //   67: istore_3
    //   68: iconst_4
    //   69: istore_2
    //   70: iload_3
    //   71: newarray <illegal type>
    //   73: astore 6
    //   75: aload_0
    //   76: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   79: aload 6
    //   81: invokevirtual 150	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
    //   84: pop
    //   85: aload 6
    //   87: astore 5
    //   89: iload_3
    //   90: iload_2
    //   91: iadd
    //   92: iconst_4
    //   93: irem
    //   94: ifeq +48 -> 142
    //   97: aload_0
    //   98: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   101: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   104: pop
    //   105: iload_2
    //   106: iconst_1
    //   107: iadd
    //   108: istore_2
    //   109: goto -24 -> 85
    //   112: astore 5
    //   114: iload_1
    //   115: ifeq +15 -> 130
    //   118: new 137	java/lang/RuntimeException
    //   121: dup
    //   122: ldc -104
    //   124: aload 5
    //   126: invokespecial 155	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   129: athrow
    //   130: ldc 88
    //   132: ldc -104
    //   134: invokestatic 96	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   137: iconst_0
    //   138: newarray <illegal type>
    //   140: astore 5
    //   142: aload 5
    //   144: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	145	0	this	NativeByteBuffer
    //   0	145	1	paramBoolean	boolean
    //   1	108	2	i	int
    //   17	75	3	j	int
    //   13	11	4	k	int
    //   87	1	5	arrayOfByte1	byte[]
    //   112	13	5	localException	Exception
    //   140	3	5	arrayOfByte2	byte[]
    //   73	13	6	arrayOfByte3	byte[]
    // Exception table:
    //   from	to	target	type
    //   2	15	112	java/lang/Exception
    //   26	68	112	java/lang/Exception
    //   70	85	112	java/lang/Exception
    //   97	105	112	java/lang/Exception
  }
  
  /* Error */
  public NativeByteBuffer readByteBuffer(boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: aload_0
    //   4: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   7: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   10: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   13: istore 4
    //   15: iload 4
    //   17: istore_3
    //   18: iload 4
    //   20: sipush 254
    //   23: if_icmplt +47 -> 70
    //   26: aload_0
    //   27: aload_0
    //   28: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   31: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   34: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   42: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   45: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   48: bipush 8
    //   50: ishl
    //   51: ior
    //   52: aload_0
    //   53: aload_0
    //   54: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   57: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   60: invokevirtual 147	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   63: bipush 16
    //   65: ishl
    //   66: ior
    //   67: istore_3
    //   68: iconst_4
    //   69: istore_2
    //   70: new 2	org/telegram/tgnet/NativeByteBuffer
    //   73: dup
    //   74: iload_3
    //   75: invokespecial 159	org/telegram/tgnet/NativeByteBuffer:<init>	(I)V
    //   78: astore 6
    //   80: aload_0
    //   81: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   84: invokevirtual 103	java/nio/ByteBuffer:limit	()I
    //   87: istore 4
    //   89: aload_0
    //   90: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   93: aload_0
    //   94: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   97: invokevirtual 115	java/nio/ByteBuffer:position	()I
    //   100: iload_3
    //   101: iadd
    //   102: invokevirtual 52	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   105: pop
    //   106: aload 6
    //   108: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   111: aload_0
    //   112: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   115: invokevirtual 127	java/nio/ByteBuffer:put	(Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
    //   118: pop
    //   119: aload_0
    //   120: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   123: iload 4
    //   125: invokevirtual 52	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   128: pop
    //   129: aload 6
    //   131: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   134: iconst_0
    //   135: invokevirtual 49	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   138: pop
    //   139: aload 6
    //   141: astore 5
    //   143: iload_3
    //   144: iload_2
    //   145: iadd
    //   146: iconst_4
    //   147: irem
    //   148: ifeq +46 -> 194
    //   151: aload_0
    //   152: getfield 43	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   155: invokevirtual 145	java/nio/ByteBuffer:get	()B
    //   158: pop
    //   159: iload_2
    //   160: iconst_1
    //   161: iadd
    //   162: istore_2
    //   163: goto -24 -> 139
    //   166: astore 5
    //   168: iload_1
    //   169: ifeq +15 -> 184
    //   172: new 137	java/lang/RuntimeException
    //   175: dup
    //   176: ldc -104
    //   178: aload 5
    //   180: invokespecial 155	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   183: athrow
    //   184: ldc 88
    //   186: ldc -104
    //   188: invokestatic 96	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   191: aconst_null
    //   192: astore 5
    //   194: aload 5
    //   196: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	197	0	this	NativeByteBuffer
    //   0	197	1	paramBoolean	boolean
    //   1	162	2	i	int
    //   17	129	3	j	int
    //   13	111	4	k	int
    //   141	1	5	localNativeByteBuffer1	NativeByteBuffer
    //   166	13	5	localException	Exception
    //   192	3	5	localNativeByteBuffer2	NativeByteBuffer
    //   78	62	6	localNativeByteBuffer3	NativeByteBuffer
    // Exception table:
    //   from	to	target	type
    //   2	15	166	java/lang/Exception
    //   26	68	166	java/lang/Exception
    //   70	139	166	java/lang/Exception
    //   151	159	166	java/lang/Exception
  }
  
  public void readBytes(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    try
    {
      this.buffer.get(paramArrayOfByte);
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      if (paramBoolean) {
        throw new RuntimeException("read raw error", paramArrayOfByte);
      }
      FileLog.e("tmessages", "read raw error");
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
    try
    {
      int i = this.buffer.getInt();
      return i;
    }
    catch (Exception localException)
    {
      if (paramBoolean) {
        throw new RuntimeException("read int32 error", localException);
      }
      FileLog.e("tmessages", "read int32 error");
    }
    return 0;
  }
  
  public long readInt64(boolean paramBoolean)
  {
    try
    {
      long l = this.buffer.getLong();
      return l;
    }
    catch (Exception localException)
    {
      if (paramBoolean) {
        throw new RuntimeException("read int64 error", localException);
      }
      FileLog.e("tmessages", "read int64 error");
    }
    return 0L;
  }
  
  public String readString(boolean paramBoolean)
  {
    int i = 1;
    try
    {
      int k = getIntFromByte(this.buffer.get());
      int j = k;
      if (k >= 254)
      {
        j = getIntFromByte(this.buffer.get()) | getIntFromByte(this.buffer.get()) << 8 | getIntFromByte(this.buffer.get()) << 16;
        i = 4;
      }
      Object localObject = new byte[j];
      this.buffer.get((byte[])localObject);
      while ((j + i) % 4 != 0)
      {
        this.buffer.get();
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
    return "";
  }
  
  public void reuse()
  {
    if (this.address != 0)
    {
      this.reused = true;
      native_reuse(this.address);
    }
  }
  
  public void rewind()
  {
    if (this.justCalc)
    {
      this.len = 0;
      return;
    }
    this.buffer.rewind();
  }
  
  public void skip(int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    if (!this.justCalc)
    {
      this.buffer.position(this.buffer.position() + paramInt);
      return;
    }
    this.len += paramInt;
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
        this.buffer.put(paramByte);
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
    writeByte((byte)paramInt);
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
            this.buffer.put((byte)paramArrayOfByte.length);
            if (this.justCalc) {
              break label174;
            }
            this.buffer.put(paramArrayOfByte);
            if (paramArrayOfByte.length > 253) {
              break label205;
            }
            i = 1;
            if ((paramArrayOfByte.length + i) % 4 != 0)
            {
              if (this.justCalc) {
                break label188;
              }
              this.buffer.put((byte)0);
              break label198;
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
        this.buffer.put((byte)-2);
        this.buffer.put((byte)paramArrayOfByte.length);
        this.buffer.put((byte)(paramArrayOfByte.length >> 8));
        this.buffer.put((byte)(paramArrayOfByte.length >> 16));
      }
      else
      {
        this.len += 4;
        continue;
        label174:
        this.len += paramArrayOfByte.length;
        continue;
        label188:
        this.len += 1;
        label198:
        i += 1;
        continue;
        label205:
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
          this.buffer.put((byte)paramInt2);
          if (this.justCalc) {
            break label163;
          }
          this.buffer.put(paramArrayOfByte, paramInt1, paramInt2);
          break label189;
          if ((paramInt2 + paramInt1) % 4 != 0)
          {
            if (this.justCalc) {
              break label176;
            }
            this.buffer.put((byte)0);
            break label201;
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
        this.buffer.put((byte)-2);
        this.buffer.put((byte)paramInt2);
        this.buffer.put((byte)(paramInt2 >> 8));
        this.buffer.put((byte)(paramInt2 >> 16));
      }
      else
      {
        this.len += 4;
        continue;
        label163:
        this.len += paramInt2;
        break label189;
        label176:
        this.len += 1;
        label189:
        if (paramInt2 <= 253)
        {
          paramInt1 = 1;
          continue;
          label201:
          paramInt1 += 1;
        }
        else
        {
          paramInt1 = 4;
        }
      }
    }
  }
  
  public void writeByteBuffer(NativeByteBuffer paramNativeByteBuffer)
  {
    for (;;)
    {
      int j;
      int i;
      try
      {
        j = paramNativeByteBuffer.limit();
        if (j <= 253)
        {
          if (!this.justCalc)
          {
            this.buffer.put((byte)j);
            if (this.justCalc) {
              break label172;
            }
            paramNativeByteBuffer.rewind();
            this.buffer.put(paramNativeByteBuffer.buffer);
            break label198;
            if ((j + i) % 4 != 0)
            {
              if (this.justCalc) {
                break label185;
              }
              this.buffer.put((byte)0);
              break label210;
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
      catch (Exception paramNativeByteBuffer)
      {
        FileLog.e("tmessages", paramNativeByteBuffer);
      }
      if (!this.justCalc)
      {
        this.buffer.put((byte)-2);
        this.buffer.put((byte)j);
        this.buffer.put((byte)(j >> 8));
        this.buffer.put((byte)(j >> 16));
      }
      else
      {
        this.len += 4;
        continue;
        label172:
        this.len += j;
        break label198;
        label185:
        this.len += 1;
        label198:
        if (j <= 253)
        {
          i = 1;
          continue;
          label210:
          i += 1;
        }
        else
        {
          i = 4;
        }
      }
    }
  }
  
  public void writeBytes(NativeByteBuffer paramNativeByteBuffer)
  {
    if (this.justCalc)
    {
      this.len += paramNativeByteBuffer.limit();
      return;
    }
    paramNativeByteBuffer.rewind();
    this.buffer.put(paramNativeByteBuffer.buffer);
  }
  
  public void writeBytes(byte[] paramArrayOfByte)
  {
    try
    {
      if (!this.justCalc)
      {
        this.buffer.put(paramArrayOfByte);
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
        this.buffer.put(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      this.len += paramInt2;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      FileLog.e("tmessages", "write raw error");
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
    try
    {
      if (!this.justCalc)
      {
        this.buffer.putInt(paramInt);
        return;
      }
      this.len += 4;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", "write int32 error");
    }
  }
  
  public void writeInt64(long paramLong)
  {
    try
    {
      if (!this.justCalc)
      {
        this.buffer.putLong(paramLong);
        return;
      }
      this.len += 8;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", "write int64 error");
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
      FileLog.e("tmessages", "write string error");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/NativeByteBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */