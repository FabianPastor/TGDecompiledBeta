package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_searchCounter extends TLObject {
    public static int constructor = -NUM;
    public int count;
    public TLRPC$MessagesFilter filter;
    public int flags;
    public boolean inexact;

    public static TLRPC$TL_messages_searchCounter TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_searchCounter", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_messages_searchCounter tLRPC$TL_messages_searchCounter = new TLRPC$TL_messages_searchCounter();
        tLRPC$TL_messages_searchCounter.readParams(abstractSerializedData, z);
        return tLRPC$TL_messages_searchCounter;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        this.inexact = (readInt32 & 2) != 0;
        this.filter = TLRPC$MessagesFilter.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.count = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.inexact ? this.flags | 2 : this.flags & (-3);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.filter.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.count);
    }
}
