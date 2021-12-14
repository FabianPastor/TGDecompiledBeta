package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_help_termsOfService extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public int flags;
    public TLRPC$TL_dataJSON id;
    public int min_age_confirm;
    public boolean popup;
    public String text;

    public static TLRPC$TL_help_termsOfService TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService = new TLRPC$TL_help_termsOfService();
            tLRPC$TL_help_termsOfService.readParams(abstractSerializedData, z);
            return tLRPC$TL_help_termsOfService;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_help_termsOfService", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.popup = (readInt32 & 1) != 0;
        this.id = TLRPC$TL_dataJSON.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
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
                this.min_age_confirm = abstractSerializedData.readInt32(z);
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
        this.id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.text);
        abstractSerializedData.writeInt32(NUM);
        int size = this.entities.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.entities.get(i2).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.min_age_confirm);
        }
    }
}
