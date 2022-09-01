package org.telegram.tgnet;

public class TLRPC$TL_messagePeerReaction_layer144 extends TLRPC$MessagePeerReaction {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.big = (readInt32 & 1) != 0;
        if ((readInt32 & 2) != 0) {
            z2 = true;
        }
        this.unread = z2;
        this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        TLRPC$TL_reactionEmoji tLRPC$TL_reactionEmoji = new TLRPC$TL_reactionEmoji();
        this.reaction = tLRPC$TL_reactionEmoji;
        tLRPC$TL_reactionEmoji.emoticon = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.big ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.unread ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        this.peer_id.serializeToStream(abstractSerializedData);
        TLRPC$Reaction tLRPC$Reaction = this.reaction;
        if (tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) {
            abstractSerializedData.writeString(((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon);
        } else {
            abstractSerializedData.writeString("");
        }
    }
}
