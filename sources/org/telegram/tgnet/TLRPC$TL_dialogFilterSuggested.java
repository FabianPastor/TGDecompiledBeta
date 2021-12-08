package org.telegram.tgnet;

public class TLRPC$TL_dialogFilterSuggested extends TLObject {
    public static int constructor = NUM;
    public String description;
    public TLRPC$TL_dialogFilter filter;

    public static TLRPC$TL_dialogFilterSuggested TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested = new TLRPC$TL_dialogFilterSuggested();
            tLRPC$TL_dialogFilterSuggested.readParams(abstractSerializedData, z);
            return tLRPC$TL_dialogFilterSuggested;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_dialogFilterSuggested", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.filter = TLRPC$TL_dialogFilter.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.description = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.filter.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.description);
    }
}
