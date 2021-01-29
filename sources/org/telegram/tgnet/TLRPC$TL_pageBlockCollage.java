package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_pageBlockCollage extends TLRPC$PageBlock {
    public static int constructor = NUM;
    public TLRPC$TL_pageCaption caption;
    public ArrayList<TLRPC$PageBlock> items = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.items.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.caption = TLRPC$TL_pageCaption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.items.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.items.get(i).serializeToStream(abstractSerializedData);
        }
        this.caption.serializeToStream(abstractSerializedData);
    }
}
