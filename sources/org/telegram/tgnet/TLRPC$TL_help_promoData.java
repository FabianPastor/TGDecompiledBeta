package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_help_promoData extends TLRPC$help_PromoData {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public int expires;
    public int flags;
    public TLRPC$Peer peer;
    public boolean proxy;
    public String psa_message;
    public String psa_type;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.proxy = (readInt32 & 1) != 0;
        this.expires = abstractSerializedData.readInt32(z);
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt323) {
                TLRPC$Chat TLdeserialize = TLRPC$Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.chats.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            if (readInt324 == NUM) {
                int readInt325 = abstractSerializedData.readInt32(z);
                while (i < readInt325) {
                    TLRPC$User TLdeserialize2 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.users.add(TLdeserialize2);
                        i++;
                    } else {
                        return;
                    }
                }
                if ((this.flags & 2) != 0) {
                    this.psa_type = abstractSerializedData.readString(z);
                }
                if ((this.flags & 4) != 0) {
                    this.psa_message = abstractSerializedData.readString(z);
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
        int i = this.proxy ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(this.expires);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.chats.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.chats.get(i2).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.users.size();
        abstractSerializedData.writeInt32(size2);
        for (int i3 = 0; i3 < size2; i3++) {
            this.users.get(i3).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.psa_type);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.psa_message);
        }
    }
}
