package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_phone_groupCall extends TLObject {
    public static int constructor = NUM;
    public TLRPC$GroupCall call;
    public ArrayList<TLRPC$TL_groupCallParticipant> participants = new ArrayList<>();
    public String participants_next_offset;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$TL_phone_groupCall TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_phone_groupCall tLRPC$TL_phone_groupCall = new TLRPC$TL_phone_groupCall();
            tLRPC$TL_phone_groupCall.readParams(abstractSerializedData, z);
            return tLRPC$TL_phone_groupCall;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_phone_groupCall", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.call = TLRPC$GroupCall.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt322) {
                TLRPC$TL_groupCallParticipant TLdeserialize = TLRPC$TL_groupCallParticipant.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.participants.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            this.participants_next_offset = abstractSerializedData.readString(z);
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 == NUM) {
                int readInt324 = abstractSerializedData.readInt32(z);
                while (i < readInt324) {
                    TLRPC$User TLdeserialize2 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.users.add(TLdeserialize2);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt323)}));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.call.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.participants.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.participants.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.participants_next_offset);
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.users.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            this.users.get(i2).serializeToStream(abstractSerializedData);
        }
    }
}
