package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$ReactionCount extends TLObject {
    public boolean chosen;
    public int chosen_order;
    public int count;
    public int flags;
    public int lastDrawnPosition;
    public TLRPC$Reaction reaction;

    public static TLRPC$ReactionCount TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_reactionCount tLRPC$TL_reactionCount = i != -NUM ? null : new TLRPC$TL_reactionCount();
        if (tLRPC$TL_reactionCount != null || !z) {
            if (tLRPC$TL_reactionCount != null) {
                tLRPC$TL_reactionCount.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_reactionCount;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ReactionCount", Integer.valueOf(i)));
    }
}
