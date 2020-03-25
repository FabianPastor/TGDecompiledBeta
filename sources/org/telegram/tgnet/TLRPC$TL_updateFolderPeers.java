package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_updateFolderPeers extends TLRPC$Update {
    public static int constructor = NUM;
    public ArrayList<TLRPC$TL_folderPeer> folder_peers = new ArrayList<>();
    public int pts;
    public int pts_count;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_folderPeer TLdeserialize = TLRPC$TL_folderPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.folder_peers.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.pts = abstractSerializedData.readInt32(z);
            this.pts_count = abstractSerializedData.readInt32(z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.folder_peers.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.folder_peers.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(this.pts);
        abstractSerializedData.writeInt32(this.pts_count);
    }
}
