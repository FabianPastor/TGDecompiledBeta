package org.telegram.tgnet;

public class TLRPC$TL_help_getPromoData extends TLObject {
    public static int constructor = -NUM;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$help_PromoData.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
