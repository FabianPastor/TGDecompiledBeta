package org.telegram.tgnet;

public class TLRPC$TL_pageBlockAuthorDate extends TLRPC$PageBlock {
    public static int constructor = -NUM;
    public TLRPC$RichText author;
    public int published_date;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.author = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.published_date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.author.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.published_date);
    }
}
