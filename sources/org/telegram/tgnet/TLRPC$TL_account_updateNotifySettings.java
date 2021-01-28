package org.telegram.tgnet;

public class TLRPC$TL_account_updateNotifySettings extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputNotifyPeer peer;
    public TLRPC$TL_inputPeerNotifySettings settings;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        this.settings.serializeToStream(abstractSerializedData);
    }
}
