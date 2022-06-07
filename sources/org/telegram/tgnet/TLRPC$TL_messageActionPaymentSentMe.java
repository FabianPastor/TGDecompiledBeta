package org.telegram.tgnet;

public class TLRPC$TL_messageActionPaymentSentMe extends TLRPC$MessageAction {
    public static int constructor = -NUM;
    public int flags;
    public TLRPC$TL_paymentRequestedInfo info;
    public byte[] payload;
    public String shipping_option_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.recurring_init = (readInt32 & 4) != 0;
        if ((readInt32 & 8) != 0) {
            z2 = true;
        }
        this.recurring_used = z2;
        this.currency = abstractSerializedData.readString(z);
        this.total_amount = abstractSerializedData.readInt64(z);
        this.payload = abstractSerializedData.readByteArray(z);
        if ((this.flags & 1) != 0) {
            this.info = TLRPC$TL_paymentRequestedInfo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 2) != 0) {
            this.shipping_option_id = abstractSerializedData.readString(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.recurring_init ? this.flags | 4 : this.flags & -5;
        this.flags = i;
        int i2 = this.recurring_used ? i | 8 : i & -9;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeString(this.currency);
        abstractSerializedData.writeInt64(this.total_amount);
        abstractSerializedData.writeByteArray(this.payload);
        if ((this.flags & 1) != 0) {
            this.info.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.shipping_option_id);
        }
    }
}
