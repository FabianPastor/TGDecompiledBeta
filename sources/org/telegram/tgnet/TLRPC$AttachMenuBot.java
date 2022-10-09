package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$AttachMenuBot extends TLObject {
    public static TLRPC$TL_attachMenuBot TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot;
        if (i != -NUM) {
            tLRPC$TL_attachMenuBot = i != -NUM ? null : new TLRPC$TL_attachMenuBot() { // from class: org.telegram.tgnet.TLRPC$TL_attachMenuBot_layer140
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLRPC$TL_attachMenuBot, org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    this.inactive = (readInt32 & 1) != 0;
                    this.bot_id = abstractSerializedData2.readInt64(z2);
                    this.short_name = abstractSerializedData2.readString(z2);
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    if (readInt322 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                        }
                        return;
                    }
                    int readInt323 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt323; i2++) {
                        TLRPC$TL_attachMenuBotIcon TLdeserialize = TLRPC$TL_attachMenuBotIcon.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.icons.add(TLdeserialize);
                    }
                }

                @Override // org.telegram.tgnet.TLRPC$TL_attachMenuBot, org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.inactive ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    abstractSerializedData2.writeInt32(i2);
                    abstractSerializedData2.writeInt64(this.bot_id);
                    abstractSerializedData2.writeString(this.short_name);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.icons.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i3 = 0; i3 < size; i3++) {
                        this.icons.get(i3).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        } else {
            tLRPC$TL_attachMenuBot = new TLRPC$TL_attachMenuBot();
        }
        if (tLRPC$TL_attachMenuBot != null || !z) {
            if (tLRPC$TL_attachMenuBot != null) {
                tLRPC$TL_attachMenuBot.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_attachMenuBot;
        }
        throw new RuntimeException(String.format("can't parse magic %x in AttachMenuBot", Integer.valueOf(i)));
    }
}
