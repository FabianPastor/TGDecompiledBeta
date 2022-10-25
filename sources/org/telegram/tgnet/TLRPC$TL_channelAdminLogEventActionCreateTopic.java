package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_channelAdminLogEventActionCreateTopic extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = NUM;
    public TLRPC$ForumTopic topic;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.topic = TLRPC$ForumTopic.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.topic.serializeToStream(abstractSerializedData);
    }
}
