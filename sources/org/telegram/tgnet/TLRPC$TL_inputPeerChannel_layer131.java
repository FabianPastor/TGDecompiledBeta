package org.telegram.tgnet;

public class TLRPC$TL_inputPeerChannel_layer131 extends TLRPC$TL_inputPeerChannel {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel_id = (long) abstractSerializedData.readInt32(z);
        this.access_hash = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32((int) this.channel_id);
        abstractSerializedData.writeInt64(this.access_hash);
    }
}
