package org.telegram.tgnet;

public class TLRPC$TL_payments_getBankCardData extends TLObject {
    public static int constructor = NUM;
    public String number;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_payments_bankCardData.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.number);
    }
}
