package com.googlecode.mp4parser.boxes.cenc;

import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class CencEncryptingSampleList
  extends AbstractList<Sample>
{
  List<CencSampleAuxiliaryDataFormat> auxiliaryDataFormats;
  RangeStartMap<Integer, SecretKey> ceks = new RangeStartMap();
  Cipher cipher;
  private final String encryptionAlgo;
  List<Sample> parent;
  
  public CencEncryptingSampleList(RangeStartMap<Integer, SecretKey> paramRangeStartMap, List<Sample> paramList, List<CencSampleAuxiliaryDataFormat> paramList1, String paramString)
  {
    this.auxiliaryDataFormats = paramList1;
    this.ceks = paramRangeStartMap;
    this.encryptionAlgo = paramString;
    this.parent = paramList;
    try
    {
      if ("cenc".equals(paramString))
      {
        this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
        return;
      }
      if ("cbc1".equals(paramString))
      {
        this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
        return;
      }
    }
    catch (NoSuchAlgorithmException paramRangeStartMap)
    {
      throw new RuntimeException(paramRangeStartMap);
      throw new RuntimeException("Only cenc & cbc1 is supported as encryptionAlgo");
    }
    catch (NoSuchPaddingException paramRangeStartMap)
    {
      throw new RuntimeException(paramRangeStartMap);
    }
  }
  
  public CencEncryptingSampleList(SecretKey paramSecretKey, List<Sample> paramList, List<CencSampleAuxiliaryDataFormat> paramList1)
  {
    this(new RangeStartMap(Integer.valueOf(0), paramSecretKey), paramList, paramList1, "cenc");
  }
  
  public Sample get(int paramInt)
  {
    Sample localSample = (Sample)this.parent.get(paramInt);
    if (this.ceks.get(Integer.valueOf(paramInt)) != null) {
      return new EncryptedSampleImpl(localSample, (CencSampleAuxiliaryDataFormat)this.auxiliaryDataFormats.get(paramInt), this.cipher, (SecretKey)this.ceks.get(Integer.valueOf(paramInt)), null);
    }
    return localSample;
  }
  
  protected void initCipher(byte[] paramArrayOfByte, SecretKey paramSecretKey)
  {
    try
    {
      byte[] arrayOfByte = new byte[16];
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
      this.cipher.init(1, paramSecretKey, new IvParameterSpec(arrayOfByte));
      return;
    }
    catch (InvalidAlgorithmParameterException paramArrayOfByte)
    {
      throw new RuntimeException(paramArrayOfByte);
    }
    catch (InvalidKeyException paramArrayOfByte)
    {
      throw new RuntimeException(paramArrayOfByte);
    }
  }
  
  public int size()
  {
    return this.parent.size();
  }
  
  private class EncryptedSampleImpl
    implements Sample
  {
    private final SecretKey cek;
    private final CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat;
    private final Cipher cipher;
    private final Sample clearSample;
    
    static
    {
      if (!CencEncryptingSampleList.class.desiredAssertionStatus()) {}
      for (boolean bool = true;; bool = false)
      {
        $assertionsDisabled = bool;
        return;
      }
    }
    
    private EncryptedSampleImpl(Sample paramSample, CencSampleAuxiliaryDataFormat paramCencSampleAuxiliaryDataFormat, Cipher paramCipher, SecretKey paramSecretKey)
    {
      this.clearSample = paramSample;
      this.cencSampleAuxiliaryDataFormat = paramCencSampleAuxiliaryDataFormat;
      this.cipher = paramCipher;
      this.cek = paramSecretKey;
    }
    
    public ByteBuffer asByteBuffer()
    {
      ByteBuffer localByteBuffer1 = (ByteBuffer)this.clearSample.asByteBuffer().rewind();
      ByteBuffer localByteBuffer2 = ByteBuffer.allocate(localByteBuffer1.limit());
      Object localObject = this.cencSampleAuxiliaryDataFormat;
      CencEncryptingSampleList.this.initCipher(this.cencSampleAuxiliaryDataFormat.iv, this.cek);
      int j;
      int i;
      try
      {
        if (((CencSampleAuxiliaryDataFormat)localObject).pairs == null) {
          break label240;
        }
        localObject = ((CencSampleAuxiliaryDataFormat)localObject).pairs;
        j = localObject.length;
        i = 0;
      }
      catch (IllegalBlockSizeException localIllegalBlockSizeException)
      {
        throw new RuntimeException(localIllegalBlockSizeException);
        arrayOfByte2 = this.cipher.update(arrayOfByte1);
        if (($assertionsDisabled) || (arrayOfByte2.length == arrayOfByte1.length)) {
          break label229;
        }
        throw new AssertionError();
      }
      catch (BadPaddingException localBadPaddingException)
      {
        throw new RuntimeException(localBadPaddingException);
      }
      localByteBuffer1.rewind();
      localByteBuffer2.rewind();
      return localByteBuffer2;
      label229:
      label240:
      label355:
      label360:
      for (;;)
      {
        byte[] arrayOfByte1 = localObject[i];
        byte[] arrayOfByte2 = new byte[arrayOfByte1.clear()];
        localByteBuffer1.get(arrayOfByte2);
        localByteBuffer2.put(arrayOfByte2);
        if (arrayOfByte1.encrypted() > 0L)
        {
          arrayOfByte1 = new byte[CastUtils.l2i(arrayOfByte1.encrypted())];
          localByteBuffer1.get(arrayOfByte1);
          assert (arrayOfByte1.length % 16 == 0);
          localByteBuffer2.put(arrayOfByte2);
          break label355;
          localObject = new byte[localBadPaddingException.limit()];
          localBadPaddingException.get((byte[])localObject);
          if ("cbc1".equals(CencEncryptingSampleList.this.encryptionAlgo))
          {
            i = localObject.length / 16 * 16;
            localByteBuffer2.put(this.cipher.doFinal((byte[])localObject, 0, i));
            localByteBuffer2.put((byte[])localObject, i, localObject.length - i);
            break;
          }
          if (!"cenc".equals(CencEncryptingSampleList.this.encryptionAlgo)) {
            break;
          }
          localByteBuffer2.put(this.cipher.doFinal((byte[])localObject));
          break;
        }
        for (;;)
        {
          if (i < j) {
            break label360;
          }
          break;
          i += 1;
        }
      }
    }
    
    public long getSize()
    {
      return this.clearSample.getSize();
    }
    
    public void writeTo(WritableByteChannel paramWritableByteChannel)
      throws IOException
    {
      ByteBuffer localByteBuffer = (ByteBuffer)this.clearSample.asByteBuffer().rewind();
      CencEncryptingSampleList.this.initCipher(this.cencSampleAuxiliaryDataFormat.iv, this.cek);
      for (;;)
      {
        int j;
        try
        {
          if ((this.cencSampleAuxiliaryDataFormat.pairs == null) || (this.cencSampleAuxiliaryDataFormat.pairs.length <= 0)) {
            continue;
          }
          arrayOfByte = new byte[localByteBuffer.limit()];
          localByteBuffer.get(arrayOfByte);
          i = 0;
          arrayOfPair = this.cencSampleAuxiliaryDataFormat.pairs;
          int m = arrayOfPair.length;
          j = 0;
          if (j < m) {
            continue;
          }
          paramWritableByteChannel.write(ByteBuffer.wrap(arrayOfByte));
        }
        catch (IllegalBlockSizeException paramWritableByteChannel)
        {
          byte[] arrayOfByte;
          int i;
          CencSampleAuxiliaryDataFormat.Pair[] arrayOfPair;
          CencSampleAuxiliaryDataFormat.Pair localPair;
          int k;
          throw new RuntimeException(paramWritableByteChannel);
          if (!"cenc".equals(CencEncryptingSampleList.this.encryptionAlgo)) {
            continue;
          }
          paramWritableByteChannel.write(ByteBuffer.wrap(this.cipher.doFinal(arrayOfByte)));
          continue;
        }
        catch (BadPaddingException paramWritableByteChannel)
        {
          throw new RuntimeException(paramWritableByteChannel);
        }
        catch (ShortBufferException paramWritableByteChannel)
        {
          throw new RuntimeException(paramWritableByteChannel);
        }
        localByteBuffer.rewind();
        return;
        localPair = arrayOfPair[j];
        k = i + localPair.clear();
        i = k;
        if (localPair.encrypted() > 0L)
        {
          this.cipher.update(arrayOfByte, k, CastUtils.l2i(localPair.encrypted()), arrayOfByte, k);
          i = (int)(k + localPair.encrypted());
          break label341;
          arrayOfByte = new byte[localByteBuffer.limit()];
          localByteBuffer.get(arrayOfByte);
          if ("cbc1".equals(CencEncryptingSampleList.this.encryptionAlgo))
          {
            i = arrayOfByte.length / 16 * 16;
            paramWritableByteChannel.write(ByteBuffer.wrap(this.cipher.doFinal(arrayOfByte, 0, i)));
            paramWritableByteChannel.write(ByteBuffer.wrap(arrayOfByte, i, arrayOfByte.length - i));
            continue;
          }
        }
        label341:
        j += 1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/cenc/CencEncryptingSampleList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */