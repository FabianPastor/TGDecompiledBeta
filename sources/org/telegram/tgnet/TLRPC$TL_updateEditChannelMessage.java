package org.telegram.tgnet;

public class TLRPC$TL_updateEditChannelMessage extends TLRPC$Update {
    public static int constructor = NUM;
    public TLRPC$Message message;
    public int pts;
    public int pts_count;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.message = TLRPC$Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.pts = abstractSerializedData.readInt32(z);
        this.pts_count = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.message.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.pts);
        abstractSerializedData.writeInt32(this.pts_count);
    }
}
