package org.telegram.tgnet;

public class TLRPC$TL_account_setAuthorizationTTL extends TLObject {
    public static int constructor = -NUM;
    public int authorization_ttl_days;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.authorization_ttl_days);
    }
}
