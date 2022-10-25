package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_channelAdminLogEventActionEditTopic extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = -NUM;
    public TLRPC$ForumTopic new_topic;
    public TLRPC$ForumTopic prev_topic;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_topic = TLRPC$ForumTopic.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.new_topic = TLRPC$ForumTopic.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.prev_topic.serializeToStream(abstractSerializedData);
        this.new_topic.serializeToStream(abstractSerializedData);
    }
}
