package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_payments_getPaymentForm extends TLObject {
    public static int constructor = NUM;
    public int flags;
    public TLRPC$InputInvoice invoice;
    public TLRPC$TL_dataJSON theme_params;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_payments_paymentForm.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.invoice.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            this.theme_params.serializeToStream(abstractSerializedData);
        }
    }
}
