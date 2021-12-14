package org.telegram.tgnet;

public class TLRPC$TL_updateMessageID extends TLRPC$Update {
    public static int constructor = NUM;
    public int id;
    public long random_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt32(z);
        this.random_id = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt64(this.random_id);
    }
}
