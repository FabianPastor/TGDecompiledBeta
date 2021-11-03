package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_searchResultsPositions extends TLObject {
    public static int constructor = NUM;
    public int count;
    public ArrayList<TLRPC$TL_searchResultPosition> positions = new ArrayList<>();

    public static TLRPC$TL_messages_searchResultsPositions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messages_searchResultsPositions tLRPC$TL_messages_searchResultsPositions = new TLRPC$TL_messages_searchResultsPositions();
            tLRPC$TL_messages_searchResultsPositions.readParams(abstractSerializedData, z);
            return tLRPC$TL_messages_searchResultsPositions;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messages_searchResultsPositions", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.count = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_searchResultPosition TLdeserialize = TLRPC$TL_searchResultPosition.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.positions.add(TLdeserialize);
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
        abstractSerializedData.writeInt32(this.count);
        abstractSerializedData.writeInt32(NUM);
        int size = this.positions.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.positions.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
