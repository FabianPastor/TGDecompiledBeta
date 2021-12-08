package org.telegram.tgnet;

public class TLRPC$TL_account_setAccountTTL extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_accountDaysTTL ttl;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.ttl.serializeToStream(abstractSerializedData);
    }
}
