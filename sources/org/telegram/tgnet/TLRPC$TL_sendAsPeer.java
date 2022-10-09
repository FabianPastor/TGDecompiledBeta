package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_sendAsPeer extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public TLRPC$Peer peer;
    public boolean premium_required;

    public static TLRPC$TL_sendAsPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_sendAsPeer", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_sendAsPeer tLRPC$TL_sendAsPeer = new TLRPC$TL_sendAsPeer();
        tLRPC$TL_sendAsPeer.readParams(abstractSerializedData, z);
        return tLRPC$TL_sendAsPeer;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.premium_required = z2;
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.premium_required ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
    }
}
