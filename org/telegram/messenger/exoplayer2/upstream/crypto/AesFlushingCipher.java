package org.telegram.messenger.exoplayer2.upstream.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class AesFlushingCipher
{
  private final int blockSize;
  private final Cipher cipher;
  private final byte[] flushedBlock;
  private int pendingXorBytes;
  private final byte[] zerosBlock;
  
  public AesFlushingCipher(int paramInt, byte[] paramArrayOfByte, long paramLong1, long paramLong2)
  {
    try
    {
      this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
      this.blockSize = this.cipher.getBlockSize();
      this.zerosBlock = new byte[this.blockSize];
      this.flushedBlock = new byte[this.blockSize];
      long l = paramLong2 / this.blockSize;
      int i = (int)(paramLong2 % this.blockSize);
      Cipher localCipher = this.cipher;
      SecretKeySpec localSecretKeySpec = new javax/crypto/spec/SecretKeySpec;
      localSecretKeySpec.<init>(paramArrayOfByte, this.cipher.getAlgorithm().split("/")[0]);
      paramArrayOfByte = new javax/crypto/spec/IvParameterSpec;
      paramArrayOfByte.<init>(getInitializationVector(paramLong1, l));
      localCipher.init(paramInt, localSecretKeySpec, paramArrayOfByte);
      if (i != 0) {
        updateInPlace(new byte[i], 0, i);
      }
      return;
    }
    catch (InvalidAlgorithmParameterException paramArrayOfByte)
    {
      throw new RuntimeException(paramArrayOfByte);
    }
    catch (NoSuchAlgorithmException paramArrayOfByte)
    {
      for (;;) {}
    }
    catch (NoSuchPaddingException paramArrayOfByte)
    {
      for (;;) {}
    }
    catch (InvalidKeyException paramArrayOfByte)
    {
      for (;;) {}
    }
  }
  
  private byte[] getInitializationVector(long paramLong1, long paramLong2)
  {
    return ByteBuffer.allocate(16).putLong(paramLong1).putLong(paramLong2).array();
  }
  
  private int nonFlushingUpdate(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    try
    {
      paramInt1 = this.cipher.update(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
      return paramInt1;
    }
    catch (ShortBufferException paramArrayOfByte1)
    {
      throw new RuntimeException(paramArrayOfByte1);
    }
  }
  
  public void update(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    int i;
    do
    {
      if (this.pendingXorBytes <= 0) {
        break;
      }
      paramArrayOfByte2[paramInt3] = ((byte)(byte)(paramArrayOfByte1[paramInt1] ^ this.flushedBlock[(this.blockSize - this.pendingXorBytes)]));
      paramInt3++;
      paramInt1++;
      this.pendingXorBytes -= 1;
      i = paramInt2 - 1;
      paramInt2 = i;
    } while (i != 0);
    for (;;)
    {
      return;
      paramInt1 = nonFlushingUpdate(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
      if (paramInt2 != paramInt1)
      {
        i = paramInt2 - paramInt1;
        if (i < this.blockSize)
        {
          bool = true;
          Assertions.checkState(bool);
          this.pendingXorBytes = (this.blockSize - i);
          if (nonFlushingUpdate(this.zerosBlock, 0, this.pendingXorBytes, this.flushedBlock, 0) != this.blockSize) {
            break label184;
          }
        }
        label184:
        for (boolean bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          paramInt2 = 0;
          for (paramInt1 = paramInt3 + paramInt1; paramInt2 < i; paramInt1++)
          {
            paramArrayOfByte2[paramInt1] = ((byte)this.flushedBlock[paramInt2]);
            paramInt2++;
          }
          bool = false;
          break;
        }
      }
    }
  }
  
  public void updateInPlace(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    update(paramArrayOfByte, paramInt1, paramInt2, paramArrayOfByte, paramInt1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/crypto/AesFlushingCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */