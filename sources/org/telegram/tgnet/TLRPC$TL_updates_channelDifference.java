package org.telegram.tgnet;

public class TLRPC$TL_updates_channelDifference extends TLRPC$updates_ChannelDifference {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.isFinal = (readInt32 & 1) != 0;
        this.pts = abstractSerializedData.readInt32(z);
        if ((this.flags & 2) != 0) {
            this.timeout = abstractSerializedData.readInt32(z);
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt323) {
                TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.new_messages.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            if (readInt324 == NUM) {
                int readInt325 = abstractSerializedData.readInt32(z);
                int i3 = 0;
                while (i3 < readInt325) {
                    TLRPC$Update TLdeserialize2 = TLRPC$Update.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.other_updates.add(TLdeserialize2);
                        i3++;
                    } else {
                        return;
                    }
                }
                int readInt326 = abstractSerializedData.readInt32(z);
                if (readInt326 == NUM) {
                    int readInt327 = abstractSerializedData.readInt32(z);
                    int i4 = 0;
                    while (i4 < readInt327) {
                        TLRPC$Chat TLdeserialize3 = TLRPC$Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                        if (TLdeserialize3 != null) {
                            this.chats.add(TLdeserialize3);
                            i4++;
                        } else {
                            return;
                        }
                    }
                    int readInt328 = abstractSerializedData.readInt32(z);
                    if (readInt328 == NUM) {
                        int readInt329 = abstractSerializedData.readInt32(z);
                        while (i < readInt329) {
                            TLRPC$User TLdeserialize4 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                            if (TLdeserialize4 != null) {
                                this.users.add(TLdeserialize4);
                                i++;
                            } else {
                                return;
                            }
                        }
                    } else if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt328)}));
                    }
                } else if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt326)}));
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt324)}));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.isFinal ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(this.pts);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.timeout);
        }
        abstractSerializedData.writeInt32(NUM);
        int size = this.new_messages.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.new_messages.get(i2).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.other_updates.size();
        abstractSerializedData.writeInt32(size2);
        for (int i3 = 0; i3 < size2; i3++) {
            this.other_updates.get(i3).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.chats.size();
        abstractSerializedData.writeInt32(size3);
        for (int i4 = 0; i4 < size3; i4++) {
            this.chats.get(i4).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size4 = this.users.size();
        abstractSerializedData.writeInt32(size4);
        for (int i5 = 0; i5 < size4; i5++) {
            this.users.get(i5).serializeToStream(abstractSerializedData);
        }
    }
}
