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
    Object localObject1 = null;
    Object localObject2 = localObject1;
    int i;
    Object localObject3;
    if (paramString1.length() >= this.matchLen)
    {
      localObject2 = paramString1.substring(0, this.matchLen);
      i = 0;
      localObject2 = pattern.matcher((CharSequence)localObject2);
      if (((Matcher)localObject2).find()) {
        i = Integer.parseInt(((Matcher)localObject2).group(0));
      }
      for (localObject2 = this.rules.iterator();; localObject2 = ((PhoneRule)localObject3).format(paramString1, paramString2, paramString3)) {
        do
        {
          do
          {
            if (!((Iterator)localObject2).hasNext()) {
              break;
            }
            localObject3 = (PhoneRule)((Iterator)localObject2).next();
          } while ((i < ((PhoneRule)localObject3).minVal) || (i > ((PhoneRule)localObject3).maxVal) || (paramString1.length() > ((PhoneRule)localObject3).maxLen));
          if (!paramBoolean) {
            break;
          }
        } while ((((((PhoneRule)localObject3).flag12 & 0x3) != 0) || (paramString3 != null) || (paramString2 != null)) && ((paramString3 == null) || ((((PhoneRule)localObject3).flag12 & 0x1) == 0)) && ((paramString2 == null) || ((((PhoneRule)localObject3).flag12 & 0x2) == 0)));
      }
    }
    for (;;)
    {
      return (String)localObject2;
      if (((paramString3 != null) || (paramString2 != null)) && ((paramString3 == null) || ((((PhoneRule)localObject3).flag12 & 0x1) == 0)) && ((paramString2 == null) || ((((PhoneRule)localObject3).flag12 & 0x2) == 0))) {
        break;
      }
      localObject2 = ((PhoneRule)localObject3).format(paramString1, paramString2, paramString3);
      continue;
      localObject2 = localObject1;
      if (!paramBoolean) {
        if (paramString2 != null)
        {
          localObject3 = this.rules.iterator();
          do
          {
            localObject2 = localObject1;
            if (!((Iterator)localObject3).hasNext()) {
              break;
            }
            localObject2 = (PhoneRule)((Iterator)localObject3).next();
          } while ((i < ((PhoneRule)localObject2).minVal) || (i > ((PhoneRule)localObject2).maxVal) || (paramString1.length() > ((PhoneRule)localObject2).maxLen) || ((paramString3 != null) && ((((PhoneRule)localObject2).flag12 & 0x1) == 0)));
          localObject2 = ((PhoneRule)localObject2).format(paramString1, paramString2, paramString3);
        }
        else
        {
          localObject2 = localObject1;
          if (paramString3 != null)
          {
            localObject3 = this.rules.iterator();
            do
            {
              localObject2 = localObject1;
              if (!((Iterator)localObject3).hasNext()) {
                break;
              }
              localObject2 = (PhoneRule)((Iterator)localObject3).next();
            } while ((i < ((PhoneRule)localObject2).minVal) || (i > ((PhoneRule)localObject2).maxVal) || (paramString1.length() > ((PhoneRule)localObject2).maxLen) || ((paramString2 != null) && ((((PhoneRule)localObject2).flag12 & 0x2) == 0)));
            localObject2 = ((PhoneRule)localObject2).format(paramString1, paramString2, paramString3);
          }
        }
      }
    }
  }
  
  boolean isValid(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    boolean bool1 = true;
    Object localObject;
    int i;
    PhoneRule localPhoneRule;
    boolean bool2;
    if (paramString1.length() >= this.matchLen)
    {
      localObject = paramString1.substring(0, this.matchLen);
      i = 0;
      localObject = pattern.matcher((CharSequence)localObject);
      if (((Matcher)localObject).find()) {
        i = Integer.parseInt(((Matcher)localObject).group(0));
      }
      localObject = this.rules.iterator();
      for (;;)
      {
        if (((Iterator)localObject).hasNext())
        {
          localPhoneRule = (PhoneRule)((Iterator)localObject).next();
          if ((i >= localPhoneRule.minVal) && (i <= localPhoneRule.maxVal) && (paramString1.length() == localPhoneRule.maxLen)) {
            if (paramBoolean) {
              if (((localPhoneRule.flag12 & 0x3) == 0) && (paramString3 == null))
              {
                bool2 = bool1;
                if (paramString2 == null) {
                  break;
                }
              }
              else if (paramString3 != null)
              {
                bool2 = bool1;
                if ((localPhoneRule.flag12 & 0x1) != 0) {
                  break;
                }
              }
              else
              {
                if ((paramString2 == null) || ((localPhoneRule.flag12 & 0x2) == 0)) {
                  continue;
                }
                bool2 = bool1;
              }
            }
          }
        }
      }
    }
    for (;;)
    {
      return bool2;
      if (paramString3 == null)
      {
        bool2 = bool1;
        if (paramString2 == null) {}
      }
      else if (paramString3 != null)
      {
        bool2 = bool1;
        if ((localPhoneRule.flag12 & 0x1) != 0) {}
      }
      else
      {
        if ((paramString2 == null) || ((localPhoneRule.flag12 & 0x2) == 0)) {
          break;
        }
        bool2 = bool1;
        continue;
        if (!paramBoolean)
        {
          if ((paramString2 != null) && (!this.hasRuleWithIntlPrefix)) {
            localObject = this.rules.iterator();
          }
          for (;;)
          {
            if (((Iterator)localObject).hasNext())
            {
              paramString2 = (PhoneRule)((Iterator)localObject).next();
              if ((i >= paramString2.minVal) && (i <= paramString2.maxVal) && (paramString1.length() == paramString2.maxLen))
              {
                bool2 = bool1;
                if (paramString3 == null) {
                  break;
                }
                if ((paramString2.flag12 & 0x1) != 0)
                {
                  bool2 = bool1;
                  break;
                  if ((paramString3 != null) && (!this.hasRuleWithTrunkPrefix))
                  {
                    localObject = this.rules.iterator();
                    for (;;)
                    {
                      if (((Iterator)localObject).hasNext())
                      {
                        paramString3 = (PhoneRule)((Iterator)localObject).next();
                        if ((i >= paramString3.minVal) && (i <= paramString3.maxVal) && (paramString1.length() == paramString3.maxLen))
                        {
                          bool2 = bool1;
                          if (paramString2 == null) {
                            break;
                          }
                          if ((paramString3.flag12 & 0x2) != 0)
                          {
                            bool2 = bool1;
                            break;
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
        bool2 = false;
        continue;
        bool2 = false;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/RuleSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */