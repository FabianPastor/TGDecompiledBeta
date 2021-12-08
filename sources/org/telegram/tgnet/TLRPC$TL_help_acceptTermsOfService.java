package org.telegram.tgnet;

public class TLRPC$TL_help_acceptTermsOfService extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$TL_dataJSON id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.id.serializeToStream(abstractSerializedData);
    }
}
