package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$MessageUserVote extends TLObject {
    public int date;
    public long user_id;

    public static TLRPC$MessageUserVote TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageUserVote tLRPC$MessageUserVote;
        if (i == -NUM) {
            tLRPC$MessageUserVote = new TLRPC$MessageUserVote() { // from class: org.telegram.tgnet.TLRPC$TL_messageUserVoteMultiple
                public static int constructor = -NUM;
                public ArrayList<byte[]> options = new ArrayList<>();

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.user_id = abstractSerializedData2.readInt64(z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    if (readInt32 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                        }
                        return;
                    }
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt322; i2++) {
                        this.options.add(abstractSerializedData2.readByteArray(z2));
                    }
                    this.date = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.user_id);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.options.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i2 = 0; i2 < size; i2++) {
                        abstractSerializedData2.writeByteArray(this.options.get(i2));
                    }
                    abstractSerializedData2.writeInt32(this.date);
                }
            };
        } else if (i == NUM) {
            tLRPC$MessageUserVote = new TLRPC$MessageUserVote() { // from class: org.telegram.tgnet.TLRPC$TL_messageUserVote
                public static int constructor = NUM;
                public byte[] option;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.user_id = abstractSerializedData2.readInt64(z2);
                    this.option = abstractSerializedData2.readByteArray(z2);
                    this.date = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.user_id);
                    abstractSerializedData2.writeByteArray(this.option);
                    abstractSerializedData2.writeInt32(this.date);
                }
            };
        } else {
            tLRPC$MessageUserVote = i != NUM ? null : new TLRPC$TL_messageUserVoteInputOption();
        }
        if (tLRPC$MessageUserVote != null || !z) {
            if (tLRPC$MessageUserVote != null) {
                tLRPC$MessageUserVote.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessageUserVote;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageUserVote", Integer.valueOf(i)));
    }
}
