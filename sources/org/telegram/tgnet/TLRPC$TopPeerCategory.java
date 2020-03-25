package org.telegram.tgnet;

public abstract class TLRPC$TopPeerCategory extends TLObject {
    public static TLRPC$TopPeerCategory TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TopPeerCategory tLRPC$TopPeerCategory;
        switch (i) {
            case -1472172887:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryForwardUsers();
                break;
            case -1419371685:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryBotsPM();
                break;
            case -1122524854:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryGroups();
                break;
            case -68239120:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryForwardChats();
                break;
            case 104314861:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryCorrespondents();
                break;
            case 344356834:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryBotsInline();
                break;
            case 371037736:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryChannels();
                break;
            case 511092620:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryPhoneCalls();
                break;
            default:
                tLRPC$TopPeerCategory = null;
                break;
        }
        if (tLRPC$TopPeerCategory != null || !z) {
            if (tLRPC$TopPeerCategory != null) {
                tLRPC$TopPeerCategory.readParams(abstractSerializedData, z);
            }
            return tLRPC$TopPeerCategory;
        }
        throw new RuntimeException(String.format("can't parse magic %x in TopPeerCategory", new Object[]{Integer.valueOf(i)}));
    }
}
