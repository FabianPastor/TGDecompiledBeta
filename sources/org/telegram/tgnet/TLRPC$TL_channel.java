package org.telegram.tgnet;

public class TLRPC$TL_channel extends TLRPC$Chat {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.creator = (readInt32 & 1) != 0;
        this.left = (readInt32 & 4) != 0;
        this.broadcast = (readInt32 & 32) != 0;
        this.verified = (readInt32 & 128) != 0;
        this.megagroup = (readInt32 & 256) != 0;
        this.restricted = (readInt32 & 512) != 0;
        this.signatures = (readInt32 & 2048) != 0;
        this.min = (readInt32 & 4096) != 0;
        this.scam = (524288 & readInt32) != 0;
        this.has_link = (1048576 & readInt32) != 0;
        this.has_geo = (2097152 & readInt32) != 0;
        this.slowmode_enabled = (4194304 & readInt32) != 0;
        this.call_active = (8388608 & readInt32) != 0;
        this.call_not_empty = (16777216 & readInt32) != 0;
        this.fake = (33554432 & readInt32) != 0;
        this.gigagroup = (readInt32 & 67108864) != 0;
        this.id = abstractSerializedData.readInt32(z);
        if ((this.flags & 8192) != 0) {
            this.access_hash = abstractSerializedData.readInt64(z);
        }
        this.title = abstractSerializedData.readString(z);
        if ((this.flags & 64) != 0) {
            this.username = abstractSerializedData.readString(z);
        }
        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.date = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readInt32(z);
        if ((this.flags & 512) != 0) {
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 == NUM) {
                int readInt323 = abstractSerializedData.readInt32(z);
                while (i < readInt323) {
                    TLRPC$TL_restrictionReason TLdeserialize = TLRPC$TL_restrictionReason.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.restriction_reason.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
            } else {
                return;
            }
        }
        if ((this.flags & 16384) != 0) {
            this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 32768) != 0) {
            this.banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 262144) != 0) {
            this.default_banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 131072) != 0) {
            this.participants_count = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.creator ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.left ? i | 4 : i & -5;
        this.flags = i2;
        int i3 = this.broadcast ? i2 | 32 : i2 & -33;
        this.flags = i3;
        int i4 = this.verified ? i3 | 128 : i3 & -129;
        this.flags = i4;
        int i5 = this.megagroup ? i4 | 256 : i4 & -257;
        this.flags = i5;
        int i6 = this.restricted ? i5 | 512 : i5 & -513;
        this.flags = i6;
        int i7 = this.signatures ? i6 | 2048 : i6 & -2049;
        this.flags = i7;
        int i8 = this.min ? i7 | 4096 : i7 & -4097;
        this.flags = i8;
        int i9 = this.scam ? i8 | 524288 : i8 & -524289;
        this.flags = i9;
        int i10 = this.has_link ? i9 | 1048576 : i9 & -1048577;
        this.flags = i10;
        int i11 = this.has_geo ? i10 | 2097152 : i10 & -2097153;
        this.flags = i11;
        int i12 = this.slowmode_enabled ? i11 | 4194304 : i11 & -4194305;
        this.flags = i12;
        int i13 = this.call_active ? i12 | 8388608 : i12 & -8388609;
        this.flags = i13;
        int i14 = this.call_not_empty ? i13 | 16777216 : i13 & -16777217;
        this.flags = i14;
        int i15 = this.fake ? i14 | 33554432 : i14 & -33554433;
        this.flags = i15;
        int i16 = this.gigagroup ? i15 | 67108864 : i15 & -67108865;
        this.flags = i16;
        abstractSerializedData.writeInt32(i16);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 8192) != 0) {
            abstractSerializedData.writeInt64(this.access_hash);
        }
        abstractSerializedData.writeString(this.title);
        if ((this.flags & 64) != 0) {
            abstractSerializedData.writeString(this.username);
        }
        this.photo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.version);
        if ((this.flags & 512) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.restriction_reason.size();
            abstractSerializedData.writeInt32(size);
            for (int i17 = 0; i17 < size; i17++) {
                this.restriction_reason.get(i17).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 16384) != 0) {
            this.admin_rights.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 32768) != 0) {
            this.banned_rights.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 262144) != 0) {
            this.default_banned_rights.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 131072) != 0) {
            abstractSerializedData.writeInt32(this.participants_count);
        }
    }
}
