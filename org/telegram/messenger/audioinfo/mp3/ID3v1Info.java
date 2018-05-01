package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.AudioInfo;

public class ID3v1Info
  extends AudioInfo
{
  public ID3v1Info(InputStream paramInputStream)
    throws IOException
  {
    if (isID3v1StartPosition(paramInputStream))
    {
      this.brand = "ID3";
      this.version = "1.0";
      paramInputStream = readBytes(paramInputStream, 128);
      this.title = extractString(paramInputStream, 3, 30);
      this.artist = extractString(paramInputStream, 33, 30);
      this.album = extractString(paramInputStream, 63, 30);
    }
    try
    {
      this.year = Short.parseShort(extractString(paramInputStream, 93, 4));
      this.comment = extractString(paramInputStream, 97, 30);
      ID3v1Genre localID3v1Genre = ID3v1Genre.getGenre(paramInputStream[127]);
      if (localID3v1Genre != null) {
        this.genre = localID3v1Genre.getDescription();
      }
      if ((paramInputStream[125] == 0) && (paramInputStream[126] != 0))
      {
        this.version = "1.1";
        this.track = ((short)(short)(paramInputStream[126] & 0xFF));
      }
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;)
      {
        this.year = ((short)0);
      }
    }
  }
  
  /* Error */
  public static boolean isID3v1StartPosition(InputStream paramInputStream)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_3
    //   2: invokevirtual 84	java/io/InputStream:mark	(I)V
    //   5: aload_0
    //   6: invokevirtual 88	java/io/InputStream:read	()I
    //   9: bipush 84
    //   11: if_icmpne +31 -> 42
    //   14: aload_0
    //   15: invokevirtual 88	java/io/InputStream:read	()I
    //   18: bipush 65
    //   20: if_icmpne +22 -> 42
    //   23: aload_0
    //   24: invokevirtual 88	java/io/InputStream:read	()I
    //   27: istore_1
    //   28: iload_1
    //   29: bipush 71
    //   31: if_icmpne +11 -> 42
    //   34: iconst_1
    //   35: istore_2
    //   36: aload_0
    //   37: invokevirtual 91	java/io/InputStream:reset	()V
    //   40: iload_2
    //   41: ireturn
    //   42: iconst_0
    //   43: istore_2
    //   44: goto -8 -> 36
    //   47: astore_3
    //   48: aload_0
    //   49: invokevirtual 91	java/io/InputStream:reset	()V
    //   52: aload_3
    //   53: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	54	0	paramInputStream	InputStream
    //   27	5	1	i	int
    //   35	9	2	bool	boolean
    //   47	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	28	47	finally
  }
  
  String extractString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (;;)
    {
      try
      {
        str = new java/lang/String;
        str.<init>(paramArrayOfByte, paramInt1, paramInt2, "ISO-8859-1");
        paramInt1 = str.indexOf(0);
        if (paramInt1 >= 0) {
          continue;
        }
        paramArrayOfByte = str;
      }
      catch (Exception paramArrayOfByte)
      {
        String str;
        paramArrayOfByte = "";
        continue;
      }
      return paramArrayOfByte;
      paramArrayOfByte = str.substring(0, paramInt1);
    }
  }
  
  byte[] readBytes(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    int i = 0;
    byte[] arrayOfByte = new byte[paramInt];
    while (i < paramInt)
    {
      int j = paramInputStream.read(arrayOfByte, i, paramInt - i);
      if (j > 0) {
        i += j;
      } else {
        throw new EOFException();
      }
    }
    return arrayOfByte;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/mp3/ID3v1Info.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */