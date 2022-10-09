package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_topPeer extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$Peer peer;
    public double rating;

    public static TLRPC$TL_topPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_topPeer", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_topPeer tLRPC$TL_topPeer = new TLRPC$TL_topPeer();
        tLRPC$TL_topPeer.readParams(abstractSerializedData, z);
        return tLRPC$TL_topPeer;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.rating = abstractSerializedData.readDouble(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeDouble(this.rating);
    }
}
