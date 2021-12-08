package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_account_authorizations extends TLObject {
    public static int constructor = NUM;
    public int authorization_ttl_days;
    public ArrayList<TLRPC$TL_authorization> authorizations = new ArrayList<>();

    public static TLRPC$TL_account_authorizations TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_account_authorizations tLRPC$TL_account_authorizations = new TLRPC$TL_account_authorizations();
            tLRPC$TL_account_authorizations.readParams(abstractSerializedData, z);
            return tLRPC$TL_account_authorizations;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_account_authorizations", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.authorization_ttl_days = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_authorization TLdeserialize = TLRPC$TL_authorization.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.authorizations.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.authorization_ttl_days);
        abstractSerializedData.writeInt32(NUM);
        int size = this.authorizations.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.authorizations.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
