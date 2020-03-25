package org.telegram.tgnet;

public class TLRPC$TL_messages_acceptUrlAuth extends TLObject {
    public static int constructor = -NUM;
    public int button_id;
    public int flags;
    public int msg_id;
    public TLRPC$InputPeer peer;
    public boolean write_allowed;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$UrlAuthResult.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.write_allowed ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        abstractSerializedData.writeInt32(this.button_id);
    }
}
