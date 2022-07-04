package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_deleteScheduledMessages extends TLObject {
    public static int constructor = NUM;
    public ArrayList<Integer> id = new ArrayList<>();
    public TLRPC$InputPeer peer;

    public static TLRPC$TL_messages_deleteScheduledMessages TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_messages_deleteScheduledMessages tLRPC$TL_messages_deleteScheduledMessages = new TLRPC$TL_messages_deleteScheduledMessages();
            tLRPC$TL_messages_deleteScheduledMessages.readParams(abstractSerializedData, z);
            return tLRPC$TL_messages_deleteScheduledMessages;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_messages_deleteScheduledMessages", new Object[]{Integer.valueOf(i)}));
        }
    }

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                this.id.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.id.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.id.get(i).intValue());
        }
    }
}
