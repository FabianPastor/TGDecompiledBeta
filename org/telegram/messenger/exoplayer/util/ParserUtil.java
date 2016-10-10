package org.telegram.messenger.exoplayer.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class ParserUtil
{
  public static String getAttributeValue(XmlPullParser paramXmlPullParser, String paramString)
  {
    int j = paramXmlPullParser.getAttributeCount();
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(paramXmlPullParser.getAttributeName(i))) {
        return paramXmlPullParser.getAttributeValue(i);
      }
      i += 1;
    }
    return null;
  }
  
  public static boolean isEndTag(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException
  {
    return paramXmlPullParser.getEventType() == 3;
  }
  
  public static boolean isEndTag(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException
  {
    return (isEndTag(paramXmlPullParser)) && (paramXmlPullParser.getName().equals(paramString));
  }
  
  public static boolean isStartTag(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException
  {
    return paramXmlPullParser.getEventType() == 2;
  }
  
  public static boolean isStartTag(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException
  {
    return (isStartTag(paramXmlPullParser)) && (paramXmlPullParser.getName().equals(paramString));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/ParserUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */