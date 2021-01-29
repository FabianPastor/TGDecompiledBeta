package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_phone_phoneCall extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$PhoneCall phone_call;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$TL_phone_phoneCall TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_phone_phoneCall tLRPC$TL_phone_phoneCall = new TLRPC$TL_phone_phoneCall();
            tLRPC$TL_phone_phoneCall.readParams(abstractSerializedData, z);
            return tLRPC$TL_phone_phoneCall;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_phone_phoneCall", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.phone_call = TLRPC$PhoneCall.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.users.add(TLdeserialize);
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
        this.phone_call.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.users.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.users.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
