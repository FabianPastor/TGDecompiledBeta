package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_updateServiceNotification extends TLRPC$Update {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public int flags;
    public int inbox_date;
    public TLRPC$MessageMedia media;
    public String message;
    public boolean popup;
    public String type;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.popup = (readInt32 & 1) != 0;
        if ((readInt32 & 2) != 0) {
            this.inbox_date = abstractSerializedData.readInt32(z);
        }
        this.type = abstractSerializedData.readString(z);
        this.message = abstractSerializedData.readString(z);
        this.media = TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
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
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.popup ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.inbox_date);
        }
        abstractSerializedData.writeString(this.type);
        abstractSerializedData.writeString(this.message);
        this.media.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.entities.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.entities.get(i2).serializeToStream(abstractSerializedData);
        }
    }
}
