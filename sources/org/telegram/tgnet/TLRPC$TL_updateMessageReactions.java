package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateMessageReactions extends TLRPC$Update {
    public static int constructor = NUM;
    public int flags;
    public int msg_id;
    public TLRPC$Peer peer;
    public TLRPC$TL_messageReactions reactions;
    public int top_msg_id;
    public boolean updateUnreadState = true;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.msg_id = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.top_msg_id = abstractSerializedData.readInt32(z);
        }
        this.reactions = TLRPC$MessageReactions.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.top_msg_id);
        }
        this.reactions.serializeToStream(abstractSerializedData);
    }
}
