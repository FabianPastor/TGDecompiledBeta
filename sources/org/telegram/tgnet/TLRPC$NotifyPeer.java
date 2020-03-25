package org.telegram.tgnet;

public abstract class TLRPC$NotifyPeer extends TLObject {
    public static TLRPC$NotifyPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$NotifyPeer tLRPC$NotifyPeer;
        switch (i) {
            case -1613493288:
                tLRPC$NotifyPeer = new TLRPC$TL_notifyPeer();
                break;
            case -1261946036:
                tLRPC$NotifyPeer = new TLRPC$TL_notifyUsers();
                break;
            case -1073230141:
                tLRPC$NotifyPeer = new TLRPC$TL_notifyChats();
                break;
            case -703403793:
                tLRPC$NotifyPeer = new TLRPC$TL_notifyBroadcasts();
                break;
            default:
                tLRPC$NotifyPeer = null;
                break;
        }
        if (tLRPC$NotifyPeer != null || !z) {
            if (tLRPC$NotifyPeer != null) {
                tLRPC$NotifyPeer.readParams(abstractSerializedData, z);
            }
            return tLRPC$NotifyPeer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in NotifyPeer", new Object[]{Integer.valueOf(i)}));
    }
}
