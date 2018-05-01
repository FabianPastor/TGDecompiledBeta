package org.telegram.messenger.audioinfo;

import android.graphics.Bitmap;

public abstract class AudioInfo {
    protected String album;
    protected String albumArtist;
    protected String artist;
    protected String brand;
    protected String comment;
    protected boolean compilation;
    protected String composer;
    protected String copyright;
    protected Bitmap cover;
    protected short disc;
    protected short discs;
    protected long duration;
    protected String genre;
    protected String grouping;
    protected String lyrics;
    protected Bitmap smallCover;
    protected String title;
    protected short track;
    protected short tracks;
    protected String version;
    protected short year;

    public String getBrand() {
        return this.brand;
    }

    public String getVersion() {
        return this.version;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getAlbumArtist() {
        return this.albumArtist;
    }

    public String getAlbum() {
        return this.album;
    }

    public short getYear() {
        return this.year;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getComment() {
        return this.comment;
    }

    public short getTrack() {
        return this.track;
    }

    public short getTracks() {
        return this.tracks;
    }

    public short getDisc() {
        return this.disc;
    }

    public short getDiscs() {
        return this.discs;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public String getComposer() {
        return this.composer;
    }

    public String getGrouping() {
        return this.grouping;
    }

    public boolean isCompilation() {
        return this.compilation;
    }

    public String getLyrics() {
        return this.lyrics;
    }

    public Bitmap getCover() {
        return this.cover;
    }

    public Bitmap getSmallCover() {
        return this.smallCover;
    }

    public static org.telegram.messenger.audioinfo.AudioInfo getAudioInfo(java.io.File r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = 12;
        r1 = 0;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x0058 }
        r2 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0058 }
        r3 = "r";	 Catch:{ Exception -> 0x0058 }
        r2.<init>(r5, r3);	 Catch:{ Exception -> 0x0058 }
        r3 = 0;	 Catch:{ Exception -> 0x0058 }
        r4 = 8;	 Catch:{ Exception -> 0x0058 }
        r2.readFully(r0, r3, r4);	 Catch:{ Exception -> 0x0058 }
        r2.close();	 Catch:{ Exception -> 0x0058 }
        r2 = new java.io.BufferedInputStream;	 Catch:{ Exception -> 0x0058 }
        r3 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0058 }
        r3.<init>(r5);	 Catch:{ Exception -> 0x0058 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0058 }
        r3 = 4;	 Catch:{ Exception -> 0x0058 }
        r3 = r0[r3];	 Catch:{ Exception -> 0x0058 }
        r4 = 102; // 0x66 float:1.43E-43 double:5.04E-322;	 Catch:{ Exception -> 0x0058 }
        if (r3 != r4) goto L_0x0041;	 Catch:{ Exception -> 0x0058 }
    L_0x0026:
        r3 = 5;	 Catch:{ Exception -> 0x0058 }
        r3 = r0[r3];	 Catch:{ Exception -> 0x0058 }
        r4 = 116; // 0x74 float:1.63E-43 double:5.73E-322;	 Catch:{ Exception -> 0x0058 }
        if (r3 != r4) goto L_0x0041;	 Catch:{ Exception -> 0x0058 }
    L_0x002d:
        r3 = 6;	 Catch:{ Exception -> 0x0058 }
        r3 = r0[r3];	 Catch:{ Exception -> 0x0058 }
        r4 = 121; // 0x79 float:1.7E-43 double:6.0E-322;	 Catch:{ Exception -> 0x0058 }
        if (r3 != r4) goto L_0x0041;	 Catch:{ Exception -> 0x0058 }
    L_0x0034:
        r3 = 7;	 Catch:{ Exception -> 0x0058 }
        r0 = r0[r3];	 Catch:{ Exception -> 0x0058 }
        r3 = 112; // 0x70 float:1.57E-43 double:5.53E-322;	 Catch:{ Exception -> 0x0058 }
        if (r0 != r3) goto L_0x0041;	 Catch:{ Exception -> 0x0058 }
    L_0x003b:
        r5 = new org.telegram.messenger.audioinfo.m4a.M4AInfo;	 Catch:{ Exception -> 0x0058 }
        r5.<init>(r2);	 Catch:{ Exception -> 0x0058 }
        return r5;	 Catch:{ Exception -> 0x0058 }
    L_0x0041:
        r0 = r5.getAbsolutePath();	 Catch:{ Exception -> 0x0058 }
        r3 = "mp3";	 Catch:{ Exception -> 0x0058 }
        r0 = r0.endsWith(r3);	 Catch:{ Exception -> 0x0058 }
        if (r0 == 0) goto L_0x0057;	 Catch:{ Exception -> 0x0058 }
    L_0x004d:
        r0 = new org.telegram.messenger.audioinfo.mp3.MP3Info;	 Catch:{ Exception -> 0x0058 }
        r3 = r5.length();	 Catch:{ Exception -> 0x0058 }
        r0.<init>(r2, r3);	 Catch:{ Exception -> 0x0058 }
        return r0;
    L_0x0057:
        return r1;
    L_0x0058:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(java.io.File):org.telegram.messenger.audioinfo.AudioInfo");
    }
}
