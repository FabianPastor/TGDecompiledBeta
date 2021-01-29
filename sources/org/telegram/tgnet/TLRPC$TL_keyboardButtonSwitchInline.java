package org.telegram.tgnet;

public class TLRPC$TL_keyboardButtonSwitchInline extends TLRPC$KeyboardButton {
    public static int constructor = 90744648;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.same_peer = z2;
        this.text = abstractSerializedData.readString(z);
        this.query = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.same_peer ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeString(this.text);
        abstractSerializedData.writeString(this.query);
    }
}
