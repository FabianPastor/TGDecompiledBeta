package org.telegram.messenger;

import org.telegram.tgnet.SerializedData;

public class MessageKeyData {
    public byte[] aesIv;
    public byte[] aesKey;

    public static MessageKeyData generateMessageKeyData(byte[] bArr, byte[] bArr2, boolean z, int i) {
        MessageKeyData messageKeyData = new MessageKeyData();
        if (bArr == null || bArr.length == 0) {
            messageKeyData.aesIv = null;
            messageKeyData.aesKey = null;
            return messageKeyData;
        }
        int i2 = z ? 8 : 0;
        SerializedData serializedData;
        byte[] computeSHA1;
        SerializedData serializedData2;
        if (i == 1) {
            serializedData = new SerializedData();
            serializedData.writeBytes(bArr2);
            serializedData.writeBytes(bArr, i2, 32);
            computeSHA1 = Utilities.computeSHA1(serializedData.toByteArray());
            serializedData.cleanup();
            serializedData = new SerializedData();
            serializedData.writeBytes(bArr, i2 + 32, 16);
            serializedData.writeBytes(bArr2);
            serializedData.writeBytes(bArr, i2 + 48, 16);
            byte[] computeSHA12 = Utilities.computeSHA1(serializedData.toByteArray());
            serializedData.cleanup();
            serializedData = new SerializedData();
            serializedData.writeBytes(bArr, i2 + 64, 32);
            serializedData.writeBytes(bArr2);
            byte[] computeSHA13 = Utilities.computeSHA1(serializedData.toByteArray());
            serializedData.cleanup();
            serializedData = new SerializedData();
            serializedData.writeBytes(bArr2);
            serializedData.writeBytes(bArr, i2 + 96, 32);
            bArr = Utilities.computeSHA1(serializedData.toByteArray());
            serializedData.cleanup();
            serializedData2 = new SerializedData();
            serializedData2.writeBytes(computeSHA1, 0, 8);
            serializedData2.writeBytes(computeSHA12, 8, 12);
            serializedData2.writeBytes(computeSHA13, 4, 12);
            messageKeyData.aesKey = serializedData2.toByteArray();
            serializedData2.cleanup();
            serializedData2 = new SerializedData();
            serializedData2.writeBytes(computeSHA1, 8, 12);
            serializedData2.writeBytes(computeSHA12, 0, 8);
            serializedData2.writeBytes(computeSHA13, 16, 4);
            serializedData2.writeBytes(bArr, 0, 8);
            messageKeyData.aesIv = serializedData2.toByteArray();
            serializedData2.cleanup();
        } else if (i == 2) {
            serializedData = new SerializedData();
            serializedData.writeBytes(bArr2, 0, 16);
            serializedData.writeBytes(bArr, i2, 36);
            computeSHA1 = Utilities.computeSHA256(serializedData.toByteArray());
            serializedData.cleanup();
            serializedData = new SerializedData();
            serializedData.writeBytes(bArr, i2 + 40, 36);
            serializedData.writeBytes(bArr2, 0, 16);
            bArr = Utilities.computeSHA256(serializedData.toByteArray());
            serializedData.cleanup();
            serializedData2 = new SerializedData();
            serializedData2.writeBytes(computeSHA1, 0, 8);
            serializedData2.writeBytes(bArr, 8, 16);
            serializedData2.writeBytes(computeSHA1, 24, 8);
            messageKeyData.aesKey = serializedData2.toByteArray();
            serializedData2.cleanup();
            serializedData2 = new SerializedData();
            serializedData2.writeBytes(bArr, 0, 8);
            serializedData2.writeBytes(computeSHA1, 8, 16);
            serializedData2.writeBytes(bArr, 24, 8);
            messageKeyData.aesIv = serializedData2.toByteArray();
            serializedData2.cleanup();
        }
        return messageKeyData;
    }
}
