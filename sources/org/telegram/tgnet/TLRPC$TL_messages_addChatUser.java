package org.telegram.tgnet;

public class TLRPC$TL_messages_addChatUser extends TLObject {
    public static int constructor = -NUM;
    public long chat_id;
    public int fwd_limit;
    public TLRPC$InputUser user_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.chat_id);
        this.user_id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.fwd_limit);
    }
}
