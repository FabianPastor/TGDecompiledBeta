package org.telegram.tgnet;

public class TLRPC$TL_documentAttributeHasStickers extends TLRPC$DocumentAttribute {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
