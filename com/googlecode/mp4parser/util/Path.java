package com.googlecode.mp4parser.util;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path
{
  static Pattern component;
  
  static
  {
    if (!Path.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      component = Pattern.compile("(....|\\.\\.)(\\[(.*)\\])?");
      return;
    }
  }
  
  public static String createPath(Box paramBox)
  {
    return createPath(paramBox, "");
  }
  
  private static String createPath(Box paramBox, String paramString)
  {
    Container localContainer = paramBox.getParent();
    int i = 0;
    Iterator localIterator = localContainer.getBoxes().iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {}
      Box localBox;
      do
      {
        paramString = String.format("/%s[%d]", new Object[] { paramBox.getType(), Integer.valueOf(i) }) + paramString;
        paramBox = paramString;
        if ((localContainer instanceof Box)) {
          paramBox = createPath((Box)localContainer, paramString);
        }
        return paramBox;
        localBox = (Box)localIterator.next();
        if (!localBox.getType().equals(paramBox.getType())) {
          break;
        }
      } while (localBox == paramBox);
      i += 1;
    }
  }
  
  public static <T extends Box> T getPath(Box paramBox, String paramString)
  {
    paramBox = getPaths(paramBox, paramString, true);
    if (paramBox.isEmpty()) {
      return null;
    }
    return (Box)paramBox.get(0);
  }
  
  public static <T extends Box> T getPath(Container paramContainer, String paramString)
  {
    paramContainer = getPaths(paramContainer, paramString, true);
    if (paramContainer.isEmpty()) {
      return null;
    }
    return (Box)paramContainer.get(0);
  }
  
  public static <T extends Box> T getPath(AbstractContainerBox paramAbstractContainerBox, String paramString)
  {
    paramAbstractContainerBox = getPaths(paramAbstractContainerBox, paramString, true);
    if (paramAbstractContainerBox.isEmpty()) {
      return null;
    }
    return (Box)paramAbstractContainerBox.get(0);
  }
  
  public static <T extends Box> List<T> getPaths(Box paramBox, String paramString)
  {
    return getPaths(paramBox, paramString, false);
  }
  
  private static <T extends Box> List<T> getPaths(Box paramBox, String paramString, boolean paramBoolean)
  {
    return getPaths(paramBox, paramString, paramBoolean);
  }
  
  public static <T extends Box> List<T> getPaths(Container paramContainer, String paramString)
  {
    return getPaths(paramContainer, paramString, false);
  }
  
  private static <T extends Box> List<T> getPaths(Container paramContainer, String paramString, boolean paramBoolean)
  {
    return getPaths(paramContainer, paramString, paramBoolean);
  }
  
  private static <T extends Box> List<T> getPaths(AbstractContainerBox paramAbstractContainerBox, String paramString, boolean paramBoolean)
  {
    return getPaths(paramAbstractContainerBox, paramString, paramBoolean);
  }
  
  private static <T extends Box> List<T> getPaths(Object paramObject, String paramString, boolean paramBoolean)
  {
    if (paramString.startsWith("/"))
    {
      paramString = paramString.substring(1);
      if ((paramObject instanceof Box)) {}
    }
    for (;;)
    {
      if (paramString.length() == 0)
      {
        if ((paramObject instanceof Box))
        {
          paramObject = Collections.singletonList((Box)paramObject);
          return (List<T>)paramObject;
          paramObject = ((Box)paramObject).getParent();
          break;
        }
        throw new RuntimeException("Result of path expression seems to be the root container. This is not allowed!");
      }
      String str;
      Object localObject1;
      if (paramString.contains("/"))
      {
        str = paramString.substring(paramString.indexOf('/') + 1);
        localObject1 = paramString.substring(0, paramString.indexOf('/'));
      }
      Object localObject2;
      for (paramString = str;; paramString = "")
      {
        localObject2 = component.matcher((CharSequence)localObject1);
        if (!((Matcher)localObject2).matches()) {
          break label341;
        }
        str = ((Matcher)localObject2).group(1);
        if (!"..".equals(str)) {
          break label179;
        }
        if (!(paramObject instanceof Box)) {
          break;
        }
        return getPaths(((Box)paramObject).getParent(), paramString, paramBoolean);
        localObject1 = paramString;
      }
      return Collections.emptyList();
      label179:
      if ((paramObject instanceof Container))
      {
        int i = -1;
        if (((Matcher)localObject2).group(2) != null) {
          i = Integer.parseInt(((Matcher)localObject2).group(3));
        }
        localObject1 = new LinkedList();
        int j = 0;
        localObject2 = ((Container)paramObject).getBoxes().iterator();
        label320:
        do
        {
          int k;
          do
          {
            paramObject = localObject1;
            if (!((Iterator)localObject2).hasNext()) {
              break;
            }
            paramObject = (Box)((Iterator)localObject2).next();
            k = j;
            if (((Box)paramObject).getType().matches(str))
            {
              if ((i == -1) || (i == j)) {
                ((List)localObject1).addAll(getPaths((Box)paramObject, paramString, paramBoolean));
              }
              k = j + 1;
            }
            if (paramBoolean) {
              break label320;
            }
            j = k;
          } while (i < 0);
          j = k;
        } while (((List)localObject1).isEmpty());
        return (List<T>)localObject1;
      }
      return Collections.emptyList();
      label341:
      throw new RuntimeException(localObject1 + " is invalid path.");
    }
  }
  
  public static boolean isContained(Box paramBox, String paramString)
  {
    assert (paramString.startsWith("/")) : "Absolute path required";
    return getPaths(paramBox, paramString).contains(paramBox);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/Path.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */