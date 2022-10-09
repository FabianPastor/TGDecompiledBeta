package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_paymentFormMethod extends TLObject {
    public static int constructor = -NUM;
    public String title;
    public String url;

    public static TLRPC$TL_paymentFormMethod TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_PaymentFormMethod", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_paymentFormMethod tLRPC$TL_paymentFormMethod = new TLRPC$TL_paymentFormMethod();
        tLRPC$TL_paymentFormMethod.readParams(abstractSerializedData, z);
        return tLRPC$TL_paymentFormMethod;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
        this.title = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeString(this.title);
    }
}
