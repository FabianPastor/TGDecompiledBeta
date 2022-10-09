package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageEmpty extends TLRPC$Message {
    public static int constructor = -NUM;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.id = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        } else {
            this.peer_id = new TLRPC$TL_peerUser();
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 1) != 0) {
            this.peer_id.serializeToStream(abstractSerializedData);
        }
    }
}
