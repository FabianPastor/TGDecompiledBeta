package org.telegram.tgnet;

public class TLRPC$TL_inputPhotoEmpty extends TLRPC$InputPhoto {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
