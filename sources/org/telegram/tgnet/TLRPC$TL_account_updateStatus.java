package org.telegram.tgnet;

public class TLRPC$TL_account_updateStatus extends TLObject {
    public static int constructor = NUM;
    public boolean offline;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeBool(this.offline);
    }
}
