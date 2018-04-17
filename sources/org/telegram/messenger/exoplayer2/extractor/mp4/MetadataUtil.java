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

    public static Entry parseIlstElement(ParsableByteArray ilst) {
        int endPosition = ilst.readInt() + ilst.getPosition();
        int type = ilst.readInt();
        int typeTopByte = (type >> 24) & 255;
        if (typeTopByte != 169) {
            if (typeTopByte != 65533) {
                try {
                    Entry parseStandardGenreAttribute;
                    if (type == TYPE_GENRE) {
                        parseStandardGenreAttribute = parseStandardGenreAttribute(ilst);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_DISK_NUMBER) {
                        parseStandardGenreAttribute = parseIndexAndCountAttribute(type, "TPOS", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_TRACK_NUMBER) {
                        parseStandardGenreAttribute = parseIndexAndCountAttribute(type, "TRCK", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_TEMPO) {
                        parseStandardGenreAttribute = parseUint8Attribute(type, "TBPM", ilst, true, false);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_COMPILATION) {
                        parseStandardGenreAttribute = parseUint8Attribute(type, "TCMP", ilst, true, true);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_COVER_ART) {
                        parseStandardGenreAttribute = parseCoverArt(ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_ALBUM_ARTIST) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TPE2", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_SORT_TRACK_NAME) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TSOT", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_SORT_ALBUM) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TSO2", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_SORT_ARTIST) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TSOA", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_SORT_ALBUM_ARTIST) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TSOP", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_SORT_COMPOSER) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TSOC", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_RATING) {
                        parseStandardGenreAttribute = parseUint8Attribute(type, "ITUNESADVISORY", ilst, false, false);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_GAPLESS_ALBUM) {
                        parseStandardGenreAttribute = parseUint8Attribute(type, "ITUNESGAPLESS", ilst, false, true);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_TV_SORT_SHOW) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TVSHOWSORT", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else if (type == TYPE_TV_SHOW) {
                        parseStandardGenreAttribute = parseTextAttribute(type, "TVSHOW", ilst);
                        ilst.setPosition(endPosition);
                        return parseStandardGenreAttribute;
                    } else {
                        if (type == TYPE_INTERNAL) {
                            parseStandardGenreAttribute = parseInternalAttribute(ilst, endPosition);
                            ilst.setPosition(endPosition);
                            return parseStandardGenreAttribute;
                        }
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Skipped unknown metadata entry: ");
                        stringBuilder.append(Atom.getAtomTypeString(type));
                        Log.d(str, stringBuilder.toString());
                        ilst.setPosition(endPosition);
                        return null;
                    }
                } finally {
                    ilst.setPosition(endPosition);
                }
            }
        }
        int shortType = 16777215 & type;
        if (shortType == SHORT_TYPE_COMMENT) {
            Entry parseCommentAttribute = parseCommentAttribute(type, ilst);
            ilst.setPosition(endPosition);
            return parseCommentAttribute;
        }
        if (shortType != SHORT_TYPE_NAME_1) {
            if (shortType != SHORT_TYPE_NAME_2) {
                if (shortType != SHORT_TYPE_COMPOSER_1) {
                    if (shortType != SHORT_TYPE_COMPOSER_2) {
                        if (shortType == SHORT_TYPE_YEAR) {
                            parseCommentAttribute = parseTextAttribute(type, "TDRC", ilst);
                            ilst.setPosition(endPosition);
                            return parseCommentAttribute;
                        } else if (shortType == SHORT_TYPE_ARTIST) {
                            parseCommentAttribute = parseTextAttribute(type, "TPE1", ilst);
                            ilst.setPosition(endPosition);
                            return parseCommentAttribute;
                        } else if (shortType == SHORT_TYPE_ENCODER) {
                            parseCommentAttribute = parseTextAttribute(type, "TSSE", ilst);
                            ilst.setPosition(endPosition);
                            return parseCommentAttribute;
                        } else if (shortType == SHORT_TYPE_ALBUM) {
                            parseCommentAttribute = parseTextAttribute(type, "TALB", ilst);
                            ilst.setPosition(endPosition);
                            return parseCommentAttribute;
                        } else if (shortType == SHORT_TYPE_LYRICS) {
                            parseCommentAttribute = parseTextAttribute(type, "USLT", ilst);
                            ilst.setPosition(endPosition);
                            return parseCommentAttribute;
                        } else if (shortType == SHORT_TYPE_GENRE) {
                            parseCommentAttribute = parseTextAttribute(type, "TCON", ilst);
                            ilst.setPosition(endPosition);
                            return parseCommentAttribute;
                        } else if (shortType == TYPE_GROUPING) {
                            parseCommentAttribute = parseTextAttribute(type, "TIT1", ilst);
                            ilst.setPosition(endPosition);
                            return parseCommentAttribute;
                        } else {
                            String str2 = TAG;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Skipped unknown metadata entry: ");
                            stringBuilder2.append(Atom.getAtomTypeString(type));
                            Log.d(str2, stringBuilder2.toString());
                            ilst.setPosition(endPosition);
                            return null;
                        }
                    }
                }
                parseCommentAttribute = parseTextAttribute(type, "TCOM", ilst);
                ilst.setPosition(endPosition);
                return parseCommentAttribute;
            }
        }
        parseCommentAttribute = parseTextAttribute(type, "TIT2", ilst);
        ilst.setPosition(endPosition);
        return parseCommentAttribute;
    }

    private static TextInformationFrame parseTextAttribute(int type, String id, ParsableByteArray data) {
        int atomSize = data.readInt();
        if (data.readInt() == Atom.TYPE_data) {
            data.skipBytes(8);
            return new TextInformationFrame(id, null, data.readNullTerminatedString(atomSize - 16));
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to parse text attribute: ");
        stringBuilder.append(Atom.getAtomTypeString(type));
        Log.w(str, stringBuilder.toString());
        return null;
    }

    private static CommentFrame parseCommentAttribute(int type, ParsableByteArray data) {
        int atomSize = data.readInt();
        if (data.readInt() == Atom.TYPE_data) {
            data.skipBytes(8);
            String value = data.readNullTerminatedString(atomSize - 16);
            return new CommentFrame("und", value, value);
        }
        value = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to parse comment attribute: ");
        stringBuilder.append(Atom.getAtomTypeString(type));
        Log.w(value, stringBuilder.toString());
        return null;
    }

    private static Id3Frame parseUint8Attribute(int type, String id, ParsableByteArray data, boolean isTextInformationFrame, boolean isBoolean) {
        int value = parseUint8AttributeValue(data);
        if (isBoolean) {
            value = Math.min(1, value);
        }
        if (value >= 0) {
            Id3Frame textInformationFrame;
            if (isTextInformationFrame) {
                textInformationFrame = new TextInformationFrame(id, null, Integer.toString(value));
            } else {
                textInformationFrame = new CommentFrame("und", id, Integer.toString(value));
            }
            return textInformationFrame;
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to parse uint8 attribute: ");
        stringBuilder.append(Atom.getAtomTypeString(type));
        Log.w(str, stringBuilder.toString());
        return null;
    }

    private static TextInformationFrame parseIndexAndCountAttribute(int type, String attributeName, ParsableByteArray data) {
        int atomSize = data.readInt();
        if (data.readInt() == Atom.TYPE_data && atomSize >= 22) {
            data.skipBytes(10);
            int index = data.readUnsignedShort();
            if (index > 0) {
                String value = new StringBuilder();
                value.append(TtmlNode.ANONYMOUS_REGION_ID);
                value.append(index);
                value = value.toString();
                int count = data.readUnsignedShort();
                if (count > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(value);
                    stringBuilder.append("/");
                    stringBuilder.append(count);
                    value = stringBuilder.toString();
                }
                return new TextInformationFrame(attributeName, null, value);
            }
        }
        String str = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Failed to parse index/count attribute: ");
        stringBuilder2.append(Atom.getAtomTypeString(type));
        Log.w(str, stringBuilder2.toString());
        return null;
    }

    private static TextInformationFrame parseStandardGenreAttribute(ParsableByteArray data) {
        int genreCode = parseUint8AttributeValue(data);
        String genreString = (genreCode <= 0 || genreCode > STANDARD_GENRES.length) ? null : STANDARD_GENRES[genreCode - 1];
        if (genreString != null) {
            return new TextInformationFrame("TCON", null, genreString);
        }
        Log.w(TAG, "Failed to parse standard genre code");
        return null;
    }

    private static ApicFrame parseCoverArt(ParsableByteArray data) {
        int atomSize = data.readInt();
        if (data.readInt() == Atom.TYPE_data) {
            int flags = Atom.parseFullAtomFlags(data.readInt());
            String mimeType = flags == 13 ? "image/jpeg" : flags == 14 ? "image/png" : null;
            if (mimeType == null) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unrecognized cover art flags: ");
                stringBuilder.append(flags);
                Log.w(str, stringBuilder.toString());
                return null;
            }
            data.skipBytes(4);
            byte[] pictureData = new byte[(atomSize - 16)];
            data.readBytes(pictureData, 0, pictureData.length);
            return new ApicFrame(mimeType, null, 3, pictureData);
        }
        Log.w(TAG, "Failed to parse cover art attribute");
        return null;
    }

    private static Id3Frame parseInternalAttribute(ParsableByteArray data, int endPosition) {
        int dataAtomPosition = -1;
        String name = null;
        String domain = null;
        int dataAtomSize = -1;
        while (data.getPosition() < endPosition) {
            int atomPosition = data.getPosition();
            int atomSize = data.readInt();
            int atomType = data.readInt();
            data.skipBytes(4);
            if (atomType == Atom.TYPE_mean) {
                domain = data.readNullTerminatedString(atomSize - 12);
            } else if (atomType == Atom.TYPE_name) {
                name = data.readNullTerminatedString(atomSize - 12);
            } else {
                if (atomType == Atom.TYPE_data) {
                    dataAtomPosition = atomPosition;
                    dataAtomSize = atomSize;
                }
                data.skipBytes(atomSize - 12);
            }
        }
        if ("com.apple.iTunes".equals(domain) && "iTunSMPB".equals(name)) {
            if (dataAtomPosition != -1) {
                data.setPosition(dataAtomPosition);
                data.skipBytes(16);
                return new CommentFrame("und", name, data.readNullTerminatedString(dataAtomSize - 16));
            }
        }
        return null;
    }

    private static int parseUint8AttributeValue(ParsableByteArray data) {
        data.skipBytes(4);
        if (data.readInt() == Atom.TYPE_data) {
            data.skipBytes(8);
            return data.readUnsignedByte();
        }
        Log.w(TAG, "Failed to parse uint8 attribute value");
        return -1;
    }
}
