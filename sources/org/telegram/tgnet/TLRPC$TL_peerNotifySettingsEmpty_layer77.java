package org.telegram.tgnet;

public class TLRPC$TL_peerNotifySettingsEmpty_layer77 extends TLRPC$PeerNotifySettings {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
