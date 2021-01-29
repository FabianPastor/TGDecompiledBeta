package org.telegram.tgnet;

public class TLRPC$TL_inputPaymentCredentialsGooglePay extends TLRPC$InputPaymentCredentials {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.payment_token = TLRPC$TL_dataJSON.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.payment_token.serializeToStream(abstractSerializedData);
    }
}
