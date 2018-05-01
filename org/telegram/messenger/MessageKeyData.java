package org.telegram.messenger;

import org.telegram.tgnet.SerializedData;

public class MessageKeyData
{
  public byte[] aesIv;
  public byte[] aesKey;
  
  public static MessageKeyData generateMessageKeyData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean, int paramInt)
  {
    MessageKeyData localMessageKeyData = new MessageKeyData();
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte1.length == 0))
    {
      localMessageKeyData.aesIv = null;
      localMessageKeyData.aesKey = null;
    }
    for (;;)
    {
      return localMessageKeyData;
      if (paramBoolean) {}
      for (int i = 8;; i = 0) {
        switch (paramInt)
        {
        default: 
          break;
        case 1: 
          localObject1 = new SerializedData();
          ((SerializedData)localObject1).writeBytes(paramArrayOfByte2);
          ((SerializedData)localObject1).writeBytes(paramArrayOfByte1, i, 32);
          arrayOfByte = Utilities.computeSHA1(((SerializedData)localObject1).toByteArray());
          ((SerializedData)localObject1).cleanup();
          Object localObject2 = new SerializedData();
          ((SerializedData)localObject2).writeBytes(paramArrayOfByte1, i + 32, 16);
          ((SerializedData)localObject2).writeBytes(paramArrayOfByte2);
          ((SerializedData)localObject2).writeBytes(paramArrayOfByte1, i + 48, 16);
          localObject1 = Utilities.computeSHA1(((SerializedData)localObject2).toByteArray());
          ((SerializedData)localObject2).cleanup();
          SerializedData localSerializedData = new SerializedData();
          localSerializedData.writeBytes(paramArrayOfByte1, i + 64, 32);
          localSerializedData.writeBytes(paramArrayOfByte2);
          localObject2 = Utilities.computeSHA1(localSerializedData.toByteArray());
          localSerializedData.cleanup();
          localSerializedData = new SerializedData();
          localSerializedData.writeBytes(paramArrayOfByte2);
          localSerializedData.writeBytes(paramArrayOfByte1, i + 96, 32);
          paramArrayOfByte1 = Utilities.computeSHA1(localSerializedData.toByteArray());
          localSerializedData.cleanup();
          paramArrayOfByte2 = new SerializedData();
          paramArrayOfByte2.writeBytes(arrayOfByte, 0, 8);
          paramArrayOfByte2.writeBytes((byte[])localObject1, 8, 12);
          paramArrayOfByte2.writeBytes((byte[])localObject2, 4, 12);
          localMessageKeyData.aesKey = paramArrayOfByte2.toByteArray();
          paramArrayOfByte2.cleanup();
          paramArrayOfByte2 = new SerializedData();
          paramArrayOfByte2.writeBytes(arrayOfByte, 8, 12);
          paramArrayOfByte2.writeBytes((byte[])localObject1, 0, 8);
          paramArrayOfByte2.writeBytes((byte[])localObject2, 16, 4);
          paramArrayOfByte2.writeBytes(paramArrayOfByte1, 0, 8);
          localMessageKeyData.aesIv = paramArrayOfByte2.toByteArray();
          paramArrayOfByte2.cleanup();
          break;
        }
      }
      Object localObject1 = new SerializedData();
      ((SerializedData)localObject1).writeBytes(paramArrayOfByte2, 0, 16);
      ((SerializedData)localObject1).writeBytes(paramArrayOfByte1, i, 36);
      byte[] arrayOfByte = Utilities.computeSHA256(((SerializedData)localObject1).toByteArray());
      ((SerializedData)localObject1).cleanup();
      localObject1 = new SerializedData();
      ((SerializedData)localObject1).writeBytes(paramArrayOfByte1, i + 40, 36);
      ((SerializedData)localObject1).writeBytes(paramArrayOfByte2, 0, 16);
      paramArrayOfByte1 = Utilities.computeSHA256(((SerializedData)localObject1).toByteArray());
      ((SerializedData)localObject1).cleanup();
      paramArrayOfByte2 = new SerializedData();
      paramArrayOfByte2.writeBytes(arrayOfByte, 0, 8);
      paramArrayOfByte2.writeBytes(paramArrayOfByte1, 8, 16);
      paramArrayOfByte2.writeBytes(arrayOfByte, 24, 8);
      localMessageKeyData.aesKey = paramArrayOfByte2.toByteArray();
      paramArrayOfByte2.cleanup();
      paramArrayOfByte2 = new SerializedData();
      paramArrayOfByte2.writeBytes(paramArrayOfByte1, 0, 8);
      paramArrayOfByte2.writeBytes(arrayOfByte, 8, 16);
      paramArrayOfByte2.writeBytes(paramArrayOfByte1, 24, 8);
      localMessageKeyData.aesIv = paramArrayOfByte2.toByteArray();
      paramArrayOfByte2.cleanup();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MessageKeyData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */