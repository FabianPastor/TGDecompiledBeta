package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_exportedChatInvite extends TLObject {
    public static int constructor = NUM;
    public TLRPC$ExportedChatInvite invite;
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$TL_messages_exportedChatInvite TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messages_exportedChatInvite tLRPC$TL_messages_exportedChatInvite = new TLRPC$TL_messages_exportedChatInvite();
            tLRPC$TL_messages_exportedChatInvite.readParams(abstractSerializedData, z);
            return tLRPC$TL_messages_exportedChatInvite;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messages_exportedChatInvite", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.invite = TLRPC$ExportedChatInvite.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.users.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.invite.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.users.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.users.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
