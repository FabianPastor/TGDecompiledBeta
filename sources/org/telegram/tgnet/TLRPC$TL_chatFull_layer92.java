package org.telegram.tgnet;

public class TLRPC$TL_chatFull_layer92 extends TLRPC$TL_chatFull {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.id = (long) abstractSerializedData.readInt32(z);
        this.participants = TLRPC$ChatParticipants.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 4) != 0) {
            this.chat_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.notify_settings = TLRPC$PeerNotifySettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        TLRPC$TL_chatInviteExported TLdeserialize = TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if (TLdeserialize instanceof TLRPC$TL_chatInviteExported) {
            this.exported_invite = TLdeserialize;
        }
        if ((this.flags & 8) != 0) {
            int readInt32 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt32 == NUM) {
                int readInt322 = abstractSerializedData.readInt32(z);
                while (i < readInt322) {
                    TLRPC$BotInfo TLdeserialize2 = TLRPC$BotInfo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.bot_info.add(TLdeserialize2);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
            } else {
                return;
            }
        }
        if ((this.flags & 64) != 0) {
            this.pinned_msg_id = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32((int) this.id);
        this.participants.serializeToStream(abstractSerializedData);
        if ((this.flags & 4) != 0) {
            this.chat_photo.serializeToStream(abstractSerializedData);
        }
        this.notify_settings.serializeToStream(abstractSerializedData);
        this.exported_invite.serializeToStream(abstractSerializedData);
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.bot_info.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.bot_info.get(i).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 64) != 0) {
            abstractSerializedData.writeInt32(this.pinned_msg_id);
        }
    }
}
