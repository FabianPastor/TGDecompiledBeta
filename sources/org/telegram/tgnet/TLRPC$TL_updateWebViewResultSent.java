package org.telegram.tgnet;

public class TLRPC$TL_updateWebViewResultSent extends TLRPC$Update {
    public static int constructor = NUM;
    public long query_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.query_id = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.query_id);
    }
}
