package org.telegram.tgnet;

public class TLRPC$TL_inputPeerSelf extends TLRPC$InputPeer {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
