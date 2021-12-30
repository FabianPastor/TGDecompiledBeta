package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$ChatFull extends TLObject {
    public String about;
    public int admins_count;
    public int available_min_id;
    public ArrayList<String> available_reactions = new ArrayList<>();
    public int banned_count;
    public boolean blocked;
    public ArrayList<TLRPC$BotInfo> bot_info = new ArrayList<>();
    public TLRPC$TL_inputGroupCall call;
    public int call_msg_id;
    public boolean can_set_location;
    public boolean can_set_stickers;
    public boolean can_set_username;
    public boolean can_view_participants;
    public boolean can_view_stats;
    public TLRPC$Photo chat_photo;
    public TLRPC$Peer default_send_as;
    public TLRPC$TL_chatInviteExported exported_invite;
    public int flags;
    public int folder_id;
    public TLRPC$Peer groupcall_default_join_as;
    public boolean has_scheduled;
    public boolean hidden_prehistory;
    public long id;
    public long inviterId;
    public int invitesCount;
    public int kicked_count;
    public long linked_chat_id;
    public TLRPC$ChannelLocation location;
    public long migrated_from_chat_id;
    public int migrated_from_max_id;
    public TLRPC$PeerNotifySettings notify_settings;
    public int online_count;
    public TLRPC$ChatParticipants participants;
    public int participants_count;
    public ArrayList<String> pending_suggestions = new ArrayList<>();
    public int pinned_msg_id;
    public int pts;
    public int read_inbox_max_id;
    public int read_outbox_max_id;
    public ArrayList<Long> recent_requesters = new ArrayList<>();
    public int requests_pending;
    public int slowmode_next_send_date;
    public int slowmode_seconds;
    public int stats_dc;
    public TLRPC$StickerSet stickerset;
    public String theme_emoticon;
    public int ttl_period;
    public int unread_count;
    public int unread_important_count;

    public static TLRPC$ChatFull TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatFull tLRPC$ChatFull;
        switch (i) {
            case -1977734781:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer131();
                break;
            case -1781833897:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer70();
                break;
            case -1749097118:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer52();
                break;
            case -1736252138:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer101();
                break;
            case -1640751649:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer48();
                break;
            case -1009430225:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer67();
                break;
            case -877254512:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer89();
                break;
            case -779165146:
                tLRPC$ChatFull = new TLRPC$TL_chatFull();
                break;
            case -516145888:
                tLRPC$ChatFull = new TLRPC$TL_channelFull();
                break;
            case -374179305:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer133();
                break;
            case -304961647:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer92();
                break;
            case -281384243:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer122();
                break;
            case -261341160:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer124();
                break;
            case -253335766:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer121();
                break;
            case -213431562:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer123();
                break;
            case -88925533:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_old();
                break;
            case 56920439:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer99();
                break;
            case 231260545:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer122();
                break;
            case 277964371:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer103();
                break;
            case 401891279:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer71();
                break;
            case 461151667:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer121();
                break;
            case 478652186:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer98();
                break;
            case 581055962:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer98();
                break;
            case 625524791:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer124();
                break;
            case 763976820:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer110();
                break;
            case 771925524:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer87();
                break;
            case 793980732:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer132();
                break;
            case 1185349556:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer135();
                break;
            case 1235264985:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer132();
                break;
            case 1304281241:
                tLRPC$ChatFull = new TLRPC$TL_chatFull_layer133();
                break;
            case 1418477459:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer131();
                break;
            case 1449537070:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer135();
                break;
            case 1506802019:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer134();
                break;
            case 1991201921:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer72();
                break;
            case 2055070967:
                tLRPC$ChatFull = new TLRPC$TL_channelFull_layer123();
                break;
            default:
                tLRPC$ChatFull = null;
                break;
        }
        if (tLRPC$ChatFull != null || !z) {
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChatFull;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatFull", new Object[]{Integer.valueOf(i)}));
    }
}
