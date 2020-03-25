package org.telegram.tgnet;

public class TLRPC$TL_updateChatPinnedMessage extends TLRPC$Update {
    public static int constructor = -NUM;
    public int chat_id;
    public int id;
    public int version;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.chat_id = abstractSerializedData.readInt32(z);
        this.id = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.chat_id);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt32(this.version);
    }
}
