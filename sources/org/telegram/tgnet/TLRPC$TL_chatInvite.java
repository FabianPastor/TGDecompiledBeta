package org.telegram.tgnet;

public class TLRPC$TL_chatInvite extends TLRPC$ChatInvite {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.channel = (readInt32 & 1) != 0;
        this.broadcast = (readInt32 & 2) != 0;
        this.isPublic = (readInt32 & 4) != 0;
        this.megagroup = (readInt32 & 8) != 0;
        this.title = abstractSerializedData.readString(z);
        this.photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.participants_count = abstractSerializedData.readInt32(z);
        if ((this.flags & 16) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.participants.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
            }
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.channel ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.broadcast ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.isPublic ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.megagroup ? i3 | 8 : i3 & -9;
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeString(this.title);
        this.photo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.participants_count);
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.participants.size();
            abstractSerializedData.writeInt32(size);
            for (int i5 = 0; i5 < size; i5++) {
                this.participants.get(i5).serializeToStream(abstractSerializedData);
            }
        }
    }
}
