package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_channels_getForumTopicsByID extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public ArrayList<Integer> topics = new ArrayList<>();

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_forumTopics.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.topics.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.topics.get(i).intValue());
        }
    }
}
