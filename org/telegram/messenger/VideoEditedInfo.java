package org.telegram.messenger;

import java.util.Locale;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

public class VideoEditedInfo
{
  public int bitrate;
  public TLRPC.InputEncryptedFile encryptedFile;
  public long endTime;
  public long estimatedDuration;
  public long estimatedSize;
  public TLRPC.InputFile file;
  public int framerate = 24;
  public byte[] iv;
  public byte[] key;
  public boolean muted;
  public int originalHeight;
  public String originalPath;
  public int originalWidth;
  public int resultHeight;
  public int resultWidth;
  public int rotationValue;
  public boolean roundVideo;
  public long startTime;
  
  public String getString()
  {
    return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[] { Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), Integer.valueOf(this.framerate), this.originalPath });
  }
  
  public boolean needConvert()
  {
    if ((!this.roundVideo) || ((this.roundVideo) && ((this.startTime > 0L) || ((this.endTime != -1L) && (this.endTime != this.estimatedDuration))))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  /* Error */
  public boolean parseString(String paramString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_1
    //   3: invokevirtual 97	java/lang/String:length	()I
    //   6: bipush 6
    //   8: if_icmpge +5 -> 13
    //   11: iload_2
    //   12: ireturn
    //   13: aload_1
    //   14: ldc 99
    //   16: invokevirtual 103	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   19: astore_1
    //   20: aload_1
    //   21: arraylength
    //   22: bipush 10
    //   24: if_icmplt +202 -> 226
    //   27: aload_0
    //   28: aload_1
    //   29: iconst_1
    //   30: aaload
    //   31: invokestatic 107	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   34: putfield 48	org/telegram/messenger/VideoEditedInfo:startTime	J
    //   37: aload_0
    //   38: aload_1
    //   39: iconst_2
    //   40: aaload
    //   41: invokestatic 107	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   44: putfield 56	org/telegram/messenger/VideoEditedInfo:endTime	J
    //   47: aload_0
    //   48: aload_1
    //   49: iconst_3
    //   50: aaload
    //   51: invokestatic 111	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   54: putfield 58	org/telegram/messenger/VideoEditedInfo:rotationValue	I
    //   57: aload_0
    //   58: aload_1
    //   59: iconst_4
    //   60: aaload
    //   61: invokestatic 111	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   64: putfield 65	org/telegram/messenger/VideoEditedInfo:originalWidth	I
    //   67: aload_0
    //   68: aload_1
    //   69: iconst_5
    //   70: aaload
    //   71: invokestatic 111	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   74: putfield 67	org/telegram/messenger/VideoEditedInfo:originalHeight	I
    //   77: aload_0
    //   78: aload_1
    //   79: bipush 6
    //   81: aaload
    //   82: invokestatic 111	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   85: putfield 69	org/telegram/messenger/VideoEditedInfo:bitrate	I
    //   88: aload_0
    //   89: aload_1
    //   90: bipush 7
    //   92: aaload
    //   93: invokestatic 111	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   96: putfield 71	org/telegram/messenger/VideoEditedInfo:resultWidth	I
    //   99: aload_0
    //   100: aload_1
    //   101: bipush 8
    //   103: aaload
    //   104: invokestatic 111	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   107: putfield 73	org/telegram/messenger/VideoEditedInfo:resultHeight	I
    //   110: aload_1
    //   111: arraylength
    //   112: istore_3
    //   113: iload_3
    //   114: bipush 11
    //   116: if_icmplt +14 -> 130
    //   119: aload_0
    //   120: aload_1
    //   121: bipush 9
    //   123: aaload
    //   124: invokestatic 111	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   127: putfield 35	org/telegram/messenger/VideoEditedInfo:framerate	I
    //   130: aload_0
    //   131: getfield 35	org/telegram/messenger/VideoEditedInfo:framerate	I
    //   134: ifne +38 -> 172
    //   137: bipush 9
    //   139: istore_3
    //   140: aload_0
    //   141: bipush 24
    //   143: putfield 35	org/telegram/messenger/VideoEditedInfo:framerate	I
    //   146: iload_3
    //   147: aload_1
    //   148: arraylength
    //   149: if_icmpge +77 -> 226
    //   152: aload_0
    //   153: getfield 75	org/telegram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   156: ifnonnull +22 -> 178
    //   159: aload_0
    //   160: aload_1
    //   161: iload_3
    //   162: aaload
    //   163: putfield 75	org/telegram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   166: iinc 3 1
    //   169: goto -23 -> 146
    //   172: bipush 10
    //   174: istore_3
    //   175: goto -29 -> 146
    //   178: new 113	java/lang/StringBuilder
    //   181: astore 4
    //   183: aload 4
    //   185: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   188: aload_0
    //   189: aload 4
    //   191: aload_0
    //   192: getfield 75	org/telegram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   195: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: ldc 99
    //   200: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   203: aload_1
    //   204: iload_3
    //   205: aaload
    //   206: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   212: putfield 75	org/telegram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   215: goto -49 -> 166
    //   218: astore_1
    //   219: aload_1
    //   220: invokestatic 127	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   223: goto -212 -> 11
    //   226: iconst_1
    //   227: istore_2
    //   228: goto -217 -> 11
    //   231: astore 4
    //   233: goto -103 -> 130
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	236	0	this	VideoEditedInfo
    //   0	236	1	paramString	String
    //   1	227	2	bool	boolean
    //   112	93	3	i	int
    //   181	9	4	localStringBuilder	StringBuilder
    //   231	1	4	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   13	113	218	java/lang/Exception
    //   130	137	218	java/lang/Exception
    //   140	146	218	java/lang/Exception
    //   146	166	218	java/lang/Exception
    //   178	215	218	java/lang/Exception
    //   119	130	231	java/lang/Exception
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/VideoEditedInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */