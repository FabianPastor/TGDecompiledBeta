package org.telegram.tgnet;

public class TLRPC$TL_messagePeerReaction_layer137 extends TLRPC$MessagePeerReaction {
    public static int constructor = -NUM;
    public long user_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        TLRPC$TL_reactionEmoji tLRPC$TL_reactionEmoji = new TLRPC$TL_reactionEmoji();
        this.reaction = tLRPC$TL_reactionEmoji;
        tLRPC$TL_reactionEmoji.emoticon = abstractSerializedData.readString(z);
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        this.peer_id = tLRPC$TL_peerUser;
        tLRPC$TL_peerUser.user_id = this.user_id;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        TLRPC$Reaction tLRPC$Reaction = this.reaction;
        if (tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) {
            abstractSerializedData.writeString(((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon);
        } else {
            abstractSerializedData.writeString("");
        }
    }
}
