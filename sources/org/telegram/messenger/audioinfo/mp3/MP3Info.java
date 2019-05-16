package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.audioinfo.mp3.MP3Frame.Header;

public class MP3Info extends AudioInfo {
    static final Logger LOGGER = Logger.getLogger(MP3Info.class.getName());

    interface StopReadCondition {
        boolean stopRead(MP3Input mP3Input) throws IOException;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0095 in {5, 12, 16, 19, 20, 22} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    long calculateDuration(org.telegram.messenger.audioinfo.mp3.MP3Input r19, long r20, org.telegram.messenger.audioinfo.mp3.MP3Info.StopReadCondition r22) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.MP3Exception {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = r22;
        r3 = r0.readFirstFrame(r1, r2);
        if (r3 == 0) goto L_0x008d;
        r4 = r3.getNumberOfFrames();
        if (r4 <= 0) goto L_0x0022;
        r1 = r3.getHeader();
        r2 = r3.getSize();
        r4 = r4 * r2;
        r2 = (long) r4;
        r1 = r1.getTotalDuration(r2);
        return r1;
        r4 = r19.getPosition();
        r6 = r3.getSize();
        r6 = (long) r6;
        r4 = r4 - r6;
        r6 = r3.getSize();
        r6 = (long) r6;
        r8 = r3.getHeader();
        r8 = r8.getBitrate();
        r9 = (long) r8;
        r11 = 0;
        r12 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r13 = r3.getHeader();
        r13 = r13.getDuration();
        r12 = r12 / r13;
        r13 = 1;
        r14 = r9;
        r9 = r6;
        r6 = r3;
        r3 = 1;
        if (r3 != r12) goto L_0x0060;
        if (r11 != 0) goto L_0x0060;
        r16 = 0;
        r7 = (r20 > r16 ? 1 : (r20 == r16 ? 0 : -1));
        if (r7 <= 0) goto L_0x0060;
        r1 = r6.getHeader();
        r2 = r20 - r4;
        r1 = r1.getTotalDuration(r2);
        return r1;
        r6 = r0.readNextFrame(r1, r2, r6);
        if (r6 != 0) goto L_0x0073;
        r1 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r9 = r9 * r1;
        r1 = (long) r3;
        r9 = r9 * r1;
        r1 = 8;
        r9 = r9 * r1;
        r9 = r9 / r14;
        return r9;
        r7 = r6.getHeader();
        r7 = r7.getBitrate();
        if (r7 == r8) goto L_0x007e;
        r11 = 1;
        r0 = (long) r7;
        r14 = r14 + r0;
        r0 = r6.getSize();
        r0 = (long) r0;
        r9 = r9 + r0;
        r3 = r3 + 1;
        r0 = r18;
        r1 = r19;
        goto L_0x004b;
        r0 = new org.telegram.messenger.audioinfo.mp3.MP3Exception;
        r1 = "No audio frame";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.MP3Info.calculateDuration(org.telegram.messenger.audioinfo.mp3.MP3Input, long, org.telegram.messenger.audioinfo.mp3.MP3Info$StopReadCondition):long");
    }

    public MP3Info(InputStream inputStream, long j) throws IOException, ID3v2Exception, MP3Exception {
        this(inputStream, j, Level.FINEST);
    }

    public MP3Info(InputStream inputStream, final long j, Level level) throws IOException, ID3v2Exception, MP3Exception {
        this.brand = "MP3";
        this.version = "0";
        MP3Input mP3Input = new MP3Input(inputStream);
        if (ID3v2Info.isID3v2StartPosition(mP3Input)) {
            ID3v2Info iD3v2Info = new ID3v2Info(mP3Input, level);
            this.album = iD3v2Info.getAlbum();
            this.albumArtist = iD3v2Info.getAlbumArtist();
            this.artist = iD3v2Info.getArtist();
            this.comment = iD3v2Info.getComment();
            this.cover = iD3v2Info.getCover();
            this.smallCover = iD3v2Info.getSmallCover();
            this.compilation = iD3v2Info.isCompilation();
            this.composer = iD3v2Info.getComposer();
            this.copyright = iD3v2Info.getCopyright();
            this.disc = iD3v2Info.getDisc();
            this.discs = iD3v2Info.getDiscs();
            this.duration = iD3v2Info.getDuration();
            this.genre = iD3v2Info.getGenre();
            this.grouping = iD3v2Info.getGrouping();
            this.lyrics = iD3v2Info.getLyrics();
            this.title = iD3v2Info.getTitle();
            this.track = iD3v2Info.getTrack();
            this.tracks = iD3v2Info.getTracks();
            this.year = iD3v2Info.getYear();
        }
        long j2 = this.duration;
        if (j2 <= 0 || j2 >= 3600000) {
            try {
                this.duration = calculateDuration(mP3Input, j, new StopReadCondition() {
                    final long stopPosition = (j - 128);

                    public boolean stopRead(MP3Input mP3Input) throws IOException {
                        return mP3Input.getPosition() == this.stopPosition && ID3v1Info.isID3v1StartPosition(mP3Input);
                    }
                });
            } catch (MP3Exception e) {
                if (LOGGER.isLoggable(level)) {
                    LOGGER.log(level, "Could not determine MP3 duration", e);
                }
            }
        }
        if (this.title == null || this.album == null || this.artist == null) {
            j -= 128;
            if (mP3Input.getPosition() <= j) {
                mP3Input.skipFully(j - mP3Input.getPosition());
                if (ID3v1Info.isID3v1StartPosition(inputStream)) {
                    ID3v1Info iD3v1Info = new ID3v1Info(inputStream);
                    if (this.album == null) {
                        this.album = iD3v1Info.getAlbum();
                    }
                    if (this.artist == null) {
                        this.artist = iD3v1Info.getArtist();
                    }
                    if (this.comment == null) {
                        this.comment = iD3v1Info.getComment();
                    }
                    if (this.genre == null) {
                        this.genre = iD3v1Info.getGenre();
                    }
                    if (this.title == null) {
                        this.title = iD3v1Info.getTitle();
                    }
                    if (this.track == (short) 0) {
                        this.track = iD3v1Info.getTrack();
                    }
                    if (this.year == (short) 0) {
                        this.year = iD3v1Info.getYear();
                    }
                }
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public MP3Frame readFirstFrame(MP3Input mP3Input, StopReadCondition stopReadCondition) throws IOException {
        int read = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
        int i = 0;
        while (read != -1) {
            if (i == 255 && (read & 224) == 224) {
                mP3Input.mark(2);
                int read2 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                if (read2 == -1) {
                    break;
                }
                int read3 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                if (read3 == -1) {
                    break;
                }
                Header header;
                try {
                    header = new Header(read, read2, read3);
                } catch (MP3Exception unused) {
                    header = null;
                }
                if (header != null) {
                    mP3Input.reset();
                    mP3Input.mark(header.getFrameSize() + 2);
                    byte[] bArr = new byte[header.getFrameSize()];
                    bArr[0] = (byte) -1;
                    bArr[1] = (byte) read;
                    try {
                        mP3Input.readFully(bArr, 2, bArr.length - 2);
                        MP3Frame mP3Frame = new MP3Frame(header, bArr);
                        if (!mP3Frame.isChecksumError()) {
                            read3 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                            int read4 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                            if (!(read3 == -1 || read4 == -1)) {
                                if (read3 == 255 && (read4 & 254) == (read & 254)) {
                                    int read5 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                                    read3 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                                    if (!(read5 == -1 || read3 == -1)) {
                                        try {
                                            if (new Header(read4, read5, read3).isCompatible(header)) {
                                                mP3Input.reset();
                                                mP3Input.skipFully((long) (bArr.length - 2));
                                            }
                                        } catch (MP3Exception unused2) {
                                        }
                                    }
                                }
                            }
                            return mP3Frame;
                        }
                    } catch (EOFException unused3) {
                    }
                }
                mP3Input.reset();
            }
            i = read;
            read = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
        }
        return null;
    }

    /* Access modifiers changed, original: 0000 */
    public MP3Frame readNextFrame(MP3Input mP3Input, StopReadCondition stopReadCondition, MP3Frame mP3Frame) throws IOException {
        Header header = mP3Frame.getHeader();
        mP3Input.mark(4);
        int read = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
        int read2 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
        if (!(read == -1 || read2 == -1)) {
            if (read == 255 && (read2 & 224) == 224) {
                int read3 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                int read4 = stopReadCondition.stopRead(mP3Input) ? -1 : mP3Input.read();
                if (!(read3 == -1 || read4 == -1)) {
                    Header header2;
                    try {
                        header2 = new Header(read2, read3, read4);
                    } catch (MP3Exception unused) {
                        header2 = null;
                    }
                    if (header2 != null && header2.isCompatible(header)) {
                        byte[] bArr = new byte[header2.getFrameSize()];
                        bArr[0] = (byte) read;
                        bArr[1] = (byte) read2;
                        bArr[2] = (byte) read3;
                        bArr[3] = (byte) read4;
                        try {
                            mP3Input.readFully(bArr, 4, bArr.length - 4);
                            return new MP3Frame(header2, bArr);
                        } catch (EOFException unused2) {
                        }
                    }
                }
                return null;
            }
            mP3Input.reset();
        }
        return null;
    }
}
