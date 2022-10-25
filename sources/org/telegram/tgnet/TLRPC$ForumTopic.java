package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$ForumTopic extends TLObject {
    public static TLRPC$ForumTopic TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ForumTopic tLRPC$ForumTopic;
        if (i == 37687451) {
            tLRPC$ForumTopic = new TLRPC$ForumTopic() { // from class: org.telegram.tgnet.TLRPC$TL_forumTopicDeleted
                public static int constructor = 37687451;
                public int id;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.id = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.id);
                }
            };
        } else if (i == NUM) {
            tLRPC$ForumTopic = new TLRPC$TL_forumTopic_layer147();
        } else {
            tLRPC$ForumTopic = i != NUM ? null : new TLRPC$TL_forumTopic();
        }
        if (tLRPC$ForumTopic != null || !z) {
            if (tLRPC$ForumTopic != null) {
                tLRPC$ForumTopic.readParams(abstractSerializedData, z);
            }
            return tLRPC$ForumTopic;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ForumTopic", Integer.valueOf(i)));
    }
}
