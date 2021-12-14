package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_updatePinnedDialogs extends TLRPC$Update {
    public static int constructor = -99664734;
    public int flags;
    public int folder_id;
    public ArrayList<TLRPC$DialogPeer> order = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 2) != 0) {
            this.folder_id = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 1) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$DialogPeer TLdeserialize = TLRPC$DialogPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.order.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
            }
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.folder_id);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.order.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.order.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }
}
