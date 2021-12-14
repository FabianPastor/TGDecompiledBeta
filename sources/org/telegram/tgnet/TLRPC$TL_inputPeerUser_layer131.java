package org.telegram.tgnet;

public class TLRPC$TL_inputPeerUser_layer131 extends TLRPC$TL_inputPeerUser {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = (long) abstractSerializedData.readInt32(z);
        this.access_hash = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32((int) this.user_id);
        abstractSerializedData.writeInt64(this.access_hash);
    }
}
