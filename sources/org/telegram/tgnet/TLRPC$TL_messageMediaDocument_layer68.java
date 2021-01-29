package org.telegram.tgnet;

public class TLRPC$TL_messageMediaDocument_layer68 extends TLRPC$TL_messageMediaDocument {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.document = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.captionLegacy = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.document.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.captionLegacy);
    }
}
