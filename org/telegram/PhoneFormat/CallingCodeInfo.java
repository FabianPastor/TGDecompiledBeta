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
    String str1 = paramString;
    Object localObject1 = null;
    Iterator localIterator = null;
    Object localObject2;
    String str2;
    if (str1.startsWith(this.callingCode))
    {
      localObject2 = this.callingCode;
      str2 = str1.substring(((String)localObject2).length());
      localIterator = this.ruleSets.iterator();
      while (localIterator.hasNext())
      {
        str1 = ((RuleSet)localIterator.next()).format(str2, (String)localObject2, (String)localObject1, true);
        if (str1 != null) {
          paramString = str1;
        }
      }
    }
    for (;;)
    {
      return paramString;
      String str3 = matchingTrunkCode(str1);
      localObject2 = localIterator;
      str2 = str1;
      if (str3 == null) {
        break;
      }
      localObject1 = str3;
      str2 = str1.substring(((String)localObject1).length());
      localObject2 = localIterator;
      break;
      localIterator = this.ruleSets.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          str1 = ((RuleSet)localIterator.next()).format(str2, (String)localObject2, (String)localObject1, false);
          if (str1 != null)
          {
            paramString = str1;
            break;
          }
        }
      }
      if ((localObject2 != null) && (str2.length() != 0)) {
        paramString = String.format("%s %s", new Object[] { localObject2, str2 });
      }
    }
  }
  
  boolean isValidPhoneNumber(String paramString)
  {
    boolean bool = true;
    Object localObject1 = paramString;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4;
    if (((String)localObject1).startsWith(this.callingCode))
    {
      localObject4 = this.callingCode;
      paramString = ((String)localObject1).substring(((String)localObject4).length());
      localObject1 = this.ruleSets.iterator();
      do
      {
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
      } while (!((RuleSet)((Iterator)localObject1).next()).isValid(paramString, (String)localObject4, (String)localObject2, true));
    }
    for (;;)
    {
      return bool;
      String str = matchingTrunkCode((String)localObject1);
      localObject4 = localObject3;
      paramString = (String)localObject1;
      if (str == null) {
        break;
      }
      localObject2 = str;
      paramString = ((String)localObject1).substring(((String)localObject2).length());
      localObject4 = localObject3;
      break;
      localObject1 = this.ruleSets.iterator();
      for (;;)
      {
        if (((Iterator)localObject1).hasNext()) {
          if (((RuleSet)((Iterator)localObject1).next()).isValid(paramString, (String)localObject4, (String)localObject2, false)) {
            break;
          }
        }
      }
      bool = false;
    }
  }
  
  String matchingAccessCode(String paramString)
  {
    Iterator localIterator = this.intlPrefixes.iterator();
    String str;
    do
    {
      if (!localIterator.hasNext()) {
        break;
      }
      str = (String)localIterator.next();
    } while (!paramString.startsWith(str));
    for (paramString = str;; paramString = null) {
      return paramString;
    }
  }
  
  String matchingTrunkCode(String paramString)
  {
    Iterator localIterator = this.trunkPrefixes.iterator();
    String str;
    do
    {
      if (!localIterator.hasNext()) {
        break;
      }
      str = (String)localIterator.next();
    } while (!paramString.startsWith(str));
    for (paramString = str;; paramString = null) {
      return paramString;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/CallingCodeInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */