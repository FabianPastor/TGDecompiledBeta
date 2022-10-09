package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$ChatInvite extends TLObject {
    public String about;
    public boolean broadcast;
    public boolean channel;
    public TLRPC$Chat chat;
    public int expires;
    public int flags;
    public boolean isPublic;
    public boolean megagroup;
    public ArrayList<TLRPC$User> participants = new ArrayList<>();
    public int participants_count;
    public TLRPC$Photo photo;
    public boolean request_needed;
    public String title;

    public static TLRPC$ChatInvite TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatInvite tLRPC$ChatInvite;
        if (i == NUM) {
            tLRPC$ChatInvite = new TLRPC$ChatInvite() { // from class: org.telegram.tgnet.TLRPC$TL_chatInvite
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    this.channel = (readInt32 & 1) != 0;
                    this.broadcast = (readInt32 & 2) != 0;
                    this.isPublic = (readInt32 & 4) != 0;
                    this.megagroup = (readInt32 & 8) != 0;
                    this.request_needed = (readInt32 & 64) != 0;
                    this.title = abstractSerializedData2.readString(z2);
                    if ((this.flags & 32) != 0) {
                        this.about = abstractSerializedData2.readString(z2);
                    }
                    this.photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    this.participants_count = abstractSerializedData2.readInt32(z2);
                    if ((this.flags & 16) != 0) {
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        if (readInt322 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                            }
                            return;
                        }
                        int readInt323 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt323; i2++) {
                            TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize == null) {
                                return;
                            }
                            this.participants.add(TLdeserialize);
                        }
                    }
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.channel ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    int i3 = this.broadcast ? i2 | 2 : i2 & (-3);
                    this.flags = i3;
                    int i4 = this.isPublic ? i3 | 4 : i3 & (-5);
                    this.flags = i4;
                    int i5 = this.megagroup ? i4 | 8 : i4 & (-9);
                    this.flags = i5;
                    int i6 = this.about != null ? i5 | 32 : i5 & (-33);
                    this.flags = i6;
                    int i7 = this.request_needed ? i6 | 64 : i6 & (-65);
                    this.flags = i7;
                    abstractSerializedData2.writeInt32(i7);
                    abstractSerializedData2.writeString(this.title);
                    String str = this.about;
                    if (str != null) {
                        abstractSerializedData2.writeString(str);
                    }
                    this.photo.serializeToStream(abstractSerializedData2);
                    abstractSerializedData2.writeInt32(this.participants_count);
                    if ((this.flags & 16) != 0) {
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.participants.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i8 = 0; i8 < size; i8++) {
                            this.participants.get(i8).serializeToStream(abstractSerializedData2);
                        }
                    }
                }
            };
        } else if (i != NUM) {
            tLRPC$ChatInvite = i != NUM ? null : new TLRPC$ChatInvite() { // from class: org.telegram.tgnet.TLRPC$TL_chatInvitePeek
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.chat = TLRPC$Chat.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    this.expires = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    this.chat.serializeToStream(abstractSerializedData2);
                    abstractSerializedData2.writeInt32(this.expires);
                }
            };
        } else {
            tLRPC$ChatInvite = new TLRPC$ChatInvite() { // from class: org.telegram.tgnet.TLRPC$TL_chatInviteAlready
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.chat = TLRPC$Chat.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    this.chat.serializeToStream(abstractSerializedData2);
                }
            };
        }
        if (tLRPC$ChatInvite != null || !z) {
            if (tLRPC$ChatInvite != null) {
                tLRPC$ChatInvite.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChatInvite;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatInvite", Integer.valueOf(i)));
    }
}
