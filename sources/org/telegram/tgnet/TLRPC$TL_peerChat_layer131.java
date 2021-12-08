package org.telegram.tgnet;

public class TLRPC$TL_peerChat_layer131 extends TLRPC$TL_peerChat {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.chat_id = (long) abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32((int) this.chat_id);
    }
}
