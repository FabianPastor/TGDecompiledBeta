package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;

public class MP3Info extends AudioInfo {
    static final Logger LOGGER = Logger.getLogger(MP3Info.class.getName());

    interface StopReadCondition {
        boolean stopRead(MP3Input mP3Input) throws IOException;
    }

    public MP3Info(InputStream inputStream, long j) throws IOException, ID3v2Exception, MP3Exception {
        this(inputStream, j, Level.FINEST);
    }

    public MP3Info(InputStream inputStream, final long j, Level level) throws IOException, ID3v2Exception, MP3Exception {
        this.brand = "MP3";
        this.version = "0";
        InputStream mP3Input = new MP3Input(inputStream);
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
        if (this.duration <= 0 || this.duration >= 3600000) {
            try {
                this.duration = calculateDuration(mP3Input, j, new StopReadCondition() {
                    final long stopPosition = (j - 128);

                    public boolean stopRead(MP3Input mP3Input) throws IOException {
                        return (mP3Input.getPosition() != this.stopPosition || ID3v1Info.isID3v1StartPosition(mP3Input) == null) ? null : true;
                    }
                });
            } catch (Throwable e) {
                if (LOGGER.isLoggable(level)) {
                    LOGGER.log(level, "Could not determine MP3 duration", e);
                }
            }
        }
        if (this.title == null || this.album == null || this.artist == null) {
            long j2 = j - 128;
            if (mP3Input.getPosition() <= j2) {
                mP3Input.skipFully(j2 - mP3Input.getPosition());
                if (ID3v1Info.isID3v1StartPosition(inputStream) != null) {
                    j = new ID3v1Info(inputStream);
                    if (this.album == null) {
                        this.album = j.getAlbum();
                    }
                    if (this.artist == null) {
                        this.artist = j.getArtist();
                    }
                    if (this.comment == null) {
                        this.comment = j.getComment();
                    }
                    if (this.genre == null) {
                        this.genre = j.getGenre();
                    }
                    if (this.title == null) {
                        this.title = j.getTitle();
                    }
                    if (this.track == null) {
                        this.track = j.getTrack();
                    }
                    if (this.year == null) {
                        this.year = j.getYear();
                    }
                }
            }
        }
    }

    org.telegram.messenger.audioinfo.mp3.MP3Frame readFirstFrame(org.telegram.messenger.audioinfo.mp3.MP3Input r13, org.telegram.messenger.audioinfo.mp3.MP3Info.StopReadCondition r14) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r12 = this;
        r0 = r14.stopRead(r13);
        r1 = -1;
        if (r0 == 0) goto L_0x0009;
    L_0x0007:
        r0 = r1;
        goto L_0x000d;
    L_0x0009:
        r0 = r13.read();
    L_0x000d:
        r2 = 0;
        r3 = r2;
    L_0x000f:
        r4 = 0;
        if (r0 == r1) goto L_0x00dd;
    L_0x0012:
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r3 != r5) goto L_0x00cc;
    L_0x0016:
        r3 = r0 & 224;
        r6 = 224; // 0xe0 float:3.14E-43 double:1.107E-321;
        if (r3 != r6) goto L_0x00cc;
    L_0x001c:
        r3 = 2;
        r13.mark(r3);
        r6 = r14.stopRead(r13);
        if (r6 == 0) goto L_0x0028;
    L_0x0026:
        r6 = r1;
        goto L_0x002c;
    L_0x0028:
        r6 = r13.read();
    L_0x002c:
        if (r6 != r1) goto L_0x0030;
    L_0x002e:
        goto L_0x00dd;
    L_0x0030:
        r7 = r14.stopRead(r13);
        if (r7 == 0) goto L_0x0038;
    L_0x0036:
        r7 = r1;
        goto L_0x003c;
    L_0x0038:
        r7 = r13.read();
    L_0x003c:
        if (r7 != r1) goto L_0x0040;
    L_0x003e:
        goto L_0x00dd;
    L_0x0040:
        r8 = new org.telegram.messenger.audioinfo.mp3.MP3Frame$Header;	 Catch:{ MP3Exception -> 0x0046 }
        r8.<init>(r0, r6, r7);	 Catch:{ MP3Exception -> 0x0046 }
        goto L_0x0047;
    L_0x0046:
        r8 = r4;
    L_0x0047:
        if (r8 == 0) goto L_0x00c9;
    L_0x0049:
        r13.reset();
        r6 = r8.getFrameSize();
        r6 = r6 + r3;
        r13.mark(r6);
        r6 = r8.getFrameSize();
        r6 = new byte[r6];
        r6[r2] = r1;
        r7 = 1;
        r9 = (byte) r0;
        r6[r7] = r9;
        r7 = r6.length;	 Catch:{ EOFException -> 0x00dd }
        r7 = r7 - r3;	 Catch:{ EOFException -> 0x00dd }
        r13.readFully(r6, r3, r7);	 Catch:{ EOFException -> 0x00dd }
        r4 = new org.telegram.messenger.audioinfo.mp3.MP3Frame;
        r4.<init>(r8, r6);
        r7 = r4.isChecksumError();
        if (r7 != 0) goto L_0x00c9;
    L_0x0070:
        r7 = r14.stopRead(r13);
        if (r7 == 0) goto L_0x0078;
    L_0x0076:
        r7 = r1;
        goto L_0x007c;
    L_0x0078:
        r7 = r13.read();
    L_0x007c:
        r9 = r14.stopRead(r13);
        if (r9 == 0) goto L_0x0084;
    L_0x0082:
        r9 = r1;
        goto L_0x0088;
    L_0x0084:
        r9 = r13.read();
    L_0x0088:
        if (r7 == r1) goto L_0x00c8;
    L_0x008a:
        if (r9 != r1) goto L_0x008d;
    L_0x008c:
        goto L_0x00c8;
    L_0x008d:
        if (r7 != r5) goto L_0x00c9;
    L_0x008f:
        r5 = r9 & 254;
        r7 = r0 & 254;
        if (r5 != r7) goto L_0x00c9;
    L_0x0095:
        r5 = r14.stopRead(r13);
        if (r5 == 0) goto L_0x009d;
    L_0x009b:
        r5 = r1;
        goto L_0x00a1;
    L_0x009d:
        r5 = r13.read();
    L_0x00a1:
        r7 = r14.stopRead(r13);
        if (r7 == 0) goto L_0x00a9;
    L_0x00a7:
        r7 = r1;
        goto L_0x00ad;
    L_0x00a9:
        r7 = r13.read();
    L_0x00ad:
        if (r5 == r1) goto L_0x00c7;
    L_0x00af:
        if (r7 != r1) goto L_0x00b2;
    L_0x00b1:
        goto L_0x00c7;
    L_0x00b2:
        r10 = new org.telegram.messenger.audioinfo.mp3.MP3Frame$Header;	 Catch:{ MP3Exception -> 0x00c9 }
        r10.<init>(r9, r5, r7);	 Catch:{ MP3Exception -> 0x00c9 }
        r5 = r10.isCompatible(r8);	 Catch:{ MP3Exception -> 0x00c9 }
        if (r5 == 0) goto L_0x00c9;	 Catch:{ MP3Exception -> 0x00c9 }
    L_0x00bd:
        r13.reset();	 Catch:{ MP3Exception -> 0x00c9 }
        r5 = r6.length;	 Catch:{ MP3Exception -> 0x00c9 }
        r5 = r5 - r3;	 Catch:{ MP3Exception -> 0x00c9 }
        r5 = (long) r5;	 Catch:{ MP3Exception -> 0x00c9 }
        r13.skipFully(r5);	 Catch:{ MP3Exception -> 0x00c9 }
        return r4;
    L_0x00c7:
        return r4;
    L_0x00c8:
        return r4;
    L_0x00c9:
        r13.reset();
    L_0x00cc:
        r3 = r14.stopRead(r13);
        if (r3 == 0) goto L_0x00d4;
    L_0x00d2:
        r3 = r1;
        goto L_0x00d8;
    L_0x00d4:
        r3 = r13.read();
    L_0x00d8:
        r11 = r3;
        r3 = r0;
        r0 = r11;
        goto L_0x000f;
    L_0x00dd:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.MP3Info.readFirstFrame(org.telegram.messenger.audioinfo.mp3.MP3Input, org.telegram.messenger.audioinfo.mp3.MP3Info$StopReadCondition):org.telegram.messenger.audioinfo.mp3.MP3Frame");
    }

    org.telegram.messenger.audioinfo.mp3.MP3Frame readNextFrame(org.telegram.messenger.audioinfo.mp3.MP3Input r8, org.telegram.messenger.audioinfo.mp3.MP3Info.StopReadCondition r9, org.telegram.messenger.audioinfo.mp3.MP3Frame r10) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r7 = this;
        r10 = r10.getHeader();
        r0 = 4;
        r8.mark(r0);
        r1 = r9.stopRead(r8);
        r2 = -1;
        if (r1 == 0) goto L_0x0011;
    L_0x000f:
        r1 = r2;
        goto L_0x0015;
    L_0x0011:
        r1 = r8.read();
    L_0x0015:
        r3 = r9.stopRead(r8);
        if (r3 == 0) goto L_0x001d;
    L_0x001b:
        r3 = r2;
        goto L_0x0021;
    L_0x001d:
        r3 = r8.read();
    L_0x0021:
        r4 = 0;
        if (r1 == r2) goto L_0x0084;
    L_0x0024:
        if (r3 != r2) goto L_0x0027;
    L_0x0026:
        goto L_0x0084;
    L_0x0027:
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r1 != r5) goto L_0x0080;
    L_0x002b:
        r5 = r3 & 224;
        r6 = 224; // 0xe0 float:3.14E-43 double:1.107E-321;
        if (r5 != r6) goto L_0x0080;
    L_0x0031:
        r5 = r9.stopRead(r8);
        if (r5 == 0) goto L_0x0039;
    L_0x0037:
        r5 = r2;
        goto L_0x003d;
    L_0x0039:
        r5 = r8.read();
    L_0x003d:
        r9 = r9.stopRead(r8);
        if (r9 == 0) goto L_0x0045;
    L_0x0043:
        r9 = r2;
        goto L_0x0049;
    L_0x0045:
        r9 = r8.read();
    L_0x0049:
        if (r5 == r2) goto L_0x007f;
    L_0x004b:
        if (r9 != r2) goto L_0x004e;
    L_0x004d:
        goto L_0x007f;
    L_0x004e:
        r2 = new org.telegram.messenger.audioinfo.mp3.MP3Frame$Header;	 Catch:{ MP3Exception -> 0x0054 }
        r2.<init>(r3, r5, r9);	 Catch:{ MP3Exception -> 0x0054 }
        goto L_0x0055;
    L_0x0054:
        r2 = r4;
    L_0x0055:
        if (r2 == 0) goto L_0x0080;
    L_0x0057:
        r10 = r2.isCompatible(r10);
        if (r10 == 0) goto L_0x0080;
    L_0x005d:
        r10 = r2.getFrameSize();
        r10 = new byte[r10];
        r6 = 0;
        r1 = (byte) r1;
        r10[r6] = r1;
        r1 = 1;
        r3 = (byte) r3;
        r10[r1] = r3;
        r1 = 2;
        r3 = (byte) r5;
        r10[r1] = r3;
        r1 = 3;
        r9 = (byte) r9;
        r10[r1] = r9;
        r9 = r10.length;	 Catch:{ EOFException -> 0x007e }
        r9 = r9 - r0;	 Catch:{ EOFException -> 0x007e }
        r8.readFully(r10, r0, r9);	 Catch:{ EOFException -> 0x007e }
        r8 = new org.telegram.messenger.audioinfo.mp3.MP3Frame;
        r8.<init>(r2, r10);
        return r8;
    L_0x007e:
        return r4;
    L_0x007f:
        return r4;
    L_0x0080:
        r8.reset();
        return r4;
    L_0x0084:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.MP3Info.readNextFrame(org.telegram.messenger.audioinfo.mp3.MP3Input, org.telegram.messenger.audioinfo.mp3.MP3Info$StopReadCondition, org.telegram.messenger.audioinfo.mp3.MP3Frame):org.telegram.messenger.audioinfo.mp3.MP3Frame");
    }

    long calculateDuration(MP3Input mP3Input, long j, StopReadCondition stopReadCondition) throws IOException, MP3Exception {
        MP3Input mP3Input2 = mP3Input;
        StopReadCondition stopReadCondition2 = stopReadCondition;
        MP3Frame readFirstFrame = readFirstFrame(mP3Input2, stopReadCondition2);
        if (readFirstFrame != null) {
            int numberOfFrames = readFirstFrame.getNumberOfFrames();
            if (numberOfFrames > 0) {
                return readFirstFrame.getHeader().getTotalDuration((long) (numberOfFrames * readFirstFrame.getSize()));
            }
            long position = mP3Input.getPosition() - ((long) readFirstFrame.getSize());
            long size = (long) readFirstFrame.getSize();
            int bitrate = readFirstFrame.getHeader().getBitrate();
            Object obj = null;
            int duration = 10000 / readFirstFrame.getHeader().getDuration();
            long j2 = (long) bitrate;
            long j3 = size;
            MP3Frame mP3Frame = readFirstFrame;
            int i = 1;
            while (true) {
                if (i == duration && r9 == null && j > 0) {
                    return mP3Frame.getHeader().getTotalDuration(j - position);
                }
                MP3Info mP3Info;
                mP3Frame = mP3Info.readNextFrame(mP3Input2, stopReadCondition2, mP3Frame);
                if (mP3Frame == null) {
                    return (((1000 * j3) * ((long) i)) * 8) / j2;
                }
                int bitrate2 = mP3Frame.getHeader().getBitrate();
                if (bitrate2 != bitrate) {
                    obj = 1;
                }
                i++;
                j3 += (long) mP3Frame.getSize();
                j2 += (long) bitrate2;
                mP3Info = this;
                mP3Input2 = mP3Input;
            }
        } else {
            throw new MP3Exception("No audio frame");
        }
    }
}
