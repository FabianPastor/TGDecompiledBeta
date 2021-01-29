package org.telegram.tgnet;

public class TLRPC$TL_textUrl extends TLRPC$RichText {
    public static int constructor = NUM;
    public TLRPC$RichText text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.url = abstractSerializedData.readString(z);
        this.webpage_id = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.text.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeInt64(this.webpage_id);
    }
}
