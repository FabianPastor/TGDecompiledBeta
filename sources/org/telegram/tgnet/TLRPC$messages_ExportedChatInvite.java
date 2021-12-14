package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_ExportedChatInvite extends TLObject {
    public TLRPC$ExportedChatInvite invite;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$messages_ExportedChatInvite TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_ExportedChatInvite tLRPC$messages_ExportedChatInvite;
        if (i != NUM) {
            tLRPC$messages_ExportedChatInvite = i != NUM ? null : new TLRPC$TL_messages_exportedChatInviteReplaced();
        } else {
            tLRPC$messages_ExportedChatInvite = new TLRPC$TL_messages_exportedChatInvite();
        }
        if (tLRPC$messages_ExportedChatInvite != null || !z) {
            if (tLRPC$messages_ExportedChatInvite != null) {
                tLRPC$messages_ExportedChatInvite.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_ExportedChatInvite;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_ExportedChatInvite", new Object[]{Integer.valueOf(i)}));
    }
}
