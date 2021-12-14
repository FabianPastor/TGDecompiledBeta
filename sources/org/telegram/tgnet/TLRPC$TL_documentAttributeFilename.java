package org.telegram.tgnet;

public class TLRPC$TL_documentAttributeFilename extends TLRPC$DocumentAttribute {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.file_name = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.file_name);
    }
}
