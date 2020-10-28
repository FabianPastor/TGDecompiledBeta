package org.telegram.tgnet;

public class TLRPC$TL_inputPaymentCredentialsAndroidPay extends TLRPC$InputPaymentCredentials {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.payment_token = TLRPC$TL_dataJSON.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.google_transaction_id = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.payment_token.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.google_transaction_id);
    }
}
