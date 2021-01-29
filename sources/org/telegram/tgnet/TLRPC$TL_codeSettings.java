package org.telegram.tgnet;

public class TLRPC$TL_codeSettings extends TLObject {
    public static int constructor = -NUM;
    public boolean allow_app_hash;
    public boolean allow_flashcall;
    public boolean current_number;
    public int flags;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.allow_flashcall = (readInt32 & 1) != 0;
        this.current_number = (readInt32 & 2) != 0;
        if ((readInt32 & 16) != 0) {
            z2 = true;
        }
        this.allow_app_hash = z2;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.allow_flashcall ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.current_number ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.allow_app_hash ? i2 | 16 : i2 & -17;
        this.flags = i3;
        abstractSerializedData.writeInt32(i3);
    }
}
