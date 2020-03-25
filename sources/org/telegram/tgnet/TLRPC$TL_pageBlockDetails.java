package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_pageBlockDetails extends TLRPC$PageBlock {
    public static int constructor = NUM;
    public ArrayList<TLRPC$PageBlock> blocks = new ArrayList<>();
    public int flags;
    public boolean open;
    public TLRPC$RichText title;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.open = (readInt32 & 1) != 0;
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.blocks.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.title = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.open ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(NUM);
        int size = this.blocks.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.blocks.get(i2).serializeToStream(abstractSerializedData);
        }
        this.title.serializeToStream(abstractSerializedData);
    }
}
