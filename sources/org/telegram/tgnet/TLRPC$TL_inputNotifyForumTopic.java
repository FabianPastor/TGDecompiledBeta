package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputNotifyForumTopic extends TLRPC$InputNotifyPeer {
    public static int constructor = NUM;
    public TLRPC$InputPeer peer;
    public int top_msg_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.top_msg_id = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.top_msg_id);
    }
}
