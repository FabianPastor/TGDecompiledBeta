package org.telegram.tgnet;

public class TLRPC$TL_auth_sentCodeTypeEmailCode extends TLRPC$auth_SentCodeType {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.apple_signin_allowed = (readInt32 & 1) != 0;
        if ((readInt32 & 2) != 0) {
            z2 = true;
        }
        this.google_signin_allowed = z2;
        this.email_pattern = abstractSerializedData.readString(z);
        this.length = abstractSerializedData.readInt32(z);
        if ((this.flags & 4) != 0) {
            this.next_phone_login_date = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.apple_signin_allowed ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.google_signin_allowed ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeString(this.email_pattern);
        abstractSerializedData.writeInt32(this.length);
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.next_phone_login_date);
        }
    }
}
