package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_help_countriesList extends TLRPC$help_CountriesList {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$TL_help_country> countries = new ArrayList<>();
    public int hash;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_help_country TLdeserialize = TLRPC$TL_help_country.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.countries.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.hash = abstractSerializedData.readInt32(z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.countries.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.countries.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(this.hash);
    }
}
