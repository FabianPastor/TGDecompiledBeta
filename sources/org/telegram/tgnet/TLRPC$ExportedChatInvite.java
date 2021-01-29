package org.telegram.tgnet;

public abstract class TLRPC$ExportedChatInvite extends TLObject {
    public static TLRPC$ExportedChatInvite TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ExportedChatInvite tLRPC$ExportedChatInvite;
        if (i == -64092740) {
            tLRPC$ExportedChatInvite = new TLRPC$TL_chatInviteExported_layer122();
        } else if (i != NUM) {
            tLRPC$ExportedChatInvite = i != NUM ? null : new TLRPC$TL_chatInviteExported();
        } else {
            tLRPC$ExportedChatInvite = new TLRPC$TL_chatInviteEmpty_layer122();
        }
        if (tLRPC$ExportedChatInvite != null || !z) {
            if (tLRPC$ExportedChatInvite != null) {
                tLRPC$ExportedChatInvite.readParams(abstractSerializedData, z);
            }
            return tLRPC$ExportedChatInvite;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ExportedChatInvite", new Object[]{Integer.valueOf(i)}));
    }
}
