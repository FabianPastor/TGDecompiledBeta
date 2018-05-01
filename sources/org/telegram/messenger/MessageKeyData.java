package org.telegram.messenger;

import org.telegram.tgnet.SerializedData;

public class MessageKeyData {
    public byte[] aesIv;
    public byte[] aesKey;

    public static MessageKeyData generateMessageKeyData(byte[] bArr, byte[] bArr2, boolean z, int i) {
        MessageKeyData messageKeyData = new MessageKeyData();
        if (bArr != null) {
            if (bArr.length != 0) {
                z = z ? true : false;
                byte[] computeSHA1;
                switch (i) {
                    case 1:
                        i = new SerializedData();
                        i.writeBytes(bArr2);
                        i.writeBytes(bArr, z, 32);
                        computeSHA1 = Utilities.computeSHA1(i.toByteArray());
                        i.cleanup();
                        i = new SerializedData();
                        i.writeBytes(bArr, 32 + z, 16);
                        i.writeBytes(bArr2);
                        i.writeBytes(bArr, 48 + z, 16);
                        byte[] computeSHA12 = Utilities.computeSHA1(i.toByteArray());
                        i.cleanup();
                        i = new SerializedData();
                        i.writeBytes(bArr, 64 + z, 32);
                        i.writeBytes(bArr2);
                        byte[] computeSHA13 = Utilities.computeSHA1(i.toByteArray());
                        i.cleanup();
                        i = new SerializedData();
                        i.writeBytes(bArr2);
                        i.writeBytes(bArr, 96 + z, 32);
                        bArr = Utilities.computeSHA1(i.toByteArray());
                        i.cleanup();
                        bArr2 = new SerializedData();
                        bArr2.writeBytes(computeSHA1, 0, 8);
                        bArr2.writeBytes(computeSHA12, 8, 12);
                        bArr2.writeBytes(computeSHA13, 4, 12);
                        messageKeyData.aesKey = bArr2.toByteArray();
                        bArr2.cleanup();
                        bArr2 = new SerializedData();
                        bArr2.writeBytes(computeSHA1, 8, 12);
                        bArr2.writeBytes(computeSHA12, 0, 8);
                        bArr2.writeBytes(computeSHA13, 16, 4);
                        bArr2.writeBytes(bArr, 0, 8);
                        messageKeyData.aesIv = bArr2.toByteArray();
                        bArr2.cleanup();
                        break;
                    case 2:
                        i = new SerializedData();
                        i.writeBytes(bArr2, 0, 16);
                        i.writeBytes(bArr, z, 36);
                        computeSHA1 = Utilities.computeSHA256(i.toByteArray());
                        i.cleanup();
                        i = new SerializedData();
                        i.writeBytes(bArr, 40 + z, 36);
                        i.writeBytes(bArr2, 0, 16);
                        bArr = Utilities.computeSHA256(i.toByteArray());
                        i.cleanup();
                        bArr2 = new SerializedData();
                        bArr2.writeBytes(computeSHA1, 0, 8);
                        bArr2.writeBytes(bArr, 8, 16);
                        bArr2.writeBytes(computeSHA1, 24, 8);
                        messageKeyData.aesKey = bArr2.toByteArray();
                        bArr2.cleanup();
                        bArr2 = new SerializedData();
                        bArr2.writeBytes(bArr, 0, 8);
                        bArr2.writeBytes(computeSHA1, 8, 16);
                        bArr2.writeBytes(bArr, 24, 8);
                        messageKeyData.aesIv = bArr2.toByteArray();
                        bArr2.cleanup();
                        break;
                    default:
                        break;
                }
                return messageKeyData;
            }
        }
        messageKeyData.aesIv = null;
        messageKeyData.aesKey = null;
        return messageKeyData;
    }
}
