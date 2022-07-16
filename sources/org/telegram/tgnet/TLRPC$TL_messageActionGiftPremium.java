package org.telegram.tgnet;

public class TLRPC$TL_messageActionGiftPremium extends TLRPC$MessageAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.currency = abstractSerializedData.readString(z);
        this.amount = abstractSerializedData.readInt64(z);
        this.months = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.currency);
        abstractSerializedData.writeInt64(this.amount);
        abstractSerializedData.writeInt32(this.months);
    }
}
