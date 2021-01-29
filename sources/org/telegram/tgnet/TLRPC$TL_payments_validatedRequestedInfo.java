package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_payments_validatedRequestedInfo extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public String id;
    public ArrayList<TLRPC$TL_shippingOption> shipping_options = new ArrayList<>();

    public static TLRPC$TL_payments_validatedRequestedInfo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_payments_validatedRequestedInfo tLRPC$TL_payments_validatedRequestedInfo = new TLRPC$TL_payments_validatedRequestedInfo();
            tLRPC$TL_payments_validatedRequestedInfo.readParams(abstractSerializedData, z);
            return tLRPC$TL_payments_validatedRequestedInfo;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_payments_validatedRequestedInfo", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.id = abstractSerializedData.readString(z);
        }
        if ((this.flags & 2) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$TL_shippingOption TLdeserialize = TLRPC$TL_shippingOption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.shipping_options.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
            }
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.id);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.shipping_options.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.shipping_options.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }
}
