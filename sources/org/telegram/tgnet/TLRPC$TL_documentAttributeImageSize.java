package org.telegram.tgnet;

public class TLRPC$TL_documentAttributeImageSize extends TLRPC$DocumentAttribute {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.w = abstractSerializedData.readInt32(z);
        this.h = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.w);
        abstractSerializedData.writeInt32(this.h);
    }
}
