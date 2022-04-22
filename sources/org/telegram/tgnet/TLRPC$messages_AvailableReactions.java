package org.telegram.tgnet;

public abstract class TLRPC$messages_AvailableReactions extends TLObject {
    public static TLRPC$messages_AvailableReactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_AvailableReactions tLRPC$messages_AvailableReactions;
        if (i != -NUM) {
            tLRPC$messages_AvailableReactions = i != NUM ? null : new TLRPC$TL_messages_availableReactions();
        } else {
            tLRPC$messages_AvailableReactions = new TLRPC$TL_messages_availableReactionsNotModified();
        }
        if (tLRPC$messages_AvailableReactions != null || !z) {
            if (tLRPC$messages_AvailableReactions != null) {
                tLRPC$messages_AvailableReactions.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_AvailableReactions;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_AvailableReactions", new Object[]{Integer.valueOf(i)}));
    }
}
