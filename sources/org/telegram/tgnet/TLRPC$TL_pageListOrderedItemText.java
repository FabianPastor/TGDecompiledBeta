package org.telegram.tgnet;

public class TLRPC$TL_pageListOrderedItemText extends TLRPC$PageListOrderedItem {
    public static int constructor = NUM;
    public String num;
    public TLRPC$RichText text;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.num = abstractSerializedData.readString(z);
        this.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.num);
        this.text.serializeToStream(abstractSerializedData);
    }
}
