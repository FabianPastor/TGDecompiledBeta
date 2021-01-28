package org.telegram.tgnet;

public class TLRPC$TL_messages_getRecentLocations extends TLObject {
    public static int constructor = -NUM;
    public int hash;
    public int limit;
    public TLRPC$InputPeer peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_Messages.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.limit);
        abstractSerializedData.writeInt32(this.hash);
    }
}
