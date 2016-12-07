package org.telegram.messenger;

import org.telegram.tgnet.SerializedData;

public class MessageKeyData {
    public byte[] aesIv;
    public byte[] aesKey;

    public static MessageKeyData generateMessageKeyData(byte[] authKey, byte[] messageKey, boolean incoming) {
        MessageKeyData keyData = new MessageKeyData();
        if (authKey == null || authKey.length == 0) {
            keyData.aesIv = null;
            keyData.aesKey = null;
        } else {
            int x;
            if (incoming) {
                x = 8;
            } else {
                x = 0;
            }
            SerializedData data = new SerializedData();
            data.writeBytes(messageKey);
            data.writeBytes(authKey, x, 32);
            byte[] sha1_a = Utilities.computeSHA1(data.toByteArray());
            data.cleanup();
            data = new SerializedData();
            data.writeBytes(authKey, x + 32, 16);
            data.writeBytes(messageKey);
            data.writeBytes(authKey, x + 48, 16);
            byte[] sha1_b = Utilities.computeSHA1(data.toByteArray());
            data.cleanup();
            data = new SerializedData();
            data.writeBytes(authKey, x + 64, 32);
            data.writeBytes(messageKey);
            byte[] sha1_c = Utilities.computeSHA1(data.toByteArray());
            data.cleanup();
            data = new SerializedData();
            data.writeBytes(messageKey);
            data.writeBytes(authKey, x + 96, 32);
            byte[] sha1_d = Utilities.computeSHA1(data.toByteArray());
            data.cleanup();
            data = new SerializedData();
            data.writeBytes(sha1_a, 0, 8);
            data.writeBytes(sha1_b, 8, 12);
            data.writeBytes(sha1_c, 4, 12);
            keyData.aesKey = data.toByteArray();
            data.cleanup();
            data = new SerializedData();
            data.writeBytes(sha1_a, 8, 12);
            data.writeBytes(sha1_b, 0, 8);
            data.writeBytes(sha1_c, 16, 4);
            data.writeBytes(sha1_d, 0, 8);
            keyData.aesIv = data.toByteArray();
            data.cleanup();
        }
        return keyData;
    }
}
