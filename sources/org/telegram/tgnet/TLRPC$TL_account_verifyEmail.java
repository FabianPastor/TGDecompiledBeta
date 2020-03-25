package org.telegram.tgnet;

public class TLRPC$TL_account_verifyEmail extends TLObject {
    public static int constructor = -NUM;
    public String code;
    public String email;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.email);
        abstractSerializedData.writeString(this.code);
    }
}
