package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$Config extends TLObject {
    public String autoupdate_url_prefix;
    public int base_lang_pack_version;
    public boolean blocked_mode;
    public int call_connect_timeout_ms;
    public int call_packet_timeout_ms;
    public int call_receive_timeout_ms;
    public int call_ring_timeout_ms;
    public int caption_length_max;
    public int channels_read_media_period;
    public int chat_size_max;
    public int date;
    public ArrayList<TLRPC$TL_dcOption> dc_options = new ArrayList<>();
    public String dc_txt_domain_name;
    public boolean default_p2p_contacts;
    public int edit_time_limit;
    public int expires;
    public int flags;
    public boolean force_try_ipv6;
    public int forwarded_count_max;
    public String gif_search_username;
    public boolean ignore_phone_entities;
    public String img_search_username;
    public int lang_pack_version;
    public String me_url_prefix;
    public int megagroup_size_max;
    public int message_length_max;
    public int notify_cloud_delay_ms;
    public int notify_default_delay_ms;
    public int offline_blur_timeout_ms;
    public int offline_idle_timeout_ms;
    public int online_cloud_timeout_ms;
    public int online_update_period_ms;
    public boolean pfs_enabled;
    public boolean phonecalls_enabled;
    public int pinned_dialogs_count_max;
    public int pinned_infolder_count_max;
    public boolean preload_featured_stickers;
    public int push_chat_limit;
    public int push_chat_period_ms;
    public int rating_e_decay;
    public TLRPC$Reaction reactions_default;
    public boolean revoke_pm_inbox;
    public int revoke_pm_time_limit;
    public int revoke_time_limit;
    public int saved_gifs_limit;
    public String static_maps_provider;
    public int stickers_faved_limit;
    public int stickers_recent_limit;
    public String suggested_lang_code;
    public boolean test_mode;
    public int this_dc;
    public int tmp_sessions;
    public String venue_search_username;
    public int webfile_dc_id;

    public static TLRPC$Config TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Config tLRPC$Config;
        if (i != NUM) {
            tLRPC$Config = i != NUM ? null : new TLRPC$TL_config_layer144();
        } else {
            tLRPC$Config = new TLRPC$TL_config();
        }
        if (tLRPC$Config != null || !z) {
            if (tLRPC$Config != null) {
                tLRPC$Config.readParams(abstractSerializedData, z);
            }
            return tLRPC$Config;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Config", new Object[]{Integer.valueOf(i)}));
    }
}
