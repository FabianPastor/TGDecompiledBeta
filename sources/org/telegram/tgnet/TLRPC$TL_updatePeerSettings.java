package org.telegram.tgnet;

public class TLRPC$TL_updatePeerSettings extends TLRPC$Update {
    public static int constructor = NUM;
    public TLRPC$Peer peer;
    public TLRPC$TL_peerSettings settings;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.settings = TLRPC$TL_peerSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        this.settings.serializeToStream(abstractSerializedData);
    }
}
