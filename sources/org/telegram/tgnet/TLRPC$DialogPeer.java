package org.telegram.tgnet;

public abstract class TLRPC$DialogPeer extends TLObject {
    public static TLRPC$DialogPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$DialogPeer tLRPC$DialogPeer;
        if (i != -NUM) {
            tLRPC$DialogPeer = i != NUM ? null : new TLRPC$TL_dialogPeerFolder();
        } else {
            tLRPC$DialogPeer = new TLRPC$TL_dialogPeer();
        }
        if (tLRPC$DialogPeer != null || !z) {
            if (tLRPC$DialogPeer != null) {
                tLRPC$DialogPeer.readParams(abstractSerializedData, z);
            }
            return tLRPC$DialogPeer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in DialogPeer", new Object[]{Integer.valueOf(i)}));
    }
}
