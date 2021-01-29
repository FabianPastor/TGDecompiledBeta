package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_pageBlockEmbedPost extends TLRPC$PageBlock {
    public static int constructor = -NUM;
    public String author;
    public long author_photo_id;
    public ArrayList<TLRPC$PageBlock> blocks = new ArrayList<>();
    public TLRPC$TL_pageCaption caption;
    public int date;
    public String url;
    public long webpage_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.url = abstractSerializedData.readString(z);
        this.webpage_id = abstractSerializedData.readInt64(z);
        this.author_photo_id = abstractSerializedData.readInt64(z);
        this.author = abstractSerializedData.readString(z);
        this.date = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.blocks.add(TLdeserialize);
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
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeInt64(this.webpage_id);
        abstractSerializedData.writeInt64(this.author_photo_id);
        abstractSerializedData.writeString(this.author);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(NUM);
        int size = this.blocks.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.blocks.get(i).serializeToStream(abstractSerializedData);
        }
        this.caption.serializeToStream(abstractSerializedData);
    }
}
