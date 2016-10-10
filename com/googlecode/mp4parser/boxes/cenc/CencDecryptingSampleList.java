package com.googlecode.mp4parser.boxes.cenc;

import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import java.io.PrintStream;
import java.nio.ByteBuffer;
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
import javax.crypto.spec.IvParameterSpec;

public class CencDecryptingSampleList
  extends AbstractList<Sample>
{
  String encryptionAlgo;
  RangeStartMap<Integer, SecretKey> keys = new RangeStartMap();
  List<Sample> parent;
  List<CencSampleAuxiliaryDataFormat> sencInfo;
  
  public CencDecryptingSampleList(RangeStartMap<Integer, SecretKey> paramRangeStartMap, List<Sample> paramList, List<CencSampleAuxiliaryDataFormat> paramList1, String paramString)
  {
    this.sencInfo = paramList1;
    this.keys = paramRangeStartMap;
    this.parent = paramList;
    this.encryptionAlgo = paramString;
  }
  
  public CencDecryptingSampleList(SecretKey paramSecretKey, List<Sample> paramList, List<CencSampleAuxiliaryDataFormat> paramList1)
  {
    this(new RangeStartMap(Integer.valueOf(0), paramSecretKey), paramList, paramList1, "cenc");
  }
  
  public Sample get(int paramInt)
  {
    Object localObject1;
    ByteBuffer localByteBuffer1;
    ByteBuffer localByteBuffer2;
    Object localObject2;
    Cipher localCipher;
    if (this.keys.get(Integer.valueOf(paramInt)) != null)
    {
      localObject1 = (Sample)this.parent.get(paramInt);
      localByteBuffer1 = ((Sample)localObject1).asByteBuffer();
      localByteBuffer1.rewind();
      localByteBuffer2 = ByteBuffer.allocate(localByteBuffer1.limit());
      localObject2 = (CencSampleAuxiliaryDataFormat)this.sencInfo.get(paramInt);
      localCipher = getCipher((SecretKey)this.keys.get(Integer.valueOf(paramInt)), ((CencSampleAuxiliaryDataFormat)localObject2).iv);
    }
    for (;;)
    {
      try
      {
        if ((((CencSampleAuxiliaryDataFormat)localObject2).pairs == null) || (((CencSampleAuxiliaryDataFormat)localObject2).pairs.length <= 0)) {
          continue;
        }
        localObject2 = ((CencSampleAuxiliaryDataFormat)localObject2).pairs;
        int i = localObject2.length;
        paramInt = 0;
        if (paramInt < i) {
          continue;
        }
        if (localByteBuffer1.remaining() > 0) {
          System.err.println("Decrypted sample but still data remaining: " + ((Sample)localObject1).getSize());
        }
        localByteBuffer2.put(localCipher.doFinal());
      }
      catch (IllegalBlockSizeException localIllegalBlockSizeException)
      {
        byte[] arrayOfByte;
        int j;
        int k;
        throw new RuntimeException(localIllegalBlockSizeException);
        if (!"cenc".equals(this.encryptionAlgo)) {
          continue;
        }
        localByteBuffer2.put(localCipher.doFinal((byte[])localObject1));
        continue;
      }
      catch (BadPaddingException localBadPaddingException)
      {
        throw new RuntimeException(localBadPaddingException);
      }
      localByteBuffer1.rewind();
      localByteBuffer2.rewind();
      return new SampleImpl(localByteBuffer2);
      arrayOfByte = localObject2[paramInt];
      j = arrayOfByte.clear();
      k = CastUtils.l2i(arrayOfByte.encrypted());
      arrayOfByte = new byte[j];
      localByteBuffer1.get(arrayOfByte);
      localByteBuffer2.put(arrayOfByte);
      if (k > 0)
      {
        arrayOfByte = new byte[k];
        localByteBuffer1.get(arrayOfByte);
        localByteBuffer2.put(localCipher.update(arrayOfByte));
        break label417;
        localObject1 = new byte[localByteBuffer1.limit()];
        localByteBuffer1.get((byte[])localObject1);
        if ("cbc1".equals(this.encryptionAlgo))
        {
          paramInt = localObject1.length / 16 * 16;
          localByteBuffer2.put(localCipher.doFinal((byte[])localObject1, 0, paramInt));
          localByteBuffer2.put((byte[])localObject1, paramInt, localObject1.length - paramInt);
          continue;
        }
        return (Sample)this.parent.get(paramInt);
      }
      label417:
      paramInt += 1;
    }
  }
  
  Cipher getCipher(SecretKey paramSecretKey, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    try
    {
      if ("cenc".equals(this.encryptionAlgo))
      {
        paramArrayOfByte = Cipher.getInstance("AES/CTR/NoPadding");
        paramArrayOfByte.init(2, paramSecretKey, new IvParameterSpec(arrayOfByte));
        return paramArrayOfByte;
      }
      if ("cbc1".equals(this.encryptionAlgo))
      {
        paramArrayOfByte = Cipher.getInstance("AES/CBC/NoPadding");
        paramArrayOfByte.init(2, paramSecretKey, new IvParameterSpec(arrayOfByte));
        return paramArrayOfByte;
      }
      throw new RuntimeException("Only cenc & cbc1 is supported as encryptionAlgo");
    }
    catch (NoSuchAlgorithmException paramSecretKey)
    {
      throw new RuntimeException(paramSecretKey);
    }
    catch (NoSuchPaddingException paramSecretKey)
    {
      throw new RuntimeException(paramSecretKey);
    }
    catch (InvalidAlgorithmParameterException paramSecretKey)
    {
      throw new RuntimeException(paramSecretKey);
    }
    catch (InvalidKeyException paramSecretKey)
    {
      throw new RuntimeException(paramSecretKey);
    }
  }
  
  public int size()
  {
    return this.parent.size();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/cenc/CencDecryptingSampleList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */