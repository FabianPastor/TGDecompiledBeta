package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_peerBlocked extends TLObject {
    public static int constructor = -NUM;
    public int date;
    public TLRPC$Peer peer_id;

    public static TLRPC$TL_peerBlocked TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_peerBlocked", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_peerBlocked tLRPC$TL_peerBlocked = new TLRPC$TL_peerBlocked();
        tLRPC$TL_peerBlocked.readParams(abstractSerializedData, z);
        return tLRPC$TL_peerBlocked;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.date = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer_id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.date);
    }
}
