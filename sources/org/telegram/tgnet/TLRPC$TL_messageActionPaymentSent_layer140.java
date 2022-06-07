package org.telegram.tgnet;

public class TLRPC$TL_messageActionPaymentSent_layer140 extends TLRPC$TL_messageActionPaymentSent {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.currency = abstractSerializedData.readString(z);
        this.total_amount = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.currency);
        abstractSerializedData.writeInt64(this.total_amount);
    }
}
