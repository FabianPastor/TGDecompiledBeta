package org.telegram.tgnet;

public abstract class TLRPC$Peer extends TLObject {
    public long channel_id;
    public long chat_id;
    public long user_id;

    public static TLRPC$Peer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Peer tLRPC$Peer;
        switch (i) {
            case -1649296275:
                tLRPC$Peer = new TLRPC$TL_peerUser_layer131();
                break;
            case -1566230754:
                tLRPC$Peer = new TLRPC$TL_peerChannel();
                break;
            case -1160714821:
                tLRPC$Peer = new TLRPC$TL_peerChat_layer131();
                break;
            case -1109531342:
                tLRPC$Peer = new TLRPC$TL_peerChannel_layer131();
                break;
            case 918946202:
                tLRPC$Peer = new TLRPC$TL_peerChat();
                break;
            case 1498486562:
                tLRPC$Peer = new TLRPC$TL_peerUser();
                break;
            default:
                tLRPC$Peer = null;
                break;
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
