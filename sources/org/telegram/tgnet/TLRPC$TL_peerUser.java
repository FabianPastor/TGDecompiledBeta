package org.telegram.tgnet;

public class TLRPC$TL_peerUser extends TLRPC$Peer {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.user_id);
    }
}
