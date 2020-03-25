package org.telegram.tgnet;

public class TLRPC$TL_messages_getPinnedDialogs extends TLObject {
    public static int constructor = -NUM;
    public int folder_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_peerDialogs.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.folder_id);
    }
}
