package org.telegram.tgnet;

public abstract class TLRPC$RecentMeUrl extends TLObject {
    public int chat_id;
    public TLRPC$ChatInvite chat_invite;
    public TLRPC$StickerSetCovered set;
    public String url;
    public int user_id;

    public static TLRPC$RecentMeUrl TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$RecentMeUrl tLRPC$RecentMeUrl;
        switch (i) {
            case -1917045962:
                tLRPC$RecentMeUrl = new TLRPC$TL_recentMeUrlUser();
                break;
            case -1608834311:
                tLRPC$RecentMeUrl = new TLRPC$TL_recentMeUrlChat();
                break;
            case -1140172836:
                tLRPC$RecentMeUrl = new TLRPC$TL_recentMeUrlStickerSet();
                break;
            case -347535331:
                tLRPC$RecentMeUrl = new TLRPC$TL_recentMeUrlChatInvite();
                break;
            case 1189204285:
                tLRPC$RecentMeUrl = new TLRPC$TL_recentMeUrlUnknown();
                break;
            default:
                tLRPC$RecentMeUrl = null;
                break;
        }
        if (tLRPC$RecentMeUrl != null || !z) {
            if (tLRPC$RecentMeUrl != null) {
                tLRPC$RecentMeUrl.readParams(abstractSerializedData, z);
            }
            return tLRPC$RecentMeUrl;
        }
        throw new RuntimeException(String.format("can't parse magic %x in RecentMeUrl", new Object[]{Integer.valueOf(i)}));
    }
}
