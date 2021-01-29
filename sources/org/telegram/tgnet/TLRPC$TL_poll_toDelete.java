package org.telegram.tgnet;

public class TLRPC$TL_poll_toDelete extends TLRPC$TL_poll {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.closed = (readInt32 & 1) != 0;
        this.public_voters = (readInt32 & 2) != 0;
        this.multiple_choice = (readInt32 & 4) != 0;
        this.quiz = (readInt32 & 8) != 0;
        this.question = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_pollAnswer TLdeserialize = TLRPC$TL_pollAnswer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.answers.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            if ((this.flags & 16) != 0) {
                this.close_date = abstractSerializedData.readInt32(z);
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
        int i = this.closed ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.public_voters ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.multiple_choice ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.quiz ? i3 | 8 : i3 & -9;
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeString(this.question);
        abstractSerializedData.writeInt32(NUM);
        int size = this.answers.size();
        abstractSerializedData.writeInt32(size);
        for (int i5 = 0; i5 < size; i5++) {
            this.answers.get(i5).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeInt32(this.close_date);
        }
    }
}
