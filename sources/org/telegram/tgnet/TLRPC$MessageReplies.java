package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$MessageReplies extends TLObject {
    public long channel_id;
    public boolean comments;
    public int flags;
    public int max_id;
    public int read_max_id;
    public ArrayList<TLRPC$Peer> recent_repliers = new ArrayList<>();
    public int replies;
    public int replies_pts;

    public static TLRPC$MessageReplies TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageReplies tLRPC$MessageReplies;
        if (i != -NUM) {
            tLRPC$MessageReplies = i != NUM ? null : new TLRPC$TL_messageReplies_layer131();
        } else {
            tLRPC$MessageReplies = new TLRPC$TL_messageReplies();
        }
        if (tLRPC$MessageReplies != null || !z) {
            if (tLRPC$MessageReplies != null) {
                tLRPC$MessageReplies.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessageReplies;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageReplies", new Object[]{Integer.valueOf(i)}));
    }
}
