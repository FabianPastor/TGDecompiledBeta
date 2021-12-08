package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_Chats extends TLObject {
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int count;

    public static TLRPC$messages_Chats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Chats tLRPC$messages_Chats;
        if (i != -NUM) {
            tLRPC$messages_Chats = i != NUM ? null : new TLRPC$TL_messages_chats();
        } else {
            tLRPC$messages_Chats = new TLRPC$TL_messages_chatsSlice();
        }
        if (tLRPC$messages_Chats != null || !z) {
            if (tLRPC$messages_Chats != null) {
                tLRPC$messages_Chats.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Chats;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Chats", new Object[]{Integer.valueOf(i)}));
    }
}
