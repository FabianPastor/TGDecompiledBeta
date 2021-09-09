package org.telegram.tgnet;

public class TLRPC$TL_messageActionChannelMigrateFrom_layer131 extends TLRPC$TL_messageActionChannelMigrateFrom {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.title = abstractSerializedData.readString(z);
        this.chat_id = (long) abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeInt32((int) this.chat_id);
    }
}
