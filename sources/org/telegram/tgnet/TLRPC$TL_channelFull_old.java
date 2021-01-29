package org.telegram.tgnet;

public class TLRPC$TL_channelFull_old extends TLRPC$TL_channelFull {
    public static int constructor = -88925533;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        this.can_view_participants = (readInt32 & 8) != 0;
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
        this.unread_count = abstractSerializedData.readInt32(z);
        this.unread_important_count = abstractSerializedData.readInt32(z);
        this.chat_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        TLRPC$ExportedChatInvite TLdeserialize = TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if (TLdeserialize instanceof TLRPC$TL_chatInviteExported) {
            this.exported_invite = (TLRPC$TL_chatInviteExported) TLdeserialize;
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.can_view_participants ? this.flags | 8 : this.flags & -9;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
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
        abstractSerializedData.writeInt32(this.unread_count);
        abstractSerializedData.writeInt32(this.unread_important_count);
        this.chat_photo.serializeToStream(abstractSerializedData);
        this.notify_settings.serializeToStream(abstractSerializedData);
        this.exported_invite.serializeToStream(abstractSerializedData);
    }
}
