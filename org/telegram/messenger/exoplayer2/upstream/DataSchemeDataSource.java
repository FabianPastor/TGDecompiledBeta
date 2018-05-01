package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.util.Base64;
import java.io.IOException;
import java.net.URLDecoder;
import org.telegram.messenger.exoplayer2.ParserException;

public final class DataSchemeDataSource
  implements DataSource
{
  public static final String SCHEME_DATA = "data";
  private int bytesRead;
  private byte[] data;
  private DataSpec dataSpec;
  
  public void close()
    throws IOException
  {
    this.dataSpec = null;
    this.data = null;
  }
  
  public Uri getUri()
  {
    if (this.dataSpec != null) {}
    for (Uri localUri = this.dataSpec.uri;; localUri = null) {
      return localUri;
    }
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    this.dataSpec = paramDataSpec;
    Uri localUri = paramDataSpec.uri;
    paramDataSpec = localUri.getScheme();
    if (!"data".equals(paramDataSpec)) {
      throw new ParserException("Unsupported scheme: " + paramDataSpec);
    }
    paramDataSpec = localUri.getSchemeSpecificPart().split(",");
    if (paramDataSpec.length > 2) {
      throw new ParserException("Unexpected URI format: " + localUri);
    }
    localUri = paramDataSpec[1];
    if (paramDataSpec[0].contains(";base64")) {}
    for (;;)
    {
      try
      {
        this.data = Base64.decode(localUri, 0);
        return this.data.length;
      }
      catch (IllegalArgumentException paramDataSpec)
      {
        throw new ParserException("Error while parsing Base64 encoded string: " + localUri, paramDataSpec);
      }
      this.data = URLDecoder.decode(localUri, "US-ASCII").getBytes();
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      paramInt1 = 0;
    }
    for (;;)
    {
      return paramInt1;
      int i = this.data.length - this.bytesRead;
      if (i == 0)
      {
        paramInt1 = -1;
      }
      else
      {
        paramInt2 = Math.min(paramInt2, i);
        System.arraycopy(this.data, this.bytesRead, paramArrayOfByte, paramInt1, paramInt2);
        this.bytesRead += paramInt2;
        paramInt1 = paramInt2;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DataSchemeDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */