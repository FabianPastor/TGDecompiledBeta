package org.telegram.tgnet;

public class TLRPC$TL_account_deleteAccount extends TLObject {
    public static int constructor = NUM;
    public String reason;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.reason);
    }
}
