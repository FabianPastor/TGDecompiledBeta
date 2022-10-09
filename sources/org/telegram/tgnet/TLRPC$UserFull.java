package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
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
                tLRPC$UserFull = new TLRPC$TL_userFull() { // from class: org.telegram.tgnet.TLRPC$TL_userFull_layer143
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.blocked = (readInt32 & 1) != 0;
                        this.phone_calls_available = (readInt32 & 16) != 0;
                        this.phone_calls_private = (readInt32 & 32) != 0;
                        this.can_pin_message = (readInt32 & 128) != 0;
                        this.has_scheduled = (readInt32 & 4096) != 0;
                        if ((readInt32 & 8192) != 0) {
                            z3 = true;
                        }
                        this.video_calls_available = z3;
                        this.id = abstractSerializedData2.readInt64(z2);
                        if ((this.flags & 2) != 0) {
                            this.about = abstractSerializedData2.readString(z2);
                        }
                        this.settings = TLRPC$TL_peerSettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 64) != 0) {
                            this.pinned_msg_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.common_chats_count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2048) != 0) {
                            this.folder_id = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            this.ttl_period = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.theme_emoticon = abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 65536) != 0) {
                            this.private_forward_name = abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 131072) != 0) {
                            this.bot_group_admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.bot_broadcast_admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.blocked ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.phone_calls_available ? i2 | 16 : i2 & (-17);
                        this.flags = i3;
                        int i4 = this.phone_calls_private ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.can_pin_message ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.has_scheduled ? i5 | 4096 : i5 & (-4097);
                        this.flags = i6;
                        int i7 = this.video_calls_available ? i6 | 8192 : i6 & (-8193);
                        this.flags = i7;
                        abstractSerializedData2.writeInt32(i7);
                        abstractSerializedData2.writeInt64(this.id);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.about);
                        }
                        this.settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo.serializeToStream(abstractSerializedData2);
                        }
                        this.notify_settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeInt32(this.pinned_msg_id);
                        }
                        abstractSerializedData2.writeInt32(this.common_chats_count);
                        if ((this.flags & 2048) != 0) {
                            abstractSerializedData2.writeInt32(this.folder_id);
                        }
                        if ((this.flags & 16384) != 0) {
                            abstractSerializedData2.writeInt32(this.ttl_period);
                        }
                        if ((this.flags & 32768) != 0) {
                            abstractSerializedData2.writeString(this.theme_emoticon);
                        }
                        if ((this.flags & 65536) != 0) {
                            abstractSerializedData2.writeString(this.private_forward_name);
                        }
                        if ((this.flags & 131072) != 0) {
                            this.bot_group_admin_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.bot_broadcast_admin_rights.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case -1901811583:
                tLRPC$UserFull = new TLRPC$TL_userFull() { // from class: org.telegram.tgnet.TLRPC$TL_userFull_layer98
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.blocked = (readInt32 & 1) != 0;
                        this.phone_calls_available = (readInt32 & 16) != 0;
                        this.phone_calls_private = (readInt32 & 32) != 0;
                        if ((readInt32 & 128) != 0) {
                            z3 = true;
                        }
                        this.can_pin_message = z3;
                        this.user = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 2) != 0) {
                            this.about = abstractSerializedData2.readString(z2);
                        }
                        this.link = TLRPC$TL_contacts_link_layer101.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 64) != 0) {
                            this.pinned_msg_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.common_chats_count = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.blocked ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.phone_calls_available ? i2 | 16 : i2 & (-17);
                        this.flags = i3;
                        int i4 = this.phone_calls_private ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.can_pin_message ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        abstractSerializedData2.writeInt32(i5);
                        this.user.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.about);
                        }
                        this.link.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo.serializeToStream(abstractSerializedData2);
                        }
                        this.notify_settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeInt32(this.pinned_msg_id);
                        }
                        abstractSerializedData2.writeInt32(this.common_chats_count);
                    }
                };
                break;
            case -994968513:
                tLRPC$UserFull = new TLRPC$TL_userFull();
                break;
            case -818518751:
                tLRPC$UserFull = new TLRPC$UserFull() { // from class: org.telegram.tgnet.TLRPC$TL_userFull_layer139
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.blocked = (readInt32 & 1) != 0;
                        this.phone_calls_available = (readInt32 & 16) != 0;
                        this.phone_calls_private = (readInt32 & 32) != 0;
                        this.can_pin_message = (readInt32 & 128) != 0;
                        this.has_scheduled = (readInt32 & 4096) != 0;
                        if ((readInt32 & 8192) != 0) {
                            z3 = true;
                        }
                        this.video_calls_available = z3;
                        this.id = abstractSerializedData2.readInt64(z2);
                        if ((this.flags & 2) != 0) {
                            this.about = abstractSerializedData2.readString(z2);
                        }
                        this.settings = TLRPC$TL_peerSettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 64) != 0) {
                            this.pinned_msg_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.common_chats_count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2048) != 0) {
                            this.folder_id = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            this.ttl_period = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.theme_emoticon = abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 65536) != 0) {
                            this.private_forward_name = abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.blocked ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.phone_calls_available ? i2 | 16 : i2 & (-17);
                        this.flags = i3;
                        int i4 = this.phone_calls_private ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.can_pin_message ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.has_scheduled ? i5 | 4096 : i5 & (-4097);
                        this.flags = i6;
                        int i7 = this.video_calls_available ? i6 | 8192 : i6 & (-8193);
                        this.flags = i7;
                        abstractSerializedData2.writeInt32(i7);
                        abstractSerializedData2.writeInt64(this.id);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.about);
                        }
                        this.settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo.serializeToStream(abstractSerializedData2);
                        }
                        this.notify_settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeInt32(this.pinned_msg_id);
                        }
                        abstractSerializedData2.writeInt32(this.common_chats_count);
                        if ((this.flags & 2048) != 0) {
                            abstractSerializedData2.writeInt32(this.folder_id);
                        }
                        if ((this.flags & 16384) != 0) {
                            abstractSerializedData2.writeInt32(this.ttl_period);
                        }
                        if ((this.flags & 32768) != 0) {
                            abstractSerializedData2.writeString(this.theme_emoticon);
                        }
                        if ((this.flags & 65536) != 0) {
                            abstractSerializedData2.writeString(this.private_forward_name);
                        }
                    }
                };
                break;
            case -694681851:
                tLRPC$UserFull = new TLRPC$TL_userFull() { // from class: org.telegram.tgnet.TLRPC$TL_userFull_layer134
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.blocked = (readInt32 & 1) != 0;
                        this.phone_calls_available = (readInt32 & 16) != 0;
                        this.phone_calls_private = (readInt32 & 32) != 0;
                        this.can_pin_message = (readInt32 & 128) != 0;
                        this.has_scheduled = (readInt32 & 4096) != 0;
                        if ((readInt32 & 8192) != 0) {
                            z3 = true;
                        }
                        this.video_calls_available = z3;
                        this.user = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 2) != 0) {
                            this.about = abstractSerializedData2.readString(z2);
                        }
                        this.settings = TLRPC$TL_peerSettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 64) != 0) {
                            this.pinned_msg_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.common_chats_count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2048) != 0) {
                            this.folder_id = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            this.ttl_period = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.theme_emoticon = abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.blocked ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.phone_calls_available ? i2 | 16 : i2 & (-17);
                        this.flags = i3;
                        int i4 = this.phone_calls_private ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.can_pin_message ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.has_scheduled ? i5 | 4096 : i5 & (-4097);
                        this.flags = i6;
                        int i7 = this.video_calls_available ? i6 | 8192 : i6 & (-8193);
                        this.flags = i7;
                        abstractSerializedData2.writeInt32(i7);
                        this.user.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.about);
                        }
                        this.settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo.serializeToStream(abstractSerializedData2);
                        }
                        this.notify_settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeInt32(this.pinned_msg_id);
                        }
                        abstractSerializedData2.writeInt32(this.common_chats_count);
                        if ((this.flags & 2048) != 0) {
                            abstractSerializedData2.writeInt32(this.folder_id);
                        }
                        if ((this.flags & 16384) != 0) {
                            abstractSerializedData2.writeInt32(this.ttl_period);
                        }
                        if ((this.flags & 32768) != 0) {
                            abstractSerializedData2.writeString(this.theme_emoticon);
                        }
                    }
                };
                break;
            case -302941166:
                tLRPC$UserFull = new TLRPC$TL_userFull() { // from class: org.telegram.tgnet.TLRPC$TL_userFull_layer123
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.blocked = (readInt32 & 1) != 0;
                        this.phone_calls_available = (readInt32 & 16) != 0;
                        this.phone_calls_private = (readInt32 & 32) != 0;
                        this.can_pin_message = (readInt32 & 128) != 0;
                        this.has_scheduled = (readInt32 & 4096) != 0;
                        if ((readInt32 & 8192) != 0) {
                            z3 = true;
                        }
                        this.video_calls_available = z3;
                        this.user = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 2) != 0) {
                            this.about = abstractSerializedData2.readString(z2);
                        }
                        this.settings = TLRPC$TL_peerSettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 64) != 0) {
                            this.pinned_msg_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.common_chats_count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2048) != 0) {
                            this.folder_id = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.blocked ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.phone_calls_available ? i2 | 16 : i2 & (-17);
                        this.flags = i3;
                        int i4 = this.phone_calls_private ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.can_pin_message ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.has_scheduled ? i5 | 4096 : i5 & (-4097);
                        this.flags = i6;
                        abstractSerializedData2.writeInt32(i6);
                        this.user.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.about);
                        }
                        this.settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo.serializeToStream(abstractSerializedData2);
                        }
                        this.notify_settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeInt32(this.pinned_msg_id);
                        }
                        abstractSerializedData2.writeInt32(this.common_chats_count);
                        if ((this.flags & 2048) != 0) {
                            abstractSerializedData2.writeInt32(this.folder_id);
                        }
                    }
                };
                break;
            case 328899191:
                tLRPC$UserFull = new TLRPC$TL_userFull() { // from class: org.telegram.tgnet.TLRPC$TL_userFull_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.blocked = (readInt32 & 1) != 0;
                        this.phone_calls_available = (readInt32 & 16) != 0;
                        this.phone_calls_private = (readInt32 & 32) != 0;
                        this.can_pin_message = (readInt32 & 128) != 0;
                        this.has_scheduled = (readInt32 & 4096) != 0;
                        if ((readInt32 & 8192) != 0) {
                            z3 = true;
                        }
                        this.video_calls_available = z3;
                        this.user = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 2) != 0) {
                            this.about = abstractSerializedData2.readString(z2);
                        }
                        this.settings = TLRPC$TL_peerSettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 64) != 0) {
                            this.pinned_msg_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.common_chats_count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2048) != 0) {
                            this.folder_id = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            this.ttl_period = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.blocked ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.phone_calls_available ? i2 | 16 : i2 & (-17);
                        this.flags = i3;
                        int i4 = this.phone_calls_private ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.can_pin_message ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.has_scheduled ? i5 | 4096 : i5 & (-4097);
                        this.flags = i6;
                        int i7 = this.video_calls_available ? i6 | 8192 : i6 & (-8193);
                        this.flags = i7;
                        abstractSerializedData2.writeInt32(i7);
                        this.user.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.about);
                        }
                        this.settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo.serializeToStream(abstractSerializedData2);
                        }
                        this.notify_settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeInt32(this.pinned_msg_id);
                        }
                        abstractSerializedData2.writeInt32(this.common_chats_count);
                        if ((this.flags & 2048) != 0) {
                            abstractSerializedData2.writeInt32(this.folder_id);
                        }
                        if ((this.flags & 16384) != 0) {
                            abstractSerializedData2.writeInt32(this.ttl_period);
                        }
                    }
                };
                break;
            case 1951750604:
                tLRPC$UserFull = new TLRPC$TL_userFull() { // from class: org.telegram.tgnet.TLRPC$TL_userFull_layer101
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.blocked = (readInt32 & 1) != 0;
                        this.phone_calls_available = (readInt32 & 16) != 0;
                        this.phone_calls_private = (readInt32 & 32) != 0;
                        if ((readInt32 & 128) != 0) {
                            z3 = true;
                        }
                        this.can_pin_message = z3;
                        this.user = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 2) != 0) {
                            this.about = abstractSerializedData2.readString(z2);
                        }
                        this.link = TLRPC$TL_contacts_link_layer101.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 64) != 0) {
                            this.pinned_msg_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.common_chats_count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 2048) != 0) {
                            this.folder_id = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userFull, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.blocked ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.phone_calls_available ? i2 | 16 : i2 & (-17);
                        this.flags = i3;
                        int i4 = this.phone_calls_private ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.can_pin_message ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        abstractSerializedData2.writeInt32(i5);
                        this.user.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.about);
                        }
                        this.link.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.profile_photo.serializeToStream(abstractSerializedData2);
                        }
                        this.notify_settings.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 8) != 0) {
                            this.bot_info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeInt32(this.pinned_msg_id);
                        }
                        abstractSerializedData2.writeInt32(this.common_chats_count);
                        if ((this.flags & 2048) != 0) {
                            abstractSerializedData2.writeInt32(this.folder_id);
                        }
                    }
                };
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
        throw new RuntimeException(String.format("can't parse magic %x in UserFull", Integer.valueOf(i)));
    }
}
