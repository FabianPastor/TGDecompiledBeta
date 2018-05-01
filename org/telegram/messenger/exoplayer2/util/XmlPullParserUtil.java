package org.telegram.messenger.exoplayer2.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class XmlPullParserUtil
{
  public static String getAttributeValue(XmlPullParser paramXmlPullParser, String paramString)
  {
    int i = paramXmlPullParser.getAttributeCount();
    int j = 0;
    if (j < i) {
      if (!paramString.equals(paramXmlPullParser.getAttributeName(j))) {}
    }
    for (paramXmlPullParser = paramXmlPullParser.getAttributeValue(j);; paramXmlPullParser = null)
    {
      return paramXmlPullParser;
      j++;
      break;
    }
  }
  
  public static boolean isEndTag(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException
  {
    if (paramXmlPullParser.getEventType() == 3) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isEndTag(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException
  {
    if ((isEndTag(paramXmlPullParser)) && (paramXmlPullParser.getName().equals(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isStartTag(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException
  {
    if (paramXmlPullParser.getEventType() == 2) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isStartTag(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException
  {
    if ((isStartTag(paramXmlPullParser)) && (paramXmlPullParser.getName().equals(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/XmlPullParserUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */