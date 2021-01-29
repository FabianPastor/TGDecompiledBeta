package org.telegram.tgnet;

public class TLRPC$TL_messages_getRecentStickers extends TLObject {
    public static int constructor = NUM;
    public boolean attached;
    public int flags;
    public int hash;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_RecentStickers.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.attached ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(this.hash);
    }
}
