package org.telegram.messenger.exoplayer2.upstream.crypto;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public final class AesCipherDataSink
  implements DataSink
{
  private AesFlushingCipher cipher;
  private final byte[] scratch;
  private final byte[] secretKey;
  private final DataSink wrappedDataSink;
  
  public AesCipherDataSink(byte[] paramArrayOfByte, DataSink paramDataSink)
  {
    this(paramArrayOfByte, paramDataSink, null);
  }
  
  public AesCipherDataSink(byte[] paramArrayOfByte1, DataSink paramDataSink, byte[] paramArrayOfByte2)
  {
    this.wrappedDataSink = paramDataSink;
    this.secretKey = paramArrayOfByte1;
    this.scratch = paramArrayOfByte2;
  }
  
  public void close()
    throws IOException
  {
    this.cipher = null;
    this.wrappedDataSink.close();
  }
  
  public void open(DataSpec paramDataSpec)
    throws IOException
  {
    this.wrappedDataSink.open(paramDataSpec);
    long l = CryptoUtil.getFNV64Hash(paramDataSpec.key);
    this.cipher = new AesFlushingCipher(1, this.secretKey, l, paramDataSpec.absoluteStreamPosition);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.scratch == null)
    {
      this.cipher.updateInPlace(paramArrayOfByte, paramInt1, paramInt2);
      this.wrappedDataSink.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    for (;;)
    {
      return;
      int i = 0;
      while (i < paramInt2)
      {
        int j = Math.min(paramInt2 - i, this.scratch.length);
        this.cipher.update(paramArrayOfByte, paramInt1 + i, j, this.scratch, 0);
        this.wrappedDataSink.write(this.scratch, 0, j);
        i += j;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/crypto/AesCipherDataSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */