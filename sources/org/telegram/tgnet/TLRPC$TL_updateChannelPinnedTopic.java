package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateChannelPinnedTopic extends TLRPC$Update {
    public static int constructor = -NUM;
    public long channel_id;
    public int flags;
    public int topic_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.channel_id = abstractSerializedData.readInt64(z);
        if ((this.flags & 1) != 0) {
            this.topic_id = abstractSerializedData.readInt32(z);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt64(this.channel_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.topic_id);
        }
    }
}
