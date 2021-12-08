package org.telegram.tgnet;

public class TLRPC$TL_account_sendVerifyPhoneCode extends TLObject {
    public static int constructor = -NUM;
    public String phone_number;
    public TLRPC$TL_codeSettings settings;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_auth_sentCode.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.phone_number);
        this.settings.serializeToStream(abstractSerializedData);
    }
}
