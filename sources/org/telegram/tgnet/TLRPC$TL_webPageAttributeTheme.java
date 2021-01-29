package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_webPageAttributeTheme extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$Document> documents = new ArrayList<>();
    public int flags;
    public TLRPC$TL_themeSettings settings;

    public static TLRPC$TL_webPageAttributeTheme TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_webPageAttributeTheme tLRPC$TL_webPageAttributeTheme = new TLRPC$TL_webPageAttributeTheme();
            tLRPC$TL_webPageAttributeTheme.readParams(abstractSerializedData, z);
            return tLRPC$TL_webPageAttributeTheme;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_webPageAttributeTheme", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.documents.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
            } else {
                return;
            }
        }
        if ((this.flags & 2) != 0) {
            this.settings = TLRPC$TL_themeSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.documents.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.documents.get(i).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 2) != 0) {
            this.settings.serializeToStream(abstractSerializedData);
        }
    }
}
