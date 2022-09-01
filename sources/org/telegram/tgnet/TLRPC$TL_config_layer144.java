package org.telegram.tgnet;

public class TLRPC$TL_config_layer144 extends TLRPC$Config {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.phonecalls_enabled = (readInt32 & 2) != 0;
        this.default_p2p_contacts = (readInt32 & 8) != 0;
        this.preload_featured_stickers = (readInt32 & 16) != 0;
        this.ignore_phone_entities = (readInt32 & 32) != 0;
        this.revoke_pm_inbox = (readInt32 & 64) != 0;
        this.blocked_mode = (readInt32 & 256) != 0;
        this.pfs_enabled = (readInt32 & 8192) != 0;
        this.force_try_ipv6 = (readInt32 & 16384) != 0;
        this.date = abstractSerializedData.readInt32(z);
        this.expires = abstractSerializedData.readInt32(z);
        this.test_mode = abstractSerializedData.readBool(z);
        this.this_dc = abstractSerializedData.readInt32(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_dcOption TLdeserialize = TLRPC$TL_dcOption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.dc_options.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.dc_txt_domain_name = abstractSerializedData.readString(z);
            this.chat_size_max = abstractSerializedData.readInt32(z);
            this.megagroup_size_max = abstractSerializedData.readInt32(z);
            this.forwarded_count_max = abstractSerializedData.readInt32(z);
            this.online_update_period_ms = abstractSerializedData.readInt32(z);
            this.offline_blur_timeout_ms = abstractSerializedData.readInt32(z);
            this.offline_idle_timeout_ms = abstractSerializedData.readInt32(z);
            this.online_cloud_timeout_ms = abstractSerializedData.readInt32(z);
            this.notify_cloud_delay_ms = abstractSerializedData.readInt32(z);
            this.notify_default_delay_ms = abstractSerializedData.readInt32(z);
            this.push_chat_period_ms = abstractSerializedData.readInt32(z);
            this.push_chat_limit = abstractSerializedData.readInt32(z);
            this.saved_gifs_limit = abstractSerializedData.readInt32(z);
            this.edit_time_limit = abstractSerializedData.readInt32(z);
            this.revoke_time_limit = abstractSerializedData.readInt32(z);
            this.revoke_pm_time_limit = abstractSerializedData.readInt32(z);
            this.rating_e_decay = abstractSerializedData.readInt32(z);
            this.stickers_recent_limit = abstractSerializedData.readInt32(z);
            this.stickers_faved_limit = abstractSerializedData.readInt32(z);
            this.channels_read_media_period = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.tmp_sessions = abstractSerializedData.readInt32(z);
            }
            this.pinned_dialogs_count_max = abstractSerializedData.readInt32(z);
            this.pinned_infolder_count_max = abstractSerializedData.readInt32(z);
            this.call_receive_timeout_ms = abstractSerializedData.readInt32(z);
            this.call_ring_timeout_ms = abstractSerializedData.readInt32(z);
            this.call_connect_timeout_ms = abstractSerializedData.readInt32(z);
            this.call_packet_timeout_ms = abstractSerializedData.readInt32(z);
            this.me_url_prefix = abstractSerializedData.readString(z);
            if ((this.flags & 128) != 0) {
                this.autoupdate_url_prefix = abstractSerializedData.readString(z);
            }
            if ((this.flags & 512) != 0) {
                this.gif_search_username = abstractSerializedData.readString(z);
            }
            if ((this.flags & 1024) != 0) {
                this.venue_search_username = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2048) != 0) {
                this.img_search_username = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4096) != 0) {
                this.static_maps_provider = abstractSerializedData.readString(z);
            }
            this.caption_length_max = abstractSerializedData.readInt32(z);
            this.message_length_max = abstractSerializedData.readInt32(z);
            this.webfile_dc_id = abstractSerializedData.readInt32(z);
            if ((this.flags & 4) != 0) {
                this.suggested_lang_code = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.lang_pack_version = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 4) != 0) {
                this.base_lang_pack_version = abstractSerializedData.readInt32(z);
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.phonecalls_enabled ? this.flags | 2 : this.flags & -3;
        this.flags = i;
        int i2 = this.default_p2p_contacts ? i | 8 : i & -9;
        this.flags = i2;
        int i3 = this.preload_featured_stickers ? i2 | 16 : i2 & -17;
        this.flags = i3;
        int i4 = this.ignore_phone_entities ? i3 | 32 : i3 & -33;
        this.flags = i4;
        int i5 = this.revoke_pm_inbox ? i4 | 64 : i4 & -65;
        this.flags = i5;
        int i6 = this.blocked_mode ? i5 | 256 : i5 & -257;
        this.flags = i6;
        int i7 = this.pfs_enabled ? i6 | 8192 : i6 & -8193;
        this.flags = i7;
        int i8 = this.force_try_ipv6 ? i7 | 16384 : i7 & -16385;
        this.flags = i8;
        abstractSerializedData.writeInt32(i8);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.expires);
        abstractSerializedData.writeBool(this.test_mode);
        abstractSerializedData.writeInt32(this.this_dc);
        abstractSerializedData.writeInt32(NUM);
        int size = this.dc_options.size();
        abstractSerializedData.writeInt32(size);
        for (int i9 = 0; i9 < size; i9++) {
            this.dc_options.get(i9).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.dc_txt_domain_name);
        abstractSerializedData.writeInt32(this.chat_size_max);
        abstractSerializedData.writeInt32(this.megagroup_size_max);
        abstractSerializedData.writeInt32(this.forwarded_count_max);
        abstractSerializedData.writeInt32(this.online_update_period_ms);
        abstractSerializedData.writeInt32(this.offline_blur_timeout_ms);
        abstractSerializedData.writeInt32(this.offline_idle_timeout_ms);
        abstractSerializedData.writeInt32(this.online_cloud_timeout_ms);
        abstractSerializedData.writeInt32(this.notify_cloud_delay_ms);
        abstractSerializedData.writeInt32(this.notify_default_delay_ms);
        abstractSerializedData.writeInt32(this.push_chat_period_ms);
        abstractSerializedData.writeInt32(this.push_chat_limit);
        abstractSerializedData.writeInt32(this.saved_gifs_limit);
        abstractSerializedData.writeInt32(this.edit_time_limit);
        abstractSerializedData.writeInt32(this.revoke_time_limit);
        abstractSerializedData.writeInt32(this.revoke_pm_time_limit);
        abstractSerializedData.writeInt32(this.rating_e_decay);
        abstractSerializedData.writeInt32(this.stickers_recent_limit);
        abstractSerializedData.writeInt32(this.stickers_faved_limit);
        abstractSerializedData.writeInt32(this.channels_read_media_period);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.tmp_sessions);
        }
        abstractSerializedData.writeInt32(this.pinned_dialogs_count_max);
        abstractSerializedData.writeInt32(this.pinned_infolder_count_max);
        abstractSerializedData.writeInt32(this.call_receive_timeout_ms);
        abstractSerializedData.writeInt32(this.call_ring_timeout_ms);
        abstractSerializedData.writeInt32(this.call_connect_timeout_ms);
        abstractSerializedData.writeInt32(this.call_packet_timeout_ms);
        abstractSerializedData.writeString(this.me_url_prefix);
        if ((this.flags & 128) != 0) {
            abstractSerializedData.writeString(this.autoupdate_url_prefix);
        }
        if ((this.flags & 512) != 0) {
            abstractSerializedData.writeString(this.gif_search_username);
        }
        if ((this.flags & 1024) != 0) {
            abstractSerializedData.writeString(this.venue_search_username);
        }
        if ((this.flags & 2048) != 0) {
            abstractSerializedData.writeString(this.img_search_username);
        }
        if ((this.flags & 4096) != 0) {
            abstractSerializedData.writeString(this.static_maps_provider);
        }
        abstractSerializedData.writeInt32(this.caption_length_max);
        abstractSerializedData.writeInt32(this.message_length_max);
        abstractSerializedData.writeInt32(this.webfile_dc_id);
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.suggested_lang_code);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.lang_pack_version);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.base_lang_pack_version);
        }
    }
}
