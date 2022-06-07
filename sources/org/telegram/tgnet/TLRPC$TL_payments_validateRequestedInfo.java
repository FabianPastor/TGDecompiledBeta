package org.telegram.tgnet;

public class TLRPC$TL_payments_validateRequestedInfo extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public TLRPC$TL_paymentRequestedInfo info;
    public TLRPC$InputInvoice invoice;
    public boolean save;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_payments_validatedRequestedInfo.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.save ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.invoice.serializeToStream(abstractSerializedData);
        this.info.serializeToStream(abstractSerializedData);
    }
}
