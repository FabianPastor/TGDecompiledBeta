package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_help_promoDataEmpty extends TLRPC$help_PromoData {
    public static int constructor = -NUM;
    public int expires;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.expires = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.expires);
    }
}
