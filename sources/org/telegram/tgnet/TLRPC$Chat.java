package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$Chat extends TLObject {
    public long access_hash;
    public TLRPC$TL_chatAdminRights admin_rights;
    public TLRPC$TL_channelAdminRights_layer92 admin_rights_layer92;
    public TLRPC$TL_chatBannedRights banned_rights;
    public TLRPC$TL_channelBannedRights_layer92 banned_rights_layer92;
    public boolean broadcast;
    public boolean call_active;
    public boolean call_not_empty;
    public boolean creator;
    public int date;
    public boolean deactivated;
    public TLRPC$TL_chatBannedRights default_banned_rights;
    public boolean explicit_content;
    public boolean fake;
    public int flags;
    public int flags2;
    public boolean forum;
    public boolean gigagroup;
    public boolean has_geo;
    public boolean has_link;
    public long id;
    public boolean join_request;
    public boolean join_to_send;
    public boolean kicked;
    public boolean left;
    public boolean megagroup;
    public TLRPC$InputChannel migrated_to;
    public boolean min;
    public boolean moderator;
    public boolean noforwards;
    public int participants_count;
    public TLRPC$ChatPhoto photo;
    public boolean restricted;
    public boolean scam;
    public boolean signatures;
    public boolean slowmode_enabled;
    public String title;
    public int until_date;
    public String username;
    public boolean verified;
    public int version;
    public ArrayList<TLRPC$TL_restrictionReason> restriction_reason = new ArrayList<>();
    public ArrayList<TLRPC$TL_username> usernames = new ArrayList<>();

    public static TLRPC$Chat TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Chat tLRPC$Chat;
        switch (i) {
            case -2107528095:
                tLRPC$Chat = new TLRPC$Chat() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer147
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
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
                        this.gigagroup = (67108864 & readInt32) != 0;
                        this.noforwards = (NUM & readInt32) != 0;
                        this.join_to_send = (NUM & readInt32) != 0;
                        this.join_request = (NUM & readInt32) != 0;
                        this.forum = (readInt32 & NUM) != 0;
                        this.id = abstractSerializedData2.readInt64(z2);
                        if ((this.flags & 8192) != 0) {
                            this.access_hash = abstractSerializedData2.readInt64(z2);
                        }
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$TL_restrictionReason TLdeserialize = TLRPC$TL_restrictionReason.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.restriction_reason.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 131072) != 0) {
                            this.participants_count = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.left ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.broadcast ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.verified ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.megagroup ? i5 | 256 : i5 & (-257);
                        this.flags = i6;
                        int i7 = this.restricted ? i6 | 512 : i6 & (-513);
                        this.flags = i7;
                        int i8 = this.signatures ? i7 | 2048 : i7 & (-2049);
                        this.flags = i8;
                        int i9 = this.min ? i8 | 4096 : i8 & (-4097);
                        this.flags = i9;
                        int i10 = this.scam ? i9 | 524288 : i9 & (-524289);
                        this.flags = i10;
                        int i11 = this.has_link ? i10 | 1048576 : i10 & (-1048577);
                        this.flags = i11;
                        int i12 = this.has_geo ? i11 | 2097152 : i11 & (-2097153);
                        this.flags = i12;
                        int i13 = this.slowmode_enabled ? i12 | 4194304 : i12 & (-4194305);
                        this.flags = i13;
                        int i14 = this.call_active ? i13 | 8388608 : i13 & (-8388609);
                        this.flags = i14;
                        int i15 = this.call_not_empty ? i14 | 16777216 : i14 & (-16777217);
                        this.flags = i15;
                        int i16 = this.fake ? i15 | 33554432 : i15 & (-33554433);
                        this.flags = i16;
                        int i17 = this.gigagroup ? i16 | 67108864 : i16 & (-67108865);
                        this.flags = i17;
                        int i18 = this.noforwards ? i17 | NUM : i17 & (-NUM);
                        this.flags = i18;
                        int i19 = this.join_to_send ? i18 | NUM : i18 & (-NUM);
                        this.flags = i19;
                        int i20 = this.join_request ? i19 | NUM : i19 & (-NUM);
                        this.flags = i20;
                        int i21 = this.forum ? i20 | NUM : i20 & (-NUM);
                        this.flags = i21;
                        abstractSerializedData2.writeInt32(i21);
                        abstractSerializedData2.writeInt64(this.id);
                        if ((this.flags & 8192) != 0) {
                            abstractSerializedData2.writeInt64(this.access_hash);
                        }
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.restriction_reason.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i22 = 0; i22 < size; i22++) {
                                this.restriction_reason.get(i22).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 131072) != 0) {
                            abstractSerializedData2.writeInt32(this.participants_count);
                        }
                    }
                };
                break;
            case -2094689180:
                tLRPC$Chat = new TLRPC$TL_channel();
                break;
            case -2059962289:
                tLRPC$Chat = new TLRPC$TL_channelForbidden() { // from class: org.telegram.tgnet.TLRPC$TL_channelForbidden_layer67
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelForbidden, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        this.broadcast = (readInt32 & 32) != 0;
                        if ((readInt32 & 256) == 0) {
                            z3 = false;
                        }
                        this.megagroup = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelForbidden, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.broadcast ? this.flags | 32 : this.flags & (-33);
                        this.flags = i2;
                        int i3 = this.megagroup ? i2 | 256 : i2 & (-257);
                        this.flags = i3;
                        abstractSerializedData2.writeInt32(i3);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                    }
                };
                break;
            case -1683826688:
                tLRPC$Chat = new TLRPC$TL_chatEmpty() { // from class: org.telegram.tgnet.TLRPC$TL_chatEmpty_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chatEmpty, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.title = "DELETED";
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chatEmpty, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.id);
                    }
                };
                break;
            case -1588737454:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer67
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.kicked = (readInt32 & 2) != 0;
                        this.left = (readInt32 & 4) != 0;
                        this.moderator = (readInt32 & 16) != 0;
                        this.broadcast = (readInt32 & 32) != 0;
                        this.verified = (readInt32 & 128) != 0;
                        this.megagroup = (readInt32 & 256) != 0;
                        this.restricted = (readInt32 & 512) != 0;
                        this.signatures = (readInt32 & 2048) != 0;
                        if ((readInt32 & 4096) != 0) {
                            z3 = true;
                        }
                        this.min = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 8192) != 0) {
                            this.access_hash = abstractSerializedData2.readInt64(z2);
                        }
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.kicked ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.left ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.moderator ? i4 | 16 : i4 & (-17);
                        this.flags = i5;
                        int i6 = this.broadcast ? i5 | 32 : i5 & (-33);
                        this.flags = i6;
                        int i7 = this.verified ? i6 | 128 : i6 & (-129);
                        this.flags = i7;
                        int i8 = this.megagroup ? i7 | 256 : i7 & (-257);
                        this.flags = i8;
                        int i9 = this.restricted ? i8 | 512 : i8 & (-513);
                        this.flags = i9;
                        int i10 = this.signatures ? i9 | 2048 : i9 & (-2049);
                        this.flags = i10;
                        int i11 = this.min ? i10 | 4096 : i10 & (-4097);
                        this.flags = i11;
                        abstractSerializedData2.writeInt32(i11);
                        abstractSerializedData2.writeInt32((int) this.id);
                        if ((this.flags & 8192) != 0) {
                            abstractSerializedData2.writeInt64(this.access_hash);
                        }
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeString("");
                        }
                    }
                };
                break;
            case -930515796:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer92
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.left = (readInt32 & 4) != 0;
                        this.broadcast = (readInt32 & 32) != 0;
                        this.verified = (readInt32 & 128) != 0;
                        this.megagroup = (readInt32 & 256) != 0;
                        this.restricted = (readInt32 & 512) != 0;
                        this.signatures = (readInt32 & 2048) != 0;
                        if ((readInt32 & 4096) != 0) {
                            z3 = true;
                        }
                        this.min = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 8192) != 0) {
                            this.access_hash = abstractSerializedData2.readInt64(z2);
                        }
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            TLRPC$TL_channelAdminRights_layer92 TLdeserialize = TLRPC$TL_channelAdminRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            this.admin_rights_layer92 = TLdeserialize;
                            this.admin_rights = TLRPC$Chat.mergeAdminRights(TLdeserialize);
                        }
                        if ((this.flags & 32768) != 0) {
                            TLRPC$TL_channelBannedRights_layer92 TLdeserialize2 = TLRPC$TL_channelBannedRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            this.banned_rights_layer92 = TLdeserialize2;
                            this.banned_rights = TLRPC$Chat.mergeBannedRights(TLdeserialize2);
                        }
                        if ((this.flags & 131072) != 0) {
                            this.participants_count = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.left ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.broadcast ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.verified ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.megagroup ? i5 | 256 : i5 & (-257);
                        this.flags = i6;
                        int i7 = this.restricted ? i6 | 512 : i6 & (-513);
                        this.flags = i7;
                        int i8 = this.signatures ? i7 | 2048 : i7 & (-2049);
                        this.flags = i8;
                        int i9 = this.min ? i8 | 4096 : i8 & (-4097);
                        this.flags = i9;
                        abstractSerializedData2.writeInt32(i9);
                        abstractSerializedData2.writeInt32((int) this.id);
                        if ((this.flags & 8192) != 0) {
                            abstractSerializedData2.writeInt64(this.access_hash);
                        }
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeString("");
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights_layer92.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights_layer92.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 131072) != 0) {
                            abstractSerializedData2.writeInt32(this.participants_count);
                        }
                    }
                };
                break;
            case -753232354:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
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
                        this.id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 8192) != 0) {
                            this.access_hash = abstractSerializedData2.readInt64(z2);
                        }
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$TL_restrictionReason TLdeserialize = TLRPC$TL_restrictionReason.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.restriction_reason.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 131072) != 0) {
                            this.participants_count = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.left ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.broadcast ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.verified ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.megagroup ? i5 | 256 : i5 & (-257);
                        this.flags = i6;
                        int i7 = this.restricted ? i6 | 512 : i6 & (-513);
                        this.flags = i7;
                        int i8 = this.signatures ? i7 | 2048 : i7 & (-2049);
                        this.flags = i8;
                        int i9 = this.min ? i8 | 4096 : i8 & (-4097);
                        this.flags = i9;
                        int i10 = this.scam ? i9 | 524288 : i9 & (-524289);
                        this.flags = i10;
                        int i11 = this.has_link ? i10 | 1048576 : i10 & (-1048577);
                        this.flags = i11;
                        int i12 = this.has_geo ? i11 | 2097152 : i11 & (-2097153);
                        this.flags = i12;
                        int i13 = this.slowmode_enabled ? i12 | 4194304 : i12 & (-4194305);
                        this.flags = i13;
                        int i14 = this.call_active ? i13 | 8388608 : i13 & (-8388609);
                        this.flags = i14;
                        int i15 = this.call_not_empty ? i14 | 16777216 : i14 & (-16777217);
                        this.flags = i15;
                        int i16 = this.fake ? i15 | 33554432 : i15 & (-33554433);
                        this.flags = i16;
                        int i17 = this.gigagroup ? i16 | 67108864 : i16 & (-67108865);
                        this.flags = i17;
                        abstractSerializedData2.writeInt32(i17);
                        abstractSerializedData2.writeInt32((int) this.id);
                        if ((this.flags & 8192) != 0) {
                            abstractSerializedData2.writeInt64(this.access_hash);
                        }
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.restriction_reason.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i18 = 0; i18 < size; i18++) {
                                this.restriction_reason.get(i18).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 131072) != 0) {
                            abstractSerializedData2.writeInt32(this.participants_count);
                        }
                    }
                };
                break;
            case -652419756:
                tLRPC$Chat = new TLRPC$TL_chat() { // from class: org.telegram.tgnet.TLRPC$TL_chat_layer92
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.kicked = (readInt32 & 2) != 0;
                        this.left = (readInt32 & 4) != 0;
                        if ((readInt32 & 32) != 0) {
                            z3 = true;
                        }
                        this.deactivated = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.participants_count = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 64) != 0) {
                            this.migrated_to = TLRPC$InputChannel.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.kicked ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.left ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.deactivated ? i4 | 32 : i4 & (-33);
                        this.flags = i5;
                        abstractSerializedData2.writeInt32(i5);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeString(this.title);
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.participants_count);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 64) != 0) {
                            this.migrated_to.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case -83047359:
                tLRPC$Chat = new TLRPC$TL_chatForbidden() { // from class: org.telegram.tgnet.TLRPC$TL_chatForbidden_old
                    public static int constructor = -83047359;

                    @Override // org.telegram.tgnet.TLRPC$TL_chatForbidden, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chatForbidden, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeInt32(this.date);
                    }
                };
                break;
            case 120753115:
                tLRPC$Chat = new TLRPC$TL_chatForbidden() { // from class: org.telegram.tgnet.TLRPC$TL_chatForbidden_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chatForbidden, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.title = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chatForbidden, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeString(this.title);
                    }
                };
                break;
            case 213142300:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer72
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.left = (readInt32 & 4) != 0;
                        this.broadcast = (readInt32 & 32) != 0;
                        this.verified = (readInt32 & 128) != 0;
                        this.megagroup = (readInt32 & 256) != 0;
                        this.restricted = (readInt32 & 512) != 0;
                        this.signatures = (readInt32 & 2048) != 0;
                        if ((readInt32 & 4096) != 0) {
                            z3 = true;
                        }
                        this.min = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 8192) != 0) {
                            this.access_hash = abstractSerializedData2.readInt64(z2);
                        }
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            TLRPC$TL_channelAdminRights_layer92 TLdeserialize = TLRPC$TL_channelAdminRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            this.admin_rights_layer92 = TLdeserialize;
                            this.admin_rights = TLRPC$Chat.mergeAdminRights(TLdeserialize);
                        }
                        if ((this.flags & 32768) != 0) {
                            TLRPC$TL_channelBannedRights_layer92 TLdeserialize2 = TLRPC$TL_channelBannedRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            this.banned_rights_layer92 = TLdeserialize2;
                            this.banned_rights = TLRPC$Chat.mergeBannedRights(TLdeserialize2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.kicked ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.left ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.broadcast ? i4 | 32 : i4 & (-33);
                        this.flags = i5;
                        int i6 = this.verified ? i5 | 128 : i5 & (-129);
                        this.flags = i6;
                        int i7 = this.megagroup ? i6 | 256 : i6 & (-257);
                        this.flags = i7;
                        int i8 = this.restricted ? i7 | 512 : i7 & (-513);
                        this.flags = i8;
                        int i9 = this.signatures ? i8 | 2048 : i8 & (-2049);
                        this.flags = i9;
                        int i10 = this.min ? i9 | 4096 : i9 & (-4097);
                        this.flags = i10;
                        abstractSerializedData2.writeInt32(i10);
                        abstractSerializedData2.writeInt32((int) this.id);
                        if ((this.flags & 8192) != 0) {
                            abstractSerializedData2.writeInt64(this.access_hash);
                        }
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeString("");
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights_layer92.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights_layer92.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 399807445:
                tLRPC$Chat = new TLRPC$TL_channelForbidden();
                break;
            case 681420594:
                tLRPC$Chat = new TLRPC$TL_channelForbidden() { // from class: org.telegram.tgnet.TLRPC$TL_channelForbidden_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelForbidden, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        this.broadcast = (readInt32 & 32) != 0;
                        if ((readInt32 & 256) == 0) {
                            z3 = false;
                        }
                        this.megagroup = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 65536) != 0) {
                            this.until_date = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelForbidden, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.broadcast ? this.flags | 32 : this.flags & (-33);
                        this.flags = i2;
                        int i3 = this.megagroup ? i2 | 256 : i2 & (-257);
                        this.flags = i3;
                        abstractSerializedData2.writeInt32(i3);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 65536) != 0) {
                            abstractSerializedData2.writeInt32(this.until_date);
                        }
                    }
                };
                break;
            case 693512293:
                tLRPC$Chat = new TLRPC$TL_chatEmpty();
                break;
            case 763724588:
                tLRPC$Chat = new TLRPC$TL_channelForbidden() { // from class: org.telegram.tgnet.TLRPC$TL_channelForbidden_layer52
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channelForbidden, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channelForbidden, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                    }
                };
                break;
            case 1004149726:
                tLRPC$Chat = new TLRPC$TL_chat() { // from class: org.telegram.tgnet.TLRPC$TL_chat_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.kicked = (readInt32 & 2) != 0;
                        this.left = (readInt32 & 4) != 0;
                        this.deactivated = (readInt32 & 32) != 0;
                        this.call_active = (8388608 & readInt32) != 0;
                        if ((readInt32 & 16777216) != 0) {
                            z3 = true;
                        }
                        this.call_not_empty = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.participants_count = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 64) != 0) {
                            this.migrated_to = TLRPC$InputChannel.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.kicked ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.left ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.deactivated ? i4 | 32 : i4 & (-33);
                        this.flags = i5;
                        int i6 = this.call_active ? i5 | 8388608 : i5 & (-8388609);
                        this.flags = i6;
                        int i7 = this.call_not_empty ? i6 | 16777216 : i6 & (-16777217);
                        this.flags = i7;
                        abstractSerializedData2.writeInt32(i7);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeString(this.title);
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.participants_count);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 64) != 0) {
                            this.migrated_to.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 1103884886:
                tLRPC$Chat = new TLRPC$TL_chat();
                break;
            case 1158377749:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer77
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.left = (readInt32 & 4) != 0;
                        this.broadcast = (readInt32 & 32) != 0;
                        this.verified = (readInt32 & 128) != 0;
                        this.megagroup = (readInt32 & 256) != 0;
                        this.restricted = (readInt32 & 512) != 0;
                        this.signatures = (readInt32 & 2048) != 0;
                        if ((readInt32 & 4096) != 0) {
                            z3 = true;
                        }
                        this.min = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 8192) != 0) {
                            this.access_hash = abstractSerializedData2.readInt64(z2);
                        }
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            TLRPC$TL_channelAdminRights_layer92 TLdeserialize = TLRPC$TL_channelAdminRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            this.admin_rights_layer92 = TLdeserialize;
                            this.admin_rights = TLRPC$Chat.mergeAdminRights(TLdeserialize);
                        }
                        if ((this.flags & 32768) != 0) {
                            TLRPC$TL_channelBannedRights_layer92 TLdeserialize2 = TLRPC$TL_channelBannedRights_layer92.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            this.banned_rights_layer92 = TLdeserialize2;
                            this.banned_rights = TLRPC$Chat.mergeBannedRights(TLdeserialize2);
                        }
                        if ((this.flags & 131072) != 0) {
                            this.participants_count = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.left ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.broadcast ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.verified ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.megagroup ? i5 | 256 : i5 & (-257);
                        this.flags = i6;
                        int i7 = this.restricted ? i6 | 512 : i6 & (-513);
                        this.flags = i7;
                        int i8 = this.signatures ? i7 | 2048 : i7 & (-2049);
                        this.flags = i8;
                        int i9 = this.min ? i8 | 4096 : i8 & (-4097);
                        this.flags = i9;
                        abstractSerializedData2.writeInt32(i9);
                        abstractSerializedData2.writeInt32((int) this.id);
                        if ((this.flags & 8192) != 0) {
                            abstractSerializedData2.writeInt64(this.access_hash);
                        }
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeString("");
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights_layer92.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights_layer92.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 131072) != 0) {
                            abstractSerializedData2.writeInt32(this.participants_count);
                        }
                    }
                };
                break;
            case 1260090630:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer48
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.kicked = (readInt32 & 2) != 0;
                        this.left = (readInt32 & 4) != 0;
                        this.moderator = (readInt32 & 16) != 0;
                        this.broadcast = (readInt32 & 32) != 0;
                        this.verified = (readInt32 & 128) != 0;
                        this.megagroup = (readInt32 & 256) != 0;
                        this.restricted = (readInt32 & 512) != 0;
                        if ((readInt32 & 2048) != 0) {
                            z3 = true;
                        }
                        this.signatures = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.kicked ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.left ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.moderator ? i4 | 16 : i4 & (-17);
                        this.flags = i5;
                        int i6 = this.broadcast ? i5 | 32 : i5 & (-33);
                        this.flags = i6;
                        int i7 = this.verified ? i6 | 128 : i6 & (-129);
                        this.flags = i7;
                        int i8 = this.megagroup ? i7 | 256 : i7 & (-257);
                        this.flags = i8;
                        int i9 = this.restricted ? i8 | 512 : i8 & (-513);
                        this.flags = i9;
                        int i10 = this.signatures ? i9 | 2048 : i9 & (-2049);
                        this.flags = i10;
                        abstractSerializedData2.writeInt32(i10);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeString("");
                        }
                    }
                };
                break;
            case 1307772980:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_layer104
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
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
                        if ((readInt32 & 4194304) != 0) {
                            z3 = true;
                        }
                        this.slowmode_enabled = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 8192) != 0) {
                            this.access_hash = abstractSerializedData2.readInt64(z2);
                        }
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 131072) != 0) {
                            this.participants_count = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.left ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.broadcast ? i3 | 32 : i3 & (-33);
                        this.flags = i4;
                        int i5 = this.verified ? i4 | 128 : i4 & (-129);
                        this.flags = i5;
                        int i6 = this.megagroup ? i5 | 256 : i5 & (-257);
                        this.flags = i6;
                        int i7 = this.restricted ? i6 | 512 : i6 & (-513);
                        this.flags = i7;
                        int i8 = this.signatures ? i7 | 2048 : i7 & (-2049);
                        this.flags = i8;
                        int i9 = this.min ? i8 | 4096 : i8 & (-4097);
                        this.flags = i9;
                        int i10 = this.scam ? i9 | 524288 : i9 & (-524289);
                        this.flags = i10;
                        int i11 = this.has_link ? i10 | 1048576 : i10 & (-1048577);
                        this.flags = i11;
                        int i12 = this.has_geo ? i11 | 2097152 : i11 & (-2097153);
                        this.flags = i12;
                        int i13 = this.slowmode_enabled ? i12 | 4194304 : i12 & (-4194305);
                        this.flags = i13;
                        abstractSerializedData2.writeInt32(i13);
                        abstractSerializedData2.writeInt32((int) this.id);
                        if ((this.flags & 8192) != 0) {
                            abstractSerializedData2.writeInt64(this.access_hash);
                        }
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                        if ((this.flags & 512) != 0) {
                            abstractSerializedData2.writeString("");
                        }
                        if ((this.flags & 16384) != 0) {
                            this.admin_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 32768) != 0) {
                            this.banned_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 262144) != 0) {
                            this.default_banned_rights.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 131072) != 0) {
                            abstractSerializedData2.writeInt32(this.participants_count);
                        }
                    }
                };
                break;
            case 1704108455:
                tLRPC$Chat = new TLRPC$TL_chatForbidden();
                break;
            case 1737397639:
                tLRPC$Chat = new TLRPC$TL_channel() { // from class: org.telegram.tgnet.TLRPC$TL_channel_old
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.kicked = (readInt32 & 2) != 0;
                        this.left = (readInt32 & 4) != 0;
                        this.moderator = (readInt32 & 16) != 0;
                        this.broadcast = (readInt32 & 32) != 0;
                        this.verified = (readInt32 & 128) != 0;
                        this.megagroup = (readInt32 & 256) != 0;
                        if ((readInt32 & 512) != 0) {
                            z3 = true;
                        }
                        this.explicit_content = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        if ((this.flags & 64) != 0) {
                            this.username = abstractSerializedData2.readString(z2);
                        }
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_channel, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.kicked ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.left ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.moderator ? i4 | 16 : i4 & (-17);
                        this.flags = i5;
                        int i6 = this.broadcast ? i5 | 32 : i5 & (-33);
                        this.flags = i6;
                        int i7 = this.verified ? i6 | 128 : i6 & (-129);
                        this.flags = i7;
                        int i8 = this.megagroup ? i7 | 256 : i7 & (-257);
                        this.flags = i8;
                        int i9 = this.explicit_content ? i8 | 512 : i8 & (-513);
                        this.flags = i9;
                        abstractSerializedData2.writeInt32(i9);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        if ((this.flags & 64) != 0) {
                            abstractSerializedData2.writeString(this.username);
                        }
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                    }
                };
                break;
            case 1855757255:
                tLRPC$Chat = new TLRPC$TL_chat() { // from class: org.telegram.tgnet.TLRPC$TL_chat_old
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.participants_count = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.left = abstractSerializedData2.readBool(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeString(this.title);
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.participants_count);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeBool(this.left);
                        abstractSerializedData2.writeInt32(this.version);
                    }
                };
                break;
            case 1930607688:
                tLRPC$Chat = new TLRPC$TL_chat() { // from class: org.telegram.tgnet.TLRPC$TL_chat_old2
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.creator = (readInt32 & 1) != 0;
                        this.kicked = (readInt32 & 2) != 0;
                        this.left = (readInt32 & 4) != 0;
                        if ((readInt32 & 32) != 0) {
                            z3 = true;
                        }
                        this.deactivated = z3;
                        this.id = abstractSerializedData2.readInt32(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.participants_count = abstractSerializedData2.readInt32(z2);
                        this.date = abstractSerializedData2.readInt32(z2);
                        this.version = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chat, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.creator ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.kicked ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.left ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.deactivated ? i4 | 32 : i4 & (-33);
                        this.flags = i5;
                        abstractSerializedData2.writeInt32(i5);
                        abstractSerializedData2.writeInt32((int) this.id);
                        abstractSerializedData2.writeString(this.title);
                        this.photo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.participants_count);
                        abstractSerializedData2.writeInt32(this.date);
                        abstractSerializedData2.writeInt32(this.version);
                    }
                };
                break;
            default:
                tLRPC$Chat = null;
                break;
        }
        if (tLRPC$Chat != null || !z) {
            if (tLRPC$Chat != null) {
                tLRPC$Chat.readParams(abstractSerializedData, z);
            }
            return tLRPC$Chat;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Chat", Integer.valueOf(i)));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static TLRPC$TL_chatBannedRights mergeBannedRights(TLRPC$TL_channelBannedRights_layer92 tLRPC$TL_channelBannedRights_layer92) {
        if (tLRPC$TL_channelBannedRights_layer92 == null) {
            return null;
        }
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = new TLRPC$TL_chatBannedRights();
        tLRPC$TL_chatBannedRights.view_messages = tLRPC$TL_channelBannedRights_layer92.view_messages;
        tLRPC$TL_chatBannedRights.send_messages = tLRPC$TL_channelBannedRights_layer92.send_messages;
        boolean z = tLRPC$TL_channelBannedRights_layer92.send_media;
        tLRPC$TL_chatBannedRights.send_media = z;
        tLRPC$TL_chatBannedRights.send_stickers = tLRPC$TL_channelBannedRights_layer92.send_stickers;
        tLRPC$TL_chatBannedRights.send_gifs = tLRPC$TL_channelBannedRights_layer92.send_gifs;
        tLRPC$TL_chatBannedRights.send_games = tLRPC$TL_channelBannedRights_layer92.send_games;
        tLRPC$TL_chatBannedRights.send_inline = tLRPC$TL_channelBannedRights_layer92.send_inline;
        tLRPC$TL_chatBannedRights.embed_links = tLRPC$TL_channelBannedRights_layer92.embed_links;
        tLRPC$TL_chatBannedRights.send_polls = z;
        tLRPC$TL_chatBannedRights.change_info = true;
        tLRPC$TL_chatBannedRights.invite_users = true;
        tLRPC$TL_chatBannedRights.pin_messages = true;
        tLRPC$TL_chatBannedRights.until_date = tLRPC$TL_channelBannedRights_layer92.until_date;
        return tLRPC$TL_chatBannedRights;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static TLRPC$TL_chatAdminRights mergeAdminRights(TLRPC$TL_channelAdminRights_layer92 tLRPC$TL_channelAdminRights_layer92) {
        if (tLRPC$TL_channelAdminRights_layer92 == null) {
            return null;
        }
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = new TLRPC$TL_chatAdminRights();
        tLRPC$TL_chatAdminRights.change_info = tLRPC$TL_channelAdminRights_layer92.change_info;
        tLRPC$TL_chatAdminRights.post_messages = tLRPC$TL_channelAdminRights_layer92.post_messages;
        tLRPC$TL_chatAdminRights.edit_messages = tLRPC$TL_channelAdminRights_layer92.edit_messages;
        tLRPC$TL_chatAdminRights.delete_messages = tLRPC$TL_channelAdminRights_layer92.delete_messages;
        tLRPC$TL_chatAdminRights.ban_users = tLRPC$TL_channelAdminRights_layer92.ban_users;
        tLRPC$TL_chatAdminRights.invite_users = tLRPC$TL_channelAdminRights_layer92.invite_users;
        tLRPC$TL_chatAdminRights.pin_messages = tLRPC$TL_channelAdminRights_layer92.pin_messages;
        tLRPC$TL_chatAdminRights.add_admins = tLRPC$TL_channelAdminRights_layer92.add_admins;
        return tLRPC$TL_chatAdminRights;
    }
}
