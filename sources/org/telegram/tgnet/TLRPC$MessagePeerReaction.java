package org.telegram.tgnet;

public abstract class TLRPC$MessagePeerReaction extends TLObject {
    public boolean big;
    public int flags;
    public TLRPC$Peer peer_id;
    public String reaction;
    public boolean unread;

    public static TLRPC$TL_messagePeerReaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction;
        if (i != -NUM) {
            tLRPC$TL_messagePeerReaction = i != NUM ? null : new TLRPC$TL_messagePeerReaction();
        } else {
            tLRPC$TL_messagePeerReaction = new TLRPC$TL_messagePeerReaction_layer137();
        }
        if (tLRPC$TL_messagePeerReaction != null || !z) {
            if (tLRPC$TL_messagePeerReaction != null) {
                tLRPC$TL_messagePeerReaction.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messagePeerReaction;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessagePeerReaction", new Object[]{Integer.valueOf(i)}));
    }
}
