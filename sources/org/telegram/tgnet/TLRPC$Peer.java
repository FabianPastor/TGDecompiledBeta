package org.telegram.tgnet;

public abstract class TLRPC$Peer extends TLObject {
    public int channel_id;
    public int chat_id;
    public int user_id;

    public static TLRPC$Peer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Peer tLRPC$Peer;
        if (i == -NUM) {
            tLRPC$Peer = new TLRPC$TL_peerUser();
        } else if (i != -NUM) {
            tLRPC$Peer = i != -NUM ? null : new TLRPC$TL_peerChannel();
        } else {
            tLRPC$Peer = new TLRPC$TL_peerChat();
        }
        if (tLRPC$Peer != null || !z) {
            if (tLRPC$Peer != null) {
                tLRPC$Peer.readParams(abstractSerializedData, z);
            }
            return tLRPC$Peer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Peer", new Object[]{Integer.valueOf(i)}));
    }
}
