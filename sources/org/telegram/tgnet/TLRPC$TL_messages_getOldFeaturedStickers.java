package org.telegram.tgnet;

public class TLRPC$TL_messages_getOldFeaturedStickers extends TLObject {
    public static int constructor = NUM;
    public int hash;
    public int limit;
    public int offset;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_FeaturedStickers.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.offset);
        abstractSerializedData.writeInt32(this.limit);
        abstractSerializedData.writeInt32(this.hash);
    }
}
