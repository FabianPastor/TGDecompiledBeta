package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_invoice extends TLObject {
    public static int constructor = -NUM;
    public String currency;
    public boolean email_requested;
    public boolean email_to_provider;
    public int flags;
    public boolean flexible;
    public boolean name_requested;
    public boolean phone_requested;
    public boolean phone_to_provider;
    public ArrayList<TLRPC$TL_labeledPrice> prices = new ArrayList<>();
    public boolean shipping_address_requested;
    public boolean test;

    public static TLRPC$TL_invoice TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_invoice tLRPC$TL_invoice = new TLRPC$TL_invoice();
            tLRPC$TL_invoice.readParams(abstractSerializedData, z);
            return tLRPC$TL_invoice;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_invoice", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.test = (readInt32 & 1) != 0;
        this.name_requested = (readInt32 & 2) != 0;
        this.phone_requested = (readInt32 & 4) != 0;
        this.email_requested = (readInt32 & 8) != 0;
        this.shipping_address_requested = (readInt32 & 16) != 0;
        this.flexible = (readInt32 & 32) != 0;
        this.phone_to_provider = (readInt32 & 64) != 0;
        this.email_to_provider = (readInt32 & 128) != 0;
        this.currency = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_labeledPrice TLdeserialize = TLRPC$TL_labeledPrice.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.prices.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.test ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.name_requested ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.phone_requested ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.email_requested ? i3 | 8 : i3 & -9;
        this.flags = i4;
        int i5 = this.shipping_address_requested ? i4 | 16 : i4 & -17;
        this.flags = i5;
        int i6 = this.flexible ? i5 | 32 : i5 & -33;
        this.flags = i6;
        int i7 = this.phone_to_provider ? i6 | 64 : i6 & -65;
        this.flags = i7;
        int i8 = this.email_to_provider ? i7 | 128 : i7 & -129;
        this.flags = i8;
        abstractSerializedData.writeInt32(i8);
        abstractSerializedData.writeString(this.currency);
        abstractSerializedData.writeInt32(NUM);
        int size = this.prices.size();
        abstractSerializedData.writeInt32(size);
        for (int i9 = 0; i9 < size; i9++) {
            this.prices.get(i9).serializeToStream(abstractSerializedData);
        }
    }
}
