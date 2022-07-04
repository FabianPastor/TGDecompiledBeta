package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.audioinfo.mp3.MP3Frame;

public class MP3Info extends AudioInfo {
    static final Logger LOGGER = Logger.getLogger(MP3Info.class.getName());

    interface StopReadCondition {
        boolean stopRead(MP3Input mP3Input) throws IOException;
    }

    public MP3Info(InputStream input, long fileLength) throws IOException, ID3v2Exception, MP3Exception {
        this(input, fileLength, Level.FINEST);
    }

    public MP3Info(InputStream input, long fileLength, Level debugLevel) throws IOException, ID3v2Exception, MP3Exception {
        this.brand = "MP3";
        this.version = "0";
        MP3Input data = new MP3Input(input);
        if (ID3v2Info.isID3v2StartPosition(data)) {
            ID3v2Info info = new ID3v2Info(data, debugLevel);
            this.album = info.getAlbum();
            this.albumArtist = info.getAlbumArtist();
            this.artist = info.getArtist();
            this.comment = info.getComment();
            this.cover = info.getCover();
            this.smallCover = info.getSmallCover();
            this.compilation = info.isCompilation();
            this.composer = info.getComposer();
            this.copyright = info.getCopyright();
            this.disc = info.getDisc();
            this.discs = info.getDiscs();
            this.duration = info.getDuration();
            this.genre = info.getGenre();
            this.grouping = info.getGrouping();
            this.lyrics = info.getLyrics();
            this.title = info.getTitle();
            this.track = info.getTrack();
            this.tracks = info.getTracks();
            this.year = info.getYear();
        }
        if (this.duration <= 0 || this.duration >= 3600000) {
            try {
                this.duration = calculateDuration(data, fileLength, new StopReadCondition(fileLength) {
                    final long stopPosition;
                    final /* synthetic */ long val$fileLength;

                    {
                        this.val$fileLength = r4;
                        this.stopPosition = r4 - 128;
                    }

                    public boolean stopRead(MP3Input data) throws IOException {
                        return data.getPosition() == this.stopPosition && ID3v1Info.isID3v1StartPosition(data);
                    }
                });
            } catch (MP3Exception e) {
                Logger logger = LOGGER;
                if (logger.isLoggable(debugLevel)) {
                    logger.log(debugLevel, "Could not determine MP3 duration", e);
                }
            }
        }
        if ((this.title == null || this.album == null || this.artist == null) && data.getPosition() <= fileLength - 128) {
            data.skipFully((fileLength - 128) - data.getPosition());
            if (ID3v1Info.isID3v1StartPosition(input)) {
                ID3v1Info info2 = new ID3v1Info(input);
                if (this.album == null) {
                    this.album = info2.getAlbum();
                }
                if (this.artist == null) {
                    this.artist = info2.getArtist();
                }
                if (this.comment == null) {
                    this.comment = info2.getComment();
                }
                if (this.genre == null) {
                    this.genre = info2.getGenre();
                }
                if (this.title == null) {
                    this.title = info2.getTitle();
                }
                if (this.track == 0) {
                    this.track = info2.getTrack();
                }
                if (this.year == 0) {
                    this.year = info2.getYear();
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v25, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v30, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v32, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v40, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v42, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v44, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v46, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v50, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v52, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v53, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v54, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v55, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v56, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v57, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v58, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v59, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: byte} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.messenger.audioinfo.mp3.MP3Frame readFirstFrame(org.telegram.messenger.audioinfo.mp3.MP3Input r18, org.telegram.messenger.audioinfo.mp3.MP3Info.StopReadCondition r19) throws java.io.IOException {
        /*
            r17 = this;
            r1 = r18
            r2 = r19
            r0 = 0
            boolean r3 = r2.stopRead(r1)
            r4 = -1
            if (r3 == 0) goto L_0x000e
            r3 = -1
            goto L_0x0012
        L_0x000e:
            int r3 = r18.read()
        L_0x0012:
            r5 = r3
            r3 = r0
        L_0x0014:
            if (r5 == r4) goto L_0x0113
            r6 = 255(0xff, float:3.57E-43)
            if (r3 != r6) goto L_0x00ff
            r0 = r5 & 224(0xe0, float:3.14E-43)
            r7 = 224(0xe0, float:3.14E-43)
            if (r0 != r7) goto L_0x00ff
            r7 = 2
            r1.mark(r7)
            boolean r0 = r2.stopRead(r1)
            if (r0 == 0) goto L_0x002c
            r0 = -1
            goto L_0x0030
        L_0x002c:
            int r0 = r18.read()
        L_0x0030:
            r8 = r0
            if (r8 != r4) goto L_0x0037
            r16 = r5
            goto L_0x0115
        L_0x0037:
            boolean r0 = r2.stopRead(r1)
            if (r0 == 0) goto L_0x003f
            r0 = -1
            goto L_0x0043
        L_0x003f:
            int r0 = r18.read()
        L_0x0043:
            r9 = r0
            if (r9 != r4) goto L_0x004a
            r16 = r5
            goto L_0x0115
        L_0x004a:
            r10 = 0
            org.telegram.messenger.audioinfo.mp3.MP3Frame$Header r0 = new org.telegram.messenger.audioinfo.mp3.MP3Frame$Header     // Catch:{ MP3Exception -> 0x0052 }
            r0.<init>(r5, r8, r9)     // Catch:{ MP3Exception -> 0x0052 }
            r10 = r0
            goto L_0x0053
        L_0x0052:
            r0 = move-exception
        L_0x0053:
            if (r10 == 0) goto L_0x00f9
            r18.reset()
            int r0 = r10.getFrameSize()
            int r0 = r0 + r7
            r1.mark(r0)
            int r0 = r10.getFrameSize()
            byte[] r11 = new byte[r0]
            r0 = 0
            r11[r0] = r4
            r0 = 1
            byte r12 = (byte) r5
            r11[r0] = r12
            int r0 = r11.length     // Catch:{ EOFException -> 0x00f5 }
            int r0 = r0 - r7
            r1.readFully(r11, r7, r0)     // Catch:{ EOFException -> 0x00f5 }
            org.telegram.messenger.audioinfo.mp3.MP3Frame r0 = new org.telegram.messenger.audioinfo.mp3.MP3Frame
            r0.<init>(r10, r11)
            r12 = r0
            boolean r0 = r12.isChecksumError()
            if (r0 != 0) goto L_0x00f2
            boolean r0 = r2.stopRead(r1)
            if (r0 == 0) goto L_0x0087
            r0 = -1
            goto L_0x008b
        L_0x0087:
            int r0 = r18.read()
        L_0x008b:
            r13 = r0
            boolean r0 = r2.stopRead(r1)
            if (r0 == 0) goto L_0x0094
            r0 = -1
            goto L_0x0098
        L_0x0094:
            int r0 = r18.read()
        L_0x0098:
            r14 = r0
            if (r13 == r4) goto L_0x00ef
            if (r14 != r4) goto L_0x00a0
            r16 = r5
            goto L_0x00f1
        L_0x00a0:
            if (r13 != r6) goto L_0x00ec
            r0 = r14 & 254(0xfe, float:3.56E-43)
            r6 = r5 & 254(0xfe, float:3.56E-43)
            if (r0 != r6) goto L_0x00ec
            boolean r0 = r2.stopRead(r1)
            if (r0 == 0) goto L_0x00b0
            r0 = -1
            goto L_0x00b4
        L_0x00b0:
            int r0 = r18.read()
        L_0x00b4:
            r6 = r0
            boolean r0 = r2.stopRead(r1)
            if (r0 == 0) goto L_0x00bd
            r0 = -1
            goto L_0x00c1
        L_0x00bd:
            int r0 = r18.read()
        L_0x00c1:
            r15 = r0
            if (r6 == r4) goto L_0x00e9
            if (r15 != r4) goto L_0x00c9
            r16 = r5
            goto L_0x00eb
        L_0x00c9:
            org.telegram.messenger.audioinfo.mp3.MP3Frame$Header r0 = new org.telegram.messenger.audioinfo.mp3.MP3Frame$Header     // Catch:{ MP3Exception -> 0x00e5 }
            r0.<init>(r14, r6, r15)     // Catch:{ MP3Exception -> 0x00e5 }
            boolean r0 = r0.isCompatible(r10)     // Catch:{ MP3Exception -> 0x00e5 }
            if (r0 == 0) goto L_0x00e2
            r18.reset()     // Catch:{ MP3Exception -> 0x00e5 }
            int r0 = r11.length     // Catch:{ MP3Exception -> 0x00e5 }
            int r0 = r0 - r7
            r16 = r5
            long r4 = (long) r0
            r1.skipFully(r4)     // Catch:{ MP3Exception -> 0x00e0 }
            return r12
        L_0x00e0:
            r0 = move-exception
            goto L_0x00fb
        L_0x00e2:
            r16 = r5
            goto L_0x00fb
        L_0x00e5:
            r0 = move-exception
            r16 = r5
            goto L_0x00fb
        L_0x00e9:
            r16 = r5
        L_0x00eb:
            return r12
        L_0x00ec:
            r16 = r5
            goto L_0x00fb
        L_0x00ef:
            r16 = r5
        L_0x00f1:
            return r12
        L_0x00f2:
            r16 = r5
            goto L_0x00fb
        L_0x00f5:
            r0 = move-exception
            r16 = r5
            goto L_0x0115
        L_0x00f9:
            r16 = r5
        L_0x00fb:
            r18.reset()
            goto L_0x0101
        L_0x00ff:
            r16 = r5
        L_0x0101:
            r3 = r16
            boolean r0 = r2.stopRead(r1)
            if (r0 == 0) goto L_0x010b
            r0 = -1
            goto L_0x010f
        L_0x010b:
            int r0 = r18.read()
        L_0x010f:
            r5 = r0
            r4 = -1
            goto L_0x0014
        L_0x0113:
            r16 = r5
        L_0x0115:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.MP3Info.readFirstFrame(org.telegram.messenger.audioinfo.mp3.MP3Input, org.telegram.messenger.audioinfo.mp3.MP3Info$StopReadCondition):org.telegram.messenger.audioinfo.mp3.MP3Frame");
    }

    /* access modifiers changed from: package-private */
    public MP3Frame readNextFrame(MP3Input data, StopReadCondition stopCondition, MP3Frame previousFrame) throws IOException {
        MP3Frame.Header previousHeader = previousFrame.getHeader();
        data.mark(4);
        int b0 = stopCondition.stopRead(data) ? -1 : data.read();
        int b1 = stopCondition.stopRead(data) ? -1 : data.read();
        if (b0 == -1 || b1 == -1) {
            return null;
        }
        if (b0 == 255 && (b1 & 224) == 224) {
            int b2 = stopCondition.stopRead(data) ? -1 : data.read();
            int b3 = stopCondition.stopRead(data) ? -1 : data.read();
            if (b2 == -1 || b3 == -1) {
                return null;
            }
            MP3Frame.Header nextHeader = null;
            try {
                nextHeader = new MP3Frame.Header(b1, b2, b3);
            } catch (MP3Exception e) {
            }
            if (nextHeader != null && nextHeader.isCompatible(previousHeader)) {
                byte[] frameBytes = new byte[nextHeader.getFrameSize()];
                frameBytes[0] = (byte) b0;
                frameBytes[1] = (byte) b1;
                frameBytes[2] = (byte) b2;
                frameBytes[3] = (byte) b3;
                try {
                    data.readFully(frameBytes, 4, frameBytes.length - 4);
                    return new MP3Frame(nextHeader, frameBytes);
                } catch (EOFException e2) {
                    return null;
                }
            }
        }
        data.reset();
        return null;
    }

    /* access modifiers changed from: package-private */
    public long calculateDuration(MP3Input data, long totalLength, StopReadCondition stopCondition) throws IOException, MP3Exception {
        MP3Info mP3Info = this;
        MP3Input mP3Input = data;
        StopReadCondition stopReadCondition = stopCondition;
        MP3Frame frame = mP3Info.readFirstFrame(mP3Input, stopReadCondition);
        if (frame != null) {
            int numberOfFrames = frame.getNumberOfFrames();
            if (numberOfFrames > 0) {
                return frame.getHeader().getTotalDuration((long) (frame.getSize() * numberOfFrames));
            }
            int numberOfFrames2 = 1;
            long firstFramePosition = data.getPosition() - ((long) frame.getSize());
            long frameSizeSum = (long) frame.getSize();
            int firstFrameBitrate = frame.getHeader().getBitrate();
            long bitrateSum = (long) firstFrameBitrate;
            boolean vbr = false;
            int cbrThreshold = 10000 / frame.getHeader().getDuration();
            while (true) {
                if (numberOfFrames2 != cbrThreshold || vbr || totalLength <= 0) {
                    boolean vbr2 = vbr;
                    int cbrThreshold2 = cbrThreshold;
                    MP3Frame readNextFrame = mP3Info.readNextFrame(mP3Input, stopReadCondition, frame);
                    frame = readNextFrame;
                    if (readNextFrame == null) {
                        return (((1000 * frameSizeSum) * ((long) numberOfFrames2)) * 8) / bitrateSum;
                    }
                    int bitrate = frame.getHeader().getBitrate();
                    if (bitrate != firstFrameBitrate) {
                        vbr = true;
                    } else {
                        vbr = vbr2;
                    }
                    bitrateSum += (long) bitrate;
                    frameSizeSum += (long) frame.getSize();
                    numberOfFrames2++;
                    mP3Info = this;
                    mP3Input = data;
                    cbrThreshold = cbrThreshold2;
                } else {
                    boolean z = vbr;
                    int i = cbrThreshold;
                    return frame.getHeader().getTotalDuration(totalLength - firstFramePosition);
                }
            }
        } else {
            throw new MP3Exception("No audio frame");
        }
    }
}
