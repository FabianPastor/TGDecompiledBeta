package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_channelAdminLogEventActionPinTopic extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public int flags;
    public TLRPC$ForumTopic new_topic;
    public TLRPC$ForumTopic prev_topic;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.prev_topic = TLRPC$ForumTopic.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 2) != 0) {
            this.new_topic = TLRPC$ForumTopic.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            this.prev_topic.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            this.new_topic.serializeToStream(abstractSerializedData);
        }
    }
}
