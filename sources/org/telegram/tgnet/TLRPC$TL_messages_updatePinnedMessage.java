package org.telegram.tgnet;

public class TLRPC$TL_messages_updatePinnedMessage extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public int id;
    public TLRPC$InputPeer peer;
    public boolean silent;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.silent ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.id);
    }
}
