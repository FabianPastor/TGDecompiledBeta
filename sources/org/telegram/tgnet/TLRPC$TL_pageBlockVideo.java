package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_pageBlockVideo extends TLRPC$PageBlock {
    public static int constructor = NUM;
    public boolean autoplay;
    public TLRPC$TL_pageCaption caption;
    public int flags;
    public boolean loop;
    public long video_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.autoplay = (readInt32 & 1) != 0;
        if ((readInt32 & 2) != 0) {
            z2 = true;
        }
        this.loop = z2;
        this.video_id = abstractSerializedData.readInt64(z);
        this.caption = TLRPC$TL_pageCaption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.autoplay ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        int i2 = this.loop ? i | 2 : i & (-3);
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt64(this.video_id);
        this.caption.serializeToStream(abstractSerializedData);
    }
}
