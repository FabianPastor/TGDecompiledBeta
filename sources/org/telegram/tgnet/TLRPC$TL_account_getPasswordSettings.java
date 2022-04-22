package org.telegram.tgnet;

public class TLRPC$TL_account_getPasswordSettings extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputCheckPasswordSRP password;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_account_passwordSettings.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.password.serializeToStream(abstractSerializedData);
    }
}
