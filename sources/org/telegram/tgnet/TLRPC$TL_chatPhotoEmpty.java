package org.telegram.tgnet;

public class TLRPC$TL_chatPhotoEmpty extends TLRPC$ChatPhoto {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
