package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messageReactions extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public boolean min;
    public ArrayList<TLRPC$TL_reactionCount> results = new ArrayList<>();

    public static TLRPC$TL_messageReactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messageReactions tLRPC$TL_messageReactions = new TLRPC$TL_messageReactions();
            tLRPC$TL_messageReactions.readParams(abstractSerializedData, z);
            return tLRPC$TL_messageReactions;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messageReactions", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.min = (readInt32 & 1) != 0;
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_reactionCount TLdeserialize = TLRPC$TL_reactionCount.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.results.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.min ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(NUM);
        int size = this.results.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.results.get(i2).serializeToStream(abstractSerializedData);
        }
    }
}
