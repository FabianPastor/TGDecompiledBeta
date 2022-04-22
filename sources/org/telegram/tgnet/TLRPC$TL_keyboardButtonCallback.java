package org.telegram.tgnet;

public class TLRPC$TL_keyboardButtonCallback extends TLRPC$KeyboardButton {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.requires_password = z2;
        this.text = abstractSerializedData.readString(z);
        this.data = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.requires_password ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeString(this.text);
        abstractSerializedData.writeByteArray(this.data);
    }
}
