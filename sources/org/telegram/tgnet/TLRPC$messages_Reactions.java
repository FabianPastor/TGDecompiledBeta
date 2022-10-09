package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_Reactions extends TLObject {
    public static TLRPC$messages_Reactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Reactions tLRPC$messages_Reactions;
        if (i == -NUM) {
            tLRPC$messages_Reactions = new TLRPC$messages_Reactions() { // from class: org.telegram.tgnet.TLRPC$TL_messages_reactionsNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$messages_Reactions = i != -NUM ? null : new TLRPC$TL_messages_reactions();
        }
        if (tLRPC$messages_Reactions != null || !z) {
            if (tLRPC$messages_Reactions != null) {
                tLRPC$messages_Reactions.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Reactions;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Reactions", Integer.valueOf(i)));
    }
}
