package org.telegram.tgnet;

public class TLRPC$TL_messages_startBot extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputUser bot;
    public TLRPC$InputPeer peer;
    public long random_id;
    public String start_param;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.bot.serializeToStream(abstractSerializedData);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.random_id);
        abstractSerializedData.writeString(this.start_param);
    }
}
