package org.telegram.tgnet;

public class TLRPC$TL_account_resetPasswordOk extends TLRPC$account_ResetPasswordResult {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
