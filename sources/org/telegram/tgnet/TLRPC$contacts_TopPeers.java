package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$contacts_TopPeers extends TLObject {
    public static TLRPC$contacts_TopPeers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$contacts_TopPeers tLRPC$contacts_TopPeers;
        if (i == -NUM) {
            tLRPC$contacts_TopPeers = new TLRPC$contacts_TopPeers() { // from class: org.telegram.tgnet.TLRPC$TL_contacts_topPeersDisabled
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else if (i != -NUM) {
            tLRPC$contacts_TopPeers = i != NUM ? null : new TLRPC$TL_contacts_topPeers();
        } else {
            tLRPC$contacts_TopPeers = new TLRPC$contacts_TopPeers() { // from class: org.telegram.tgnet.TLRPC$TL_contacts_topPeersNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$contacts_TopPeers != null || !z) {
            if (tLRPC$contacts_TopPeers != null) {
                tLRPC$contacts_TopPeers.readParams(abstractSerializedData, z);
            }
            return tLRPC$contacts_TopPeers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in contacts_TopPeers", Integer.valueOf(i)));
    }
}
