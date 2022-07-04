package org.telegram.tgnet;

public class TLRPC$TL_contacts_toggleTopPeers extends TLObject {
    public static int constructor = -NUM;
    public boolean enabled;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeBool(this.enabled);
    }
}
