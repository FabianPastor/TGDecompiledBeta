package org.telegram.messenger.exoplayer.upstream;

import android.net.Uri;
import java.util.Arrays;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class DataSpec
{
  public static final int FLAG_ALLOW_GZIP = 1;
  public final long absoluteStreamPosition;
  public final int flags;
  public final String key;
  public final long length;
  public final long position;
  public final byte[] postBody;
  public final Uri uri;
  
  public DataSpec(Uri paramUri)
  {
    this(paramUri, 0);
  }
  
  public DataSpec(Uri paramUri, int paramInt)
  {
    this(paramUri, 0L, -1L, null, paramInt);
  }
  
  public DataSpec(Uri paramUri, long paramLong1, long paramLong2, long paramLong3, String paramString, int paramInt)
  {
    this(paramUri, null, paramLong1, paramLong2, paramLong3, paramString, paramInt);
  }
  
  public DataSpec(Uri paramUri, long paramLong1, long paramLong2, String paramString)
  {
    this(paramUri, paramLong1, paramLong1, paramLong2, paramString, 0);
  }
  
  public DataSpec(Uri paramUri, long paramLong1, long paramLong2, String paramString, int paramInt)
  {
    this(paramUri, paramLong1, paramLong1, paramLong2, paramString, paramInt);
  }
  
  public DataSpec(Uri paramUri, byte[] paramArrayOfByte, long paramLong1, long paramLong2, long paramLong3, String paramString, int paramInt)
  {
    if (paramLong1 >= 0L)
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (paramLong2 < 0L) {
        break label103;
      }
      bool = true;
      label28:
      Assertions.checkArgument(bool);
      if ((paramLong3 <= 0L) && (paramLong3 != -1L)) {
        break label109;
      }
    }
    label103:
    label109:
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.uri = paramUri;
      this.postBody = paramArrayOfByte;
      this.absoluteStreamPosition = paramLong1;
      this.position = paramLong2;
      this.length = paramLong3;
      this.key = paramString;
      this.flags = paramInt;
      return;
      bool = false;
      break;
      bool = false;
      break label28;
    }
  }
  
  public String toString()
  {
    return "DataSpec[" + this.uri + ", " + Arrays.toString(this.postBody) + ", " + this.absoluteStreamPosition + ", " + this.position + ", " + this.length + ", " + this.key + ", " + this.flags + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/DataSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */