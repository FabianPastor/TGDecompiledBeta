package org.telegram.tgnet;

public class TLRPC$TL_pageBlockVideo_layer82 extends TLRPC$TL_pageBlockVideo {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.autoplay = (readInt32 & 1) != 0;
        if ((this.flags & 2) == 0) {
            z2 = false;
        }
        this.loop = z2;
        this.video_id = abstractSerializedData.readInt64(z);
        TLRPC$TL_pageCaption tLRPC$TL_pageCaption = new TLRPC$TL_pageCaption();
        this.caption = tLRPC$TL_pageCaption;
        tLRPC$TL_pageCaption.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.caption.credit = new TLRPC$TL_textEmpty();
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.autoplay ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.loop ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt64(this.video_id);
        this.caption.text.serializeToStream(abstractSerializedData);
    }
}
