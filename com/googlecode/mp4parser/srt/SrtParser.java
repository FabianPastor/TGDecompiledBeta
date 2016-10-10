package com.googlecode.mp4parser.srt;

import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl.Line;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

public class SrtParser
{
  private static long parse(String paramString)
  {
    return 60L * Long.parseLong(paramString.split(":")[0].trim()) * 60L * 1000L + 60L * Long.parseLong(paramString.split(":")[1].trim()) * 1000L + 1000L * Long.parseLong(paramString.split(":")[2].split(",")[0].trim()) + Long.parseLong(paramString.split(":")[2].split(",")[1].trim());
  }
  
  public static TextTrackImpl parse(InputStream paramInputStream)
    throws IOException
  {
    LineNumberReader localLineNumberReader = new LineNumberReader(new InputStreamReader(paramInputStream, "UTF-8"));
    TextTrackImpl localTextTrackImpl = new TextTrackImpl();
    if (localLineNumberReader.readLine() == null) {
      return localTextTrackImpl;
    }
    String str1 = localLineNumberReader.readLine();
    String str2;
    for (paramInputStream = "";; paramInputStream = paramInputStream + str2 + "\n")
    {
      str2 = localLineNumberReader.readLine();
      if ((str2 == null) || (str2.trim().equals("")))
      {
        long l1 = parse(str1.split("-->")[0]);
        long l2 = parse(str1.split("-->")[1]);
        localTextTrackImpl.getSubs().add(new TextTrackImpl.Line(l1, l2, paramInputStream));
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/srt/SrtParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */