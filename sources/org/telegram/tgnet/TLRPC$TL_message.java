package org.telegram.tgnet;

public class TLRPC$TL_message extends TLRPC$Message {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.out = (readInt32 & 2) != 0;
        this.mentioned = (readInt32 & 16) != 0;
        this.media_unread = (readInt32 & 32) != 0;
        this.silent = (readInt32 & 8192) != 0;
        this.post = (readInt32 & 16384) != 0;
        this.from_scheduled = (262144 & readInt32) != 0;
        this.legacy = (524288 & readInt32) != 0;
        this.edit_hide = (2097152 & readInt32) != 0;
        this.pinned = (16777216 & readInt32) != 0;
        this.noforwards = (readInt32 & 67108864) != 0;
        this.id = abstractSerializedData.readInt32(z);
        if ((this.flags & 256) != 0) {
            this.from_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 4) != 0) {
            this.fwd_from = TLRPC$MessageFwdHeader.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 2048) != 0) {
            this.via_bot_id = abstractSerializedData.readInt64(z);
        }
        if ((this.flags & 8) != 0) {
            this.reply_to = TLRPC$TL_messageReplyHeader.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.date = abstractSerializedData.readInt32(z);
        this.message = abstractSerializedData.readString(z);
        if ((this.flags & 512) != 0) {
            this.media = TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 64) != 0) {
            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 128) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                int i2 = 0;
                while (i2 < readInt323) {
                    TLRPC$MessageEntity TLdeserialize = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.entities.add(TLdeserialize);
                        i2++;
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
        if ((this.flags & 1024) != 0) {
            this.forwards = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 8388608) != 0) {
            this.replies = TLRPC$MessageReplies.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 32768) != 0) {
            this.edit_date = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 65536) != 0) {
            this.post_author = abstractSerializedData.readString(z);
        }
        if ((this.flags & 131072) != 0) {
            this.grouped_id = abstractSerializedData.readInt64(z);
        }
        if ((this.flags & 1048576) != 0) {
            this.reactions = TLRPC$TL_messageReactions.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 4194304) != 0) {
            int readInt324 = abstractSerializedData.readInt32(z);
            if (readInt324 == NUM) {
                int readInt325 = abstractSerializedData.readInt32(z);
                while (i < readInt325) {
                    TLRPC$TL_restrictionReason TLdeserialize2 = TLRPC$TL_restrictionReason.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.restriction_reason.add(TLdeserialize2);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt324)}));
            } else {
                return;
            }
        }
        if ((this.flags & 33554432) != 0) {
            this.ttl_period = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.out ? this.flags | 2 : this.flags & -3;
        this.flags = i;
        int i2 = this.mentioned ? i | 16 : i & -17;
        this.flags = i2;
        int i3 = this.media_unread ? i2 | 32 : i2 & -33;
        this.flags = i3;
        int i4 = this.silent ? i3 | 8192 : i3 & -8193;
        this.flags = i4;
        int i5 = this.post ? i4 | 16384 : i4 & -16385;
        this.flags = i5;
        int i6 = this.from_scheduled ? i5 | 262144 : i5 & -262145;
        this.flags = i6;
        int i7 = this.legacy ? i6 | 524288 : i6 & -524289;
        this.flags = i7;
        int i8 = this.edit_hide ? i7 | 2097152 : i7 & -2097153;
        this.flags = i8;
        int i9 = this.pinned ? i8 | 16777216 : i8 & -16777217;
        this.flags = i9;
        int i10 = this.noforwards ? i9 | 67108864 : i9 & -67108865;
        this.flags = i10;
        abstractSerializedData.writeInt32(i10);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 256) != 0) {
            this.from_id.serializeToStream(abstractSerializedData);
        }
        this.peer_id.serializeToStream(abstractSerializedData);
        if ((this.flags & 4) != 0) {
            this.fwd_from.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2048) != 0) {
            abstractSerializedData.writeInt64(this.via_bot_id);
        }
        if ((this.flags & 8) != 0) {
            this.reply_to.serializeToStream(abstractSerializedData);
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
            for (int i11 = 0; i11 < size; i11++) {
                this.entities.get(i11).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 1024) != 0) {
            abstractSerializedData.writeInt32(this.views);
        }
        if ((this.flags & 1024) != 0) {
            abstractSerializedData.writeInt32(this.forwards);
        }
        if ((this.flags & 8388608) != 0) {
            this.replies.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 32768) != 0) {
            abstractSerializedData.writeInt32(this.edit_date);
        }
        if ((this.flags & 65536) != 0) {
            abstractSerializedData.writeString(this.post_author);
        }
        if ((this.flags & 131072) != 0) {
            abstractSerializedData.writeInt64(this.grouped_id);
        }
        if ((this.flags & 1048576) != 0) {
            this.reactions.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 4194304) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size2 = this.restriction_reason.size();
            abstractSerializedData.writeInt32(size2);
            for (int i12 = 0; i12 < size2; i12++) {
                this.restriction_reason.get(i12).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 33554432) != 0) {
            abstractSerializedData.writeInt32(this.ttl_period);
        }
    }
}
