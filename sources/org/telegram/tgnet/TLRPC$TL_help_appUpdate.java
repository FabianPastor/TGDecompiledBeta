package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_help_appUpdate extends TLRPC$help_AppUpdate {
    public static int constructor = NUM;
    public boolean can_not_skip;
    public TLRPC$Document document;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public int flags;
    public int id;
    public String text;
    public String url;
    public String version;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.can_not_skip = (readInt32 & 1) != 0;
        this.id = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readString(z);
        this.text = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$MessageEntity TLdeserialize = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.entities.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            if ((this.flags & 2) != 0) {
                this.document = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                this.url = abstractSerializedData.readString(z);
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.can_not_skip ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeString(this.version);
        abstractSerializedData.writeString(this.text);
        abstractSerializedData.writeInt32(NUM);
        int size = this.entities.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.entities.get(i2).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            this.document.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.url);
        }
    }
}
