package org.telegram.tgnet;

public class TLRPC$TL_premiumGiftOption extends TLObject {
    public static int constructor = NUM;
    public long amount;
    public String bot_url;
    public String currency;
    public int flags;
    public int months;
    public String store_product;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.months = abstractSerializedData.readInt32(z);
        this.currency = abstractSerializedData.readString(z);
        this.amount = abstractSerializedData.readInt64(z);
        this.bot_url = abstractSerializedData.readString(z);
        if ((this.flags & 1) != 0) {
            this.store_product = abstractSerializedData.readString(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.months);
        abstractSerializedData.writeString(this.currency);
        abstractSerializedData.writeInt64(this.amount);
        abstractSerializedData.writeString(this.bot_url);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.store_product);
        }
    }
}
