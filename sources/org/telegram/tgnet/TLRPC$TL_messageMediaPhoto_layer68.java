package org.telegram.tgnet;

public class TLRPC$TL_messageMediaPhoto_layer68 extends TLRPC$TL_messageMediaPhoto {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.captionLegacy = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.photo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.captionLegacy);
    }
}
