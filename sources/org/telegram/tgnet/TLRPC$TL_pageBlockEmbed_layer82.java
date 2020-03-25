package org.telegram.tgnet;

public class TLRPC$TL_pageBlockEmbed_layer82 extends TLRPC$TL_pageBlockEmbed {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.full_width = (readInt32 & 1) != 0;
        if ((this.flags & 8) == 0) {
            z2 = false;
        }
        this.allow_scrolling = z2;
        if ((this.flags & 2) != 0) {
            this.url = abstractSerializedData.readString(z);
        }
        if ((this.flags & 4) != 0) {
            this.html = abstractSerializedData.readString(z);
        }
        if ((this.flags & 16) != 0) {
            this.poster_photo_id = abstractSerializedData.readInt64(z);
        }
        this.w = abstractSerializedData.readInt32(z);
        this.h = abstractSerializedData.readInt32(z);
        TLRPC$TL_pageCaption tLRPC$TL_pageCaption = new TLRPC$TL_pageCaption();
        this.caption = tLRPC$TL_pageCaption;
        tLRPC$TL_pageCaption.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.caption.credit = new TLRPC$TL_textEmpty();
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.full_width ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.allow_scrolling ? i | 8 : i & -9;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.url);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.html);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeInt64(this.poster_photo_id);
        }
        abstractSerializedData.writeInt32(this.w);
        abstractSerializedData.writeInt32(this.h);
        this.caption.text.serializeToStream(abstractSerializedData);
    }
}
