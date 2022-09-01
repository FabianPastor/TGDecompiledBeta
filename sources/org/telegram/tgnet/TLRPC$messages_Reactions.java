package org.telegram.tgnet;

public abstract class TLRPC$messages_Reactions extends TLObject {
    public static TLRPC$messages_Reactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Reactions tLRPC$messages_Reactions;
        if (i != -NUM) {
            tLRPC$messages_Reactions = i != -NUM ? null : new TLRPC$TL_messages_reactions();
        } else {
            tLRPC$messages_Reactions = new TLRPC$TL_messages_reactionsNotModified();
        }
        if (tLRPC$messages_Reactions != null || !z) {
            if (tLRPC$messages_Reactions != null) {
                tLRPC$messages_Reactions.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Reactions;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Reactions", new Object[]{Integer.valueOf(i)}));
    }
}
