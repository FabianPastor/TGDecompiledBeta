package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_pageBlockTable extends TLRPC$PageBlock {
    public static int constructor = -NUM;
    public boolean bordered;
    public int flags;
    public ArrayList<TLRPC$TL_pageTableRow> rows = new ArrayList<>();
    public boolean striped;
    public TLRPC$RichText title;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.bordered = (readInt32 & 1) != 0;
        this.striped = (readInt32 & 2) != 0;
        this.title = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_pageTableRow TLdeserialize = TLRPC$TL_pageTableRow.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.rows.add(TLdeserialize);
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
        int i = this.bordered ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.striped ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        this.title.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.rows.size();
        abstractSerializedData.writeInt32(size);
        for (int i3 = 0; i3 < size; i3++) {
            this.rows.get(i3).serializeToStream(abstractSerializedData);
        }
    }
}
