package org.telegram.tgnet;

public class TLRPC$TL_inputWallPaperSlug extends TLRPC$InputWallPaper {
    public static int constructor = NUM;
    public String slug;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.slug = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.slug);
    }
}
