package org.telegram.tgnet;

public abstract class TLRPC$PeerLocated extends TLObject {
    public static TLRPC$PeerLocated TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PeerLocated tLRPC$PeerLocated;
        if (i != -NUM) {
            tLRPC$PeerLocated = i != -NUM ? null : new TLRPC$TL_peerSelfLocated();
        } else {
            tLRPC$PeerLocated = new TLRPC$TL_peerLocated();
        }
        if (tLRPC$PeerLocated != null || !z) {
            if (tLRPC$PeerLocated != null) {
                tLRPC$PeerLocated.readParams(abstractSerializedData, z);
            }
            return tLRPC$PeerLocated;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PeerLocated", new Object[]{Integer.valueOf(i)}));
    }
}
