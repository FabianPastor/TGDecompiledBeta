package org.telegram.tgnet;

public class TLRPC$TL_messageEmpty_layer122 extends TLRPC$TL_messageEmpty {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt32(z);
        this.peer_id = new TLRPC$TL_peerUser();
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.id);
    }
}
