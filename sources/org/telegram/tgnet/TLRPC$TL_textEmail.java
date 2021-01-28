package org.telegram.tgnet;

public class TLRPC$TL_textEmail extends TLRPC$RichText {
    public static int constructor = -NUM;
    public TLRPC$RichText text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.email = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.text.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.email);
    }
}
