package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_discussionMessage extends TLObject {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int flags;
    public int max_id;
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();
    public int read_inbox_max_id;
    public int read_outbox_max_id;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$TL_messages_discussionMessage TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messages_discussionMessage tLRPC$TL_messages_discussionMessage = new TLRPC$TL_messages_discussionMessage();
            tLRPC$TL_messages_discussionMessage.readParams(abstractSerializedData, z);
            return tLRPC$TL_messages_discussionMessage;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messages_discussionMessage", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt322) {
                TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.messages.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            if ((this.flags & 1) != 0) {
                this.max_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 2) != 0) {
                this.read_inbox_max_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 4) != 0) {
                this.read_outbox_max_id = abstractSerializedData.readInt32(z);
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
        abstractSerializedData.writeInt32(NUM);
        int size = this.messages.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.messages.get(i).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.max_id);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.read_inbox_max_id);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.read_outbox_max_id);
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
    }
}
