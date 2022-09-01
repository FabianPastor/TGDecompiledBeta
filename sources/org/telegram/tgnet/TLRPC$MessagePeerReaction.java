package org.telegram.tgnet;

public abstract class TLRPC$MessagePeerReaction extends TLObject {
    public boolean big;
    public int flags;
    public TLRPC$Peer peer_id;
    public TLRPC$Reaction reaction;
    public boolean unread;

    public static TLRPC$MessagePeerReaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction;
        if (i == -NUM) {
            tLRPC$MessagePeerReaction = new TLRPC$TL_messagePeerReaction_layer137();
        } else if (i != -NUM) {
            tLRPC$MessagePeerReaction = i != NUM ? null : new TLRPC$TL_messagePeerReaction_layer144();
        } else {
            tLRPC$MessagePeerReaction = new TLRPC$TL_messagePeerReaction();
        }
        if (tLRPC$MessagePeerReaction != null || !z) {
            if (tLRPC$MessagePeerReaction != null) {
                tLRPC$MessagePeerReaction.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessagePeerReaction;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessagePeerReaction", new Object[]{Integer.valueOf(i)}));
    }
}
