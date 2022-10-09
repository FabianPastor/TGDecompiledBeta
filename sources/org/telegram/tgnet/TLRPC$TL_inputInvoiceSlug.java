package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputInvoiceSlug extends TLRPC$InputInvoice {
    public static int constructor = -NUM;
    public String slug;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.slug = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.slug);
    }
}
