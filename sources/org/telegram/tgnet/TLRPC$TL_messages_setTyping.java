package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_setTyping extends TLObject {
    public static int constructor = NUM;
    public TLRPC$SendMessageAction action;
    public int flags;
    public TLRPC$InputPeer peer;
    public int top_msg_id;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.peer.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.top_msg_id);
        }
        this.action.serializeToStream(abstractSerializedData);
    }
}
