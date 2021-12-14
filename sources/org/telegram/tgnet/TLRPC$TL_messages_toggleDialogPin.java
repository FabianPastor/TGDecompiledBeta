package org.telegram.tgnet;

public class TLRPC$TL_messages_toggleDialogPin extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public TLRPC$InputDialogPeer peer;
    public boolean pinned;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.pinned ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
    }
}
