package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$PollResults extends TLObject {
    public int flags;
    public boolean min;
    public String solution;
    public int total_voters;
    public ArrayList<TLRPC$TL_pollAnswerVoters> results = new ArrayList<>();
    public ArrayList<Long> recent_voters = new ArrayList<>();
    public ArrayList<TLRPC$MessageEntity> solution_entities = new ArrayList<>();

    public static TLRPC$PollResults TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PollResults tLRPC$PollResults;
        switch (i) {
            case -1159937629:
                tLRPC$PollResults = new TLRPC$TL_pollResults() { // from class: org.telegram.tgnet.TLRPC$TL_pollResults_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_pollResults, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.min = (readInt32 & 1) != 0;
                        if ((readInt32 & 2) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$TL_pollAnswerVoters TLdeserialize = TLRPC$TL_pollAnswerVoters.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.results.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            this.total_voters = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 8) != 0) {
                            int readInt324 = abstractSerializedData2.readInt32(z2);
                            if (readInt324 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                                }
                                return;
                            }
                            int readInt325 = abstractSerializedData2.readInt32(z2);
                            for (int i3 = 0; i3 < readInt325; i3++) {
                                this.recent_voters.add(Long.valueOf(abstractSerializedData2.readInt32(z2)));
                            }
                        }
                        if ((this.flags & 16) != 0) {
                            this.solution = abstractSerializedData2.readString(z2);
                        }
                        if ((this.flags & 16) != 0) {
                            int readInt326 = abstractSerializedData2.readInt32(z2);
                            if (readInt326 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                                }
                                return;
                            }
                            int readInt327 = abstractSerializedData2.readInt32(z2);
                            for (int i4 = 0; i4 < readInt327; i4++) {
                                TLRPC$MessageEntity TLdeserialize2 = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize2 == null) {
                                    return;
                                }
                                this.solution_entities.add(TLdeserialize2);
                            }
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_pollResults, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.min ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.results.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i3 = 0; i3 < size; i3++) {
                                this.results.get(i3).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            abstractSerializedData2.writeInt32(this.total_voters);
                        }
                        if ((this.flags & 8) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size2 = this.recent_voters.size();
                            abstractSerializedData2.writeInt32(size2);
                            for (int i4 = 0; i4 < size2; i4++) {
                                abstractSerializedData2.writeInt32((int) this.recent_voters.get(i4).longValue());
                            }
                        }
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeString(this.solution);
                        }
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size3 = this.solution_entities.size();
                            abstractSerializedData2.writeInt32(size3);
                            for (int i5 = 0; i5 < size3; i5++) {
                                this.solution_entities.get(i5).serializeToStream(abstractSerializedData2);
                            }
                        }
                    }
                };
                break;
            case -932174686:
                tLRPC$PollResults = new TLRPC$PollResults() { // from class: org.telegram.tgnet.TLRPC$TL_pollResults_layer111
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.min = (readInt32 & 1) != 0;
                        if ((readInt32 & 2) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$TL_pollAnswerVoters TLdeserialize = TLRPC$TL_pollAnswerVoters.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.results.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            this.total_voters = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 8) != 0) {
                            int readInt324 = abstractSerializedData2.readInt32(z2);
                            if (readInt324 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                                }
                                return;
                            }
                            int readInt325 = abstractSerializedData2.readInt32(z2);
                            for (int i3 = 0; i3 < readInt325; i3++) {
                                this.recent_voters.add(Long.valueOf(abstractSerializedData2.readInt32(z2)));
                            }
                        }
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.min ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.results.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i3 = 0; i3 < size; i3++) {
                                this.results.get(i3).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            abstractSerializedData2.writeInt32(this.total_voters);
                        }
                        if ((this.flags & 8) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size2 = this.recent_voters.size();
                            abstractSerializedData2.writeInt32(size2);
                            for (int i4 = 0; i4 < size2; i4++) {
                                abstractSerializedData2.writeInt32((int) this.recent_voters.get(i4).longValue());
                            }
                        }
                    }
                };
                break;
            case -591909213:
                tLRPC$PollResults = new TLRPC$TL_pollResults();
                break;
            case 1465219162:
                tLRPC$PollResults = new TLRPC$TL_pollResults() { // from class: org.telegram.tgnet.TLRPC$TL_pollResults_layer108
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_pollResults, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.min = (readInt32 & 1) != 0;
                        if ((readInt32 & 2) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$TL_pollAnswerVoters TLdeserialize = TLRPC$TL_pollAnswerVoters.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.results.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            this.total_voters = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_pollResults, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.min ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.results.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i3 = 0; i3 < size; i3++) {
                                this.results.get(i3).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            abstractSerializedData2.writeInt32(this.total_voters);
                        }
                    }
                };
                break;
            default:
                tLRPC$PollResults = null;
                break;
        }
        if (tLRPC$PollResults != null || !z) {
            if (tLRPC$PollResults != null) {
                tLRPC$PollResults.readParams(abstractSerializedData, z);
            }
            return tLRPC$PollResults;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PollResults", Integer.valueOf(i)));
    }
}
