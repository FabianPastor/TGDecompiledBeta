package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_help_country extends TLObject {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$TL_help_countryCode> country_codes = new ArrayList<>();
    public String default_name;
    public int flags;
    public boolean hidden;
    public String iso2;
    public String name;

    public static TLRPC$TL_help_country TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_help_country tLRPC$TL_help_country = new TLRPC$TL_help_country();
            tLRPC$TL_help_country.readParams(abstractSerializedData, z);
            return tLRPC$TL_help_country;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_help_country", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.hidden = (readInt32 & 1) != 0;
        this.iso2 = abstractSerializedData.readString(z);
        this.default_name = abstractSerializedData.readString(z);
        if ((this.flags & 2) != 0) {
            this.name = abstractSerializedData.readString(z);
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_help_countryCode TLdeserialize = TLRPC$TL_help_countryCode.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.country_codes.add(TLdeserialize);
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
        int i = this.hidden ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeString(this.iso2);
        abstractSerializedData.writeString(this.default_name);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.name);
        }
        abstractSerializedData.writeInt32(NUM);
        int size = this.country_codes.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.country_codes.get(i2).serializeToStream(abstractSerializedData);
        }
    }
}
