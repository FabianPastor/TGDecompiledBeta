package org.telegram.tgnet;

public class TLRPC$TL_account_resetPasswordFailedWait extends TLRPC$account_ResetPasswordResult {
    public static int constructor = -NUM;
    public int retry_date;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.retry_date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.retry_date);
    }
}
