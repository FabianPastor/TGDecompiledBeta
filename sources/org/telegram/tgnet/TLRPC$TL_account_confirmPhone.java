package org.telegram.tgnet;

public class TLRPC$TL_account_confirmPhone extends TLObject {
    public static int constructor = NUM;
    public String phone_code;
    public String phone_code_hash;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.phone_code_hash);
        abstractSerializedData.writeString(this.phone_code);
    }
}
