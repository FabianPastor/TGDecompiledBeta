package org.telegram.tgnet;

public class TLRPC$TL_updateChatParticipantDelete extends TLRPC$Update {
    public static int constructor = -NUM;
    public long chat_id;
    public long user_id;
    public int version;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.chat_id = abstractSerializedData.readInt64(z);
        this.user_id = abstractSerializedData.readInt64(z);
        this.version = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.chat_id);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeInt32(this.version);
    }
}
