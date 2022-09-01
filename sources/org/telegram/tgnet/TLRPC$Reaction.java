package org.telegram.tgnet;

public abstract class TLRPC$Reaction extends TLObject {
    public static TLRPC$Reaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Reaction tLRPC$Reaction;
        if (i != -NUM) {
            tLRPC$Reaction = i != NUM ? i != NUM ? null : new TLRPC$TL_reactionEmpty() : new TLRPC$TL_reactionEmoji();
        } else {
            tLRPC$Reaction = new TLRPC$TL_reactionCustomEmoji();
        }
        if (tLRPC$Reaction != null || !z) {
            if (tLRPC$Reaction != null) {
                tLRPC$Reaction.readParams(abstractSerializedData, z);
            }
            return tLRPC$Reaction;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Reaction", new Object[]{Integer.valueOf(i)}));
    }
}
