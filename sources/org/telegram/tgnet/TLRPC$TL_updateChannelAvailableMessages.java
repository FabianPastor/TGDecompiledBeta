package org.telegram.tgnet;

public class TLRPC$TL_updateChannelAvailableMessages extends TLRPC$Update {
    public static int constructor = -NUM;
    public int available_min_id;
    public long channel_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel_id = abstractSerializedData.readInt64(z);
        this.available_min_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.channel_id);
        abstractSerializedData.writeInt32(this.available_min_id);
    }
}
