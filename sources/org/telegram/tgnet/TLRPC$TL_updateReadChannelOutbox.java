package org.telegram.tgnet;

public class TLRPC$TL_updateReadChannelOutbox extends TLRPC$Update {
    public static int constructor = NUM;
    public int channel_id;
    public int max_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel_id = abstractSerializedData.readInt32(z);
        this.max_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.channel_id);
        abstractSerializedData.writeInt32(this.max_id);
    }
}
