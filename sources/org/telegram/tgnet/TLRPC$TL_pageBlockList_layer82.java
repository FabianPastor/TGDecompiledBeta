package org.telegram.tgnet;

public class TLRPC$TL_pageBlockList_layer82 extends TLRPC$TL_pageBlockList {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.ordered = abstractSerializedData.readBool(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$RichText TLdeserialize = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    TLRPC$TL_pageListItemText tLRPC$TL_pageListItemText = new TLRPC$TL_pageListItemText();
                    tLRPC$TL_pageListItemText.text = TLdeserialize;
                    this.items.add(tLRPC$TL_pageListItemText);
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
        abstractSerializedData.writeBool(this.ordered);
        abstractSerializedData.writeInt32(NUM);
        int size = this.items.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            ((TLRPC$TL_pageListItemText) this.items.get(i)).text.serializeToStream(abstractSerializedData);
        }
    }
}
