package org.telegram.tgnet;

public class TLRPC$TL_textMarked extends TLRPC$RichText {
    public static int constructor = 55281185;
    public TLRPC$RichText text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.text.serializeToStream(abstractSerializedData);
    }
}
