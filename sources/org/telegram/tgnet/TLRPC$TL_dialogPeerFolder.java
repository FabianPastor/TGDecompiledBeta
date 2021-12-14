package org.telegram.tgnet;

public class TLRPC$TL_dialogPeerFolder extends TLRPC$DialogPeer {
    public static int constructor = NUM;
    public int folder_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.folder_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.folder_id);
    }
}
