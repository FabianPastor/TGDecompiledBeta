package org.telegram.tgnet;

public class TLRPC$TL_updateDraftMessage extends TLRPC$Update {
    public static int constructor = -NUM;
    public TLRPC$DraftMessage draft;
    public TLRPC$Peer peer;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.draft = TLRPC$DraftMessage.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        this.draft.serializeToStream(abstractSerializedData);
    }
}
