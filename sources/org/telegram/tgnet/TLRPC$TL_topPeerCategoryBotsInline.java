package org.telegram.tgnet;

public class TLRPC$TL_topPeerCategoryBotsInline extends TLRPC$TopPeerCategory {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
