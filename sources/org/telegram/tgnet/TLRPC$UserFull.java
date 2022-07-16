package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$UserFull extends TLObject {
    public String about;
    public boolean blocked;
    public TLRPC$TL_chatAdminRights bot_broadcast_admin_rights;
    public TLRPC$TL_chatAdminRights bot_group_admin_rights;
    public TLRPC$BotInfo bot_info;
    public boolean can_pin_message;
    public int common_chats_count;
    public int flags;
    public int folder_id;
    public boolean has_scheduled;
    public long id;
    public TLRPC$TL_contacts_link_layer101 link;
    public TLRPC$PeerNotifySettings notify_settings;
    public boolean phone_calls_available;
    public boolean phone_calls_private;
    public int pinned_msg_id;
    public ArrayList<TLRPC$TL_premiumGiftOption> premium_gifts = new ArrayList<>();
    public String private_forward_name;
    public TLRPC$Photo profile_photo;
    public TLRPC$TL_peerSettings settings;
    public String theme_emoticon;
    public int ttl_period;
    public TLRPC$User user;
    public boolean video_calls_available;
    public boolean voice_messages_forbidden;

    public static TLRPC$UserFull TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$UserFull tLRPC$UserFull;
        switch (i) {
            case -1938625919:
                tLRPC$UserFull = new TLRPC$TL_userFull_layer143();
                break;
            case -1901811583:
                tLRPC$UserFull = new TLRPC$TL_userFull_layer98();
                break;
            case -994968513:
                tLRPC$UserFull = new TLRPC$TL_userFull();
                break;
            case -818518751:
                tLRPC$UserFull = new TLRPC$TL_userFull_layer139();
                break;
            case -694681851:
                tLRPC$UserFull = new TLRPC$TL_userFull_layer134();
                break;
            case -302941166:
                tLRPC$UserFull = new TLRPC$TL_userFull_layer123();
                break;
            case 328899191:
                tLRPC$UserFull = new TLRPC$TL_userFull_layer131();
                break;
            case 1951750604:
                tLRPC$UserFull = new TLRPC$TL_userFull_layer101();
                break;
            default:
                tLRPC$UserFull = null;
                break;
        }
        if (tLRPC$UserFull != null || !z) {
            if (tLRPC$UserFull != null) {
                tLRPC$UserFull.readParams(abstractSerializedData, z);
            }
            return tLRPC$UserFull;
        }
        throw new RuntimeException(String.format("can't parse magic %x in UserFull", new Object[]{Integer.valueOf(i)}));
    }
}
