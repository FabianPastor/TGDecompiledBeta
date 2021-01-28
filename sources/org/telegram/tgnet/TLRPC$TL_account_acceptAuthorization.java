package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_account_acceptAuthorization extends TLObject {
    public static int constructor = -NUM;
    public int bot_id;
    public TLRPC$TL_secureCredentialsEncrypted credentials;
    public String public_key;
    public String scope;
    public ArrayList<TLRPC$TL_secureValueHash> value_hashes = new ArrayList<>();

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.bot_id);
        abstractSerializedData.writeString(this.scope);
        abstractSerializedData.writeString(this.public_key);
        abstractSerializedData.writeInt32(NUM);
        int size = this.value_hashes.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.value_hashes.get(i).serializeToStream(abstractSerializedData);
        }
        this.credentials.serializeToStream(abstractSerializedData);
    }
}
