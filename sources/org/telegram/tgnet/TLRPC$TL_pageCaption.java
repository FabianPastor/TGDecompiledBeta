package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_pageCaption extends TLObject {
    public static int constructor = NUM;
    public TLRPC$RichText credit;
    public TLRPC$RichText text;

    public static TLRPC$TL_pageCaption TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_pageCaption", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_pageCaption tLRPC$TL_pageCaption = new TLRPC$TL_pageCaption();
        tLRPC$TL_pageCaption.readParams(abstractSerializedData, z);
        return tLRPC$TL_pageCaption;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.text = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.credit = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.text.serializeToStream(abstractSerializedData);
        this.credit.serializeToStream(abstractSerializedData);
    }
}
