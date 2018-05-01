package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import org.telegram.messenger.exoplayer2.metadata.id3.ApicFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.CommentFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame;
import org.telegram.messenger.exoplayer2.metadata.id3.TextInformationFrame;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class MetadataUtil
{
  private static final String LANGUAGE_UNDEFINED = "und";
  private static final int SHORT_TYPE_ALBUM;
  private static final int SHORT_TYPE_ARTIST;
  private static final int SHORT_TYPE_COMMENT;
  private static final int SHORT_TYPE_COMPOSER_1;
  private static final int SHORT_TYPE_COMPOSER_2;
  private static final int SHORT_TYPE_ENCODER;
  private static final int SHORT_TYPE_GENRE;
  private static final int SHORT_TYPE_LYRICS;
  private static final int SHORT_TYPE_NAME_1 = Util.getIntegerCodeForString("nam");
  private static final int SHORT_TYPE_NAME_2 = Util.getIntegerCodeForString("trk");
  private static final int SHORT_TYPE_YEAR;
  private static final String[] STANDARD_GENRES = { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop" };
  private static final String TAG = "MetadataUtil";
  private static final int TYPE_ALBUM_ARTIST;
  private static final int TYPE_COMPILATION;
  private static final int TYPE_COVER_ART;
  private static final int TYPE_DISK_NUMBER;
  private static final int TYPE_GAPLESS_ALBUM;
  private static final int TYPE_GENRE;
  private static final int TYPE_GROUPING;
  private static final int TYPE_INTERNAL;
  private static final int TYPE_RATING;
  private static final int TYPE_SORT_ALBUM;
  private static final int TYPE_SORT_ALBUM_ARTIST;
  private static final int TYPE_SORT_ARTIST;
  private static final int TYPE_SORT_COMPOSER;
  private static final int TYPE_SORT_TRACK_NAME;
  private static final int TYPE_TEMPO;
  private static final int TYPE_TRACK_NUMBER;
  private static final int TYPE_TV_SHOW;
  private static final int TYPE_TV_SORT_SHOW;
  
  static
  {
    SHORT_TYPE_COMMENT = Util.getIntegerCodeForString("cmt");
    SHORT_TYPE_YEAR = Util.getIntegerCodeForString("day");
    SHORT_TYPE_ARTIST = Util.getIntegerCodeForString("ART");
    SHORT_TYPE_ENCODER = Util.getIntegerCodeForString("too");
    SHORT_TYPE_ALBUM = Util.getIntegerCodeForString("alb");
    SHORT_TYPE_COMPOSER_1 = Util.getIntegerCodeForString("com");
    SHORT_TYPE_COMPOSER_2 = Util.getIntegerCodeForString("wrt");
    SHORT_TYPE_LYRICS = Util.getIntegerCodeForString("lyr");
    SHORT_TYPE_GENRE = Util.getIntegerCodeForString("gen");
    TYPE_COVER_ART = Util.getIntegerCodeForString("covr");
    TYPE_GENRE = Util.getIntegerCodeForString("gnre");
    TYPE_GROUPING = Util.getIntegerCodeForString("grp");
    TYPE_DISK_NUMBER = Util.getIntegerCodeForString("disk");
    TYPE_TRACK_NUMBER = Util.getIntegerCodeForString("trkn");
    TYPE_TEMPO = Util.getIntegerCodeForString("tmpo");
    TYPE_COMPILATION = Util.getIntegerCodeForString("cpil");
    TYPE_ALBUM_ARTIST = Util.getIntegerCodeForString("aART");
    TYPE_SORT_TRACK_NAME = Util.getIntegerCodeForString("sonm");
    TYPE_SORT_ALBUM = Util.getIntegerCodeForString("soal");
    TYPE_SORT_ARTIST = Util.getIntegerCodeForString("soar");
    TYPE_SORT_ALBUM_ARTIST = Util.getIntegerCodeForString("soaa");
    TYPE_SORT_COMPOSER = Util.getIntegerCodeForString("soco");
    TYPE_RATING = Util.getIntegerCodeForString("rtng");
    TYPE_GAPLESS_ALBUM = Util.getIntegerCodeForString("pgap");
    TYPE_TV_SORT_SHOW = Util.getIntegerCodeForString("sosn");
    TYPE_TV_SHOW = Util.getIntegerCodeForString("tvsh");
    TYPE_INTERNAL = Util.getIntegerCodeForString("----");
  }
  
  private static CommentFrame parseCommentAttribute(int paramInt, ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readInt();
    if (paramParsableByteArray.readInt() == Atom.TYPE_data)
    {
      paramParsableByteArray.skipBytes(8);
      paramParsableByteArray = paramParsableByteArray.readNullTerminatedString(i - 16);
    }
    for (paramParsableByteArray = new CommentFrame("und", paramParsableByteArray, paramParsableByteArray);; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      Log.w("MetadataUtil", "Failed to parse comment attribute: " + Atom.getAtomTypeString(paramInt));
    }
  }
  
  private static ApicFrame parseCoverArt(ParsableByteArray paramParsableByteArray)
  {
    byte[] arrayOfByte = null;
    int i = paramParsableByteArray.readInt();
    int j;
    String str;
    if (paramParsableByteArray.readInt() == Atom.TYPE_data)
    {
      j = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
      if (j == 13)
      {
        str = "image/jpeg";
        if (str != null) {
          break label91;
        }
        Log.w("MetadataUtil", "Unrecognized cover art flags: " + j);
        paramParsableByteArray = arrayOfByte;
      }
    }
    for (;;)
    {
      return paramParsableByteArray;
      if (j == 14)
      {
        str = "image/png";
        break;
      }
      str = null;
      break;
      label91:
      paramParsableByteArray.skipBytes(4);
      arrayOfByte = new byte[i - 16];
      paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
      paramParsableByteArray = new ApicFrame(str, null, 3, arrayOfByte);
      continue;
      Log.w("MetadataUtil", "Failed to parse cover art attribute");
      paramParsableByteArray = arrayOfByte;
    }
  }
  
  /* Error */
  public static org.telegram.messenger.exoplayer2.metadata.Metadata.Entry parseIlstElement(ParsableByteArray paramParsableByteArray)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 550	org/telegram/messenger/exoplayer2/util/ParsableByteArray:getPosition	()I
    //   4: aload_0
    //   5: invokevirtual 479	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readInt	()I
    //   8: iadd
    //   9: istore_1
    //   10: aload_0
    //   11: invokevirtual 479	org/telegram/messenger/exoplayer2/util/ParsableByteArray:readInt	()I
    //   14: istore_2
    //   15: iload_2
    //   16: bipush 24
    //   18: ishr
    //   19: sipush 255
    //   22: iand
    //   23: istore_3
    //   24: iload_3
    //   25: sipush 169
    //   28: if_icmpeq +10 -> 38
    //   31: iload_3
    //   32: ldc_w 551
    //   35: if_icmpne +299 -> 334
    //   38: iload_2
    //   39: ldc_w 552
    //   42: iand
    //   43: istore_3
    //   44: iload_3
    //   45: getstatic 63	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_COMMENT	I
    //   48: if_icmpne +20 -> 68
    //   51: iload_2
    //   52: aload_0
    //   53: invokestatic 554	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseCommentAttribute	(ILorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/CommentFrame;
    //   56: astore 4
    //   58: aload_0
    //   59: iload_1
    //   60: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   63: aload 4
    //   65: astore_0
    //   66: aload_0
    //   67: areturn
    //   68: iload_3
    //   69: getstatic 55	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_NAME_1	I
    //   72: if_icmpeq +10 -> 82
    //   75: iload_3
    //   76: getstatic 59	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_NAME_2	I
    //   79: if_icmpne +24 -> 103
    //   82: iload_2
    //   83: ldc_w 559
    //   86: aload_0
    //   87: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   90: astore 4
    //   92: aload_0
    //   93: iload_1
    //   94: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   97: aload 4
    //   99: astore_0
    //   100: goto -34 -> 66
    //   103: iload_3
    //   104: getstatic 83	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_COMPOSER_1	I
    //   107: if_icmpeq +10 -> 117
    //   110: iload_3
    //   111: getstatic 87	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_COMPOSER_2	I
    //   114: if_icmpne +24 -> 138
    //   117: iload_2
    //   118: ldc_w 565
    //   121: aload_0
    //   122: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   125: astore 4
    //   127: aload_0
    //   128: iload_1
    //   129: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   132: aload 4
    //   134: astore_0
    //   135: goto -69 -> 66
    //   138: iload_3
    //   139: getstatic 67	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_YEAR	I
    //   142: if_icmpne +24 -> 166
    //   145: iload_2
    //   146: ldc_w 567
    //   149: aload_0
    //   150: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   153: astore 4
    //   155: aload_0
    //   156: iload_1
    //   157: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   160: aload 4
    //   162: astore_0
    //   163: goto -97 -> 66
    //   166: iload_3
    //   167: getstatic 71	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_ARTIST	I
    //   170: if_icmpne +24 -> 194
    //   173: iload_2
    //   174: ldc_w 569
    //   177: aload_0
    //   178: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   181: astore 4
    //   183: aload_0
    //   184: iload_1
    //   185: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   188: aload 4
    //   190: astore_0
    //   191: goto -125 -> 66
    //   194: iload_3
    //   195: getstatic 75	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_ENCODER	I
    //   198: if_icmpne +24 -> 222
    //   201: iload_2
    //   202: ldc_w 571
    //   205: aload_0
    //   206: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   209: astore 4
    //   211: aload_0
    //   212: iload_1
    //   213: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   216: aload 4
    //   218: astore_0
    //   219: goto -153 -> 66
    //   222: iload_3
    //   223: getstatic 79	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_ALBUM	I
    //   226: if_icmpne +24 -> 250
    //   229: iload_2
    //   230: ldc_w 573
    //   233: aload_0
    //   234: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   237: astore 4
    //   239: aload_0
    //   240: iload_1
    //   241: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   244: aload 4
    //   246: astore_0
    //   247: goto -181 -> 66
    //   250: iload_3
    //   251: getstatic 91	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_LYRICS	I
    //   254: if_icmpne +24 -> 278
    //   257: iload_2
    //   258: ldc_w 575
    //   261: aload_0
    //   262: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   265: astore 4
    //   267: aload_0
    //   268: iload_1
    //   269: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   272: aload 4
    //   274: astore_0
    //   275: goto -209 -> 66
    //   278: iload_3
    //   279: getstatic 95	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:SHORT_TYPE_GENRE	I
    //   282: if_icmpne +24 -> 306
    //   285: iload_2
    //   286: ldc_w 577
    //   289: aload_0
    //   290: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   293: astore 4
    //   295: aload_0
    //   296: iload_1
    //   297: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   300: aload 4
    //   302: astore_0
    //   303: goto -237 -> 66
    //   306: iload_3
    //   307: getstatic 107	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_GROUPING	I
    //   310: if_icmpne +497 -> 807
    //   313: iload_2
    //   314: ldc_w 579
    //   317: aload_0
    //   318: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   321: astore 4
    //   323: aload_0
    //   324: iload_1
    //   325: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   328: aload 4
    //   330: astore_0
    //   331: goto -265 -> 66
    //   334: iload_2
    //   335: getstatic 103	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_GENRE	I
    //   338: if_icmpne +20 -> 358
    //   341: aload_0
    //   342: invokestatic 583	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseStandardGenreAttribute	(Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   345: astore 4
    //   347: aload_0
    //   348: iload_1
    //   349: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   352: aload 4
    //   354: astore_0
    //   355: goto -289 -> 66
    //   358: iload_2
    //   359: getstatic 111	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_DISK_NUMBER	I
    //   362: if_icmpne +24 -> 386
    //   365: iload_2
    //   366: ldc_w 585
    //   369: aload_0
    //   370: invokestatic 588	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseIndexAndCountAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   373: astore 4
    //   375: aload_0
    //   376: iload_1
    //   377: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   380: aload 4
    //   382: astore_0
    //   383: goto -317 -> 66
    //   386: iload_2
    //   387: getstatic 115	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_TRACK_NUMBER	I
    //   390: if_icmpne +24 -> 414
    //   393: iload_2
    //   394: ldc_w 590
    //   397: aload_0
    //   398: invokestatic 588	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseIndexAndCountAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   401: astore 4
    //   403: aload_0
    //   404: iload_1
    //   405: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   408: aload 4
    //   410: astore_0
    //   411: goto -345 -> 66
    //   414: iload_2
    //   415: getstatic 119	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_TEMPO	I
    //   418: if_icmpne +26 -> 444
    //   421: iload_2
    //   422: ldc_w 592
    //   425: aload_0
    //   426: iconst_1
    //   427: iconst_0
    //   428: invokestatic 596	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseUint8Attribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;ZZ)Lorg/telegram/messenger/exoplayer2/metadata/id3/Id3Frame;
    //   431: astore 4
    //   433: aload_0
    //   434: iload_1
    //   435: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   438: aload 4
    //   440: astore_0
    //   441: goto -375 -> 66
    //   444: iload_2
    //   445: getstatic 123	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_COMPILATION	I
    //   448: if_icmpne +26 -> 474
    //   451: iload_2
    //   452: ldc_w 598
    //   455: aload_0
    //   456: iconst_1
    //   457: iconst_1
    //   458: invokestatic 596	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseUint8Attribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;ZZ)Lorg/telegram/messenger/exoplayer2/metadata/id3/Id3Frame;
    //   461: astore 4
    //   463: aload_0
    //   464: iload_1
    //   465: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   468: aload 4
    //   470: astore_0
    //   471: goto -405 -> 66
    //   474: iload_2
    //   475: getstatic 99	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_COVER_ART	I
    //   478: if_icmpne +20 -> 498
    //   481: aload_0
    //   482: invokestatic 600	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseCoverArt	(Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/ApicFrame;
    //   485: astore 4
    //   487: aload_0
    //   488: iload_1
    //   489: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   492: aload 4
    //   494: astore_0
    //   495: goto -429 -> 66
    //   498: iload_2
    //   499: getstatic 127	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_ALBUM_ARTIST	I
    //   502: if_icmpne +24 -> 526
    //   505: iload_2
    //   506: ldc_w 602
    //   509: aload_0
    //   510: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   513: astore 4
    //   515: aload_0
    //   516: iload_1
    //   517: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   520: aload 4
    //   522: astore_0
    //   523: goto -457 -> 66
    //   526: iload_2
    //   527: getstatic 131	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_SORT_TRACK_NAME	I
    //   530: if_icmpne +24 -> 554
    //   533: iload_2
    //   534: ldc_w 604
    //   537: aload_0
    //   538: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   541: astore 4
    //   543: aload_0
    //   544: iload_1
    //   545: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   548: aload 4
    //   550: astore_0
    //   551: goto -485 -> 66
    //   554: iload_2
    //   555: getstatic 135	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_SORT_ALBUM	I
    //   558: if_icmpne +24 -> 582
    //   561: iload_2
    //   562: ldc_w 606
    //   565: aload_0
    //   566: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   569: astore 4
    //   571: aload_0
    //   572: iload_1
    //   573: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   576: aload 4
    //   578: astore_0
    //   579: goto -513 -> 66
    //   582: iload_2
    //   583: getstatic 139	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_SORT_ARTIST	I
    //   586: if_icmpne +24 -> 610
    //   589: iload_2
    //   590: ldc_w 608
    //   593: aload_0
    //   594: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   597: astore 4
    //   599: aload_0
    //   600: iload_1
    //   601: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   604: aload 4
    //   606: astore_0
    //   607: goto -541 -> 66
    //   610: iload_2
    //   611: getstatic 143	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_SORT_ALBUM_ARTIST	I
    //   614: if_icmpne +24 -> 638
    //   617: iload_2
    //   618: ldc_w 610
    //   621: aload_0
    //   622: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   625: astore 4
    //   627: aload_0
    //   628: iload_1
    //   629: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   632: aload 4
    //   634: astore_0
    //   635: goto -569 -> 66
    //   638: iload_2
    //   639: getstatic 147	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_SORT_COMPOSER	I
    //   642: if_icmpne +24 -> 666
    //   645: iload_2
    //   646: ldc_w 612
    //   649: aload_0
    //   650: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   653: astore 4
    //   655: aload_0
    //   656: iload_1
    //   657: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   660: aload 4
    //   662: astore_0
    //   663: goto -597 -> 66
    //   666: iload_2
    //   667: getstatic 151	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_RATING	I
    //   670: if_icmpne +26 -> 696
    //   673: iload_2
    //   674: ldc_w 614
    //   677: aload_0
    //   678: iconst_0
    //   679: iconst_0
    //   680: invokestatic 596	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseUint8Attribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;ZZ)Lorg/telegram/messenger/exoplayer2/metadata/id3/Id3Frame;
    //   683: astore 4
    //   685: aload_0
    //   686: iload_1
    //   687: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   690: aload 4
    //   692: astore_0
    //   693: goto -627 -> 66
    //   696: iload_2
    //   697: getstatic 155	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_GAPLESS_ALBUM	I
    //   700: if_icmpne +26 -> 726
    //   703: iload_2
    //   704: ldc_w 616
    //   707: aload_0
    //   708: iconst_0
    //   709: iconst_1
    //   710: invokestatic 596	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseUint8Attribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;ZZ)Lorg/telegram/messenger/exoplayer2/metadata/id3/Id3Frame;
    //   713: astore 4
    //   715: aload_0
    //   716: iload_1
    //   717: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   720: aload 4
    //   722: astore_0
    //   723: goto -657 -> 66
    //   726: iload_2
    //   727: getstatic 159	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_TV_SORT_SHOW	I
    //   730: if_icmpne +24 -> 754
    //   733: iload_2
    //   734: ldc_w 618
    //   737: aload_0
    //   738: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   741: astore 4
    //   743: aload_0
    //   744: iload_1
    //   745: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   748: aload 4
    //   750: astore_0
    //   751: goto -685 -> 66
    //   754: iload_2
    //   755: getstatic 163	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_TV_SHOW	I
    //   758: if_icmpne +24 -> 782
    //   761: iload_2
    //   762: ldc_w 620
    //   765: aload_0
    //   766: invokestatic 563	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseTextAttribute	(ILjava/lang/String;Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;)Lorg/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame;
    //   769: astore 4
    //   771: aload_0
    //   772: iload_1
    //   773: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   776: aload 4
    //   778: astore_0
    //   779: goto -713 -> 66
    //   782: iload_2
    //   783: getstatic 167	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:TYPE_INTERNAL	I
    //   786: if_icmpne +21 -> 807
    //   789: aload_0
    //   790: iload_1
    //   791: invokestatic 624	org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil:parseInternalAttribute	(Lorg/telegram/messenger/exoplayer2/util/ParsableByteArray;I)Lorg/telegram/messenger/exoplayer2/metadata/id3/Id3Frame;
    //   794: astore 4
    //   796: aload_0
    //   797: iload_1
    //   798: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   801: aload 4
    //   803: astore_0
    //   804: goto -738 -> 66
    //   807: new 499	java/lang/StringBuilder
    //   810: astore 4
    //   812: aload 4
    //   814: invokespecial 500	java/lang/StringBuilder:<init>	()V
    //   817: ldc 25
    //   819: aload 4
    //   821: ldc_w 626
    //   824: invokevirtual 506	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   827: iload_2
    //   828: invokestatic 509	org/telegram/messenger/exoplayer2/extractor/mp4/Atom:getAtomTypeString	(I)Ljava/lang/String;
    //   831: invokevirtual 506	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   834: invokevirtual 513	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   837: invokestatic 629	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   840: pop
    //   841: aconst_null
    //   842: astore 4
    //   844: aload_0
    //   845: iload_1
    //   846: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   849: aload 4
    //   851: astore_0
    //   852: goto -786 -> 66
    //   855: astore 4
    //   857: aload_0
    //   858: iload_1
    //   859: invokevirtual 557	org/telegram/messenger/exoplayer2/util/ParsableByteArray:setPosition	(I)V
    //   862: aload 4
    //   864: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	865	0	paramParsableByteArray	ParsableByteArray
    //   9	850	1	i	int
    //   14	814	2	j	int
    //   23	288	3	k	int
    //   56	794	4	localObject1	Object
    //   855	8	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   44	58	855	finally
    //   68	82	855	finally
    //   82	92	855	finally
    //   103	117	855	finally
    //   117	127	855	finally
    //   138	155	855	finally
    //   166	183	855	finally
    //   194	211	855	finally
    //   222	239	855	finally
    //   250	267	855	finally
    //   278	295	855	finally
    //   306	323	855	finally
    //   334	347	855	finally
    //   358	375	855	finally
    //   386	403	855	finally
    //   414	433	855	finally
    //   444	463	855	finally
    //   474	487	855	finally
    //   498	515	855	finally
    //   526	543	855	finally
    //   554	571	855	finally
    //   582	599	855	finally
    //   610	627	855	finally
    //   638	655	855	finally
    //   666	685	855	finally
    //   696	715	855	finally
    //   726	743	855	finally
    //   754	771	855	finally
    //   782	796	855	finally
    //   807	841	855	finally
  }
  
  private static TextInformationFrame parseIndexAndCountAttribute(int paramInt, String paramString, ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readInt();
    if ((paramParsableByteArray.readInt() == Atom.TYPE_data) && (i >= 22))
    {
      paramParsableByteArray.skipBytes(10);
      i = paramParsableByteArray.readUnsignedShort();
      if (i > 0)
      {
        String str = "" + i;
        paramInt = paramParsableByteArray.readUnsignedShort();
        paramParsableByteArray = str;
        if (paramInt > 0) {
          paramParsableByteArray = str + "/" + paramInt;
        }
      }
    }
    for (paramString = new TextInformationFrame(paramString, null, paramParsableByteArray);; paramString = null)
    {
      return paramString;
      Log.w("MetadataUtil", "Failed to parse index/count attribute: " + Atom.getAtomTypeString(paramInt));
    }
  }
  
  private static Id3Frame parseInternalAttribute(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    String str1 = null;
    String str2 = null;
    int i = -1;
    int j = -1;
    while (paramParsableByteArray.getPosition() < paramInt)
    {
      int k = paramParsableByteArray.getPosition();
      int m = paramParsableByteArray.readInt();
      int n = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      if (n == Atom.TYPE_mean)
      {
        str1 = paramParsableByteArray.readNullTerminatedString(m - 12);
      }
      else if (n == Atom.TYPE_name)
      {
        str2 = paramParsableByteArray.readNullTerminatedString(m - 12);
      }
      else
      {
        if (n == Atom.TYPE_data)
        {
          i = k;
          j = m;
        }
        paramParsableByteArray.skipBytes(m - 12);
      }
    }
    if ((!"com.apple.iTunes".equals(str1)) || (!"iTunSMPB".equals(str2)) || (i == -1)) {}
    for (paramParsableByteArray = null;; paramParsableByteArray = new CommentFrame("und", str2, paramParsableByteArray.readNullTerminatedString(j - 16)))
    {
      return paramParsableByteArray;
      paramParsableByteArray.setPosition(i);
      paramParsableByteArray.skipBytes(16);
    }
  }
  
  private static TextInformationFrame parseStandardGenreAttribute(ParsableByteArray paramParsableByteArray)
  {
    int i = parseUint8AttributeValue(paramParsableByteArray);
    if ((i > 0) && (i <= STANDARD_GENRES.length))
    {
      paramParsableByteArray = STANDARD_GENRES[(i - 1)];
      if (paramParsableByteArray == null) {
        break label49;
      }
    }
    for (paramParsableByteArray = new TextInformationFrame("TCON", null, paramParsableByteArray);; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      paramParsableByteArray = null;
      break;
      label49:
      Log.w("MetadataUtil", "Failed to parse standard genre code");
    }
  }
  
  private static TextInformationFrame parseTextAttribute(int paramInt, String paramString, ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readInt();
    if (paramParsableByteArray.readInt() == Atom.TYPE_data) {
      paramParsableByteArray.skipBytes(8);
    }
    for (paramString = new TextInformationFrame(paramString, null, paramParsableByteArray.readNullTerminatedString(i - 16));; paramString = null)
    {
      return paramString;
      Log.w("MetadataUtil", "Failed to parse text attribute: " + Atom.getAtomTypeString(paramInt));
    }
  }
  
  private static Id3Frame parseUint8Attribute(int paramInt, String paramString, ParsableByteArray paramParsableByteArray, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = parseUint8AttributeValue(paramParsableByteArray);
    int j = i;
    if (paramBoolean2) {
      j = Math.min(1, i);
    }
    if (j >= 0) {
      if (paramBoolean1) {
        paramString = new TextInformationFrame(paramString, null, Integer.toString(j));
      }
    }
    for (;;)
    {
      return paramString;
      paramString = new CommentFrame("und", paramString, Integer.toString(j));
      continue;
      Log.w("MetadataUtil", "Failed to parse uint8 attribute: " + Atom.getAtomTypeString(paramInt));
      paramString = null;
    }
  }
  
  private static int parseUint8AttributeValue(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.skipBytes(4);
    if (paramParsableByteArray.readInt() == Atom.TYPE_data) {
      paramParsableByteArray.skipBytes(8);
    }
    for (int i = paramParsableByteArray.readUnsignedByte();; i = -1)
    {
      return i;
      Log.w("MetadataUtil", "Failed to parse uint8 attribute value");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/MetadataUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */