package org.telegram.tgnet;

public class TLRPC$TL_messageActionChatMigrateTo_layer131 extends TLRPC$TL_messageActionChatMigrateTo {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel_id = (long) abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32((int) this.channel_id);
    }
}