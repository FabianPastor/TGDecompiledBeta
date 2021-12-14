package org.telegram.tgnet;

public class TLRPC$TL_inputNotifyBroadcasts extends TLRPC$InputNotifyPeer {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
