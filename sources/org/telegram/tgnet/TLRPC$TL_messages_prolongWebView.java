package org.telegram.tgnet;

public class TLRPC$TL_messages_prolongWebView extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputUser bot;
    public int flags;
    public TLRPC$InputPeer peer;
    public long query_id;
    public int reply_to_msg_id;
    public TLRPC$InputPeer send_as;
    public boolean silent;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.silent ? this.flags | 32 : this.flags & -33;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
        this.bot.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.query_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.reply_to_msg_id);
        }
        if ((this.flags & 8192) != 0) {
            this.send_as.serializeToStream(abstractSerializedData);
        }
    }
}
