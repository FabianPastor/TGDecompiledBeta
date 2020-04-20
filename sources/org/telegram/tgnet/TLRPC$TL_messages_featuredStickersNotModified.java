package org.telegram.tgnet;

public class TLRPC$TL_messages_featuredStickersNotModified extends TLRPC$messages_FeaturedStickers {
    public static int constructor = -NUM;
    public int count;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.count = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.count);
    }
}
