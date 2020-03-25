package org.telegram.tgnet;

public class TLRPC$TL_replyKeyboardMarkup extends TLRPC$ReplyMarkup {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.resize = (readInt32 & 1) != 0;
        this.single_use = (this.flags & 2) != 0;
        this.selective = (this.flags & 4) != 0;
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_keyboardButtonRow TLdeserialize = TLRPC$TL_keyboardButtonRow.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.rows.add(TLdeserialize);
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
        int i = this.resize ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.single_use ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.selective ? i2 | 4 : i2 & -5;
        this.flags = i3;
        abstractSerializedData.writeInt32(i3);
        abstractSerializedData.writeInt32(NUM);
        int size = this.rows.size();
        abstractSerializedData.writeInt32(size);
        for (int i4 = 0; i4 < size; i4++) {
            this.rows.get(i4).serializeToStream(abstractSerializedData);
        }
    }
}
