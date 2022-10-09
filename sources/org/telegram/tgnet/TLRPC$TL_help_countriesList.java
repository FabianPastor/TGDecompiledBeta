package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_help_countriesList extends TLRPC$help_CountriesList {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$TL_help_country> countries = new ArrayList<>();
    public int hash;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            TLRPC$TL_help_country TLdeserialize = TLRPC$TL_help_country.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.countries.add(TLdeserialize);
        }
        this.hash = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
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
