package org.telegram.tgnet;

public class TLRPC$TL_messages_setHistoryTTL extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputPeer peer;
    public int period;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.period);
    }
}
