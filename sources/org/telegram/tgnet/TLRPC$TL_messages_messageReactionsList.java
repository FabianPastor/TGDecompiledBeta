package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_messageReactionsList extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int count;
    public int flags;
    public String next_offset;
    public ArrayList<TLRPC$MessagePeerReaction> reactions = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$TL_messages_messageReactionsList TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList = new TLRPC$TL_messages_messageReactionsList();
            tLRPC$TL_messages_messageReactionsList.readParams(abstractSerializedData, z);
            return tLRPC$TL_messages_messageReactionsList;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messages_messageReactionsList", new Object[]{Integer.valueOf(i)}));
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
                TLRPC$MessagePeerReaction TLdeserialize = TLRPC$MessagePeerReaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.reactions.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 == NUM) {
                int readInt324 = abstractSerializedData.readInt32(z);
                int i3 = 0;
                while (i3 < readInt324) {
                    TLRPC$Chat TLdeserialize2 = TLRPC$Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.chats.add(TLdeserialize2);
                        i3++;
                    } else {
                        return;
                    }
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                if (readInt325 == NUM) {
                    int readInt326 = abstractSerializedData.readInt32(z);
                    while (i < readInt326) {
                        TLRPC$User TLdeserialize3 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                        if (TLdeserialize3 != null) {
                            this.users.add(TLdeserialize3);
                            i++;
                        } else {
                            return;
                        }
                    }
                    if ((this.flags & 1) != 0) {
                        this.next_offset = abstractSerializedData.readString(z);
                    }
                } else if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt325)}));
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
        int size = this.reactions.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.reactions.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.chats.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            this.chats.get(i2).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.users.size();
        abstractSerializedData.writeInt32(size3);
        for (int i3 = 0; i3 < size3; i3++) {
            this.users.get(i3).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.next_offset);
        }
    }
}
