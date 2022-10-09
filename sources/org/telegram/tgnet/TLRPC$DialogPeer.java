package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$DialogPeer extends TLObject {
    public static TLRPC$DialogPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$DialogPeer tLRPC$TL_dialogPeer;
        if (i == -NUM) {
            tLRPC$TL_dialogPeer = new TLRPC$TL_dialogPeer();
        } else {
            tLRPC$TL_dialogPeer = i != NUM ? null : new TLRPC$DialogPeer() { // from class: org.telegram.tgnet.TLRPC$TL_dialogPeerFolder
                public static int constructor = NUM;
                public int folder_id;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.folder_id = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.folder_id);
                }
            };
        }
        if (tLRPC$TL_dialogPeer != null || !z) {
            if (tLRPC$TL_dialogPeer != null) {
                tLRPC$TL_dialogPeer.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_dialogPeer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in DialogPeer", Integer.valueOf(i)));
    }
}
