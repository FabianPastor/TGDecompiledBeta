package org.telegram.messenger;

import org.telegram.tgnet.SerializedData;

public class MessageKeyData {
    public byte[] aesIv;
    public byte[] aesKey;

    public static MessageKeyData generateMessageKeyData(byte[] authKey, byte[] messageKey, boolean incoming, int version) {
        byte[] bArr = authKey;
        byte[] bArr2 = messageKey;
        MessageKeyData keyData = new MessageKeyData();
        if (bArr == null || bArr.length == 0) {
            keyData.aesIv = null;
            keyData.aesKey = null;
            return keyData;
        }
        int x = incoming ? 8 : 0;
        switch (version) {
            case 1:
                SerializedData data = new SerializedData();
                data.writeBytes(bArr2);
                data.writeBytes(authKey, x, 32);
                byte[] sha1_a = Utilities.computeSHA1(data.toByteArray());
                data.cleanup();
                SerializedData data2 = new SerializedData();
                data2.writeBytes(authKey, x + 32, 16);
                data2.writeBytes(bArr2);
                data2.writeBytes(authKey, x + 48, 16);
                byte[] sha1_b = Utilities.computeSHA1(data2.toByteArray());
                data2.cleanup();
                SerializedData data3 = new SerializedData();
                data3.writeBytes(authKey, x + 64, 32);
                data3.writeBytes(bArr2);
                byte[] sha1_c = Utilities.computeSHA1(data3.toByteArray());
                data3.cleanup();
                SerializedData data4 = new SerializedData();
                data4.writeBytes(bArr2);
                data4.writeBytes(authKey, x + 96, 32);
                byte[] sha1_d = Utilities.computeSHA1(data4.toByteArray());
                data4.cleanup();
                SerializedData data5 = new SerializedData();
                data5.writeBytes(sha1_a, 0, 8);
                data5.writeBytes(sha1_b, 8, 12);
                data5.writeBytes(sha1_c, 4, 12);
                keyData.aesKey = data5.toByteArray();
                data5.cleanup();
                SerializedData data6 = new SerializedData();
                data6.writeBytes(sha1_a, 8, 12);
                data6.writeBytes(sha1_b, 0, 8);
                data6.writeBytes(sha1_c, 16, 4);
                data6.writeBytes(sha1_d, 0, 8);
                keyData.aesIv = data6.toByteArray();
                data6.cleanup();
                break;
            case 2:
                SerializedData data7 = new SerializedData();
                data7.writeBytes(bArr2, 0, 16);
                data7.writeBytes(authKey, x, 36);
                byte[] sha256_a = Utilities.computeSHA256(data7.toByteArray());
                data7.cleanup();
                SerializedData data8 = new SerializedData();
                data8.writeBytes(authKey, x + 40, 36);
                data8.writeBytes(bArr2, 0, 16);
                byte[] sha256_b = Utilities.computeSHA256(data8.toByteArray());
                data8.cleanup();
                SerializedData data9 = new SerializedData();
                data9.writeBytes(sha256_a, 0, 8);
                data9.writeBytes(sha256_b, 8, 16);
                data9.writeBytes(sha256_a, 24, 8);
                keyData.aesKey = data9.toByteArray();
                data9.cleanup();
                SerializedData data10 = new SerializedData();
                data10.writeBytes(sha256_b, 0, 8);
                data10.writeBytes(sha256_a, 8, 16);
                data10.writeBytes(sha256_b, 24, 8);
                keyData.aesIv = data10.toByteArray();
                data10.cleanup();
                break;
        }
        return keyData;
    }
}
