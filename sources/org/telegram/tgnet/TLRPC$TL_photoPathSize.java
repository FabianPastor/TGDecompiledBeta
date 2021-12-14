package org.telegram.tgnet;

public class TLRPC$TL_photoPathSize extends TLRPC$PhotoSize {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.type = abstractSerializedData.readString(z);
        this.bytes = abstractSerializedData.readByteArray(z);
        this.h = 50;
        this.w = 50;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.type);
        abstractSerializedData.writeByteArray(this.bytes);
    }
}
