package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_Dialogs extends TLObject {
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int count;
    public ArrayList<TLRPC$Dialog> dialogs = new ArrayList<>();
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$messages_Dialogs TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Dialogs tLRPC$messages_Dialogs;
        if (i == -NUM) {
            tLRPC$messages_Dialogs = new TLRPC$TL_messages_dialogsNotModified();
        } else if (i != NUM) {
            tLRPC$messages_Dialogs = i != NUM ? null : new TLRPC$TL_messages_dialogsSlice();
        } else {
            tLRPC$messages_Dialogs = new TLRPC$TL_messages_dialogs();
        }
        if (tLRPC$messages_Dialogs != null || !z) {
            if (tLRPC$messages_Dialogs != null) {
                tLRPC$messages_Dialogs.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Dialogs;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Dialogs", new Object[]{Integer.valueOf(i)}));
    }
}
