package org.telegram.tgnet;

public class TLRPC$TL_attachMenuPeerTypeChat extends TLRPC$AttachMenuPeerType {
    public static int constructor = 84480319;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
