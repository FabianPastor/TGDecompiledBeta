package org.telegram.tgnet;

public class TLRPC$TL_attachMenuBot_layer140 extends TLRPC$TL_attachMenuBot {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.inactive = (readInt32 & 1) != 0;
        this.bot_id = abstractSerializedData.readInt64(z);
        this.short_name = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_attachMenuBotIcon TLdeserialize = TLRPC$TL_attachMenuBotIcon.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.icons.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.inactive ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt64(this.bot_id);
        abstractSerializedData.writeString(this.short_name);
        abstractSerializedData.writeInt32(NUM);
        int size = this.icons.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.icons.get(i2).serializeToStream(abstractSerializedData);
        }
    }
}
