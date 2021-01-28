package org.telegram.tgnet;

public class TLRPC$TL_pageBlockPullquote extends TLRPC$PageBlock {
    public static int constructor = NUM;
    public TLRPC$RichText caption;
    public TLRPC$RichText text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.caption = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.text.serializeToStream(abstractSerializedData);
        this.caption.serializeToStream(abstractSerializedData);
    }
}
