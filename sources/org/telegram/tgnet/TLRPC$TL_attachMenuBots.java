package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_attachMenuBots extends TLRPC$AttachMenuBots {
    public static int constructor = NUM;
    public long hash;
    public ArrayList<TLRPC$TL_attachMenuBot> bots = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.hash = abstractSerializedData.readInt64(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            TLRPC$TL_attachMenuBot TLdeserialize = TLRPC$AttachMenuBot.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.bots.add(TLdeserialize);
        }
        int readInt323 = abstractSerializedData.readInt32(z);
        if (readInt323 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
            }
            return;
        }
        int readInt324 = abstractSerializedData.readInt32(z);
        for (int i2 = 0; i2 < readInt324; i2++) {
            TLRPC$User TLdeserialize2 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize2 == null) {
                return;
            }
            this.users.add(TLdeserialize2);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.hash);
        abstractSerializedData.writeInt32(NUM);
        int size = this.bots.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.bots.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.users.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            this.users.get(i2).serializeToStream(abstractSerializedData);
        }
    }
}
