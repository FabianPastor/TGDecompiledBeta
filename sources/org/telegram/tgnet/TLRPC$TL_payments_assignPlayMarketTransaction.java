package org.telegram.tgnet;

public class TLRPC$TL_payments_assignPlayMarketTransaction extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputStorePaymentPurpose purpose;
    public TLRPC$TL_dataJSON receipt;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.receipt.serializeToStream(abstractSerializedData);
        this.purpose.serializeToStream(abstractSerializedData);
    }
}
