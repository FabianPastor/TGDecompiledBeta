package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_getBotCallbackAnswer extends TLObject {
    public static int constructor = -NUM;
    public byte[] data;
    public int flags;
    public boolean game;
    public int msg_id;
    public TLRPC$InputCheckPasswordSRP password;
    public TLRPC$InputPeer peer;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_botCallbackAnswer.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.game ? this.flags | 2 : this.flags & (-3);
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeByteArray(this.data);
        }
        if ((this.flags & 4) != 0) {
            this.password.serializeToStream(abstractSerializedData);
        }
    }
}
