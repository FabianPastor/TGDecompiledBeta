package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_pageBlockRelatedArticles extends TLRPC$PageBlock {
    public static int constructor = NUM;
    public ArrayList<TLRPC$TL_pageRelatedArticle> articles = new ArrayList<>();
    public TLRPC$RichText title;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.title = TLRPC$RichText.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_pageRelatedArticle TLdeserialize = TLRPC$TL_pageRelatedArticle.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.articles.add(TLdeserialize);
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
        this.title.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.articles.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.articles.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
