package org.telegram.tgnet;

public class TLRPC$TL_pollResults_layer131 extends TLRPC$TL_pollResults {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.min = (readInt32 & 1) != 0;
        if ((readInt32 & 2) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                int i2 = 0;
                while (i2 < readInt323) {
                    TLRPC$TL_pollAnswerVoters TLdeserialize = TLRPC$TL_pollAnswerVoters.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.results.add(TLdeserialize);
                        i2++;
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
        if ((this.flags & 4) != 0) {
            this.total_voters = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 8) != 0) {
            int readInt324 = abstractSerializedData.readInt32(z);
            if (readInt324 == NUM) {
                int readInt325 = abstractSerializedData.readInt32(z);
                for (int i3 = 0; i3 < readInt325; i3++) {
                    this.recent_voters.add(Long.valueOf((long) abstractSerializedData.readInt32(z)));
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt324)}));
            } else {
                return;
            }
        }
        if ((this.flags & 16) != 0) {
            this.solution = abstractSerializedData.readString(z);
        }
        if ((this.flags & 16) != 0) {
            int readInt326 = abstractSerializedData.readInt32(z);
            if (readInt326 == NUM) {
                int readInt327 = abstractSerializedData.readInt32(z);
                while (i < readInt327) {
                    TLRPC$MessageEntity TLdeserialize2 = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.solution_entities.add(TLdeserialize2);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt326)}));
            }
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.min ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.results.size();
            abstractSerializedData.writeInt32(size);
            for (int i2 = 0; i2 < size; i2++) {
                this.results.get(i2).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.total_voters);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size2 = this.recent_voters.size();
            abstractSerializedData.writeInt32(size2);
            for (int i3 = 0; i3 < size2; i3++) {
                abstractSerializedData.writeInt32((int) this.recent_voters.get(i3).longValue());
            }
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeString(this.solution);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size3 = this.solution_entities.size();
            abstractSerializedData.writeInt32(size3);
            for (int i4 = 0; i4 < size3; i4++) {
                this.solution_entities.get(i4).serializeToStream(abstractSerializedData);
            }
        }
    }
}
