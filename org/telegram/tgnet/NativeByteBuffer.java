package org.telegram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.telegram.messenger.BuildVars;
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
  protected long address;
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
      if (this.address != 0L)
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
  
  public static native long native_getFreeBuffer(int paramInt);
  
  public static native ByteBuffer native_getJavaByteBuffer(long paramLong);
  
  public static native int native_limit(long paramLong);
  
  public static native int native_position(long paramLong);
  
  public static native void native_reuse(long paramLong);
  
  public static NativeByteBuffer wrap(long paramLong)
  {
    NativeByteBuffer localNativeByteBuffer = (NativeByteBuffer)addressWrapper.get();
    if (paramLong != 0L)
    {
      if ((!localNativeByteBuffer.reused) && (BuildVars.LOGS_ENABLED)) {
        FileLog.e("forgot to reuse?");
      }
      localNativeByteBuffer.address = paramLong;
      localNativeByteBuffer.reused = false;
      localNativeByteBuffer.buffer = native_getJavaByteBuffer(paramLong);
      localNativeByteBuffer.buffer.limit(native_limit(paramLong));
      int i = native_position(paramLong);
      if (i <= localNativeByteBuffer.buffer.limit()) {
        localNativeByteBuffer.buffer.position(i);
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
    if (paramByte >= 0) {}
    for (;;)
    {
      return paramByte;
      paramByte += 256;
    }
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
    if (!this.justCalc) {}
    for (int i = this.buffer.position();; i = this.len) {
      return i;
    }
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
    //   3: aload_0
    //   4: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   7: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   10: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   13: istore_3
    //   14: iload_3
    //   15: istore 4
    //   17: iload_3
    //   18: sipush 254
    //   21: if_icmplt +48 -> 69
    //   24: aload_0
    //   25: aload_0
    //   26: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   29: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   32: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   35: aload_0
    //   36: aload_0
    //   37: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   40: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   43: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   46: bipush 8
    //   48: ishl
    //   49: ior
    //   50: aload_0
    //   51: aload_0
    //   52: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   55: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   58: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   61: bipush 16
    //   63: ishl
    //   64: ior
    //   65: istore 4
    //   67: iconst_4
    //   68: istore_2
    //   69: iload 4
    //   71: newarray <illegal type>
    //   73: astore 5
    //   75: aload_0
    //   76: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   79: aload 5
    //   81: invokevirtual 155	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
    //   84: pop
    //   85: aload 5
    //   87: astore 6
    //   89: iload 4
    //   91: iload_2
    //   92: iadd
    //   93: iconst_4
    //   94: irem
    //   95: ifeq +51 -> 146
    //   98: aload_0
    //   99: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   102: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   105: pop
    //   106: iinc 2 1
    //   109: goto -24 -> 85
    //   112: astore 6
    //   114: iload_1
    //   115: ifeq +15 -> 130
    //   118: new 142	java/lang/RuntimeException
    //   121: dup
    //   122: ldc -99
    //   124: aload 6
    //   126: invokespecial 160	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   129: athrow
    //   130: getstatic 94	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   133: ifeq +8 -> 141
    //   136: ldc -99
    //   138: invokestatic 101	org/telegram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   141: iconst_0
    //   142: newarray <illegal type>
    //   144: astore 6
    //   146: aload 6
    //   148: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	NativeByteBuffer
    //   0	149	1	paramBoolean	boolean
    //   1	106	2	i	int
    //   13	9	3	j	int
    //   15	78	4	k	int
    //   73	13	5	arrayOfByte1	byte[]
    //   87	1	6	arrayOfByte2	byte[]
    //   112	13	6	localException	Exception
    //   144	3	6	arrayOfByte3	byte[]
    // Exception table:
    //   from	to	target	type
    //   2	14	112	java/lang/Exception
    //   24	67	112	java/lang/Exception
    //   69	85	112	java/lang/Exception
    //   98	106	112	java/lang/Exception
  }
  
  /* Error */
  public NativeByteBuffer readByteBuffer(boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: aload_0
    //   4: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   7: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   10: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   13: istore_3
    //   14: iload_3
    //   15: istore 4
    //   17: iload_3
    //   18: sipush 254
    //   21: if_icmplt +48 -> 69
    //   24: aload_0
    //   25: aload_0
    //   26: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   29: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   32: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   35: aload_0
    //   36: aload_0
    //   37: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   40: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   43: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   46: bipush 8
    //   48: ishl
    //   49: ior
    //   50: aload_0
    //   51: aload_0
    //   52: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   55: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   58: invokevirtual 152	org/telegram/tgnet/NativeByteBuffer:getIntFromByte	(B)I
    //   61: bipush 16
    //   63: ishl
    //   64: ior
    //   65: istore 4
    //   67: iconst_4
    //   68: istore_2
    //   69: new 2	org/telegram/tgnet/NativeByteBuffer
    //   72: astore 5
    //   74: aload 5
    //   76: iload 4
    //   78: invokespecial 164	org/telegram/tgnet/NativeByteBuffer:<init>	(I)V
    //   81: aload_0
    //   82: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   85: invokevirtual 108	java/nio/ByteBuffer:limit	()I
    //   88: istore_3
    //   89: aload_0
    //   90: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   93: aload_0
    //   94: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   97: invokevirtual 120	java/nio/ByteBuffer:position	()I
    //   100: iload 4
    //   102: iadd
    //   103: invokevirtual 53	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   106: pop
    //   107: aload 5
    //   109: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   112: aload_0
    //   113: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   116: invokevirtual 132	java/nio/ByteBuffer:put	(Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
    //   119: pop
    //   120: aload_0
    //   121: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   124: iload_3
    //   125: invokevirtual 53	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   128: pop
    //   129: aload 5
    //   131: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   134: iconst_0
    //   135: invokevirtual 50	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   138: pop
    //   139: aload 5
    //   141: astore 6
    //   143: iload 4
    //   145: iload_2
    //   146: iadd
    //   147: iconst_4
    //   148: irem
    //   149: ifeq +49 -> 198
    //   152: aload_0
    //   153: getfield 44	org/telegram/tgnet/NativeByteBuffer:buffer	Ljava/nio/ByteBuffer;
    //   156: invokevirtual 150	java/nio/ByteBuffer:get	()B
    //   159: pop
    //   160: iinc 2 1
    //   163: goto -24 -> 139
    //   166: astore 6
    //   168: iload_1
    //   169: ifeq +15 -> 184
    //   172: new 142	java/lang/RuntimeException
    //   175: dup
    //   176: ldc -99
    //   178: aload 6
    //   180: invokespecial 160	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   183: athrow
    //   184: getstatic 94	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   187: ifeq +8 -> 195
    //   190: ldc -99
    //   192: invokestatic 101	org/telegram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   195: aconst_null
    //   196: astore 6
    //   198: aload 6
    //   200: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	201	0	this	NativeByteBuffer
    //   0	201	1	paramBoolean	boolean
    //   1	160	2	i	int
    //   13	112	3	j	int
    //   15	132	4	k	int
    //   72	68	5	localNativeByteBuffer1	NativeByteBuffer
    //   141	1	6	localNativeByteBuffer2	NativeByteBuffer
    //   166	13	6	localException	Exception
    //   196	3	6	localNativeByteBuffer3	NativeByteBuffer
    // Exception table:
    //   from	to	target	type
    //   2	14	166	java/lang/Exception
    //   24	67	166	java/lang/Exception
    //   69	139	166	java/lang/Exception
    //   152	160	166	java/lang/Exception
  }
  
  public void readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    try
    {
      this.buffer.get(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      for (;;)
      {
        if (paramBoolean) {
          throw new RuntimeException("read raw error", paramArrayOfByte);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read raw error");
        }
      }
    }
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
      for (;;)
      {
        if (paramBoolean) {
          throw new RuntimeException("read raw error", paramArrayOfByte);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read raw error");
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
    try
    {
      i = this.buffer.getInt();
      return i;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (paramBoolean) {
          throw new RuntimeException("read int32 error", localException);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read int32 error");
        }
        int i = 0;
      }
    }
  }
  
  public long readInt64(boolean paramBoolean)
  {
    try
    {
      l = this.buffer.getLong();
      return l;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (paramBoolean) {
          throw new RuntimeException("read int64 error", localException);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read int64 error");
        }
        long l = 0L;
      }
    }
  }
  
  public String readString(boolean paramBoolean)
  {
    i = getPosition();
    int j = 1;
    try
    {
      int k = getIntFromByte(this.buffer.get());
      int m = k;
      if (k >= 254)
      {
        m = getIntFromByte(this.buffer.get()) | getIntFromByte(this.buffer.get()) << 8 | getIntFromByte(this.buffer.get()) << 16;
        j = 4;
      }
      localObject = new byte[m];
      this.buffer.get((byte[])localObject);
      while ((m + j) % 4 != 0)
      {
        this.buffer.get();
        j++;
      }
      localObject = new String((byte[])localObject, "UTF-8");
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject;
        if (paramBoolean) {
          throw new RuntimeException("read string error", localException);
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("read string error");
        }
        position(i);
        String str = "";
      }
    }
    return (String)localObject;
  }
  
  public int remaining()
  {
    return this.buffer.remaining();
  }
  
  public void reuse()
  {
    if (this.address != 0L)
    {
      this.reused = true;
      native_reuse(this.address);
    }
  }
  
  public void rewind()
  {
    if (this.justCalc) {
      this.len = 0;
    }
    for (;;)
    {
      return;
      this.buffer.rewind();
    }
  }
  
  public void skip(int paramInt)
  {
    if (paramInt == 0) {}
    for (;;)
    {
      return;
      if (!this.justCalc) {
        this.buffer.position(this.buffer.position() + paramInt);
      } else {
        this.len += paramInt;
      }
    }
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
        this.buffer.put(paramByte);
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
              break label181;
            }
            this.buffer.put(paramArrayOfByte);
            if (paramArrayOfByte.length > 253) {
              break label195;
            }
            i = 1;
            if ((paramArrayOfByte.length + i) % 4 != 0)
            {
              if (this.justCalc) {
                break label200;
              }
              this.buffer.put((byte)0);
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
        this.buffer.put((byte)-2);
        this.buffer.put((byte)paramArrayOfByte.length);
        this.buffer.put((byte)(paramArrayOfByte.length >> 8));
        this.buffer.put((byte)(paramArrayOfByte.length >> 16));
      }
      else
      {
        this.len += 4;
        continue;
        label181:
        this.len += paramArrayOfByte.length;
        continue;
        label195:
        i = 4;
        continue;
        label200:
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
          this.buffer.put((byte)paramInt2);
          if (this.justCalc) {
            break label176;
          }
          this.buffer.put(paramArrayOfByte, paramInt1, paramInt2);
          if (paramInt2 > 253) {
            break label189;
          }
          paramInt1 = 1;
          if ((paramInt2 + paramInt1) % 4 != 0)
          {
            if (this.justCalc) {
              break label194;
            }
            this.buffer.put((byte)0);
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
        this.buffer.put((byte)-2);
        this.buffer.put((byte)paramInt2);
        this.buffer.put((byte)(paramInt2 >> 8));
        this.buffer.put((byte)(paramInt2 >> 16));
      }
      else
      {
        this.len += 4;
        continue;
        label176:
        this.len += paramInt2;
        continue;
        label189:
        paramInt1 = 4;
        continue;
        label194:
        this.len += 1;
      }
    }
  }
  
  public void writeByteBuffer(NativeByteBuffer paramNativeByteBuffer)
  {
    for (;;)
    {
      int i;
      int j;
      try
      {
        i = paramNativeByteBuffer.limit();
        if (i <= 253)
        {
          if (!this.justCalc)
          {
            this.buffer.put((byte)i);
            if (this.justCalc) {
              break label179;
            }
            paramNativeByteBuffer.rewind();
            this.buffer.put(paramNativeByteBuffer.buffer);
            if (i > 253) {
              break label192;
            }
            j = 1;
            if ((i + j) % 4 != 0)
            {
              if (this.justCalc) {
                break label197;
              }
              this.buffer.put((byte)0);
              j++;
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
      catch (Exception paramNativeByteBuffer)
      {
        FileLog.e(paramNativeByteBuffer);
      }
      if (!this.justCalc)
      {
        this.buffer.put((byte)-2);
        this.buffer.put((byte)i);
        this.buffer.put((byte)(i >> 8));
        this.buffer.put((byte)(i >> 16));
      }
      else
      {
        this.len += 4;
        continue;
        label179:
        this.len += i;
        continue;
        label192:
        j = 4;
        continue;
        label197:
        this.len += 1;
      }
    }
  }
  
  public void writeBytes(NativeByteBuffer paramNativeByteBuffer)
  {
    if (this.justCalc) {
      this.len += paramNativeByteBuffer.limit();
    }
    for (;;)
    {
      return;
      paramNativeByteBuffer.rewind();
      this.buffer.put(paramNativeByteBuffer.buffer);
    }
  }
  
  public void writeBytes(byte[] paramArrayOfByte)
  {
    try
    {
      if (!this.justCalc) {
        this.buffer.put(paramArrayOfByte);
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
        this.buffer.put(paramArrayOfByte, paramInt1, paramInt2);
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
          FileLog.e("write raw error");
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
    try
    {
      if (!this.justCalc) {
        this.buffer.putInt(paramInt);
      }
      for (;;)
      {
        return;
        this.len += 4;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write int32 error");
        }
      }
    }
  }
  
  public void writeInt64(long paramLong)
  {
    try
    {
      if (!this.justCalc) {
        this.buffer.putLong(paramLong);
      }
      for (;;)
      {
        return;
        this.len += 8;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("write int64 error");
        }
      }
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/NativeByteBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */