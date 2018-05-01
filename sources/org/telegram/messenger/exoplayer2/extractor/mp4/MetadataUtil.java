package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import com.coremedia.iso.boxes.GenreBox;
import com.coremedia.iso.boxes.RatingBox;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.metadata.id3.ApicFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.CommentFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame;
import org.telegram.messenger.exoplayer2.metadata.id3.TextInformationFrame;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class MetadataUtil {
    private static final String LANGUAGE_UNDEFINED = "und";
    private static final int SHORT_TYPE_ALBUM = Util.getIntegerCodeForString("alb");
    private static final int SHORT_TYPE_ARTIST = Util.getIntegerCodeForString("ART");
    private static final int SHORT_TYPE_COMMENT = Util.getIntegerCodeForString("cmt");
    private static final int SHORT_TYPE_COMPOSER_1 = Util.getIntegerCodeForString("com");
    private static final int SHORT_TYPE_COMPOSER_2 = Util.getIntegerCodeForString("wrt");
    private static final int SHORT_TYPE_ENCODER = Util.getIntegerCodeForString("too");
    private static final int SHORT_TYPE_GENRE = Util.getIntegerCodeForString("gen");
    private static final int SHORT_TYPE_LYRICS = Util.getIntegerCodeForString("lyr");
    private static final int SHORT_TYPE_NAME_1 = Util.getIntegerCodeForString("nam");
    private static final int SHORT_TYPE_NAME_2 = Util.getIntegerCodeForString("trk");
    private static final int SHORT_TYPE_YEAR = Util.getIntegerCodeForString("day");
    private static final String[] STANDARD_GENRES = new String[]{"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop"};
    private static final String TAG = "MetadataUtil";
    private static final int TYPE_ALBUM_ARTIST = Util.getIntegerCodeForString("aART");
    private static final int TYPE_COMPILATION = Util.getIntegerCodeForString("cpil");
    private static final int TYPE_COVER_ART = Util.getIntegerCodeForString("covr");
    private static final int TYPE_DISK_NUMBER = Util.getIntegerCodeForString("disk");
    private static final int TYPE_GAPLESS_ALBUM = Util.getIntegerCodeForString("pgap");
    private static final int TYPE_GENRE = Util.getIntegerCodeForString(GenreBox.TYPE);
    private static final int TYPE_GROUPING = Util.getIntegerCodeForString("grp");
    private static final int TYPE_INTERNAL = Util.getIntegerCodeForString("----");
    private static final int TYPE_RATING = Util.getIntegerCodeForString(RatingBox.TYPE);
    private static final int TYPE_SORT_ALBUM = Util.getIntegerCodeForString("soal");
    private static final int TYPE_SORT_ALBUM_ARTIST = Util.getIntegerCodeForString("soaa");
    private static final int TYPE_SORT_ARTIST = Util.getIntegerCodeForString("soar");
    private static final int TYPE_SORT_COMPOSER = Util.getIntegerCodeForString("soco");
    private static final int TYPE_SORT_TRACK_NAME = Util.getIntegerCodeForString("sonm");
    private static final int TYPE_TEMPO = Util.getIntegerCodeForString("tmpo");
    private static final int TYPE_TRACK_NUMBER = Util.getIntegerCodeForString("trkn");
    private static final int TYPE_TV_SHOW = Util.getIntegerCodeForString("tvsh");
    private static final int TYPE_TV_SORT_SHOW = Util.getIntegerCodeForString("sosn");

    private MetadataUtil() {
    }

    public static Entry parseIlstElement(ParsableByteArray parsableByteArray) {
        Entry parseStandardGenreAttribute;
        int position = parsableByteArray.getPosition() + parsableByteArray.readInt();
        int readInt = parsableByteArray.readInt();
        int i = (readInt >> 24) & 255;
        if (i != 169) {
            if (i != 65533) {
                try {
                    if (readInt == TYPE_GENRE) {
                        parseStandardGenreAttribute = parseStandardGenreAttribute(parsableByteArray);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_DISK_NUMBER) {
                        parseStandardGenreAttribute = parseIndexAndCountAttribute(readInt, "TPOS", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_TRACK_NUMBER) {
                        parseStandardGenreAttribute = parseIndexAndCountAttribute(readInt, "TRCK", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_TEMPO) {
                        parseStandardGenreAttribute = parseUint8Attribute(readInt, "TBPM", parsableByteArray, true, false);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_COMPILATION) {
                        parseStandardGenreAttribute = parseUint8Attribute(readInt, "TCMP", parsableByteArray, true, true);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_COVER_ART) {
                        parseStandardGenreAttribute = parseCoverArt(parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_ALBUM_ARTIST) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TPE2", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_SORT_TRACK_NAME) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TSOT", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_SORT_ALBUM) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TSO2", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_SORT_ARTIST) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TSOA", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_SORT_ALBUM_ARTIST) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TSOP", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_SORT_COMPOSER) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TSOC", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_RATING) {
                        parseStandardGenreAttribute = parseUint8Attribute(readInt, "ITUNESADVISORY", parsableByteArray, false, false);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_GAPLESS_ALBUM) {
                        parseStandardGenreAttribute = parseUint8Attribute(readInt, "ITUNESGAPLESS", parsableByteArray, false, true);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_TV_SORT_SHOW) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TVSHOWSORT", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else if (readInt == TYPE_TV_SHOW) {
                        parseStandardGenreAttribute = parseTextAttribute(readInt, "TVSHOW", parsableByteArray);
                        parsableByteArray.setPosition(position);
                        return parseStandardGenreAttribute;
                    } else {
                        if (readInt == TYPE_INTERNAL) {
                            parseStandardGenreAttribute = parseInternalAttribute(parsableByteArray, position);
                            parsableByteArray.setPosition(position);
                            return parseStandardGenreAttribute;
                        }
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Skipped unknown metadata entry: ");
                        stringBuilder.append(Atom.getAtomTypeString(readInt));
                        Log.d(str, stringBuilder.toString());
                        parsableByteArray.setPosition(position);
                        return null;
                    }
                } finally {
                    parsableByteArray.setPosition(position);
                }
            }
        }
        i = 16777215 & readInt;
        if (i == SHORT_TYPE_COMMENT) {
            parseStandardGenreAttribute = parseCommentAttribute(readInt, parsableByteArray);
            parsableByteArray.setPosition(position);
            return parseStandardGenreAttribute;
        }
        if (i != SHORT_TYPE_NAME_1) {
            if (i != SHORT_TYPE_NAME_2) {
                if (i != SHORT_TYPE_COMPOSER_1) {
                    if (i != SHORT_TYPE_COMPOSER_2) {
                        if (i == SHORT_TYPE_YEAR) {
                            parseStandardGenreAttribute = parseTextAttribute(readInt, "TDRC", parsableByteArray);
                            parsableByteArray.setPosition(position);
                            return parseStandardGenreAttribute;
                        } else if (i == SHORT_TYPE_ARTIST) {
                            parseStandardGenreAttribute = parseTextAttribute(readInt, "TPE1", parsableByteArray);
                            parsableByteArray.setPosition(position);
                            return parseStandardGenreAttribute;
                        } else if (i == SHORT_TYPE_ENCODER) {
                            parseStandardGenreAttribute = parseTextAttribute(readInt, "TSSE", parsableByteArray);
                            parsableByteArray.setPosition(position);
                            return parseStandardGenreAttribute;
                        } else if (i == SHORT_TYPE_ALBUM) {
                            parseStandardGenreAttribute = parseTextAttribute(readInt, "TALB", parsableByteArray);
                            parsableByteArray.setPosition(position);
                            return parseStandardGenreAttribute;
                        } else if (i == SHORT_TYPE_LYRICS) {
                            parseStandardGenreAttribute = parseTextAttribute(readInt, "USLT", parsableByteArray);
                            parsableByteArray.setPosition(position);
                            return parseStandardGenreAttribute;
                        } else if (i == SHORT_TYPE_GENRE) {
                            parseStandardGenreAttribute = parseTextAttribute(readInt, "TCON", parsableByteArray);
                            parsableByteArray.setPosition(position);
                            return parseStandardGenreAttribute;
                        } else {
                            if (i == TYPE_GROUPING) {
                                parseStandardGenreAttribute = parseTextAttribute(readInt, "TIT1", parsableByteArray);
                                parsableByteArray.setPosition(position);
                                return parseStandardGenreAttribute;
                            }
                            String str2 = TAG;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Skipped unknown metadata entry: ");
                            stringBuilder2.append(Atom.getAtomTypeString(readInt));
                            Log.d(str2, stringBuilder2.toString());
                            parsableByteArray.setPosition(position);
                            return null;
                        }
                    }
                }
                parseStandardGenreAttribute = parseTextAttribute(readInt, "TCOM", parsableByteArray);
                parsableByteArray.setPosition(position);
                return parseStandardGenreAttribute;
            }
        }
        parseStandardGenreAttribute = parseTextAttribute(readInt, "TIT2", parsableByteArray);
        parsableByteArray.setPosition(position);
        return parseStandardGenreAttribute;
    }

    private static TextInformationFrame parseTextAttribute(int i, String str, ParsableByteArray parsableByteArray) {
        int readInt = parsableByteArray.readInt();
        if (parsableByteArray.readInt() == Atom.TYPE_data) {
            parsableByteArray.skipBytes(8);
            return new TextInformationFrame(str, null, parsableByteArray.readNullTerminatedString(readInt - 16));
        }
        str = TAG;
        parsableByteArray = new StringBuilder();
        parsableByteArray.append("Failed to parse text attribute: ");
        parsableByteArray.append(Atom.getAtomTypeString(i));
        Log.w(str, parsableByteArray.toString());
        return null;
    }

    private static CommentFrame parseCommentAttribute(int i, ParsableByteArray parsableByteArray) {
        int readInt = parsableByteArray.readInt();
        if (parsableByteArray.readInt() == Atom.TYPE_data) {
            parsableByteArray.skipBytes(8);
            i = parsableByteArray.readNullTerminatedString(readInt - 16);
            return new CommentFrame("und", i, i);
        }
        parsableByteArray = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to parse comment attribute: ");
        stringBuilder.append(Atom.getAtomTypeString(i));
        Log.w(parsableByteArray, stringBuilder.toString());
        return 0;
    }

    private static Id3Frame parseUint8Attribute(int i, String str, ParsableByteArray parsableByteArray, boolean z, boolean z2) {
        parsableByteArray = parseUint8AttributeValue(parsableByteArray);
        if (z2) {
            parsableByteArray = Math.min(true, parsableByteArray);
        }
        if (parsableByteArray >= null) {
            if (z) {
                i = new TextInformationFrame(str, null, Integer.toString(parsableByteArray));
            } else {
                i = new CommentFrame("und", str, Integer.toString(parsableByteArray));
            }
            return i;
        }
        str = TAG;
        parsableByteArray = new StringBuilder();
        parsableByteArray.append("Failed to parse uint8 attribute: ");
        parsableByteArray.append(Atom.getAtomTypeString(i));
        Log.w(str, parsableByteArray.toString());
        return null;
    }

    private static TextInformationFrame parseIndexAndCountAttribute(int i, String str, ParsableByteArray parsableByteArray) {
        int readInt = parsableByteArray.readInt();
        if (parsableByteArray.readInt() == Atom.TYPE_data && readInt >= 22) {
            parsableByteArray.skipBytes(10);
            readInt = parsableByteArray.readUnsignedShort();
            if (readInt > 0) {
                i = new StringBuilder();
                i.append(TtmlNode.ANONYMOUS_REGION_ID);
                i.append(readInt);
                i = i.toString();
                parsableByteArray = parsableByteArray.readUnsignedShort();
                if (parsableByteArray > null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(i);
                    stringBuilder.append("/");
                    stringBuilder.append(parsableByteArray);
                    i = stringBuilder.toString();
                }
                return new TextInformationFrame(str, null, i);
            }
        }
        str = TAG;
        parsableByteArray = new StringBuilder();
        parsableByteArray.append("Failed to parse index/count attribute: ");
        parsableByteArray.append(Atom.getAtomTypeString(i));
        Log.w(str, parsableByteArray.toString());
        return null;
    }

    private static TextInformationFrame parseStandardGenreAttribute(ParsableByteArray parsableByteArray) {
        parsableByteArray = parseUint8AttributeValue(parsableByteArray);
        parsableByteArray = (parsableByteArray <= null || parsableByteArray > STANDARD_GENRES.length) ? null : STANDARD_GENRES[parsableByteArray - 1];
        if (parsableByteArray != null) {
            return new TextInformationFrame("TCON", null, parsableByteArray);
        }
        Log.w(TAG, "Failed to parse standard genre code");
        return null;
    }

    private static ApicFrame parseCoverArt(ParsableByteArray parsableByteArray) {
        int readInt = parsableByteArray.readInt();
        if (parsableByteArray.readInt() == Atom.TYPE_data) {
            int parseFullAtomFlags = Atom.parseFullAtomFlags(parsableByteArray.readInt());
            String str = parseFullAtomFlags == 13 ? "image/jpeg" : parseFullAtomFlags == 14 ? "image/png" : null;
            if (str == null) {
                parsableByteArray = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unrecognized cover art flags: ");
                stringBuilder.append(parseFullAtomFlags);
                Log.w(parsableByteArray, stringBuilder.toString());
                return null;
            }
            parsableByteArray.skipBytes(4);
            byte[] bArr = new byte[(readInt - 16)];
            parsableByteArray.readBytes(bArr, 0, bArr.length);
            return new ApicFrame(str, null, 3, bArr);
        }
        Log.w(TAG, "Failed to parse cover art attribute");
        return null;
    }

    private static Id3Frame parseInternalAttribute(ParsableByteArray parsableByteArray, int i) {
        int i2 = -1;
        int i3 = i2;
        Object obj = null;
        String str = obj;
        while (parsableByteArray.getPosition() < i) {
            int position = parsableByteArray.getPosition();
            int readInt = parsableByteArray.readInt();
            int readInt2 = parsableByteArray.readInt();
            parsableByteArray.skipBytes(4);
            if (readInt2 == Atom.TYPE_mean) {
                obj = parsableByteArray.readNullTerminatedString(readInt - 12);
            } else if (readInt2 == Atom.TYPE_name) {
                str = parsableByteArray.readNullTerminatedString(readInt - 12);
            } else {
                if (readInt2 == Atom.TYPE_data) {
                    i2 = position;
                    i3 = readInt;
                }
                parsableByteArray.skipBytes(readInt - 12);
            }
        }
        if (!("com.apple.iTunes".equals(obj) == 0 || "iTunSMPB".equals(str) == 0)) {
            if (i2 != -1) {
                parsableByteArray.setPosition(i2);
                parsableByteArray.skipBytes(16);
                return new CommentFrame("und", str, parsableByteArray.readNullTerminatedString(i3 - 16));
            }
        }
        return null;
    }

    private static int parseUint8AttributeValue(ParsableByteArray parsableByteArray) {
        parsableByteArray.skipBytes(4);
        if (parsableByteArray.readInt() == Atom.TYPE_data) {
            parsableByteArray.skipBytes(8);
            return parsableByteArray.readUnsignedByte();
        }
        Log.w(TAG, "Failed to parse uint8 attribute value");
        return -1;
    }
}
