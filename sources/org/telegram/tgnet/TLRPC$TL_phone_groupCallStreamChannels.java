package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_phone_groupCallStreamChannels extends TLObject {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$TL_groupCallStreamChannel> channels = new ArrayList<>();

    public static TLRPC$TL_phone_groupCallStreamChannels TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_phone_groupCallStreamChannels", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_phone_groupCallStreamChannels tLRPC$TL_phone_groupCallStreamChannels = new TLRPC$TL_phone_groupCallStreamChannels();
        tLRPC$TL_phone_groupCallStreamChannels.readParams(abstractSerializedData, z);
        return tLRPC$TL_phone_groupCallStreamChannels;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            TLRPC$TL_groupCallStreamChannel TLdeserialize = TLRPC$TL_groupCallStreamChannel.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.channels.add(TLdeserialize);
        }
    }

    @Override // org.telegram.tgnet.TLObject
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
