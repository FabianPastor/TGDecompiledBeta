package org.telegram.tgnet;

public class TLRPC$TL_account_passwordInputSettings extends TLObject {
    public static int constructor = -NUM;
    public String email;
    public int flags;
    public String hint;
    public TLRPC$PasswordKdfAlgo new_algo;
    public byte[] new_password_hash;
    public TLRPC$TL_secureSecretSettings new_secure_settings;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.new_algo = TLRPC$PasswordKdfAlgo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 1) != 0) {
            this.new_password_hash = abstractSerializedData.readByteArray(z);
        }
        if ((this.flags & 1) != 0) {
            this.hint = abstractSerializedData.readString(z);
        }
        if ((this.flags & 2) != 0) {
            this.email = abstractSerializedData.readString(z);
        }
        if ((this.flags & 4) != 0) {
            this.new_secure_settings = TLRPC$TL_secureSecretSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            this.new_algo.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeByteArray(this.new_password_hash);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.hint);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.email);
        }
        if ((this.flags & 4) != 0) {
            this.new_secure_settings.serializeToStream(abstractSerializedData);
        }
    }
}
