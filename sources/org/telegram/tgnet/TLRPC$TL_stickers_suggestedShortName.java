package org.telegram.tgnet;

public class TLRPC$TL_stickers_suggestedShortName extends TLObject {
    public static int constructor = -NUM;
    public String short_name;

    public static TLRPC$TL_stickers_suggestedShortName TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_stickers_suggestedShortName tLRPC$TL_stickers_suggestedShortName = new TLRPC$TL_stickers_suggestedShortName();
            tLRPC$TL_stickers_suggestedShortName.readParams(abstractSerializedData, z);
            return tLRPC$TL_stickers_suggestedShortName;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_stickers_suggestedShortName", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.short_name = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.short_name);
    }
}
