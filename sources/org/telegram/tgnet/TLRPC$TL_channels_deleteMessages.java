package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_channels_deleteMessages extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public ArrayList<Integer> id = new ArrayList<>();

    public static TLRPC$TL_channels_deleteMessages TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_channels_deleteMessages", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_channels_deleteMessages tLRPC$TL_channels_deleteMessages = new TLRPC$TL_channels_deleteMessages();
        tLRPC$TL_channels_deleteMessages.readParams(abstractSerializedData, z);
        return tLRPC$TL_channels_deleteMessages;
    }

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_affectedMessages.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel = TLRPC$InputChannel.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            this.id.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.id.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.id.get(i).intValue());
        }
    }
}
