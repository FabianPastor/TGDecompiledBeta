package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_getNotifyExceptions extends TLObject {
    public static int constructor = NUM;
    public boolean compare_sound;
    public int flags;
    public TLRPC$InputNotifyPeer peer;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.compare_sound ? this.flags | 2 : this.flags & (-3);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        if ((this.flags & 1) != 0) {
            this.peer.serializeToStream(abstractSerializedData);
        }
    }
}
