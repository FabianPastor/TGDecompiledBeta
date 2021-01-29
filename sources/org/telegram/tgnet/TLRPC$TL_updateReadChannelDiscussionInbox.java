package org.telegram.tgnet;

public class TLRPC$TL_updateReadChannelDiscussionInbox extends TLRPC$Update {
    public static int constructor = NUM;
    public int broadcast_id;
    public int broadcast_post;
    public int channel_id;
    public int flags;
    public int read_max_id;
    public int top_msg_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.channel_id = abstractSerializedData.readInt32(z);
        this.top_msg_id = abstractSerializedData.readInt32(z);
        this.read_max_id = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.broadcast_id = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 1) != 0) {
            this.broadcast_post = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.channel_id);
        abstractSerializedData.writeInt32(this.top_msg_id);
        abstractSerializedData.writeInt32(this.read_max_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.broadcast_id);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.broadcast_post);
        }
    }
}
