package org.telegram.tgnet;

public class TLRPC$TL_secureSecretSettings extends TLObject {
    public static int constructor = NUM;
    public TLRPC$SecurePasswordKdfAlgo secure_algo;
    public byte[] secure_secret;
    public long secure_secret_id;

    public static TLRPC$TL_secureSecretSettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings = new TLRPC$TL_secureSecretSettings();
            tLRPC$TL_secureSecretSettings.readParams(abstractSerializedData, z);
            return tLRPC$TL_secureSecretSettings;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_secureSecretSettings", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.secure_algo = TLRPC$SecurePasswordKdfAlgo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.secure_secret = abstractSerializedData.readByteArray(z);
        this.secure_secret_id = abstractSerializedData.readInt64(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.secure_algo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeByteArray(this.secure_secret);
        abstractSerializedData.writeInt64(this.secure_secret_id);
    }
}
