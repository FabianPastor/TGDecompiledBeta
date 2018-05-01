package org.telegram.messenger.exoplayer2.metadata.emsg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class EventMessageEncoder
{
  private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
  private final DataOutputStream dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);
  
  private static void writeNullTerminatedString(DataOutputStream paramDataOutputStream, String paramString)
    throws IOException
  {
    paramDataOutputStream.writeBytes(paramString);
    paramDataOutputStream.writeByte(0);
  }
  
  private static void writeUnsignedInt(DataOutputStream paramDataOutputStream, long paramLong)
    throws IOException
  {
    paramDataOutputStream.writeByte((int)(paramLong >>> 24) & 0xFF);
    paramDataOutputStream.writeByte((int)(paramLong >>> 16) & 0xFF);
    paramDataOutputStream.writeByte((int)(paramLong >>> 8) & 0xFF);
    paramDataOutputStream.writeByte((int)paramLong & 0xFF);
  }
  
  /* Error */
  public byte[] encode(EventMessage paramEventMessage, long paramLong)
  {
    // Byte code:
    //   0: lload_2
    //   1: lconst_0
    //   2: lcmp
    //   3: iflt +138 -> 141
    //   6: iconst_1
    //   7: istore 4
    //   9: iload 4
    //   11: invokestatic 49	org/telegram/messenger/exoplayer2/util/Assertions:checkArgument	(Z)V
    //   14: aload_0
    //   15: getfield 19	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:byteArrayOutputStream	Ljava/io/ByteArrayOutputStream;
    //   18: invokevirtual 52	java/io/ByteArrayOutputStream:reset	()V
    //   21: aload_0
    //   22: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   25: aload_1
    //   26: getfield 58	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage:schemeIdUri	Ljava/lang/String;
    //   29: invokestatic 60	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:writeNullTerminatedString	(Ljava/io/DataOutputStream;Ljava/lang/String;)V
    //   32: aload_1
    //   33: getfield 63	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage:value	Ljava/lang/String;
    //   36: ifnull +111 -> 147
    //   39: aload_1
    //   40: getfield 63	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage:value	Ljava/lang/String;
    //   43: astore 5
    //   45: aload_0
    //   46: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   49: aload 5
    //   51: invokestatic 60	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:writeNullTerminatedString	(Ljava/io/DataOutputStream;Ljava/lang/String;)V
    //   54: aload_0
    //   55: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   58: lload_2
    //   59: invokestatic 65	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:writeUnsignedInt	(Ljava/io/DataOutputStream;J)V
    //   62: aload_1
    //   63: getfield 69	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage:presentationTimeUs	J
    //   66: lload_2
    //   67: ldc2_w 70
    //   70: invokestatic 77	org/telegram/messenger/exoplayer2/util/Util:scaleLargeTimestamp	(JJJ)J
    //   73: lstore 6
    //   75: aload_0
    //   76: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   79: lload 6
    //   81: invokestatic 65	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:writeUnsignedInt	(Ljava/io/DataOutputStream;J)V
    //   84: aload_1
    //   85: getfield 80	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage:durationMs	J
    //   88: lload_2
    //   89: ldc2_w 81
    //   92: invokestatic 77	org/telegram/messenger/exoplayer2/util/Util:scaleLargeTimestamp	(JJJ)J
    //   95: lstore_2
    //   96: aload_0
    //   97: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   100: lload_2
    //   101: invokestatic 65	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:writeUnsignedInt	(Ljava/io/DataOutputStream;J)V
    //   104: aload_0
    //   105: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   108: aload_1
    //   109: getfield 85	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage:id	J
    //   112: invokestatic 65	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:writeUnsignedInt	(Ljava/io/DataOutputStream;J)V
    //   115: aload_0
    //   116: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   119: aload_1
    //   120: getfield 89	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage:messageData	[B
    //   123: invokevirtual 93	java/io/DataOutputStream:write	([B)V
    //   126: aload_0
    //   127: getfield 26	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:dataOutputStream	Ljava/io/DataOutputStream;
    //   130: invokevirtual 96	java/io/DataOutputStream:flush	()V
    //   133: aload_0
    //   134: getfield 19	org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder:byteArrayOutputStream	Ljava/io/ByteArrayOutputStream;
    //   137: invokevirtual 100	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   140: areturn
    //   141: iconst_0
    //   142: istore 4
    //   144: goto -135 -> 9
    //   147: ldc 102
    //   149: astore 5
    //   151: goto -106 -> 45
    //   154: astore_1
    //   155: new 104	java/lang/RuntimeException
    //   158: dup
    //   159: aload_1
    //   160: invokespecial 107	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   163: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	this	EventMessageEncoder
    //   0	164	1	paramEventMessage	EventMessage
    //   0	164	2	paramLong	long
    //   7	136	4	bool	boolean
    //   43	107	5	str	String
    //   73	7	6	l	long
    // Exception table:
    //   from	to	target	type
    //   21	45	154	java/io/IOException
    //   45	141	154	java/io/IOException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */