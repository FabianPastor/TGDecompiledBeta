package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_phone_groupCallStreamChannels extends TLObject {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$TL_groupCallStreamChannel> channels = new ArrayList<>();

    public static TLRPC$TL_phone_groupCallStreamChannels TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_phone_groupCallStreamChannels tLRPC$TL_phone_groupCallStreamChannels = new TLRPC$TL_phone_groupCallStreamChannels();
            tLRPC$TL_phone_groupCallStreamChannels.readParams(abstractSerializedData, z);
            return tLRPC$TL_phone_groupCallStreamChannels;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_phone_groupCallStreamChannels", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_groupCallStreamChannel TLdeserialize = TLRPC$TL_groupCallStreamChannel.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.channels.add(TLdeserialize);
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
        abstractSerializedData.writeInt32(NUM);
        int size = this.channels.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.channels.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
