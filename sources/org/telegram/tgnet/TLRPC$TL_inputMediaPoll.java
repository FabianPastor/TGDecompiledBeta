package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_inputMediaPoll extends TLRPC$InputMedia {
    public static int constructor = NUM;
    public ArrayList<byte[]> correct_answers = new ArrayList<>();
    public TLRPC$Poll poll;
    public String solution;
    public ArrayList<TLRPC$MessageEntity> solution_entities = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.poll = TLRPC$Poll.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int i = 0;
        if ((this.flags & 1) != 0) {
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 == NUM) {
                int readInt322 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt322; i2++) {
                    this.correct_answers.add(abstractSerializedData.readByteArray(z));
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
            } else {
                return;
            }
        }
        if ((this.flags & 2) != 0) {
            this.solution = abstractSerializedData.readString(z);
        }
        if ((this.flags & 2) != 0) {
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 == NUM) {
                int readInt324 = abstractSerializedData.readInt32(z);
                while (i < readInt324) {
                    TLRPC$MessageEntity TLdeserialize = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.solution_entities.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt323)}));
            }
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.poll.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.correct_answers.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeByteArray(this.correct_answers.get(i));
            }
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.solution);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size2 = this.solution_entities.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.solution_entities.get(i2).serializeToStream(abstractSerializedData);
            }
        }
    }
}
