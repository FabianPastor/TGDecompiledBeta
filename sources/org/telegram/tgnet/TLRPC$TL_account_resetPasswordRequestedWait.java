package org.telegram.tgnet;

public class TLRPC$TL_account_resetPasswordRequestedWait extends TLRPC$account_ResetPasswordResult {
    public static int constructor = -NUM;
    public int until_date;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.until_date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.until_date);
    }
}
