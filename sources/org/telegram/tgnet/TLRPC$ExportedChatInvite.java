package org.telegram.tgnet;

public abstract class TLRPC$ExportedChatInvite extends TLObject {
    public static TLRPC$ExportedChatInvite TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ExportedChatInvite tLRPC$ExportedChatInvite;
        switch (i) {
            case -1316944408:
                tLRPC$ExportedChatInvite = new TLRPC$TL_chatInviteExported();
                break;
            case -64092740:
                tLRPC$ExportedChatInvite = new TLRPC$TL_chatInviteExported_layer122();
                break;
            case 1776236393:
                tLRPC$ExportedChatInvite = new TLRPC$TL_chatInviteEmpty_layer122();
                break;
            case 1847917725:
                tLRPC$ExportedChatInvite = new TLRPC$TL_chatInviteExported_layer131();
                break;
            default:
                tLRPC$ExportedChatInvite = null;
                break;
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
