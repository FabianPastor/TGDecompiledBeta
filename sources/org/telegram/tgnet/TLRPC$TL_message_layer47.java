package org.telegram.tgnet;

import android.text.TextUtils;

public class TLRPC$TL_message_layer47 extends TLRPC$TL_message {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.unread = (readInt32 & 1) != 0;
        this.out = (this.flags & 2) != 0;
        this.mentioned = (this.flags & 16) != 0;
        this.media_unread = (this.flags & 32) != 0;
        this.id = abstractSerializedData.readInt32(z);
        if ((this.flags & 256) != 0) {
            this.from_id = abstractSerializedData.readInt32(z);
        }
        TLRPC$Peer TLdeserialize = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.to_id = TLdeserialize;
        if (this.from_id == 0) {
            int i2 = TLdeserialize.user_id;
            if (i2 != 0) {
                this.from_id = i2;
            } else {
                this.from_id = -TLdeserialize.channel_id;
            }
        }
        if ((this.flags & 4) != 0) {
            this.fwd_from = new TLRPC$TL_messageFwdHeader();
            TLRPC$Peer TLdeserialize2 = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize2 instanceof TLRPC$TL_peerChannel) {
                TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.fwd_from;
                tLRPC$MessageFwdHeader.channel_id = TLdeserialize2.channel_id;
                tLRPC$MessageFwdHeader.flags |= 2;
            } else if (TLdeserialize2 instanceof TLRPC$TL_peerUser) {
                TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader2 = this.fwd_from;
                tLRPC$MessageFwdHeader2.from_id = TLdeserialize2.user_id;
                tLRPC$MessageFwdHeader2.flags |= 1;
            }
            this.fwd_from.date = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 2048) != 0) {
            this.via_bot_id = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 8) != 0) {
            this.reply_to_msg_id = abstractSerializedData.readInt32(z);
        }
        this.date = abstractSerializedData.readInt32(z);
        this.message = abstractSerializedData.readString(z);
        if ((this.flags & 512) != 0) {
            TLRPC$MessageMedia TLdeserialize3 = TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.media = TLdeserialize3;
            if (TLdeserialize3 != null && !TextUtils.isEmpty(TLdeserialize3.captionLegacy)) {
                this.message = this.media.captionLegacy;
            }
        } else {
            this.media = new TLRPC$TL_messageMediaEmpty();
        }
        if ((this.flags & 64) != 0) {
            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 128) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$MessageEntity TLdeserialize4 = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize4 != null) {
                        this.entities.add(TLdeserialize4);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
            } else {
                return;
            }
        }
        if ((this.flags & 1024) != 0) {
            this.views = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.unread ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.out ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.mentioned ? i2 | 16 : i2 & -17;
        this.flags = i3;
        int i4 = this.media_unread ? i3 | 32 : i3 & -33;
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 256) != 0) {
            abstractSerializedData.writeInt32(this.from_id);
        }
        this.to_id.serializeToStream(abstractSerializedData);
        if ((this.flags & 4) != 0) {
            if (this.fwd_from.from_id != 0) {
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_peerUser.user_id = this.fwd_from.from_id;
                tLRPC$TL_peerUser.serializeToStream(abstractSerializedData);
            } else {
                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                tLRPC$TL_peerChannel.channel_id = this.fwd_from.channel_id;
                tLRPC$TL_peerChannel.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(this.fwd_from.date);
        }
        if ((this.flags & 2048) != 0) {
            abstractSerializedData.writeInt32(this.via_bot_id);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.reply_to_msg_id);
        }
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeString(this.message);
        if ((this.flags & 512) != 0) {
            this.media.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 64) != 0) {
            this.reply_markup.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 128) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.entities.size();
            abstractSerializedData.writeInt32(size);
            for (int i5 = 0; i5 < size; i5++) {
                this.entities.get(i5).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 1024) != 0) {
            abstractSerializedData.writeInt32(this.views);
        }
        writeAttachPath(abstractSerializedData);
    }
}