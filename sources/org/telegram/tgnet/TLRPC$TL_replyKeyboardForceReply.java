package org.telegram.tgnet;

public class TLRPC$TL_replyKeyboardForceReply extends TLRPC$ReplyMarkup {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.single_use = (readInt32 & 2) != 0;
        if ((readInt32 & 4) == 0) {
            z2 = false;
        }
        this.selective = z2;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.single_use ? this.flags | 2 : this.flags & -3;
        this.flags = i;
        int i2 = this.selective ? i | 4 : i & -5;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
    }
}
