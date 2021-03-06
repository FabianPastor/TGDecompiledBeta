package org.telegram.tgnet;

public class TLRPC$TL_account_getPrivacy extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputPrivacyKey key;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_account_privacyRules.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.key.serializeToStream(abstractSerializedData);
    }
}
