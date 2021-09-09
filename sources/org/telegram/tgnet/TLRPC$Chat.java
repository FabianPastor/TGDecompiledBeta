package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$Chat extends TLObject {
    public long access_hash;
    public TLRPC$TL_chatAdminRights admin_rights;
    public TLRPC$TL_channelAdminRights_layer92 admin_rights_layer92;
    public TLRPC$TL_chatBannedRights banned_rights;
    public TLRPC$TL_channelBannedRights_layer92 banned_rights_layer92;
    public boolean broadcast;
    public boolean call_active;
    public boolean call_not_empty;
    public boolean creator;
    public int date;
    public boolean deactivated;
    public TLRPC$TL_chatBannedRights default_banned_rights;
    public boolean explicit_content;
    public boolean fake;
    public int flags;
    public boolean gigagroup;
    public boolean has_geo;
    public boolean has_link;
    public long id;
    public boolean kicked;
    public boolean left;
    public boolean megagroup;
    public TLRPC$InputChannel migrated_to;
    public boolean min;
    public boolean moderator;
    public int participants_count;
    public TLRPC$ChatPhoto photo;
    public boolean restricted;
    public ArrayList<TLRPC$TL_restrictionReason> restriction_reason = new ArrayList<>();
    public boolean scam;
    public boolean signatures;
    public boolean slowmode_enabled;
    public String title;
    public int until_date;
    public String username;
    public boolean verified;
    public int version;

    public static TLRPC$Chat TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Chat tLRPC$Chat;
        switch (i) {
            case -2107528095:
                tLRPC$Chat = new TLRPC$TL_channel();
                break;
            case -2059962289:
                tLRPC$Chat = new TLRPC$TL_channelForbidden_layer67();
                break;
            case -1683826688:
                tLRPC$Chat = new TLRPC$TL_chatEmpty_layer131();
                break;
            case -1588737454:
                tLRPC$Chat = new TLRPC$TL_channel_layer67();
                break;
            case -930515796:
                tLRPC$Chat = new TLRPC$TL_channel_layer92();
                break;
            case -753232354:
                tLRPC$Chat = new TLRPC$TL_channel_layer131();
                break;
            case -652419756:
                tLRPC$Chat = new TLRPC$TL_chat_layer92();
                break;
            case -83047359:
                tLRPC$Chat = new TLRPC$TL_chatForbidden_old();
                break;
            case 120753115:
                tLRPC$Chat = new TLRPC$TL_chatForbidden_layer131();
                break;
            case 213142300:
                tLRPC$Chat = new TLRPC$TL_channel_layer72();
                break;
            case 399807445:
                tLRPC$Chat = new TLRPC$TL_channelForbidden();
                break;
            case 681420594:
                tLRPC$Chat = new TLRPC$TL_channelForbidden_layer131();
                break;
            case 693512293:
                tLRPC$Chat = new TLRPC$TL_chatEmpty();
                break;
            case 763724588:
                tLRPC$Chat = new TLRPC$TL_channelForbidden_layer52();
                break;
            case 1004149726:
                tLRPC$Chat = new TLRPC$TL_chat_layer131();
                break;
            case 1103884886:
                tLRPC$Chat = new TLRPC$TL_chat();
                break;
            case 1158377749:
                tLRPC$Chat = new TLRPC$TL_channel_layer77();
                break;
            case 1260090630:
                tLRPC$Chat = new TLRPC$TL_channel_layer48();
                break;
            case 1307772980:
                tLRPC$Chat = new TLRPC$TL_channel_layer104();
                break;
            case 1704108455:
                tLRPC$Chat = new TLRPC$TL_chatForbidden();
                break;
            case 1737397639:
                tLRPC$Chat = new TLRPC$TL_channel_old();
                break;
            case 1855757255:
                tLRPC$Chat = new TLRPC$TL_chat_old();
                break;
            case 1930607688:
                tLRPC$Chat = new TLRPC$TL_chat_old2();
                break;
            default:
                tLRPC$Chat = null;
                break;
        }
        if (tLRPC$Chat != null || !z) {
            if (tLRPC$Chat != null) {
                tLRPC$Chat.readParams(abstractSerializedData, z);
            }
            return tLRPC$Chat;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Chat", new Object[]{Integer.valueOf(i)}));
    }

    protected static TLRPC$TL_chatBannedRights mergeBannedRights(TLRPC$TL_channelBannedRights_layer92 tLRPC$TL_channelBannedRights_layer92) {
        if (tLRPC$TL_channelBannedRights_layer92 == null) {
            return null;
        }
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = new TLRPC$TL_chatBannedRights();
        tLRPC$TL_chatBannedRights.view_messages = tLRPC$TL_channelBannedRights_layer92.view_messages;
        tLRPC$TL_chatBannedRights.send_messages = tLRPC$TL_channelBannedRights_layer92.send_messages;
        boolean z = tLRPC$TL_channelBannedRights_layer92.send_media;
        tLRPC$TL_chatBannedRights.send_media = z;
        tLRPC$TL_chatBannedRights.send_stickers = tLRPC$TL_channelBannedRights_layer92.send_stickers;
        tLRPC$TL_chatBannedRights.send_gifs = tLRPC$TL_channelBannedRights_layer92.send_gifs;
        tLRPC$TL_chatBannedRights.send_games = tLRPC$TL_channelBannedRights_layer92.send_games;
        tLRPC$TL_chatBannedRights.send_inline = tLRPC$TL_channelBannedRights_layer92.send_inline;
        tLRPC$TL_chatBannedRights.embed_links = tLRPC$TL_channelBannedRights_layer92.embed_links;
        tLRPC$TL_chatBannedRights.send_polls = z;
        tLRPC$TL_chatBannedRights.change_info = true;
        tLRPC$TL_chatBannedRights.invite_users = true;
        tLRPC$TL_chatBannedRights.pin_messages = true;
        tLRPC$TL_chatBannedRights.until_date = tLRPC$TL_channelBannedRights_layer92.until_date;
        return tLRPC$TL_chatBannedRights;
    }

    protected static TLRPC$TL_chatAdminRights mergeAdminRights(TLRPC$TL_channelAdminRights_layer92 tLRPC$TL_channelAdminRights_layer92) {
        if (tLRPC$TL_channelAdminRights_layer92 == null) {
            return null;
        }
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = new TLRPC$TL_chatAdminRights();
        tLRPC$TL_chatAdminRights.change_info = tLRPC$TL_channelAdminRights_layer92.change_info;
        tLRPC$TL_chatAdminRights.post_messages = tLRPC$TL_channelAdminRights_layer92.post_messages;
        tLRPC$TL_chatAdminRights.edit_messages = tLRPC$TL_channelAdminRights_layer92.edit_messages;
        tLRPC$TL_chatAdminRights.delete_messages = tLRPC$TL_channelAdminRights_layer92.delete_messages;
        tLRPC$TL_chatAdminRights.ban_users = tLRPC$TL_channelAdminRights_layer92.ban_users;
        tLRPC$TL_chatAdminRights.invite_users = tLRPC$TL_channelAdminRights_layer92.invite_users;
        tLRPC$TL_chatAdminRights.pin_messages = tLRPC$TL_channelAdminRights_layer92.pin_messages;
        tLRPC$TL_chatAdminRights.add_admins = tLRPC$TL_channelAdminRights_layer92.add_admins;
        return tLRPC$TL_chatAdminRights;
    }
}
