package org.telegram.tgnet;

public class TLRPC$TL_botInfo extends TLRPC$BotInfo {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.description = abstractSerializedData.readString(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_botCommand TLdeserialize = TLRPC$TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.commands.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.menu_button = TLRPC$BotMenuButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeString(this.description);
        abstractSerializedData.writeInt32(NUM);
        int size = this.commands.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.commands.get(i).serializeToStream(abstractSerializedData);
        }
        this.menu_button.serializeToStream(abstractSerializedData);
    }
}
