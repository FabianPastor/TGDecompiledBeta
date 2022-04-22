package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_shippingOption extends TLObject {
    public static int constructor = -NUM;
    public String id;
    public ArrayList<TLRPC$TL_labeledPrice> prices = new ArrayList<>();
    public String title;

    public static TLRPC$TL_shippingOption TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_shippingOption tLRPC$TL_shippingOption = new TLRPC$TL_shippingOption();
            tLRPC$TL_shippingOption.readParams(abstractSerializedData, z);
            return tLRPC$TL_shippingOption;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_shippingOption", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readString(z);
        this.title = abstractSerializedData.readString(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_labeledPrice TLdeserialize = TLRPC$TL_labeledPrice.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.prices.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.id);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeInt32(NUM);
        int size = this.prices.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.prices.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
