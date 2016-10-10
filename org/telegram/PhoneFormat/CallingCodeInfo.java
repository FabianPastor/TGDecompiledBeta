package org.telegram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;

public class CallingCodeInfo
{
  public String callingCode = "";
  public ArrayList<String> countries = new ArrayList();
  public ArrayList<String> intlPrefixes = new ArrayList();
  public ArrayList<RuleSet> ruleSets = new ArrayList();
  public ArrayList<String> trunkPrefixes = new ArrayList();
  
  String format(String paramString)
  {
    Object localObject3 = paramString;
    Object localObject2 = null;
    String str2 = null;
    String str1;
    Object localObject1;
    if (((String)localObject3).startsWith(this.callingCode))
    {
      str1 = this.callingCode;
      localObject1 = ((String)localObject3).substring(str1.length());
    }
    for (;;)
    {
      localObject3 = this.ruleSets.iterator();
      do
      {
        if (!((Iterator)localObject3).hasNext()) {
          break;
        }
        str2 = ((RuleSet)((Iterator)localObject3).next()).format((String)localObject1, str1, (String)localObject2, true);
      } while (str2 == null);
      return str2;
      String str3 = matchingTrunkCode((String)localObject3);
      str1 = str2;
      localObject1 = localObject3;
      if (str3 != null)
      {
        localObject2 = str3;
        localObject1 = ((String)localObject3).substring(((String)localObject2).length());
        str1 = str2;
      }
    }
    localObject3 = this.ruleSets.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      str2 = ((RuleSet)((Iterator)localObject3).next()).format((String)localObject1, str1, (String)localObject2, false);
      if (str2 != null) {
        return str2;
      }
    }
    if ((str1 != null) && (((String)localObject1).length() != 0)) {
      return String.format("%s %s", new Object[] { str1, localObject1 });
    }
    return paramString;
  }
  
  boolean isValidPhoneNumber(String paramString)
  {
    Object localObject3 = paramString;
    Object localObject2 = null;
    Object localObject4 = null;
    Object localObject1;
    if (((String)localObject3).startsWith(this.callingCode))
    {
      localObject1 = this.callingCode;
      paramString = ((String)localObject3).substring(((String)localObject1).length());
    }
    for (;;)
    {
      localObject3 = this.ruleSets.iterator();
      do
      {
        if (!((Iterator)localObject3).hasNext()) {
          break;
        }
      } while (!((RuleSet)((Iterator)localObject3).next()).isValid(paramString, (String)localObject1, (String)localObject2, true));
      return true;
      String str = matchingTrunkCode((String)localObject3);
      localObject1 = localObject4;
      paramString = (String)localObject3;
      if (str != null)
      {
        localObject2 = str;
        paramString = ((String)localObject3).substring(((String)localObject2).length());
        localObject1 = localObject4;
      }
    }
    localObject3 = this.ruleSets.iterator();
    while (((Iterator)localObject3).hasNext()) {
      if (((RuleSet)((Iterator)localObject3).next()).isValid(paramString, (String)localObject1, (String)localObject2, false)) {
        return true;
      }
    }
    return false;
  }
  
  String matchingAccessCode(String paramString)
  {
    Iterator localIterator = this.intlPrefixes.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramString.startsWith(str)) {
        return str;
      }
    }
    return null;
  }
  
  String matchingTrunkCode(String paramString)
  {
    Iterator localIterator = this.trunkPrefixes.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramString.startsWith(str)) {
        return str;
      }
    }
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/CallingCodeInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */