package org.telegram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleSet
{
  public static Pattern pattern = Pattern.compile("[0-9]+");
  public boolean hasRuleWithIntlPrefix;
  public boolean hasRuleWithTrunkPrefix;
  public int matchLen;
  public ArrayList<PhoneRule> rules = new ArrayList();
  
  String format(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    int i;
    if (paramString1.length() >= this.matchLen)
    {
      localObject1 = paramString1.substring(0, this.matchLen);
      i = 0;
      localObject1 = pattern.matcher((CharSequence)localObject1);
      if (((Matcher)localObject1).find()) {
        i = Integer.parseInt(((Matcher)localObject1).group(0));
      }
      for (localObject1 = this.rules.iterator();; localObject1 = ((PhoneRule)localObject3).format(paramString1, paramString2, paramString3)) {
        do
        {
          do
          {
            if (!((Iterator)localObject1).hasNext()) {
              break;
            }
            localObject3 = (PhoneRule)((Iterator)localObject1).next();
          } while ((i < ((PhoneRule)localObject3).minVal) || (i > ((PhoneRule)localObject3).maxVal) || (paramString1.length() > ((PhoneRule)localObject3).maxLen));
          if (!paramBoolean) {
            break;
          }
        } while ((((((PhoneRule)localObject3).flag12 & 0x3) != 0) || (paramString3 != null) || (paramString2 != null)) && ((paramString3 == null) || ((((PhoneRule)localObject3).flag12 & 0x1) == 0)) && ((paramString2 == null) || ((((PhoneRule)localObject3).flag12 & 0x2) == 0)));
      }
    }
    do
    {
      do
      {
        return (String)localObject1;
        if (((paramString3 != null) || (paramString2 != null)) && ((paramString3 == null) || ((((PhoneRule)localObject3).flag12 & 0x1) == 0)) && ((paramString2 == null) || ((((PhoneRule)localObject3).flag12 & 0x2) == 0))) {
          break;
        }
        return ((PhoneRule)localObject3).format(paramString1, paramString2, paramString3);
        localObject1 = localObject2;
      } while (paramBoolean);
      if (paramString2 != null)
      {
        localObject3 = this.rules.iterator();
        do
        {
          localObject1 = localObject2;
          if (!((Iterator)localObject3).hasNext()) {
            break;
          }
          localObject1 = (PhoneRule)((Iterator)localObject3).next();
        } while ((i < ((PhoneRule)localObject1).minVal) || (i > ((PhoneRule)localObject1).maxVal) || (paramString1.length() > ((PhoneRule)localObject1).maxLen) || ((paramString3 != null) && ((((PhoneRule)localObject1).flag12 & 0x1) == 0)));
        return ((PhoneRule)localObject1).format(paramString1, paramString2, paramString3);
      }
      localObject1 = localObject2;
    } while (paramString3 == null);
    Object localObject3 = this.rules.iterator();
    do
    {
      localObject1 = localObject2;
      if (!((Iterator)localObject3).hasNext()) {
        break;
      }
      localObject1 = (PhoneRule)((Iterator)localObject3).next();
    } while ((i < ((PhoneRule)localObject1).minVal) || (i > ((PhoneRule)localObject1).maxVal) || (paramString1.length() > ((PhoneRule)localObject1).maxLen) || ((paramString2 != null) && ((((PhoneRule)localObject1).flag12 & 0x2) == 0)));
    return ((PhoneRule)localObject1).format(paramString1, paramString2, paramString3);
  }
  
  boolean isValid(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (paramString1.length() >= this.matchLen)
    {
      Object localObject = paramString1.substring(0, this.matchLen);
      int i = 0;
      localObject = pattern.matcher((CharSequence)localObject);
      if (((Matcher)localObject).find()) {
        i = Integer.parseInt(((Matcher)localObject).group(0));
      }
      localObject = this.rules.iterator();
      while (((Iterator)localObject).hasNext())
      {
        PhoneRule localPhoneRule = (PhoneRule)((Iterator)localObject).next();
        if ((i >= localPhoneRule.minVal) && (i <= localPhoneRule.maxVal) && (paramString1.length() == localPhoneRule.maxLen)) {
          if (paramBoolean)
          {
            if ((((localPhoneRule.flag12 & 0x3) != 0) || (paramString3 != null) || (paramString2 != null)) && ((paramString3 == null) || ((localPhoneRule.flag12 & 0x1) == 0)) && ((paramString2 == null) || ((localPhoneRule.flag12 & 0x2) == 0))) {
              break;
            }
          }
          else
          {
            while (((paramString3 == null) && (paramString2 == null)) || ((paramString3 != null) && ((localPhoneRule.flag12 & 0x1) != 0))) {
              return true;
            }
            if ((paramString2 != null) && ((localPhoneRule.flag12 & 0x2) != 0)) {
              return true;
            }
          }
        }
      }
      if (!paramBoolean)
      {
        if ((paramString2 != null) && (!this.hasRuleWithIntlPrefix)) {
          paramString2 = this.rules.iterator();
        }
        for (;;)
        {
          if (paramString2.hasNext())
          {
            localObject = (PhoneRule)paramString2.next();
            if ((i >= ((PhoneRule)localObject).minVal) && (i <= ((PhoneRule)localObject).maxVal) && (paramString1.length() == ((PhoneRule)localObject).maxLen))
            {
              if (paramString3 == null) {
                break;
              }
              if ((((PhoneRule)localObject).flag12 & 0x1) != 0)
              {
                return true;
                if ((paramString3 != null) && (!this.hasRuleWithTrunkPrefix))
                {
                  paramString3 = this.rules.iterator();
                  for (;;)
                  {
                    if (paramString3.hasNext())
                    {
                      localObject = (PhoneRule)paramString3.next();
                      if ((i >= ((PhoneRule)localObject).minVal) && (i <= ((PhoneRule)localObject).maxVal) && (paramString1.length() == ((PhoneRule)localObject).maxLen))
                      {
                        if (paramString2 == null) {
                          break;
                        }
                        if ((((PhoneRule)localObject).flag12 & 0x2) != 0) {
                          return true;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      return false;
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/RuleSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */