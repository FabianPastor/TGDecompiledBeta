package org.telegram.tgnet;

public class TLRPC$TL_contacts_resolveUsername extends TLObject {
    public static int constructor = -NUM;
    public String username;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_contacts_resolvedPeer.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.username);
    }
}
