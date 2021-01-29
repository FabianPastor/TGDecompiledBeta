package org.telegram.tgnet;

public abstract class TLRPC$contacts_TopPeers extends TLObject {
    public static TLRPC$contacts_TopPeers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$contacts_TopPeers tLRPC$contacts_TopPeers;
        if (i == -NUM) {
            tLRPC$contacts_TopPeers = new TLRPC$TL_contacts_topPeersDisabled();
        } else if (i != -NUM) {
            tLRPC$contacts_TopPeers = i != NUM ? null : new TLRPC$TL_contacts_topPeers();
        } else {
            tLRPC$contacts_TopPeers = new TLRPC$TL_contacts_topPeersNotModified();
        }
        if (tLRPC$contacts_TopPeers != null || !z) {
            if (tLRPC$contacts_TopPeers != null) {
                tLRPC$contacts_TopPeers.readParams(abstractSerializedData, z);
            }
            return tLRPC$contacts_TopPeers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in contacts_TopPeers", new Object[]{Integer.valueOf(i)}));
    }
}
