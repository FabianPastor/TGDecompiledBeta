package org.telegram.tgnet;

public class TLRPC$TL_photos_uploadProfilePhoto extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputFile file;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_photos_photo.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.file.serializeToStream(abstractSerializedData);
    }
}
