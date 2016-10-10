package org.telegram.messenger;

import java.util.Locale;

public class VideoEditedInfo
{
  public int bitrate;
  public long endTime;
  public int originalHeight;
  public String originalPath;
  public int originalWidth;
  public int resultHeight;
  public int resultWidth;
  public int rotationValue;
  public long startTime;
  
  public String getString()
  {
    return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[] { Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), this.originalPath });
  }
  
  public boolean parseString(String paramString)
  {
    if (paramString.length() < 6) {
      return false;
    }
    for (;;)
    {
      int i;
      try
      {
        paramString = paramString.split("_");
        if (paramString.length >= 10)
        {
          this.startTime = Long.parseLong(paramString[1]);
          this.endTime = Long.parseLong(paramString[2]);
          this.rotationValue = Integer.parseInt(paramString[3]);
          this.originalWidth = Integer.parseInt(paramString[4]);
          this.originalHeight = Integer.parseInt(paramString[5]);
          this.bitrate = Integer.parseInt(paramString[6]);
          this.resultWidth = Integer.parseInt(paramString[7]);
          this.resultHeight = Integer.parseInt(paramString[8]);
          i = 9;
          if (i < paramString.length) {
            if (this.originalPath == null) {
              this.originalPath = paramString[i];
            } else {
              this.originalPath = (this.originalPath + "_" + paramString[i]);
            }
          }
        }
      }
      catch (Exception paramString)
      {
        FileLog.e("tmessages", paramString);
        return false;
      }
      return true;
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/VideoEditedInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */