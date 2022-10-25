package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_SponsoredMessages extends TLObject {
    public static TLRPC$messages_SponsoredMessages TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_SponsoredMessages tLRPC$TL_messages_sponsoredMessages;
        if (i != -NUM) {
            tLRPC$TL_messages_sponsoredMessages = i != NUM ? null : new TLRPC$messages_SponsoredMessages() { // from class: org.telegram.tgnet.TLRPC$TL_messages_sponsoredMessagesEmpty
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$TL_messages_sponsoredMessages = new TLRPC$TL_messages_sponsoredMessages();
        }
        if (tLRPC$TL_messages_sponsoredMessages != null || !z) {
            if (tLRPC$TL_messages_sponsoredMessages != null) {
                tLRPC$TL_messages_sponsoredMessages.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_sponsoredMessages;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_SponsoredMessages", Integer.valueOf(i)));
    }
}
