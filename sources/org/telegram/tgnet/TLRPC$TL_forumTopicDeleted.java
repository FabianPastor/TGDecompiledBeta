package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_forumTopicDeleted extends TLRPC$TL_forumTopic {
    public static int constructor = 37687451;

    @Override // org.telegram.tgnet.TLRPC$TL_forumTopic, org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLRPC$TL_forumTopic, org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.id);
    }
}
