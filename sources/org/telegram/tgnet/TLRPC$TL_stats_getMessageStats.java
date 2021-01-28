package org.telegram.tgnet;

public class TLRPC$TL_stats_getMessageStats extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public boolean dark;
    public int flags;
    public int msg_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_stats_messageStats.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.dark ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
    }
}
