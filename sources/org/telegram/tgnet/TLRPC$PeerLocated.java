package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$PeerLocated extends TLObject {
    public static TLRPC$PeerLocated TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PeerLocated tLRPC$TL_peerLocated;
        if (i != -NUM) {
            tLRPC$TL_peerLocated = i != -NUM ? null : new TLRPC$TL_peerSelfLocated();
        } else {
            tLRPC$TL_peerLocated = new TLRPC$TL_peerLocated();
        }
        if (tLRPC$TL_peerLocated != null || !z) {
            if (tLRPC$TL_peerLocated != null) {
                tLRPC$TL_peerLocated.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_peerLocated;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PeerLocated", Integer.valueOf(i)));
    }
}
