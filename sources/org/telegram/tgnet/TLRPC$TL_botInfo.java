package org.telegram.tgnet;

public class TLRPC$TL_botInfo extends TLRPC$BotInfo {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.user_id = abstractSerializedData.readInt64(z);
        }
        if ((this.flags & 2) != 0) {
            this.description = abstractSerializedData.readString(z);
        }
        if ((this.flags & 16) != 0) {
            this.description_photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 32) != 0) {
            this.description_document = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 4) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$TL_botCommand TLdeserialize = TLRPC$TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.commands.add(TLdeserialize);
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
        if ((this.flags & 8) != 0) {
            this.menu_button = TLRPC$BotMenuButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt64(this.user_id);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.description);
        }
        if ((this.flags & 16) != 0) {
            this.description_photo.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 32) != 0) {
            this.description_document.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.commands.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.commands.get(i).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 8) != 0) {
            this.menu_button.serializeToStream(abstractSerializedData);
        }
    }
}
