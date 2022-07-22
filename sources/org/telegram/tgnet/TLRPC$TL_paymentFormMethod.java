package org.telegram.tgnet;

public class TLRPC$TL_paymentFormMethod extends TLObject {
    public static int constructor = -NUM;
    public String title;
    public String url;

    public static TLRPC$TL_paymentFormMethod TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_paymentFormMethod tLRPC$TL_paymentFormMethod = new TLRPC$TL_paymentFormMethod();
            tLRPC$TL_paymentFormMethod.readParams(abstractSerializedData, z);
            return tLRPC$TL_paymentFormMethod;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_PaymentFormMethod", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
        this.title = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeString(this.title);
    }
}
