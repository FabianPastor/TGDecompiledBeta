package org.telegram.tgnet;

public class TLRPC$TL_messages_sendReaction extends TLObject {
    public static int constructor = NUM;
    public int flags;
    public int msg_id;
    public TLRPC$InputPeer peer;
    public String reaction;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.reaction);
        }
    }
}
