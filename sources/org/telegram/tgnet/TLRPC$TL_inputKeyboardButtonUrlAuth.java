package org.telegram.tgnet;

public class TLRPC$TL_inputKeyboardButtonUrlAuth extends TLRPC$KeyboardButton {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.request_write_access = z2;
        this.text = abstractSerializedData.readString(z);
        if ((this.flags & 2) != 0) {
            this.fwd_text = abstractSerializedData.readString(z);
        }
        this.url = abstractSerializedData.readString(z);
        this.bot = TLRPC$InputUser.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.request_write_access ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeString(this.text);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.fwd_text);
        }
        abstractSerializedData.writeString(this.url);
        this.bot.serializeToStream(abstractSerializedData);
    }
}
