package org.telegram.tgnet;

public class TLRPC$TL_account_sendConfirmPhoneCode extends TLObject {
    public static int constructor = NUM;
    public String hash;
    public TLRPC$TL_codeSettings settings;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_auth_sentCode.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.hash);
        this.settings.serializeToStream(abstractSerializedData);
    }
}
