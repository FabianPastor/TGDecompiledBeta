package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.source.UnrecognizedInputFormatException;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class HlsPlaylistParser
  implements ParsingLoadable.Parser<HlsPlaylist>
{
  private static final String ATTR_CLOSED_CAPTIONS_NONE = "CLOSED-CAPTIONS=NONE";
  private static final String BOOLEAN_FALSE = "NO";
  private static final String BOOLEAN_TRUE = "YES";
  private static final String KEYFORMAT_IDENTITY = "identity";
  private static final String KEYFORMAT_WIDEVINE_PSSH_BINARY = "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed";
  private static final String KEYFORMAT_WIDEVINE_PSSH_JSON = "com.widevine";
  private static final String METHOD_AES_128 = "AES-128";
  private static final String METHOD_NONE = "NONE";
  private static final String METHOD_SAMPLE_AES = "SAMPLE-AES";
  private static final String METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC";
  private static final String METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR";
  private static final String PLAYLIST_HEADER = "#EXTM3U";
  private static final Pattern REGEX_ATTR_BYTERANGE;
  private static final Pattern REGEX_AUDIO;
  private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
  private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
  private static final Pattern REGEX_BANDWIDTH;
  private static final Pattern REGEX_BYTERANGE;
  private static final Pattern REGEX_CODECS;
  private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
  private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
  private static final Pattern REGEX_FRAME_RATE;
  private static final Pattern REGEX_GROUP_ID;
  private static final Pattern REGEX_INSTREAM_ID;
  private static final Pattern REGEX_IV;
  private static final Pattern REGEX_KEYFORMAT;
  private static final Pattern REGEX_LANGUAGE;
  private static final Pattern REGEX_MEDIA_DURATION;
  private static final Pattern REGEX_MEDIA_SEQUENCE;
  private static final Pattern REGEX_METHOD;
  private static final Pattern REGEX_NAME;
  private static final Pattern REGEX_PLAYLIST_TYPE;
  private static final Pattern REGEX_RESOLUTION;
  private static final Pattern REGEX_TARGET_DURATION;
  private static final Pattern REGEX_TIME_OFFSET;
  private static final Pattern REGEX_TYPE;
  private static final Pattern REGEX_URI;
  private static final Pattern REGEX_VERSION;
  private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
  private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
  private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
  private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
  private static final String TAG_INDEPENDENT_SEGMENTS = "#EXT-X-INDEPENDENT-SEGMENTS";
  private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
  private static final String TAG_KEY = "#EXT-X-KEY";
  private static final String TAG_MEDIA = "#EXT-X-MEDIA";
  private static final String TAG_MEDIA_DURATION = "#EXTINF";
  private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
  private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
  private static final String TAG_PREFIX = "#EXT";
  private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
  private static final String TAG_START = "#EXT-X-START";
  private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
  private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
  private static final String TAG_VERSION = "#EXT-X-VERSION";
  private static final String TYPE_AUDIO = "AUDIO";
  private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
  private static final String TYPE_SUBTITLES = "SUBTITLES";
  private static final String TYPE_VIDEO = "VIDEO";
  
  static
  {
    REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)");
    REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
  }
  
  private static boolean checkPlaylistHeader(BufferedReader paramBufferedReader)
    throws IOException
  {
    boolean bool1 = false;
    int i = paramBufferedReader.read();
    int j = i;
    boolean bool2;
    if (i == 239)
    {
      bool2 = bool1;
      if (paramBufferedReader.read() == 187)
      {
        if (paramBufferedReader.read() == 191) {
          break label45;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      label45:
      j = paramBufferedReader.read();
      j = skipIgnorableWhitespace(paramBufferedReader, true, j);
      int k = "#EXTM3U".length();
      for (i = 0;; i++)
      {
        if (i >= k) {
          break label96;
        }
        bool2 = bool1;
        if (j != "#EXTM3U".charAt(i)) {
          break;
        }
        j = paramBufferedReader.read();
      }
      label96:
      bool2 = Util.isLinebreak(skipIgnorableWhitespace(paramBufferedReader, false, j));
    }
  }
  
  private static Pattern compileBooleanAttrPattern(String paramString)
  {
    return Pattern.compile(paramString + "=(" + "NO" + "|" + "YES" + ")");
  }
  
  private static boolean parseBooleanAttribute(String paramString, Pattern paramPattern, boolean paramBoolean)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find()) {
      paramBoolean = paramString.group(1).equals("YES");
    }
    return paramBoolean;
  }
  
  private static double parseDoubleAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    return Double.parseDouble(parseStringAttr(paramString, paramPattern));
  }
  
  private static int parseIntAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    return Integer.parseInt(parseStringAttr(paramString, paramPattern));
  }
  
  private static HlsMasterPlaylist parseMasterPlaylist(LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    Object localObject1 = new HashSet();
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    ArrayList localArrayList5 = new ArrayList();
    Object localObject2 = null;
    Object localObject3 = null;
    int i = 0;
    String str1;
    int k;
    String str2;
    Object localObject4;
    int m;
    while (paramLineIterator.hasNext())
    {
      str1 = paramLineIterator.next();
      if (str1.startsWith("#EXT")) {
        localArrayList5.add(str1);
      }
      if (str1.startsWith("#EXT-X-MEDIA"))
      {
        localArrayList4.add(str1);
      }
      else if (str1.startsWith("#EXT-X-STREAM-INF"))
      {
        int j = i | str1.contains("CLOSED-CAPTIONS=NONE");
        k = parseIntAttr(str1, REGEX_BANDWIDTH);
        str2 = parseOptionalStringAttr(str1, REGEX_AVERAGE_BANDWIDTH);
        if (str2 != null) {
          k = Integer.parseInt(str2);
        }
        str2 = parseOptionalStringAttr(str1, REGEX_CODECS);
        localObject4 = parseOptionalStringAttr(str1, REGEX_RESOLUTION);
        if (localObject4 != null)
        {
          localObject4 = ((String)localObject4).split("x");
          m = Integer.parseInt(localObject4[0]);
          i = Integer.parseInt(localObject4[1]);
          if (m > 0)
          {
            n = i;
            if (i > 0) {}
          }
          else
          {
            m = -1;
          }
        }
        for (n = -1;; n = -1)
        {
          float f = -1.0F;
          localObject4 = parseOptionalStringAttr(str1, REGEX_FRAME_RATE);
          if (localObject4 != null) {
            f = Float.parseFloat((String)localObject4);
          }
          str1 = parseOptionalStringAttr(str1, REGEX_AUDIO);
          if ((str1 != null) && (str2 != null)) {
            localHashMap.put(str1, Util.getCodecsOfType(str2, 1));
          }
          str1 = paramLineIterator.next();
          i = j;
          if (!((HashSet)localObject1).add(str1)) {
            break;
          }
          localArrayList1.add(new HlsMasterPlaylist.HlsUrl(str1, Format.createVideoContainerFormat(Integer.toString(localArrayList1.size()), "application/x-mpegURL", null, str2, k, m, n, f, null, 0)));
          i = j;
          break;
          m = -1;
        }
      }
    }
    int n = 0;
    paramLineIterator = (LineIterator)localObject3;
    if (n < localArrayList4.size())
    {
      localObject3 = (String)localArrayList4.get(n);
      k = parseSelectionFlags((String)localObject3);
      localObject1 = parseOptionalStringAttr((String)localObject3, REGEX_URI);
      str2 = parseStringAttr((String)localObject3, REGEX_NAME);
      str1 = parseOptionalStringAttr((String)localObject3, REGEX_LANGUAGE);
      localObject4 = parseOptionalStringAttr((String)localObject3, REGEX_GROUP_ID);
      String str3 = parseStringAttr((String)localObject3, REGEX_TYPE);
      m = -1;
      switch (str3.hashCode())
      {
      default: 
        label512:
        switch (m)
        {
        }
        break;
      }
      for (;;)
      {
        n++;
        break;
        if (!str3.equals("AUDIO")) {
          break label512;
        }
        m = 0;
        break label512;
        if (!str3.equals("SUBTITLES")) {
          break label512;
        }
        m = 1;
        break label512;
        if (!str3.equals("CLOSED-CAPTIONS")) {
          break label512;
        }
        m = 2;
        break label512;
        localObject4 = (String)localHashMap.get(localObject4);
        if (localObject4 != null) {}
        for (localObject3 = MimeTypes.getMediaMimeType((String)localObject4);; localObject3 = null)
        {
          localObject3 = Format.createAudioContainerFormat(str2, "application/x-mpegURL", (String)localObject3, (String)localObject4, -1, -1, -1, null, k, str1);
          if (localObject1 != null) {
            break label656;
          }
          localObject2 = localObject3;
          break;
        }
        label656:
        localArrayList2.add(new HlsMasterPlaylist.HlsUrl((String)localObject1, (Format)localObject3));
        continue;
        localArrayList3.add(new HlsMasterPlaylist.HlsUrl((String)localObject1, Format.createTextContainerFormat(str2, "application/x-mpegURL", "text/vtt", null, -1, k, str1)));
      }
      localObject3 = parseStringAttr((String)localObject3, REGEX_INSTREAM_ID);
      if (((String)localObject3).startsWith("CC")) {
        localObject1 = "application/cea-608";
      }
      for (m = Integer.parseInt(((String)localObject3).substring(2));; m = Integer.parseInt(((String)localObject3).substring(7)))
      {
        localObject3 = paramLineIterator;
        if (paramLineIterator == null) {
          localObject3 = new ArrayList();
        }
        ((List)localObject3).add(Format.createTextContainerFormat(str2, null, (String)localObject1, null, -1, k, str1, m));
        paramLineIterator = (LineIterator)localObject3;
        break;
        localObject1 = "application/cea-708";
      }
    }
    if (i != 0) {
      paramLineIterator = Collections.emptyList();
    }
    return new HlsMasterPlaylist(paramString, localArrayList5, localArrayList1, localArrayList2, localArrayList3, (Format)localObject2, paramLineIterator);
  }
  
  private static HlsMediaPlaylist parseMediaPlaylist(LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    int i = 0;
    long l1 = -9223372036854775807L;
    int j = 0;
    int k = 1;
    long l2 = -9223372036854775807L;
    boolean bool1 = false;
    boolean bool2 = false;
    Object localObject1 = null;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    long l3 = 0L;
    boolean bool3 = false;
    int m = 0;
    int n = 0;
    long l4 = 0L;
    long l5 = 0L;
    long l6 = 0L;
    long l7 = -1L;
    int i1 = 0;
    Object localObject2 = null;
    Object localObject3 = null;
    DrmInitData localDrmInitData = null;
    while (paramLineIterator.hasNext())
    {
      Object localObject4 = paramLineIterator.next();
      if (((String)localObject4).startsWith("#EXT")) {
        localArrayList2.add(localObject4);
      }
      Object localObject5;
      if (((String)localObject4).startsWith("#EXT-X-PLAYLIST-TYPE"))
      {
        localObject5 = parseStringAttr((String)localObject4, REGEX_PLAYLIST_TYPE);
        if ("VOD".equals(localObject5)) {
          i = 1;
        } else if ("EVENT".equals(localObject5)) {
          i = 2;
        }
      }
      else if (((String)localObject4).startsWith("#EXT-X-START"))
      {
        l1 = (parseDoubleAttr((String)localObject4, REGEX_TIME_OFFSET) * 1000000.0D);
      }
      else
      {
        long l8;
        if (((String)localObject4).startsWith("#EXT-X-MAP"))
        {
          localObject5 = parseStringAttr((String)localObject4, REGEX_URI);
          localObject1 = parseOptionalStringAttr((String)localObject4, REGEX_ATTR_BYTERANGE);
          l8 = l6;
          if (localObject1 != null)
          {
            localObject1 = ((String)localObject1).split("@");
            long l9 = Long.parseLong(localObject1[0]);
            l8 = l6;
            l7 = l9;
            if (localObject1.length > 1)
            {
              l8 = Long.parseLong(localObject1[1]);
              l7 = l9;
            }
          }
          localObject1 = new HlsMediaPlaylist.Segment((String)localObject5, l8, l7);
          l6 = 0L;
          l7 = -1L;
        }
        else if (((String)localObject4).startsWith("#EXT-X-TARGETDURATION"))
        {
          l2 = parseIntAttr((String)localObject4, REGEX_TARGET_DURATION) * 1000000L;
        }
        else if (((String)localObject4).startsWith("#EXT-X-MEDIA-SEQUENCE"))
        {
          j = parseIntAttr((String)localObject4, REGEX_MEDIA_SEQUENCE);
          i1 = j;
        }
        else if (((String)localObject4).startsWith("#EXT-X-VERSION"))
        {
          k = parseIntAttr((String)localObject4, REGEX_VERSION);
        }
        else if (((String)localObject4).startsWith("#EXTINF"))
        {
          l3 = (parseDoubleAttr((String)localObject4, REGEX_MEDIA_DURATION) * 1000000.0D);
        }
        else if (((String)localObject4).startsWith("#EXT-X-KEY"))
        {
          String str1 = parseOptionalStringAttr((String)localObject4, REGEX_METHOD);
          String str2 = parseOptionalStringAttr((String)localObject4, REGEX_KEYFORMAT);
          Object localObject6 = null;
          localObject3 = null;
          localObject2 = localObject6;
          if (!"NONE".equals(str1))
          {
            localObject5 = parseOptionalStringAttr((String)localObject4, REGEX_IV);
            if (("identity".equals(str2)) || (str2 == null))
            {
              localObject2 = localObject6;
              localObject3 = localObject5;
              if ("AES-128".equals(str1))
              {
                localObject2 = parseStringAttr((String)localObject4, REGEX_URI);
                localObject3 = localObject5;
              }
            }
            else
            {
              localObject2 = localObject6;
              localObject3 = localObject5;
              if (str1 != null)
              {
                localObject4 = parseWidevineSchemeData((String)localObject4, str2);
                localObject2 = localObject6;
                localObject3 = localObject5;
                if (localObject4 != null)
                {
                  if (("SAMPLE-AES-CENC".equals(str1)) || ("SAMPLE-AES-CTR".equals(str1))) {}
                  for (localObject3 = "cenc";; localObject3 = "cbcs")
                  {
                    localDrmInitData = new DrmInitData((String)localObject3, new DrmInitData.SchemeData[] { localObject4 });
                    localObject2 = localObject6;
                    localObject3 = localObject5;
                    break;
                  }
                }
              }
            }
          }
        }
        else if (((String)localObject4).startsWith("#EXT-X-BYTERANGE"))
        {
          localObject5 = parseStringAttr((String)localObject4, REGEX_BYTERANGE).split("@");
          l8 = Long.parseLong(localObject5[0]);
          l7 = l8;
          if (localObject5.length > 1)
          {
            l6 = Long.parseLong(localObject5[1]);
            l7 = l8;
          }
        }
        else if (((String)localObject4).startsWith("#EXT-X-DISCONTINUITY-SEQUENCE"))
        {
          bool3 = true;
          m = Integer.parseInt(((String)localObject4).substring(((String)localObject4).indexOf(':') + 1));
        }
        else if (((String)localObject4).equals("#EXT-X-DISCONTINUITY"))
        {
          n++;
        }
        else if (((String)localObject4).startsWith("#EXT-X-PROGRAM-DATE-TIME"))
        {
          if (l4 == 0L) {
            l4 = C.msToUs(Util.parseXsDateTime(((String)localObject4).substring(((String)localObject4).indexOf(':') + 1))) - l5;
          }
        }
        else
        {
          if (!((String)localObject4).startsWith("#"))
          {
            if (localObject2 == null) {
              localObject5 = null;
            }
            for (;;)
            {
              i1++;
              if (l7 == -1L) {
                l6 = 0L;
              }
              localArrayList1.add(new HlsMediaPlaylist.Segment((String)localObject4, l3, n, l5, (String)localObject2, (String)localObject5, l6, l7));
              l5 += l3;
              l8 = 0L;
              l3 = l6;
              if (l7 != -1L) {
                l3 = l6 + l7;
              }
              l7 = -1L;
              l6 = l3;
              l3 = l8;
              break;
              if (localObject3 != null) {
                localObject5 = localObject3;
              } else {
                localObject5 = Integer.toHexString(i1);
              }
            }
          }
          if (((String)localObject4).equals("#EXT-X-INDEPENDENT-SEGMENTS")) {
            bool1 = true;
          } else if (((String)localObject4).equals("#EXT-X-ENDLIST")) {
            bool2 = true;
          }
        }
      }
    }
    if (l4 != 0L) {}
    for (boolean bool4 = true;; bool4 = false) {
      return new HlsMediaPlaylist(i, paramString, localArrayList2, l1, l4, bool3, m, j, k, l2, bool1, bool2, bool4, localDrmInitData, (HlsMediaPlaylist.Segment)localObject1, localArrayList1);
    }
  }
  
  private static String parseOptionalStringAttr(String paramString, Pattern paramPattern)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find()) {}
    for (paramString = paramString.group(1);; paramString = null) {
      return paramString;
    }
  }
  
  private static int parseSelectionFlags(String paramString)
  {
    int i = 0;
    int j;
    if (parseBooleanAttribute(paramString, REGEX_DEFAULT, false))
    {
      j = 1;
      if (!parseBooleanAttribute(paramString, REGEX_FORCED, false)) {
        break label52;
      }
    }
    label52:
    for (int k = 2;; k = 0)
    {
      if (parseBooleanAttribute(paramString, REGEX_AUTOSELECT, false)) {
        i = 4;
      }
      return j | k | i;
      j = 0;
      break;
    }
  }
  
  private static String parseStringAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    Matcher localMatcher = paramPattern.matcher(paramString);
    if ((localMatcher.find()) && (localMatcher.groupCount() == 1)) {
      return localMatcher.group(1);
    }
    throw new ParserException("Couldn't match " + paramPattern.pattern() + " in " + paramString);
  }
  
  private static DrmInitData.SchemeData parseWidevineSchemeData(String paramString1, String paramString2)
    throws ParserException
  {
    if ("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed".equals(paramString2)) {
      paramString1 = parseStringAttr(paramString1, REGEX_URI);
    }
    for (paramString1 = new DrmInitData.SchemeData(C.WIDEVINE_UUID, "video/mp4", Base64.decode(paramString1.substring(paramString1.indexOf(',')), 0));; paramString1 = null) {
      for (;;)
      {
        return paramString1;
        if ("com.widevine".equals(paramString2)) {
          try
          {
            paramString1 = new DrmInitData.SchemeData(C.WIDEVINE_UUID, "hls", paramString1.getBytes("UTF-8"));
          }
          catch (UnsupportedEncodingException paramString1)
          {
            throw new ParserException(paramString1);
          }
        }
      }
    }
  }
  
  private static int skipIgnorableWhitespace(BufferedReader paramBufferedReader, boolean paramBoolean, int paramInt)
    throws IOException
  {
    while ((paramInt != -1) && (Character.isWhitespace(paramInt)) && ((paramBoolean) || (!Util.isLinebreak(paramInt)))) {
      paramInt = paramBufferedReader.read();
    }
    return paramInt;
  }
  
  public HlsPlaylist parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    paramInputStream = new BufferedReader(new InputStreamReader(paramInputStream));
    Object localObject1 = new ArrayDeque();
    try
    {
      if (!checkPlaylistHeader(paramInputStream))
      {
        localObject1 = new org/telegram/messenger/exoplayer2/source/UnrecognizedInputFormatException;
        ((UnrecognizedInputFormatException)localObject1).<init>("Input does not start with the #EXTM3U header.", paramUri);
        throw ((Throwable)localObject1);
      }
    }
    finally
    {
      Util.closeQuietly(paramInputStream);
    }
    for (;;)
    {
      Object localObject2 = paramInputStream.readLine();
      if (localObject2 == null) {
        break;
      }
      localObject2 = ((String)localObject2).trim();
      if (!((String)localObject2).isEmpty())
      {
        if (((String)localObject2).startsWith("#EXT-X-STREAM-INF"))
        {
          ((Queue)localObject1).add(localObject2);
          localObject2 = new org/telegram/messenger/exoplayer2/source/hls/playlist/HlsPlaylistParser$LineIterator;
          ((LineIterator)localObject2).<init>((Queue)localObject1, paramInputStream);
          paramUri = parseMasterPlaylist((LineIterator)localObject2, paramUri.toString());
          Util.closeQuietly(paramInputStream);
        }
        for (;;)
        {
          return paramUri;
          if ((!((String)localObject2).startsWith("#EXT-X-TARGETDURATION")) && (!((String)localObject2).startsWith("#EXT-X-MEDIA-SEQUENCE")) && (!((String)localObject2).startsWith("#EXTINF")) && (!((String)localObject2).startsWith("#EXT-X-KEY")) && (!((String)localObject2).startsWith("#EXT-X-BYTERANGE")) && (!((String)localObject2).equals("#EXT-X-DISCONTINUITY")) && (!((String)localObject2).equals("#EXT-X-DISCONTINUITY-SEQUENCE")) && (!((String)localObject2).equals("#EXT-X-ENDLIST"))) {
            break;
          }
          ((Queue)localObject1).add(localObject2);
          localObject2 = new org/telegram/messenger/exoplayer2/source/hls/playlist/HlsPlaylistParser$LineIterator;
          ((LineIterator)localObject2).<init>((Queue)localObject1, paramInputStream);
          paramUri = parseMediaPlaylist((LineIterator)localObject2, paramUri.toString());
          Util.closeQuietly(paramInputStream);
        }
        ((Queue)localObject1).add(localObject2);
      }
    }
    Util.closeQuietly(paramInputStream);
    throw new ParserException("Failed to parse the playlist, could not identify any tags.");
  }
  
  private static class LineIterator
  {
    private final Queue<String> extraLines;
    private String next;
    private final BufferedReader reader;
    
    public LineIterator(Queue<String> paramQueue, BufferedReader paramBufferedReader)
    {
      this.extraLines = paramQueue;
      this.reader = paramBufferedReader;
    }
    
    public boolean hasNext()
      throws IOException
    {
      boolean bool;
      if (this.next != null) {
        bool = true;
      }
      for (;;)
      {
        return bool;
        if (!this.extraLines.isEmpty())
        {
          this.next = ((String)this.extraLines.poll());
          bool = true;
        }
        else
        {
          do
          {
            String str = this.reader.readLine();
            this.next = str;
            if (str == null) {
              break;
            }
            this.next = this.next.trim();
          } while (this.next.isEmpty());
          bool = true;
          continue;
          bool = false;
        }
      }
    }
    
    public String next()
      throws IOException
    {
      String str = null;
      if (hasNext())
      {
        str = this.next;
        this.next = null;
      }
      return str;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/playlist/HlsPlaylistParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */