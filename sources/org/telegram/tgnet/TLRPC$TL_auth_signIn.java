package org.telegram.tgnet;

public class TLRPC$TL_auth_signIn extends TLObject {
    public static int constructor = -NUM;
    public String phone_code;
    public String phone_code_hash;
    public String phone_number;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$auth_Authorization.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.phone_number);
        abstractSerializedData.writeString(this.phone_code_hash);
        abstractSerializedData.writeString(this.phone_code);
    }
}
