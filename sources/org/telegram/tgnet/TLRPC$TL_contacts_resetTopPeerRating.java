package org.telegram.tgnet;

public class TLRPC$TL_contacts_resetTopPeerRating extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TopPeerCategory category;
    public TLRPC$InputPeer peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.category.serializeToStream(abstractSerializedData);
        this.peer.serializeToStream(abstractSerializedData);
    }
}
