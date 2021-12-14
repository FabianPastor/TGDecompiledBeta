package org.telegram.tgnet;

public class TLRPC$TL_account_getTmpPassword extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputCheckPasswordSRP password;
    public int period;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_account_tmpPassword.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.password.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.period);
    }
}
