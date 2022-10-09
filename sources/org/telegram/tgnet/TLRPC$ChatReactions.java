package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$ChatReactions extends TLObject {
    public static TLRPC$ChatReactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatReactions tLRPC$TL_chatReactionsNone;
        if (i == -NUM) {
            tLRPC$TL_chatReactionsNone = new TLRPC$TL_chatReactionsNone();
        } else if (i != NUM) {
            tLRPC$TL_chatReactionsNone = i != NUM ? null : new TLRPC$TL_chatReactionsSome();
        } else {
            tLRPC$TL_chatReactionsNone = new TLRPC$TL_chatReactionsAll();
        }
        if (tLRPC$TL_chatReactionsNone != null || !z) {
            if (tLRPC$TL_chatReactionsNone != null) {
                tLRPC$TL_chatReactionsNone.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_chatReactionsNone;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatReactions", Integer.valueOf(i)));
    }
}
