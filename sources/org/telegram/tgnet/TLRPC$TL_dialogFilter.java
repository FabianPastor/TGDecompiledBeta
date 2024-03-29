package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_dialogFilter extends TLRPC$DialogFilter {
    public static int constructor = NUM;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        this.contacts = (readInt32 & 1) != 0;
        this.non_contacts = (readInt32 & 2) != 0;
        this.groups = (readInt32 & 4) != 0;
        this.broadcasts = (readInt32 & 8) != 0;
        this.bots = (readInt32 & 16) != 0;
        this.exclude_muted = (readInt32 & 2048) != 0;
        this.exclude_read = (readInt32 & 4096) != 0;
        this.exclude_archived = (readInt32 & 8192) != 0;
        this.id = abstractSerializedData.readInt32(z);
        this.title = abstractSerializedData.readString(z);
        if ((this.flags & 33554432) != 0) {
            this.emoticon = abstractSerializedData.readString(z);
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
            }
            return;
        }
        int readInt323 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt323; i++) {
            TLRPC$InputPeer TLdeserialize = TLRPC$InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.pinned_peers.add(TLdeserialize);
        }
        int readInt324 = abstractSerializedData.readInt32(z);
        if (readInt324 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
            }
            return;
        }
        int readInt325 = abstractSerializedData.readInt32(z);
        for (int i2 = 0; i2 < readInt325; i2++) {
            TLRPC$InputPeer TLdeserialize2 = TLRPC$InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize2 == null) {
                return;
            }
            this.include_peers.add(TLdeserialize2);
        }
        int readInt326 = abstractSerializedData.readInt32(z);
        if (readInt326 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
            }
            return;
        }
        int readInt327 = abstractSerializedData.readInt32(z);
        for (int i3 = 0; i3 < readInt327; i3++) {
            TLRPC$InputPeer TLdeserialize3 = TLRPC$InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize3 == null) {
                return;
            }
            this.exclude_peers.add(TLdeserialize3);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.contacts ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        int i2 = this.non_contacts ? i | 2 : i & (-3);
        this.flags = i2;
        int i3 = this.groups ? i2 | 4 : i2 & (-5);
        this.flags = i3;
        int i4 = this.broadcasts ? i3 | 8 : i3 & (-9);
        this.flags = i4;
        int i5 = this.bots ? i4 | 16 : i4 & (-17);
        this.flags = i5;
        int i6 = this.exclude_muted ? i5 | 2048 : i5 & (-2049);
        this.flags = i6;
        int i7 = this.exclude_read ? i6 | 4096 : i6 & (-4097);
        this.flags = i7;
        int i8 = this.exclude_archived ? i7 | 8192 : i7 & (-8193);
        this.flags = i8;
        abstractSerializedData.writeInt32(i8);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeString(this.title);
        if ((this.flags & 33554432) != 0) {
            abstractSerializedData.writeString(this.emoticon);
        }
        abstractSerializedData.writeInt32(NUM);
        int size = this.pinned_peers.size();
        abstractSerializedData.writeInt32(size);
        for (int i9 = 0; i9 < size; i9++) {
            this.pinned_peers.get(i9).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.include_peers.size();
        abstractSerializedData.writeInt32(size2);
        for (int i10 = 0; i10 < size2; i10++) {
            this.include_peers.get(i10).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.exclude_peers.size();
        abstractSerializedData.writeInt32(size3);
        for (int i11 = 0; i11 < size3; i11++) {
            this.exclude_peers.get(i11).serializeToStream(abstractSerializedData);
        }
    }
}
