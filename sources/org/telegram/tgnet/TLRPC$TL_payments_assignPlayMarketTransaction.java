package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_payments_assignPlayMarketTransaction extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputStorePaymentPurpose purpose;
    public TLRPC$TL_dataJSON receipt;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.receipt.serializeToStream(abstractSerializedData);
        this.purpose.serializeToStream(abstractSerializedData);
    }
}
