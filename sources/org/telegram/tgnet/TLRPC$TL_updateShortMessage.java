package org.telegram.tgnet;

public class TLRPC$TL_updateShortMessage extends TLRPC$Updates {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.out = (readInt32 & 2) != 0;
        this.mentioned = (this.flags & 16) != 0;
        this.media_unread = (this.flags & 32) != 0;
        this.silent = (this.flags & 8192) != 0;
        this.id = abstractSerializedData.readInt32(z);
        this.user_id = abstractSerializedData.readInt32(z);
        this.message = abstractSerializedData.readString(z);
        this.pts = abstractSerializedData.readInt32(z);
        this.pts_count = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        if ((this.flags & 4) != 0) {
            this.fwd_from = TLRPC$MessageFwdHeader.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 2048) != 0) {
            this.via_bot_id = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 8) != 0) {
            this.reply_to_msg_id = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 128) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$MessageEntity TLdeserialize = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.entities.add(TLdeserialize);
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
}