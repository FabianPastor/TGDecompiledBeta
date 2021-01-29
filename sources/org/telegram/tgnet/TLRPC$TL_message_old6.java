package org.telegram.tgnet;

import android.text.TextUtils;

public class TLRPC$TL_message_old6 extends TLRPC$TL_message {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z) | 256;
        this.flags = readInt32;
        int i = 0;
        this.unread = (readInt32 & 1) != 0;
        this.out = (readInt32 & 2) != 0;
        this.mentioned = (readInt32 & 16) != 0;
        this.media_unread = (readInt32 & 32) != 0;
        this.id = abstractSerializedData.readInt32(z);
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        this.from_id = tLRPC$TL_peerUser;
        tLRPC$TL_peerUser.user_id = abstractSerializedData.readInt32(z);
        this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 4) != 0) {
            TLRPC$TL_messageFwdHeader tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader();
            this.fwd_from = tLRPC$TL_messageFwdHeader;
            tLRPC$TL_messageFwdHeader.from_id = new TLRPC$TL_peerUser();
            this.fwd_from.from_id.user_id = abstractSerializedData.readInt32(z);
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.fwd_from;
            tLRPC$MessageFwdHeader.flags |= 1;
            tLRPC$MessageFwdHeader.date = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 8) != 0) {
            TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
            this.reply_to = tLRPC$TL_messageReplyHeader;
            tLRPC$TL_messageReplyHeader.reply_to_msg_id = abstractSerializedData.readInt32(z);
        }
        this.date = abstractSerializedData.readInt32(z);
        this.message = abstractSerializedData.readString(z);
        if ((this.flags & 512) != 0) {
            TLRPC$MessageMedia TLdeserialize = TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.media = TLdeserialize;
            if (TLdeserialize != null && !TextUtils.isEmpty(TLdeserialize.captionLegacy)) {
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
                    TLRPC$MessageEntity TLdeserialize2 = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.entities.add(TLdeserialize2);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
            }
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
        abstractSerializedData.writeInt32(this.from_id.user_id);
        this.peer_id.serializeToStream(abstractSerializedData);
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.fwd_from.from_id.user_id);
            abstractSerializedData.writeInt32(this.fwd_from.date);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.reply_to.reply_to_msg_id);
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
        writeAttachPath(abstractSerializedData);
    }
}
