package org.telegram.tgnet;

public class TLRPC$TL_pageBlockPhoto_layer82 extends TLRPC$TL_pageBlockPhoto {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.photo_id = abstractSerializedData.readInt64(z);
        TLRPC$TL_pageCaption tLRPC$TL_pageCaption = new TLRPC$TL_pageCaption();
        this.caption = tLRPC$TL_pageCaption;
        tLRPC$TL_pageCaption.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.caption.credit = new TLRPC$TL_textEmpty();
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.photo_id);
        this.caption.text.serializeToStream(abstractSerializedData);
    }
}
