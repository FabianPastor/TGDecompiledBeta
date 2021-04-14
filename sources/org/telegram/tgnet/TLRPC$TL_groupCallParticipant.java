package org.telegram.tgnet;

public class TLRPC$TL_groupCallParticipant extends TLObject {
    public static int constructor = -NUM;
    public String about;
    public int active_date;
    public float amplitude;
    public boolean can_self_unmute;
    public int date;
    public int flags;
    public boolean hasVoice;
    public boolean hasVoiceDelayed;
    public boolean just_joined;
    public long lastActiveDate;
    public long lastRaiseHandDate;
    public long lastSpeakTime;
    public int lastTypingDate;
    public long lastVisibleDate;
    public long lastVoiceUpdateTime;
    public boolean left;
    public boolean min;
    public boolean muted;
    public boolean muted_by_you;
    public TLRPC$TL_dataJSON params;
    public TLRPC$Peer peer;
    public long raise_hand_rating;
    public boolean self;
    public int source;
    public boolean versioned;
    public int volume;
    public boolean volume_by_admin;

    public static TLRPC$TL_groupCallParticipant TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = new TLRPC$TL_groupCallParticipant();
            tLRPC$TL_groupCallParticipant.readParams(abstractSerializedData, z);
            return tLRPC$TL_groupCallParticipant;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_groupCallParticipant", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.muted = (readInt32 & 1) != 0;
        this.left = (readInt32 & 2) != 0;
        this.can_self_unmute = (readInt32 & 4) != 0;
        this.just_joined = (readInt32 & 16) != 0;
        this.versioned = (readInt32 & 32) != 0;
        this.min = (readInt32 & 256) != 0;
        this.muted_by_you = (readInt32 & 512) != 0;
        this.volume_by_admin = (readInt32 & 1024) != 0;
        if ((readInt32 & 4096) != 0) {
            z2 = true;
        }
        this.self = z2;
        this.peer = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.date = abstractSerializedData.readInt32(z);
        if ((this.flags & 8) != 0) {
            this.active_date = abstractSerializedData.readInt32(z);
        }
        this.source = abstractSerializedData.readInt32(z);
        if ((this.flags & 128) != 0) {
            this.volume = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 2048) != 0) {
            this.about = abstractSerializedData.readString(z);
        }
        if ((this.flags & 8192) != 0) {
            this.raise_hand_rating = abstractSerializedData.readInt64(z);
        }
        if ((this.flags & 64) != 0) {
            this.params = TLRPC$TL_dataJSON.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.muted ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.left ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.can_self_unmute ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.just_joined ? i3 | 16 : i3 & -17;
        this.flags = i4;
        int i5 = this.versioned ? i4 | 32 : i4 & -33;
        this.flags = i5;
        int i6 = this.min ? i5 | 256 : i5 & -257;
        this.flags = i6;
        int i7 = this.muted_by_you ? i6 | 512 : i6 & -513;
        this.flags = i7;
        int i8 = this.volume_by_admin ? i7 | 1024 : i7 & -1025;
        this.flags = i8;
        int i9 = this.self ? i8 | 4096 : i8 & -4097;
        this.flags = i9;
        abstractSerializedData.writeInt32(i9);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.date);
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.active_date);
        }
        abstractSerializedData.writeInt32(this.source);
        if ((this.flags & 128) != 0) {
            abstractSerializedData.writeInt32(this.volume);
        }
        if ((this.flags & 2048) != 0) {
            abstractSerializedData.writeString(this.about);
        }
        if ((this.flags & 8192) != 0) {
            abstractSerializedData.writeInt64(this.raise_hand_rating);
        }
        if ((this.flags & 64) != 0) {
            this.params.serializeToStream(abstractSerializedData);
        }
    }
}
