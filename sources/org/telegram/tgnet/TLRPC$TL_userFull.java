package org.telegram.tgnet;

public class TLRPC$TL_userFull extends TLRPC$UserFull {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.blocked = (readInt32 & 1) != 0;
        this.phone_calls_available = (this.flags & 16) != 0;
        this.phone_calls_private = (this.flags & 32) != 0;
        this.can_pin_message = (this.flags & 128) != 0;
        this.has_scheduled = (this.flags & 4096) != 0;
        if ((this.flags & 8192) == 0) {
            z2 = false;
        }
        this.video_calls_available = z2;
        this.user = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 2) != 0) {
            this.about = abstractSerializedData.readString(z);
        }
        this.settings = TLRPC$TL_peerSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 4) != 0) {
            this.profile_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 8) != 0) {
            this.bot_info = TLRPC$BotInfo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 64) != 0) {
            this.pinned_msg_id = abstractSerializedData.readInt32(z);
        }
        this.common_chats_count = abstractSerializedData.readInt32(z);
        if ((this.flags & 2048) != 0) {
            this.folder_id = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.blocked ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.phone_calls_available ? i | 16 : i & -17;
        this.flags = i2;
        int i3 = this.phone_calls_private ? i2 | 32 : i2 & -33;
        this.flags = i3;
        int i4 = this.can_pin_message ? i3 | 128 : i3 & -129;
        this.flags = i4;
        int i5 = this.has_scheduled ? i4 | 4096 : i4 & -4097;
        this.flags = i5;
        abstractSerializedData.writeInt32(i5);
        this.user.serializeToStream(abstractSerializedData);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.about);
        }
        this.settings.serializeToStream(abstractSerializedData);
        if ((this.flags & 4) != 0) {
            this.profile_photo.serializeToStream(abstractSerializedData);
        }
        this.notify_settings.serializeToStream(abstractSerializedData);
        if ((this.flags & 8) != 0) {
            this.bot_info.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 64) != 0) {
            abstractSerializedData.writeInt32(this.pinned_msg_id);
        }
        abstractSerializedData.writeInt32(this.common_chats_count);
        if ((this.flags & 2048) != 0) {
            abstractSerializedData.writeInt32(this.folder_id);
        }
    }
}
