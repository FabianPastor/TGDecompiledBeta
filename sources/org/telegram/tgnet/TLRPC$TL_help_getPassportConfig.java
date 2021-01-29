package org.telegram.tgnet;

public class TLRPC$TL_help_getPassportConfig extends TLObject {
    public static int constructor = -NUM;
    public int hash;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$help_PassportConfig.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.hash);
    }
}
