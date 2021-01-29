package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_votesList extends TLObject {
    public static int constructor = NUM;
    public int count;
    public int flags;
    public String next_offset;
    public ArrayList<TLRPC$User> users = new ArrayList<>();
    public ArrayList<TLRPC$MessageUserVote> votes = new ArrayList<>();

    public static TLRPC$TL_messages_votesList TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messages_votesList tLRPC$TL_messages_votesList = new TLRPC$TL_messages_votesList();
            tLRPC$TL_messages_votesList.readParams(abstractSerializedData, z);
            return tLRPC$TL_messages_votesList;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messages_votesList", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.count = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt322) {
                TLRPC$MessageUserVote TLdeserialize = TLRPC$MessageUserVote.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.votes.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 == NUM) {
                int readInt324 = abstractSerializedData.readInt32(z);
                while (i < readInt324) {
                    TLRPC$User TLdeserialize2 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.users.add(TLdeserialize2);
                        i++;
                    } else {
                        return;
                    }
                }
                if ((this.flags & 1) != 0) {
                    this.next_offset = abstractSerializedData.readString(z);
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt323)}));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt32(this.count);
        abstractSerializedData.writeInt32(NUM);
        int size = this.votes.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.votes.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.users.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            this.users.get(i2).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.next_offset);
        }
    }
}
