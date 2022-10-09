package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updatePeerBlocked extends TLRPC$Update {
    public static int constructor = NUM;
    public boolean blocked;
    public TLRPC$Peer peer_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.blocked = abstractSerializedData.readBool(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer_id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeBool(this.blocked);
    }
}
