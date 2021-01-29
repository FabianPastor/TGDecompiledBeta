package org.telegram.tgnet;

public class TLRPC$TL_updateReadChannelDiscussionOutbox extends TLRPC$Update {
    public static int constructor = NUM;
    public int channel_id;
    public int read_max_id;
    public int top_msg_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel_id = abstractSerializedData.readInt32(z);
        this.top_msg_id = abstractSerializedData.readInt32(z);
        this.read_max_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.channel_id);
        abstractSerializedData.writeInt32(this.top_msg_id);
        abstractSerializedData.writeInt32(this.read_max_id);
    }
}
