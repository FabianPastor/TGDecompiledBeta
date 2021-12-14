package org.telegram.tgnet;

public class TLRPC$TL_messages_requestUrlAuth extends TLObject {
    public static int constructor = NUM;
    public int button_id;
    public int flags;
    public int msg_id;
    public TLRPC$InputPeer peer;
    public String url;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$UrlAuthResult.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 2) != 0) {
            this.peer.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.msg_id);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.button_id);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.url);
        }
    }
}
