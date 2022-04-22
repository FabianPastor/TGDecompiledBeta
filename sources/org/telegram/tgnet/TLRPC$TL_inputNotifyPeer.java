package org.telegram.tgnet;

public class TLRPC$TL_inputNotifyPeer extends TLRPC$InputNotifyPeer {
    public static int constructor = -NUM;
    public TLRPC$InputPeer peer;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
    }
}
