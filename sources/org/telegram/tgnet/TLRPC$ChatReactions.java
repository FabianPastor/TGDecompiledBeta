package org.telegram.tgnet;

public abstract class TLRPC$ChatReactions extends TLObject {
    public static TLRPC$ChatReactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatReactions tLRPC$ChatReactions;
        if (i == -NUM) {
            tLRPC$ChatReactions = new TLRPC$TL_chatReactionsNone();
        } else if (i != NUM) {
            tLRPC$ChatReactions = i != NUM ? null : new TLRPC$TL_chatReactionsSome();
        } else {
            tLRPC$ChatReactions = new TLRPC$TL_chatReactionsAll();
        }
        if (tLRPC$ChatReactions != null || !z) {
            if (tLRPC$ChatReactions != null) {
                tLRPC$ChatReactions.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChatReactions;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatReactions", new Object[]{Integer.valueOf(i)}));
    }
}
