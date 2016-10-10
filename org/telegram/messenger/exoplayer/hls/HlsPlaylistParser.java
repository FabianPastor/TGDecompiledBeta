package org.telegram.messenger.exoplayer.hls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.upstream.UriLoadable.Parser;

public final class HlsPlaylistParser
  implements UriLoadable.Parser<HlsPlaylist>
{
  private static final String AUDIO_TYPE = "AUDIO";
  private static final String BANDWIDTH_ATTR = "BANDWIDTH";
  private static final Pattern BANDWIDTH_ATTR_REGEX = Pattern.compile("BANDWIDTH=(\\d+)\\b");
  private static final Pattern BYTERANGE_REGEX;
  private static final String BYTERANGE_TAG = "#EXT-X-BYTERANGE";
  private static final String CLOSED_CAPTIONS_TYPE = "CLOSED-CAPTIONS";
  private static final String CODECS_ATTR = "CODECS";
  private static final Pattern CODECS_ATTR_REGEX = Pattern.compile("CODECS=\"(.+?)\"");
  private static final String DISCONTINUITY_SEQUENCE_TAG = "#EXT-X-DISCONTINUITY-SEQUENCE";
  private static final String DISCONTINUITY_TAG = "#EXT-X-DISCONTINUITY";
  private static final String ENDLIST_TAG = "#EXT-X-ENDLIST";
  private static final String INSTREAM_ID_ATTR = "INSTREAM-ID";
  private static final Pattern INSTREAM_ID_ATTR_REGEX = Pattern.compile("INSTREAM-ID=\"(.+?)\"");
  private static final String IV_ATTR = "IV";
  private static final Pattern IV_ATTR_REGEX;
  private static final String KEY_TAG = "#EXT-X-KEY";
  private static final String LANGUAGE_ATTR = "LANGUAGE";
  private static final Pattern LANGUAGE_ATTR_REGEX;
  private static final Pattern MEDIA_DURATION_REGEX;
  private static final String MEDIA_DURATION_TAG = "#EXTINF";
  private static final Pattern MEDIA_SEQUENCE_REGEX;
  private static final String MEDIA_SEQUENCE_TAG = "#EXT-X-MEDIA-SEQUENCE";
  private static final String MEDIA_TAG = "#EXT-X-MEDIA";
  private static final String METHOD_AES128 = "AES-128";
  private static final String METHOD_ATTR = "METHOD";
  private static final Pattern METHOD_ATTR_REGEX;
  private static final String METHOD_NONE = "NONE";
  private static final String NAME_ATTR = "NAME";
  private static final Pattern NAME_ATTR_REGEX;
  private static final String RESOLUTION_ATTR = "RESOLUTION";
  private static final Pattern RESOLUTION_ATTR_REGEX = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
  private static final String STREAM_INF_TAG = "#EXT-X-STREAM-INF";
  private static final String SUBTITLES_TYPE = "SUBTITLES";
  private static final Pattern TARGET_DURATION_REGEX;
  private static final String TARGET_DURATION_TAG = "#EXT-X-TARGETDURATION";
  private static final String TYPE_ATTR = "TYPE";
  private static final Pattern TYPE_ATTR_REGEX;
  private static final String URI_ATTR = "URI";
  private static final Pattern URI_ATTR_REGEX;
  private static final Pattern VERSION_REGEX;
  private static final String VERSION_TAG = "#EXT-X-VERSION";
  private static final String VIDEO_TYPE = "VIDEO";
  
  static
  {
    MEDIA_DURATION_REGEX = Pattern.compile("#EXTINF:([\\d.]+)\\b");
    MEDIA_SEQUENCE_REGEX = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    TARGET_DURATION_REGEX = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    VERSION_REGEX = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    BYTERANGE_REGEX = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    METHOD_ATTR_REGEX = Pattern.compile("METHOD=(NONE|AES-128)");
    URI_ATTR_REGEX = Pattern.compile("URI=\"(.+?)\"");
    IV_ATTR_REGEX = Pattern.compile("IV=([^,.*]+)");
    TYPE_ATTR_REGEX = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    LANGUAGE_ATTR_REGEX = Pattern.compile("LANGUAGE=\"(.+?)\"");
    NAME_ATTR_REGEX = Pattern.compile("NAME=\"(.+?)\"");
  }
  
  private static HlsMasterPlaylist parseMasterPlaylist(LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    int j = 0;
    String str2 = null;
    int k = -1;
    int i = -1;
    Object localObject1 = null;
    String str3 = null;
    int m = 0;
    String str1 = null;
    String str4;
    while (paramLineIterator.hasNext())
    {
      str4 = paramLineIterator.next();
      Object localObject2;
      if (str4.startsWith("#EXT-X-MEDIA"))
      {
        localObject2 = HlsParserUtil.parseStringAttr(str4, TYPE_ATTR_REGEX, "TYPE");
        if ("CLOSED-CAPTIONS".equals(localObject2))
        {
          if ("CC1".equals(HlsParserUtil.parseStringAttr(str4, INSTREAM_ID_ATTR_REGEX, "INSTREAM-ID"))) {
            str3 = HlsParserUtil.parseOptionalStringAttr(str4, LANGUAGE_ATTR_REGEX);
          }
        }
        else if ("SUBTITLES".equals(localObject2))
        {
          localObject2 = HlsParserUtil.parseStringAttr(str4, NAME_ATTR_REGEX, "NAME");
          localArrayList3.add(new Variant(HlsParserUtil.parseStringAttr(str4, URI_ATTR_REGEX, "URI"), new Format((String)localObject2, "application/x-mpegURL", -1, -1, -1.0F, -1, -1, -1, HlsParserUtil.parseOptionalStringAttr(str4, LANGUAGE_ATTR_REGEX), str2)));
        }
        else if ("AUDIO".equals(localObject2))
        {
          localObject2 = HlsParserUtil.parseOptionalStringAttr(str4, LANGUAGE_ATTR_REGEX);
          String str5 = HlsParserUtil.parseOptionalStringAttr(str4, URI_ATTR_REGEX);
          if (str5 != null) {
            localArrayList2.add(new Variant(str5, new Format(HlsParserUtil.parseStringAttr(str4, NAME_ATTR_REGEX, "NAME"), "application/x-mpegURL", -1, -1, -1.0F, -1, -1, -1, (String)localObject2, str2)));
          } else {
            localObject1 = localObject2;
          }
        }
      }
      else
      {
        if (str4.startsWith("#EXT-X-STREAM-INF"))
        {
          m = HlsParserUtil.parseIntAttr(str4, BANDWIDTH_ATTR_REGEX, "BANDWIDTH");
          str2 = HlsParserUtil.parseOptionalStringAttr(str4, CODECS_ATTR_REGEX);
          str1 = HlsParserUtil.parseOptionalStringAttr(str4, NAME_ATTR_REGEX);
          localObject2 = HlsParserUtil.parseOptionalStringAttr(str4, RESOLUTION_ATTR_REGEX);
          int n;
          if (localObject2 != null)
          {
            localObject2 = ((String)localObject2).split("x");
            i = Integer.parseInt(localObject2[0]);
            j = i;
            if (i <= 0) {
              j = -1;
            }
            n = Integer.parseInt(localObject2[1]);
            i = n;
            k = j;
            if (n <= 0)
            {
              i = -1;
              k = j;
            }
          }
          for (;;)
          {
            n = 1;
            j = m;
            m = n;
            break;
            k = -1;
            i = -1;
          }
        }
        if ((!str4.startsWith("#")) && (m != 0))
        {
          if (str1 != null) {
            break label524;
          }
          str1 = Integer.toString(localArrayList1.size());
        }
      }
    }
    label524:
    for (;;)
    {
      localArrayList1.add(new Variant(str4, new Format(str1, "application/x-mpegURL", k, i, -1.0F, -1, -1, j, null, str2)));
      j = 0;
      str2 = null;
      k = -1;
      i = -1;
      m = 0;
      str1 = null;
      break;
      return new HlsMasterPlaylist(paramString, localArrayList1, localArrayList2, localArrayList3, (String)localObject1, str3);
    }
  }
  
  private static HlsMediaPlaylist parseMediaPlaylist(LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    int n = 0;
    int m = 0;
    int k = 1;
    boolean bool1 = true;
    ArrayList localArrayList = new ArrayList();
    double d = 0.0D;
    int j = 0;
    long l4 = 0L;
    long l1 = 0L;
    long l2 = -1L;
    int i = 0;
    boolean bool2 = false;
    String str2 = null;
    String str1 = null;
    while (paramLineIterator.hasNext())
    {
      String str3 = paramLineIterator.next();
      if (str3.startsWith("#EXT-X-TARGETDURATION"))
      {
        m = HlsParserUtil.parseIntAttr(str3, TARGET_DURATION_REGEX, "#EXT-X-TARGETDURATION");
      }
      else if (str3.startsWith("#EXT-X-MEDIA-SEQUENCE"))
      {
        n = HlsParserUtil.parseIntAttr(str3, MEDIA_SEQUENCE_REGEX, "#EXT-X-MEDIA-SEQUENCE");
        i = n;
      }
      else if (str3.startsWith("#EXT-X-VERSION"))
      {
        k = HlsParserUtil.parseIntAttr(str3, VERSION_REGEX, "#EXT-X-VERSION");
      }
      else if (str3.startsWith("#EXTINF"))
      {
        d = HlsParserUtil.parseDoubleAttr(str3, MEDIA_DURATION_REGEX, "#EXTINF");
      }
      else if (str3.startsWith("#EXT-X-KEY"))
      {
        bool2 = "AES-128".equals(HlsParserUtil.parseStringAttr(str3, METHOD_ATTR_REGEX, "METHOD"));
        if (bool2)
        {
          str2 = HlsParserUtil.parseStringAttr(str3, URI_ATTR_REGEX, "URI");
          str1 = HlsParserUtil.parseOptionalStringAttr(str3, IV_ATTR_REGEX);
        }
        else
        {
          str2 = null;
          str1 = null;
        }
      }
      else
      {
        Object localObject;
        long l3;
        if (str3.startsWith("#EXT-X-BYTERANGE"))
        {
          localObject = HlsParserUtil.parseStringAttr(str3, BYTERANGE_REGEX, "#EXT-X-BYTERANGE").split("@");
          l3 = Long.parseLong(localObject[0]);
          l2 = l3;
          if (localObject.length > 1)
          {
            l1 = Long.parseLong(localObject[1]);
            l2 = l3;
          }
        }
        else if (str3.startsWith("#EXT-X-DISCONTINUITY-SEQUENCE"))
        {
          j = Integer.parseInt(str3.substring(str3.indexOf(':') + 1));
        }
        else if (str3.equals("#EXT-X-DISCONTINUITY"))
        {
          j += 1;
        }
        else
        {
          if (!str3.startsWith("#"))
          {
            if (!bool2) {
              localObject = null;
            }
            for (;;)
            {
              i += 1;
              l3 = l1;
              if (l2 == -1L) {
                l3 = 0L;
              }
              localArrayList.add(new HlsMediaPlaylist.Segment(str3, d, j, l4, bool2, str2, (String)localObject, l3, l2));
              l4 += (1000000.0D * d);
              d = 0.0D;
              l1 = l3;
              if (l2 != -1L) {
                l1 = l3 + l2;
              }
              l2 = -1L;
              break;
              if (str1 != null) {
                localObject = str1;
              } else {
                localObject = Integer.toHexString(i);
              }
            }
          }
          if (str3.equals("#EXT-X-ENDLIST")) {
            bool1 = false;
          }
        }
      }
    }
    return new HlsMediaPlaylist(paramString, n, m, k, bool1, Collections.unmodifiableList(localArrayList));
  }
  
  public HlsPlaylist parse(String paramString, InputStream paramInputStream)
    throws IOException, ParserException
  {
    paramInputStream = new BufferedReader(new InputStreamReader(paramInputStream));
    LinkedList localLinkedList = new LinkedList();
    try
    {
      for (;;)
      {
        String str = paramInputStream.readLine();
        if (str == null) {
          break;
        }
        str = str.trim();
        if (!str.isEmpty())
        {
          if (str.startsWith("#EXT-X-STREAM-INF"))
          {
            localLinkedList.add(str);
            paramString = parseMasterPlaylist(new LineIterator(localLinkedList, paramInputStream), paramString);
            return paramString;
          }
          if ((str.startsWith("#EXT-X-TARGETDURATION")) || (str.startsWith("#EXT-X-MEDIA-SEQUENCE")) || (str.startsWith("#EXTINF")) || (str.startsWith("#EXT-X-KEY")) || (str.startsWith("#EXT-X-BYTERANGE")) || (str.equals("#EXT-X-DISCONTINUITY")) || (str.equals("#EXT-X-DISCONTINUITY-SEQUENCE")) || (str.equals("#EXT-X-ENDLIST")))
          {
            localLinkedList.add(str);
            paramString = parseMediaPlaylist(new LineIterator(localLinkedList, paramInputStream), paramString);
            return paramString;
          }
          localLinkedList.add(str);
        }
      }
    }
    finally
    {
      paramInputStream.close();
    }
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
      if (this.next != null) {
        return true;
      }
      if (!this.extraLines.isEmpty())
      {
        this.next = ((String)this.extraLines.poll());
        return true;
      }
      do
      {
        String str = this.reader.readLine();
        this.next = str;
        if (str == null) {
          break;
        }
        this.next = this.next.trim();
      } while (this.next.isEmpty());
      return true;
      return false;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/HlsPlaylistParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */