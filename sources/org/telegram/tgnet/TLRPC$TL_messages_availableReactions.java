package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_availableReactions extends TLRPC$messages_AvailableReactions {
    public static int constructor = NUM;
    public int hash;
    public ArrayList<TLRPC$TL_availableReaction> reactions = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.hash = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_availableReaction TLdeserialize = TLRPC$TL_availableReaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.reactions.add(TLdeserialize);
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
        abstractSerializedData.writeInt32(this.hash);
        abstractSerializedData.writeInt32(NUM);
        int size = this.reactions.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.reactions.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
