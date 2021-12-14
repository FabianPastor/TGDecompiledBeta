package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_payments_bankCardData extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$TL_bankCardOpenUrl> open_urls = new ArrayList<>();
    public String title;

    public static TLRPC$TL_payments_bankCardData TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_payments_bankCardData tLRPC$TL_payments_bankCardData = new TLRPC$TL_payments_bankCardData();
            tLRPC$TL_payments_bankCardData.readParams(abstractSerializedData, z);
            return tLRPC$TL_payments_bankCardData;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_payments_bankCardData", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.title = abstractSerializedData.readString(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_bankCardOpenUrl TLdeserialize = TLRPC$TL_bankCardOpenUrl.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.open_urls.add(TLdeserialize);
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
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeInt32(NUM);
        int size = this.open_urls.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.open_urls.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
