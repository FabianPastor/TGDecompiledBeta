package org.telegram.messenger;

import org.telegram.tgnet.SerializedData;

public class MessageKeyData {
    public byte[] aesIv;
    public byte[] aesKey;

    public static MessageKeyData generateMessageKeyData(byte[] authKey, byte[] messageKey, boolean incoming, int version) {
        byte[] bArr = authKey;
        byte[] bArr2 = messageKey;
        MessageKeyData keyData = new MessageKeyData();
        if (bArr != null) {
            if (bArr.length != 0) {
                int x = incoming ? 8 : 0;
                SerializedData data;
                byte[] sha1_a;
                byte[] sha1_d;
                switch (version) {
                    case 1:
                        data = new SerializedData();
                        data.writeBytes(bArr2);
                        data.writeBytes(bArr, x, 32);
                        sha1_a = Utilities.computeSHA1(data.toByteArray());
                        data.cleanup();
                        data = new SerializedData();
                        data.writeBytes(bArr, 32 + x, 16);
                        data.writeBytes(bArr2);
                        data.writeBytes(bArr, 48 + x, 16);
                        byte[] sha1_b = Utilities.computeSHA1(data.toByteArray());
                        data.cleanup();
                        data = new SerializedData();
                        data.writeBytes(bArr, 64 + x, 32);
                        data.writeBytes(bArr2);
                        byte[] sha1_c = Utilities.computeSHA1(data.toByteArray());
                        data.cleanup();
                        data = new SerializedData();
                        data.writeBytes(bArr2);
                        data.writeBytes(bArr, 96 + x, 32);
                        sha1_d = Utilities.computeSHA1(data.toByteArray());
                        data.cleanup();
                        data = new SerializedData();
                        data.writeBytes(sha1_a, 0, 8);
                        data.writeBytes(sha1_b, 8, 12);
                        data.writeBytes(sha1_c, 4, 12);
                        keyData.aesKey = data.toByteArray();
                        data.cleanup();
                        SerializedData data2 = new SerializedData();
                        data2.writeBytes(sha1_a, 8, 12);
                        data2.writeBytes(sha1_b, 0, 8);
                        data2.writeBytes(sha1_c, 16, 4);
                        data2.writeBytes(sha1_d, 0, 8);
                        keyData.aesIv = data2.toByteArray();
                        data2.cleanup();
                        break;
                    case 2:
                        data = new SerializedData();
                        data.writeBytes(bArr2, 0, 16);
                        data.writeBytes(bArr, x, 36);
                        sha1_a = Utilities.computeSHA256(data.toByteArray());
                        data.cleanup();
                        data = new SerializedData();
                        data.writeBytes(bArr, 40 + x, 36);
                        data.writeBytes(bArr2, 0, 16);
                        sha1_d = Utilities.computeSHA256(data.toByteArray());
                        data.cleanup();
                        data = new SerializedData();
                        data.writeBytes(sha1_a, 0, 8);
                        data.writeBytes(sha1_d, 8, 16);
                        data.writeBytes(sha1_a, 24, 8);
                        keyData.aesKey = data.toByteArray();
                        data.cleanup();
                        data = new SerializedData();
                        data.writeBytes(sha1_d, 0, 8);
                        data.writeBytes(sha1_a, 8, 16);
                        data.writeBytes(sha1_d, 24, 8);
                        keyData.aesIv = data.toByteArray();
                        data.cleanup();
                        break;
                    default:
                        break;
                }
                return keyData;
            }
        }
        keyData.aesIv = null;
        keyData.aesKey = null;
        return keyData;
    }
}
