package org.telegram.tgnet;

public class TLRPC$TL_messages_getMessageEditData extends TLObject {
    public static int constructor = -39416522;
    public int id;
    public TLRPC$InputPeer peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_messageEditData.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.id);
    }
}
