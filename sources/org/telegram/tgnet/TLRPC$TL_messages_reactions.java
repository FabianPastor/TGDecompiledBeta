package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_reactions extends TLRPC$messages_Reactions {
    public static int constructor = -NUM;
    public long hash;
    public ArrayList<TLRPC$Reaction> reactions = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.hash = abstractSerializedData.readInt64(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$Reaction TLdeserialize = TLRPC$Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
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
        abstractSerializedData.writeInt64(this.hash);
        abstractSerializedData.writeInt32(NUM);
        int size = this.reactions.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.reactions.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
