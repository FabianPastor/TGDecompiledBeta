package org.telegram.tgnet;

public class TLRPC$TL_account_getAuthorizationForm extends TLObject {
    public static int constructor = -NUM;
    public int bot_id;
    public String public_key;
    public String scope;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_account_authorizationForm.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.bot_id);
        abstractSerializedData.writeString(this.scope);
        abstractSerializedData.writeString(this.public_key);
    }
}
