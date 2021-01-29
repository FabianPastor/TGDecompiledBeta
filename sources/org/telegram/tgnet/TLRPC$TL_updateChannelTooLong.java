package org.telegram.tgnet;

public class TLRPC$TL_updateChannelTooLong extends TLRPC$Update {
    public static int constructor = -NUM;
    public int channel_id;
    public int flags;
    public int pts;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.channel_id = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.pts = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.channel_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.pts);
        }
    }
}
