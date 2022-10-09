package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$ChatParticipants extends TLObject {
    public long admin_id;
    public long chat_id;
    public int flags;
    public ArrayList<TLRPC$ChatParticipant> participants = new ArrayList<>();
    public TLRPC$ChatParticipant self_participant;
    public int version;

    public static TLRPC$ChatParticipants TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatParticipants tLRPC$TL_chatParticipantsForbidden;
        switch (i) {
            case -2023500831:
                tLRPC$TL_chatParticipantsForbidden = new TLRPC$TL_chatParticipantsForbidden();
                break;
            case -57668565:
                tLRPC$TL_chatParticipantsForbidden = new TLRPC$TL_chatParticipantsForbidden() { // from class: org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden_layer131
                    public static int constructor = -57668565;

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        this.chat_id = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 1) != 0) {
                            this.self_participant = TLRPC$ChatParticipant.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        abstractSerializedData2.writeInt32((int) this.chat_id);
                        if ((this.flags & 1) != 0) {
                            this.self_participant.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 265468810:
                tLRPC$TL_chatParticipantsForbidden = new TLRPC$TL_chatParticipantsForbidden() { // from class: org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden_old
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.chat_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.chat_id);
                    }
                };
                break;
            case 1018991608:
                tLRPC$TL_chatParticipantsForbidden = new TLRPC$TL_chatParticipants();
                break;
            case 1061556205:
                tLRPC$TL_chatParticipantsForbidden = new TLRPC$TL_chatParticipants() { // from class: org.telegram.tgnet.TLRPC$TL_chatParticipants_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipants, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.chat_id = abstractSerializedData2.readInt32(z2);
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            TLRPC$ChatParticipant TLdeserialize = TLRPC$ChatParticipant.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize == null) {
                                return;
                            }
                            this.participants.add(TLdeserialize);
                        }
                        this.version = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipants, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.chat_id);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.participants.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            this.participants.get(i2).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(this.version);
                    }
                };
                break;
            case 2017571861:
                tLRPC$TL_chatParticipantsForbidden = new TLRPC$TL_chatParticipants() { // from class: org.telegram.tgnet.TLRPC$TL_chatParticipants_old
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipants, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.chat_id = abstractSerializedData2.readInt32(z2);
                        this.admin_id = abstractSerializedData2.readInt32(z2);
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            TLRPC$ChatParticipant TLdeserialize = TLRPC$ChatParticipant.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize == null) {
                                return;
                            }
                            this.participants.add(TLdeserialize);
                        }
                        this.version = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_chatParticipants, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.chat_id);
                        abstractSerializedData2.writeInt32((int) this.admin_id);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.participants.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            this.participants.get(i2).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(this.version);
                    }
                };
                break;
            default:
                tLRPC$TL_chatParticipantsForbidden = null;
                break;
        }
        if (tLRPC$TL_chatParticipantsForbidden != null || !z) {
            if (tLRPC$TL_chatParticipantsForbidden != null) {
                tLRPC$TL_chatParticipantsForbidden.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_chatParticipantsForbidden;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatParticipants", Integer.valueOf(i)));
    }
}
