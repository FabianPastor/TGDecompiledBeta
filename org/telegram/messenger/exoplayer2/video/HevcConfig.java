package org.telegram.messenger.exoplayer2.video;

import java.util.List;

public final class HevcConfig
{
  public final List<byte[]> initializationData;
  public final int nalUnitLengthFieldLength;
  
  private HevcConfig(List<byte[]> paramList, int paramInt)
  {
    this.initializationData = paramList;
    this.nalUnitLengthFieldLength = paramInt;
  }
  
  /* Error */
  public static HevcConfig parse(org.telegram.messenger.exoplayer2.util.ParsableByteArray paramParsableByteArray)
    throws org.telegram.messenger.exoplayer2.ParserException
  {
    // Byte code:
    //   0: aload_0
    //   1: bipush 21
    //   3: invokevirtual 33	org/telegram/messenger/exoplayer2/util/ParsableByteArray:skipBytes	(I)V
    //   6: aload_0
    //   7: invokevirtual 37	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readUnsignedByte	()I
    //   10: istore_1
    //   11: aload_0
    //   12: invokevirtual 37	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readUnsignedByte	()I
    //   15: istore_2
    //   16: iconst_0
    //   17: istore_3
    //   18: aload_0
    //   19: invokevirtual 40	org/telegram/messenger/exoplayer2/util/ParsableByteArray:getPosition	()I
    //   22: istore 4
    //   24: iconst_0
    //   25: istore 5
    //   27: iload 5
    //   29: iload_2
    //   30: if_icmpge +55 -> 85
    //   33: aload_0
    //   34: iconst_1
    //   35: invokevirtual 33	org/telegram/messenger/exoplayer2/util/ParsableByteArray:skipBytes	(I)V
    //   38: aload_0
    //   39: invokevirtual 43	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readUnsignedShort	()I
    //   42: istore 6
    //   44: iconst_0
    //   45: istore 7
    //   47: iload 7
    //   49: iload 6
    //   51: if_icmpge +28 -> 79
    //   54: aload_0
    //   55: invokevirtual 43	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readUnsignedShort	()I
    //   58: istore 8
    //   60: iload_3
    //   61: iload 8
    //   63: iconst_4
    //   64: iadd
    //   65: iadd
    //   66: istore_3
    //   67: aload_0
    //   68: iload 8
    //   70: invokevirtual 33	org/telegram/messenger/exoplayer2/util/ParsableByteArray:skipBytes	(I)V
    //   73: iinc 7 1
    //   76: goto -29 -> 47
    //   79: iinc 5 1
    //   82: goto -55 -> 27
    //   85: aload_0
    //   86: iload 4
    //   88: invokevirtual 46	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   91: iload_3
    //   92: newarray <illegal type>
    //   94: astore 9
    //   96: iconst_0
    //   97: istore 4
    //   99: iconst_0
    //   100: istore 5
    //   102: iload 5
    //   104: iload_2
    //   105: if_icmpge +96 -> 201
    //   108: aload_0
    //   109: iconst_1
    //   110: invokevirtual 33	org/telegram/messenger/exoplayer2/util/ParsableByteArray:skipBytes	(I)V
    //   113: aload_0
    //   114: invokevirtual 43	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readUnsignedShort	()I
    //   117: istore 6
    //   119: iconst_0
    //   120: istore 7
    //   122: iload 7
    //   124: iload 6
    //   126: if_icmpge +69 -> 195
    //   129: aload_0
    //   130: invokevirtual 43	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readUnsignedShort	()I
    //   133: istore 8
    //   135: getstatic 52	org/telegram/messenger/exoplayer2/util/NalUnitUtil:NAL_START_CODE	[B
    //   138: iconst_0
    //   139: aload 9
    //   141: iload 4
    //   143: getstatic 52	org/telegram/messenger/exoplayer2/util/NalUnitUtil:NAL_START_CODE	[B
    //   146: arraylength
    //   147: invokestatic 58	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   150: iload 4
    //   152: getstatic 52	org/telegram/messenger/exoplayer2/util/NalUnitUtil:NAL_START_CODE	[B
    //   155: arraylength
    //   156: iadd
    //   157: istore 4
    //   159: aload_0
    //   160: getfield 61	org/telegram/messenger/exoplayer2/util/ParsableByteArray:data	[B
    //   163: aload_0
    //   164: invokevirtual 40	org/telegram/messenger/exoplayer2/util/ParsableByteArray:getPosition	()I
    //   167: aload 9
    //   169: iload 4
    //   171: iload 8
    //   173: invokestatic 58	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   176: iload 4
    //   178: iload 8
    //   180: iadd
    //   181: istore 4
    //   183: aload_0
    //   184: iload 8
    //   186: invokevirtual 33	org/telegram/messenger/exoplayer2/util/ParsableByteArray:skipBytes	(I)V
    //   189: iinc 7 1
    //   192: goto -70 -> 122
    //   195: iinc 5 1
    //   198: goto -96 -> 102
    //   201: iload_3
    //   202: ifne +19 -> 221
    //   205: aconst_null
    //   206: astore_0
    //   207: new 2	org/telegram/messenger/exoplayer2/video/HevcConfig
    //   210: dup
    //   211: aload_0
    //   212: iload_1
    //   213: iconst_3
    //   214: iand
    //   215: iconst_1
    //   216: iadd
    //   217: invokespecial 63	org/telegram/messenger/exoplayer2/video/HevcConfig:<init>	(Ljava/util/List;I)V
    //   220: areturn
    //   221: aload 9
    //   223: invokestatic 69	java/util/Collections:singletonList	(Ljava/lang/Object;)Ljava/util/List;
    //   226: astore_0
    //   227: goto -20 -> 207
    //   230: astore_0
    //   231: new 25	org/telegram/messenger/exoplayer2/ParserException
    //   234: dup
    //   235: ldc 71
    //   237: aload_0
    //   238: invokespecial 74	org/telegram/messenger/exoplayer2/ParserException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   241: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	242	0	paramParsableByteArray	org.telegram.messenger.exoplayer2.util.ParsableByteArray
    //   10	205	1	i	int
    //   15	91	2	j	int
    //   17	185	3	k	int
    //   22	160	4	m	int
    //   25	171	5	n	int
    //   42	85	6	i1	int
    //   45	145	7	i2	int
    //   58	127	8	i3	int
    //   94	128	9	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   0	16	230	java/lang/ArrayIndexOutOfBoundsException
    //   18	24	230	java/lang/ArrayIndexOutOfBoundsException
    //   33	44	230	java/lang/ArrayIndexOutOfBoundsException
    //   54	60	230	java/lang/ArrayIndexOutOfBoundsException
    //   67	73	230	java/lang/ArrayIndexOutOfBoundsException
    //   85	96	230	java/lang/ArrayIndexOutOfBoundsException
    //   108	119	230	java/lang/ArrayIndexOutOfBoundsException
    //   129	176	230	java/lang/ArrayIndexOutOfBoundsException
    //   183	189	230	java/lang/ArrayIndexOutOfBoundsException
    //   207	221	230	java/lang/ArrayIndexOutOfBoundsException
    //   221	227	230	java/lang/ArrayIndexOutOfBoundsException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/video/HevcConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */