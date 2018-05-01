package org.telegram.messenger.secretmedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.telegram.messenger.Utilities;

public class EncryptedFileInputStream
  extends FileInputStream
{
  private int fileOffset;
  private byte[] iv = new byte[16];
  private byte[] key = new byte[32];
  
  public EncryptedFileInputStream(File paramFile1, File paramFile2)
    throws Exception
  {
    super(paramFile1);
    paramFile1 = new RandomAccessFile(paramFile2, "r");
    paramFile1.read(this.key, 0, 32);
    paramFile1.read(this.iv, 0, 16);
    paramFile1.close();
  }
  
  public static void decryptBytesWithKeyFile(byte[] paramArrayOfByte, int paramInt1, int paramInt2, File paramFile)
    throws Exception
  {
    byte[] arrayOfByte1 = new byte[32];
    byte[] arrayOfByte2 = new byte[16];
    paramFile = new RandomAccessFile(paramFile, "r");
    paramFile.read(arrayOfByte1, 0, 32);
    paramFile.read(arrayOfByte2, 0, 16);
    paramFile.close();
    Utilities.aesCtrDecryptionByteArray(paramArrayOfByte, arrayOfByte1, arrayOfByte2, paramInt1, paramInt2, 0);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
    Utilities.aesCtrDecryptionByteArray(paramArrayOfByte, this.key, this.iv, paramInt1, paramInt2, this.fileOffset);
    this.fileOffset += paramInt2;
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    this.fileOffset = ((int)(this.fileOffset + paramLong));
    return super.skip(paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/secretmedia/EncryptedFileInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */