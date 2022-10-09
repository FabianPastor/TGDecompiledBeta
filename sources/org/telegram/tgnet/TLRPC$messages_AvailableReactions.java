package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_AvailableReactions extends TLObject {
    public static TLRPC$messages_AvailableReactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_AvailableReactions tLRPC$messages_AvailableReactions;
        if (i == -NUM) {
            tLRPC$messages_AvailableReactions = new TLRPC$messages_AvailableReactions() { // from class: org.telegram.tgnet.TLRPC$TL_messages_availableReactionsNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$messages_AvailableReactions = i != NUM ? null : new TLRPC$TL_messages_availableReactions();
        }
        if (tLRPC$messages_AvailableReactions != null || !z) {
            if (tLRPC$messages_AvailableReactions != null) {
                tLRPC$messages_AvailableReactions.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_AvailableReactions;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_AvailableReactions", Integer.valueOf(i)));
    }
}
