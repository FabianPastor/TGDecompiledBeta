package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_inputSingleMedia extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public int flags;
    public TLRPC$InputMedia media;
    public String message;
    public long random_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.media = TLRPC$InputMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.random_id = abstractSerializedData.readInt64(z);
        this.message = abstractSerializedData.readString(z);
        if ((this.flags & 1) != 0) {
            int readInt32 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt32 == NUM) {
                int readInt322 = abstractSerializedData.readInt32(z);
                while (i < readInt322) {
                    TLRPC$MessageEntity TLdeserialize = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.entities.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
            }
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.media.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt64(this.random_id);
        abstractSerializedData.writeString(this.message);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.entities.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.entities.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }
}
