package org.telegram.tgnet;

public class TLRPC$TL_messages_saveDefaultSendAs extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputPeer peer;
    public TLRPC$InputPeer send_as;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        this.send_as.serializeToStream(abstractSerializedData);
    }
}
