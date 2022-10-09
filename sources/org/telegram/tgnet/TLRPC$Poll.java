package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$Poll extends TLObject {
    public ArrayList<TLRPC$TL_pollAnswer> answers = new ArrayList<>();
    public int close_date;
    public int close_period;
    public boolean closed;
    public int flags;
    public long id;
    public boolean multiple_choice;
    public boolean public_voters;
    public String question;
    public boolean quiz;

    public static TLRPC$Poll TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_poll tLRPC$TL_poll;
        if (i == -NUM) {
            tLRPC$TL_poll = new TLRPC$TL_poll();
        } else if (i == -NUM) {
            tLRPC$TL_poll = new TLRPC$TL_poll() { // from class: org.telegram.tgnet.TLRPC$TL_poll_toDelete
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLRPC$TL_poll, org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.id = abstractSerializedData2.readInt64(z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    this.closed = (readInt32 & 1) != 0;
                    this.public_voters = (readInt32 & 2) != 0;
                    this.multiple_choice = (readInt32 & 4) != 0;
                    this.quiz = (readInt32 & 8) != 0;
                    this.question = abstractSerializedData2.readString(z2);
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    if (readInt322 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                        }
                        return;
                    }
                    int readInt323 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt323; i2++) {
                        TLRPC$TL_pollAnswer TLdeserialize = TLRPC$TL_pollAnswer.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.answers.add(TLdeserialize);
                    }
                    if ((this.flags & 16) == 0) {
                        return;
                    }
                    this.close_date = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLRPC$TL_poll, org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.id);
                    int i2 = this.closed ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    int i3 = this.public_voters ? i2 | 2 : i2 & (-3);
                    this.flags = i3;
                    int i4 = this.multiple_choice ? i3 | 4 : i3 & (-5);
                    this.flags = i4;
                    int i5 = this.quiz ? i4 | 8 : i4 & (-9);
                    this.flags = i5;
                    abstractSerializedData2.writeInt32(i5);
                    abstractSerializedData2.writeString(this.question);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.answers.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i6 = 0; i6 < size; i6++) {
                        this.answers.get(i6).serializeToStream(abstractSerializedData2);
                    }
                    if ((this.flags & 16) != 0) {
                        abstractSerializedData2.writeInt32(this.close_date);
                    }
                }
            };
        } else {
            tLRPC$TL_poll = i != -NUM ? null : new TLRPC$TL_poll() { // from class: org.telegram.tgnet.TLRPC$TL_poll_layer111
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLRPC$TL_poll, org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.id = abstractSerializedData2.readInt64(z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    this.closed = (readInt32 & 1) != 0;
                    this.public_voters = (readInt32 & 2) != 0;
                    this.multiple_choice = (readInt32 & 4) != 0;
                    this.quiz = (readInt32 & 8) != 0;
                    this.question = abstractSerializedData2.readString(z2);
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    if (readInt322 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                        }
                        return;
                    }
                    int readInt323 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt323; i2++) {
                        TLRPC$TL_pollAnswer TLdeserialize = TLRPC$TL_pollAnswer.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.answers.add(TLdeserialize);
                    }
                }

                @Override // org.telegram.tgnet.TLRPC$TL_poll, org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.id);
                    int i2 = this.closed ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    int i3 = this.public_voters ? i2 | 2 : i2 & (-3);
                    this.flags = i3;
                    int i4 = this.multiple_choice ? i3 | 4 : i3 & (-5);
                    this.flags = i4;
                    int i5 = this.quiz ? i4 | 8 : i4 & (-9);
                    this.flags = i5;
                    abstractSerializedData2.writeInt32(i5);
                    abstractSerializedData2.writeString(this.question);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.answers.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i6 = 0; i6 < size; i6++) {
                        this.answers.get(i6).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        }
        if (tLRPC$TL_poll != null || !z) {
            if (tLRPC$TL_poll != null) {
                tLRPC$TL_poll.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_poll;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Poll", Integer.valueOf(i)));
    }
}
