package org.telegram.tgnet;

public class TLRPC$TL_messages_searchGlobal extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public int folder_id;
    public int limit;
    public int offset_id;
    public TLRPC$InputPeer offset_peer;
    public int offset_rate;
    public String q;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_Messages.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.folder_id);
        }
        abstractSerializedData.writeString(this.q);
        abstractSerializedData.writeInt32(this.offset_rate);
        this.offset_peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.offset_id);
        abstractSerializedData.writeInt32(this.limit);
    }
}
