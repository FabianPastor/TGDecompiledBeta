package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$MessagePeerReaction extends TLObject {
    public boolean big;
    public int flags;
    public TLRPC$Peer peer_id;
    public TLRPC$Reaction reaction;
    public boolean unread;

    public static TLRPC$MessagePeerReaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction;
        if (i == -NUM) {
            tLRPC$MessagePeerReaction = new TLRPC$MessagePeerReaction() { // from class: org.telegram.tgnet.TLRPC$TL_messagePeerReaction_layer137
                public static int constructor = -NUM;
                public long user_id;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.user_id = abstractSerializedData2.readInt64(z2);
                    TLRPC$TL_reactionEmoji tLRPC$TL_reactionEmoji = new TLRPC$TL_reactionEmoji();
                    this.reaction = tLRPC$TL_reactionEmoji;
                    tLRPC$TL_reactionEmoji.emoticon = abstractSerializedData2.readString(z2);
                    TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                    this.peer_id = tLRPC$TL_peerUser;
                    tLRPC$TL_peerUser.user_id = this.user_id;
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.user_id);
                    TLRPC$Reaction tLRPC$Reaction = this.reaction;
                    if (tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) {
                        abstractSerializedData2.writeString(((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon);
                    } else {
                        abstractSerializedData2.writeString("");
                    }
                }
            };
        } else if (i == -NUM) {
            tLRPC$MessagePeerReaction = new TLRPC$TL_messagePeerReaction();
        } else {
            tLRPC$MessagePeerReaction = i != NUM ? null : new TLRPC$MessagePeerReaction() { // from class: org.telegram.tgnet.TLRPC$TL_messagePeerReaction_layer144
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    boolean z3 = false;
                    this.big = (readInt32 & 1) != 0;
                    if ((readInt32 & 2) != 0) {
                        z3 = true;
                    }
                    this.unread = z3;
                    this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    TLRPC$TL_reactionEmoji tLRPC$TL_reactionEmoji = new TLRPC$TL_reactionEmoji();
                    this.reaction = tLRPC$TL_reactionEmoji;
                    tLRPC$TL_reactionEmoji.emoticon = abstractSerializedData2.readString(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.big ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    int i3 = this.unread ? i2 | 2 : i2 & (-3);
                    this.flags = i3;
                    abstractSerializedData2.writeInt32(i3);
                    this.peer_id.serializeToStream(abstractSerializedData2);
                    TLRPC$Reaction tLRPC$Reaction = this.reaction;
                    if (tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) {
                        abstractSerializedData2.writeString(((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon);
                    } else {
                        abstractSerializedData2.writeString("");
                    }
                }
            };
        }
        if (tLRPC$MessagePeerReaction != null || !z) {
            if (tLRPC$MessagePeerReaction != null) {
                tLRPC$MessagePeerReaction.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessagePeerReaction;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessagePeerReaction", Integer.valueOf(i)));
    }
}
