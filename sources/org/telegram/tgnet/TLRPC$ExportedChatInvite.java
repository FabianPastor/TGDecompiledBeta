package org.telegram.tgnet;

public abstract class TLRPC$ExportedChatInvite extends TLObject {
    public static TLRPC$TL_chatInviteExported TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported;
        switch (i) {
            case -1316944408:
                tLRPC$TL_chatInviteExported = new TLRPC$TL_chatInviteExported_layer133();
                break;
            case -317687113:
                tLRPC$TL_chatInviteExported = new TLRPC$TL_chatInvitePublicJoinRequests();
                break;
            case -64092740:
                tLRPC$TL_chatInviteExported = new TLRPC$TL_chatInviteExported_layer122();
                break;
            case 179611673:
                tLRPC$TL_chatInviteExported = new TLRPC$TL_chatInviteExported();
                break;
            case 1776236393:
                tLRPC$TL_chatInviteExported = new TLRPC$TL_chatInviteEmpty_layer122();
                break;
            case 1847917725:
                tLRPC$TL_chatInviteExported = new TLRPC$TL_chatInviteExported_layer131();
                break;
            default:
                tLRPC$TL_chatInviteExported = null;
                break;
        }
        if (tLRPC$TL_chatInviteExported != null || !z) {
            if (tLRPC$TL_chatInviteExported != null) {
                tLRPC$TL_chatInviteExported.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_chatInviteExported;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ExportedChatInvite", new Object[]{Integer.valueOf(i)}));
    }
}
