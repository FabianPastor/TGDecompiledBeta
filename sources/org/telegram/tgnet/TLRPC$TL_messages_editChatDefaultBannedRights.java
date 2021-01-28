package org.telegram.tgnet;

public class TLRPC$TL_messages_editChatDefaultBannedRights extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$TL_chatBannedRights banned_rights;
    public TLRPC$InputPeer peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        this.banned_rights.serializeToStream(abstractSerializedData);
    }
}
