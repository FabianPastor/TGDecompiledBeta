package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_folderPeer extends TLObject {
    public static int constructor = -NUM;
    public int folder_id;
    public TLRPC$Peer peer;

    public static TLRPC$TL_folderPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_folderPeer", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_folderPeer tLRPC$TL_folderPeer = new TLRPC$TL_folderPeer();
        tLRPC$TL_folderPeer.readParams(abstractSerializedData, z);
        return tLRPC$TL_folderPeer;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.folder_id = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.folder_id);
    }
}
