package org.telegram.tgnet;

public class TLRPC$TL_messages_featuredStickersNotModified extends TLRPC$messages_FeaturedStickers {
    public static int constructor = 82699215;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
