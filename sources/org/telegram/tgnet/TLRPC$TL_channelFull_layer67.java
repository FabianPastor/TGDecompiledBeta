package org.telegram.tgnet;

public class TLRPC$TL_channelFull_layer67 extends TLRPC$TL_channelFull {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.can_view_participants = (readInt32 & 8) != 0;
        this.can_set_username = (readInt32 & 64) != 0;
        this.id = abstractSerializedData.readInt32(z);
        this.about = abstractSerializedData.readString(z);
        if ((this.flags & 1) != 0) {
            this.participants_count = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 2) != 0) {
            this.admins_count = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 4) != 0) {
            this.kicked_count = abstractSerializedData.readInt32(z);
        }
        this.read_inbox_max_id = abstractSerializedData.readInt32(z);
        this.read_outbox_max_id = abstractSerializedData.readInt32(z);
        this.unread_count = abstractSerializedData.readInt32(z);
        this.chat_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.exported_invite = TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$BotInfo TLdeserialize = TLRPC$BotInfo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.bot_info.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            if ((this.flags & 16) != 0) {
                this.migrated_from_chat_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 16) != 0) {
                this.migrated_from_max_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 32) != 0) {
                this.pinned_msg_id = abstractSerializedData.readInt32(z);
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.can_view_participants ? this.flags | 8 : this.flags & -9;
        this.flags = i;
        int i2 = this.can_set_username ? i | 64 : i & -65;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeString(this.about);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.participants_count);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.admins_count);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.kicked_count);
        }
        abstractSerializedData.writeInt32(this.read_inbox_max_id);
        abstractSerializedData.writeInt32(this.read_outbox_max_id);
        abstractSerializedData.writeInt32(this.unread_count);
        this.chat_photo.serializeToStream(abstractSerializedData);
        this.notify_settings.serializeToStream(abstractSerializedData);
        this.exported_invite.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.bot_info.size();
        abstractSerializedData.writeInt32(size);
        for (int i3 = 0; i3 < size; i3++) {
            this.bot_info.get(i3).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeInt32(this.migrated_from_chat_id);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeInt32(this.migrated_from_max_id);
        }
        if ((this.flags & 32) != 0) {
            abstractSerializedData.writeInt32(this.pinned_msg_id);
        }
    }
}
