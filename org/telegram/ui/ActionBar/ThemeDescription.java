package org.telegram.ui.ActionBar;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.text.SpannedString;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.support.widget.RecyclerView.RecycledViewPool;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.TypefaceSpan;

public class ThemeDescription
{
  public static int FLAG_AB_AM_BACKGROUND;
  public static int FLAG_AB_AM_ITEMSCOLOR;
  public static int FLAG_AB_AM_SELECTORCOLOR;
  public static int FLAG_AB_AM_TOPBACKGROUND;
  public static int FLAG_AB_ITEMSCOLOR;
  public static int FLAG_AB_SEARCH;
  public static int FLAG_AB_SEARCHPLACEHOLDER;
  public static int FLAG_AB_SELECTORCOLOR;
  public static int FLAG_AB_SUBMENUBACKGROUND = Integer.MIN_VALUE;
  public static int FLAG_AB_SUBMENUITEM;
  public static int FLAG_AB_SUBTITLECOLOR;
  public static int FLAG_AB_TITLECOLOR;
  public static int FLAG_BACKGROUND = 1;
  public static int FLAG_BACKGROUNDFILTER;
  public static int FLAG_CELLBACKGROUNDCOLOR;
  public static int FLAG_CHECKBOX;
  public static int FLAG_CHECKBOXCHECK;
  public static int FLAG_CHECKTAG;
  public static int FLAG_CURSORCOLOR;
  public static int FLAG_DRAWABLESELECTEDSTATE;
  public static int FLAG_FASTSCROLL;
  public static int FLAG_HINTTEXTCOLOR;
  public static int FLAG_IMAGECOLOR;
  public static int FLAG_LINKCOLOR = 2;
  public static int FLAG_LISTGLOWCOLOR;
  public static int FLAG_PROGRESSBAR;
  public static int FLAG_SECTIONS;
  public static int FLAG_SELECTOR;
  public static int FLAG_SELECTORWHITE;
  public static int FLAG_SERVICEBACKGROUND;
  public static int FLAG_TEXTCOLOR = 4;
  public static int FLAG_USEBACKGROUNDDRAWABLE;
  private HashMap<String, Field> cachedFields;
  private int changeFlags;
  private int currentColor;
  private String currentKey;
  private int defaultColor;
  private ThemeDescriptionDelegate delegate;
  private Drawable[] drawablesToUpdate;
  private Class[] listClasses;
  private String[] listClassesFieldName;
  private HashMap<String, Boolean> notFoundCachedFields;
  private Paint[] paintToUpdate;
  private int previousColor;
  private boolean[] previousIsDefault = new boolean[1];
  private View viewToInvalidate;
  
  static
  {
    FLAG_IMAGECOLOR = 8;
    FLAG_CELLBACKGROUNDCOLOR = 16;
    FLAG_BACKGROUNDFILTER = 32;
    FLAG_AB_ITEMSCOLOR = 64;
    FLAG_AB_TITLECOLOR = 128;
    FLAG_AB_SELECTORCOLOR = 256;
    FLAG_AB_AM_ITEMSCOLOR = 512;
    FLAG_AB_SUBTITLECOLOR = 1024;
    FLAG_PROGRESSBAR = 2048;
    FLAG_SELECTOR = 4096;
    FLAG_CHECKBOX = 8192;
    FLAG_CHECKBOXCHECK = 16384;
    FLAG_LISTGLOWCOLOR = 32768;
    FLAG_DRAWABLESELECTEDSTATE = 65536;
    FLAG_USEBACKGROUNDDRAWABLE = 131072;
    FLAG_CHECKTAG = 262144;
    FLAG_SECTIONS = 524288;
    FLAG_AB_AM_BACKGROUND = 1048576;
    FLAG_AB_AM_TOPBACKGROUND = 2097152;
    FLAG_AB_AM_SELECTORCOLOR = 4194304;
    FLAG_HINTTEXTCOLOR = 8388608;
    FLAG_CURSORCOLOR = 16777216;
    FLAG_FASTSCROLL = 33554432;
    FLAG_AB_SEARCHPLACEHOLDER = 67108864;
    FLAG_AB_SEARCH = 134217728;
    FLAG_SELECTORWHITE = 268435456;
    FLAG_SERVICEBACKGROUND = 536870912;
    FLAG_AB_SUBMENUITEM = NUM;
  }
  
  public ThemeDescription(View paramView, int paramInt, Class[] paramArrayOfClass, Paint paramPaint, Drawable[] paramArrayOfDrawable, ThemeDescriptionDelegate paramThemeDescriptionDelegate, String paramString)
  {
    this.currentKey = paramString;
    if (paramPaint != null) {
      this.paintToUpdate = new Paint[] { paramPaint };
    }
    this.drawablesToUpdate = paramArrayOfDrawable;
    this.viewToInvalidate = paramView;
    this.changeFlags = paramInt;
    this.listClasses = paramArrayOfClass;
    this.delegate = paramThemeDescriptionDelegate;
  }
  
  public ThemeDescription(View paramView, int paramInt, Class[] paramArrayOfClass, Paint[] paramArrayOfPaint, Drawable[] paramArrayOfDrawable, ThemeDescriptionDelegate paramThemeDescriptionDelegate, String paramString, Object paramObject)
  {
    this.currentKey = paramString;
    this.paintToUpdate = paramArrayOfPaint;
    this.drawablesToUpdate = paramArrayOfDrawable;
    this.viewToInvalidate = paramView;
    this.changeFlags = paramInt;
    this.listClasses = paramArrayOfClass;
    this.delegate = paramThemeDescriptionDelegate;
  }
  
  public ThemeDescription(View paramView, int paramInt, Class[] paramArrayOfClass, String[] paramArrayOfString, Paint[] paramArrayOfPaint, Drawable[] paramArrayOfDrawable, ThemeDescriptionDelegate paramThemeDescriptionDelegate, String paramString)
  {
    this.currentKey = paramString;
    this.paintToUpdate = paramArrayOfPaint;
    this.drawablesToUpdate = paramArrayOfDrawable;
    this.viewToInvalidate = paramView;
    this.changeFlags = paramInt;
    this.listClasses = paramArrayOfClass;
    this.listClassesFieldName = paramArrayOfString;
    this.delegate = paramThemeDescriptionDelegate;
    this.cachedFields = new HashMap();
    this.notFoundCachedFields = new HashMap();
  }
  
  private void processViewColor(View paramView, int paramInt)
  {
    int i = 0;
    if (i < this.listClasses.length)
    {
      int j;
      Object localObject1;
      int k;
      Object localObject2;
      label155:
      String str;
      if (this.listClasses[i].isInstance(paramView))
      {
        paramView.invalidate();
        if (((this.changeFlags & FLAG_CHECKTAG) != 0) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || (!this.currentKey.equals(paramView.getTag())))) {
          break label372;
        }
        j = 1;
        paramView.invalidate();
        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
          break label267;
        }
        localObject1 = paramView.getBackground();
        k = j;
        if (localObject1 != null)
        {
          if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0) {
            break label222;
          }
          k = j;
          if ((localObject1 instanceof CombinedDrawable))
          {
            localObject2 = ((CombinedDrawable)localObject1).getBackground();
            k = j;
            if ((localObject2 instanceof ColorDrawable))
            {
              ((ColorDrawable)localObject2).setColor(paramInt);
              k = j;
            }
          }
        }
        if (this.listClassesFieldName == null) {
          break label1655;
        }
        str = this.listClasses[i] + "_" + this.listClassesFieldName[i];
        if ((this.notFoundCachedFields == null) || (!this.notFoundCachedFields.containsKey(str))) {
          break label378;
        }
      }
      for (;;)
      {
        i++;
        break;
        label222:
        localObject2 = localObject1;
        if ((localObject1 instanceof CombinedDrawable)) {
          localObject2 = ((CombinedDrawable)localObject1).getIcon();
        }
        ((Drawable)localObject2).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
        k = j;
        break label155;
        label267:
        if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) != 0)
        {
          paramView.setBackgroundColor(paramInt);
          k = j;
          break label155;
        }
        if ((this.changeFlags & FLAG_TEXTCOLOR) != 0)
        {
          k = j;
          if (!(paramView instanceof TextView)) {
            break label155;
          }
          ((TextView)paramView).setTextColor(paramInt);
          k = j;
          break label155;
        }
        k = j;
        if ((this.changeFlags & FLAG_SERVICEBACKGROUND) == 0) {
          break label155;
        }
        localObject2 = paramView.getBackground();
        k = j;
        if (localObject2 == null) {
          break label155;
        }
        ((Drawable)localObject2).setColorFilter(Theme.colorFilter);
        k = j;
        break label155;
        label372:
        k = 0;
        break label155;
        label378:
        Object localObject4;
        try
        {
          localObject1 = (Field)this.cachedFields.get(str);
          localObject2 = localObject1;
          if (localObject1 == null)
          {
            localObject1 = this.listClasses[i].getDeclaredField(this.listClassesFieldName[i]);
            localObject2 = localObject1;
            if (localObject1 != null)
            {
              ((Field)localObject1).setAccessible(true);
              this.cachedFields.put(str, localObject1);
              localObject2 = localObject1;
            }
          }
          if (localObject2 == null) {
            continue;
          }
          localObject4 = ((Field)localObject2).get(paramView);
          if ((localObject4 == null) || ((k == 0) && ((localObject4 instanceof View)) && (!this.currentKey.equals(((View)localObject4).getTag())))) {
            continue;
          }
          if ((localObject4 instanceof View)) {
            ((View)localObject4).invalidate();
          }
          localObject1 = localObject4;
          if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0)
          {
            localObject1 = localObject4;
            if ((localObject4 instanceof View)) {
              localObject1 = ((View)localObject4).getBackground();
            }
          }
          if (((this.changeFlags & FLAG_BACKGROUND) == 0) || (!(localObject1 instanceof View))) {
            break label606;
          }
          ((View)localObject1).setBackgroundColor(paramInt);
        }
        catch (Throwable localThrowable)
        {
          FileLog.e(localThrowable);
          this.notFoundCachedFields.put(str, Boolean.valueOf(true));
        }
        continue;
        label606:
        if ((localObject1 instanceof Switch))
        {
          ((Switch)localObject1).checkColorFilters();
        }
        else if ((localObject1 instanceof EditTextCaption))
        {
          if ((this.changeFlags & FLAG_HINTTEXTCOLOR) != 0)
          {
            ((EditTextCaption)localObject1).setHintColor(paramInt);
            ((EditTextCaption)localObject1).setHintTextColor(paramInt);
          }
          else
          {
            ((EditTextCaption)localObject1).setTextColor(paramInt);
          }
        }
        else if ((localObject1 instanceof SimpleTextView))
        {
          if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
            ((SimpleTextView)localObject1).setLinkTextColor(paramInt);
          } else {
            ((SimpleTextView)localObject1).setTextColor(paramInt);
          }
        }
        else
        {
          Object localObject3;
          if ((localObject1 instanceof TextView))
          {
            localObject3 = (TextView)localObject1;
            if ((this.changeFlags & FLAG_IMAGECOLOR) != 0)
            {
              localObject1 = ((TextView)localObject3).getCompoundDrawables();
              if (localObject1 != null) {
                for (k = 0; k < localObject1.length; k++) {
                  if (localObject1[k] != null)
                  {
                    localObject4 = localObject1[k];
                    localObject3 = new android/graphics/PorterDuffColorFilter;
                    ((PorterDuffColorFilter)localObject3).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
                    ((Drawable)localObject4).setColorFilter((ColorFilter)localObject3);
                  }
                }
              }
            }
            else if ((this.changeFlags & FLAG_LINKCOLOR) != 0)
            {
              ((TextView)localObject3).getPaint().linkColor = paramInt;
              ((TextView)localObject3).invalidate();
            }
            else if ((this.changeFlags & FLAG_FASTSCROLL) != 0)
            {
              localObject3 = ((TextView)localObject3).getText();
              if ((localObject3 instanceof SpannedString))
              {
                localObject3 = (TypefaceSpan[])((SpannedString)localObject3).getSpans(0, ((CharSequence)localObject3).length(), TypefaceSpan.class);
                if ((localObject3 != null) && (localObject3.length > 0)) {
                  for (k = 0; k < localObject3.length; k++) {
                    localObject3[k].setColor(paramInt);
                  }
                }
              }
            }
            else
            {
              ((TextView)localObject3).setTextColor(paramInt);
            }
          }
          else if ((localObject1 instanceof ImageView))
          {
            localObject3 = (ImageView)localObject1;
            localObject1 = new android/graphics/PorterDuffColorFilter;
            ((PorterDuffColorFilter)localObject1).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
            ((ImageView)localObject3).setColorFilter((ColorFilter)localObject1);
          }
          else if ((localObject1 instanceof BackupImageView))
          {
            localObject3 = ((BackupImageView)localObject1).getImageReceiver().getStaticThumb();
            if ((localObject3 instanceof CombinedDrawable))
            {
              if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0)
              {
                localObject1 = ((CombinedDrawable)localObject3).getBackground();
                localObject3 = new android/graphics/PorterDuffColorFilter;
                ((PorterDuffColorFilter)localObject3).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
                ((Drawable)localObject1).setColorFilter((ColorFilter)localObject3);
              }
              else
              {
                localObject3 = ((CombinedDrawable)localObject3).getIcon();
                localObject1 = new android/graphics/PorterDuffColorFilter;
                ((PorterDuffColorFilter)localObject1).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
                ((Drawable)localObject3).setColorFilter((ColorFilter)localObject1);
              }
            }
            else if (localObject3 != null)
            {
              localObject1 = new android/graphics/PorterDuffColorFilter;
              ((PorterDuffColorFilter)localObject1).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
              ((Drawable)localObject3).setColorFilter((ColorFilter)localObject1);
            }
          }
          else if ((localObject1 instanceof Drawable))
          {
            if ((localObject1 instanceof LetterDrawable))
            {
              if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                ((LetterDrawable)localObject1).setBackgroundColor(paramInt);
              } else {
                ((LetterDrawable)localObject1).setColor(paramInt);
              }
            }
            else if ((localObject1 instanceof CombinedDrawable))
            {
              if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0)
              {
                localObject3 = ((CombinedDrawable)localObject1).getBackground();
                localObject1 = new android/graphics/PorterDuffColorFilter;
                ((PorterDuffColorFilter)localObject1).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
                ((Drawable)localObject3).setColorFilter((ColorFilter)localObject1);
              }
              else
              {
                localObject1 = ((CombinedDrawable)localObject1).getIcon();
                localObject3 = new android/graphics/PorterDuffColorFilter;
                ((PorterDuffColorFilter)localObject3).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
                ((Drawable)localObject1).setColorFilter((ColorFilter)localObject3);
              }
            }
            else
            {
              if (((localObject1 instanceof StateListDrawable)) || ((Build.VERSION.SDK_INT >= 21) && ((localObject1 instanceof RippleDrawable))))
              {
                localObject3 = (Drawable)localObject1;
                if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {}
                for (boolean bool = true;; bool = false)
                {
                  Theme.setSelectorDrawableColor((Drawable)localObject3, paramInt, bool);
                  break;
                }
              }
              localObject1 = (Drawable)localObject1;
              localObject3 = new android/graphics/PorterDuffColorFilter;
              ((PorterDuffColorFilter)localObject3).<init>(paramInt, PorterDuff.Mode.MULTIPLY);
              ((Drawable)localObject1).setColorFilter((ColorFilter)localObject3);
            }
          }
          else if ((localObject1 instanceof CheckBox))
          {
            if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
              ((CheckBox)localObject1).setBackgroundColor(paramInt);
            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
              ((CheckBox)localObject1).setCheckColor(paramInt);
            }
          }
          else if ((localObject1 instanceof GroupCreateCheckBox))
          {
            ((GroupCreateCheckBox)localObject1).updateColors();
          }
          else if ((localObject1 instanceof Integer))
          {
            ((Field)localObject3).set(paramView, Integer.valueOf(paramInt));
          }
          else if ((localObject1 instanceof RadioButton))
          {
            if ((this.changeFlags & FLAG_CHECKBOX) != 0)
            {
              ((RadioButton)localObject1).setBackgroundColor(paramInt);
              ((RadioButton)localObject1).invalidate();
            }
            else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0)
            {
              ((RadioButton)localObject1).setCheckedColor(paramInt);
              ((RadioButton)localObject1).invalidate();
            }
          }
          else if ((localObject1 instanceof TextPaint))
          {
            if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
              ((TextPaint)localObject1).linkColor = paramInt;
            } else {
              ((TextPaint)localObject1).setColor(paramInt);
            }
          }
          else if ((localObject1 instanceof LineProgressView))
          {
            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
              ((LineProgressView)localObject1).setProgressColor(paramInt);
            } else {
              ((LineProgressView)localObject1).setBackColor(paramInt);
            }
          }
          else if ((localObject1 instanceof Paint))
          {
            ((Paint)localObject1).setColor(paramInt);
          }
          else if ((localObject1 instanceof SeekBarView))
          {
            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0)
            {
              ((SeekBarView)localObject1).setOuterColor(paramInt);
            }
            else
            {
              ((SeekBarView)localObject1).setInnerColor(paramInt);
              continue;
              label1655:
              if ((paramView instanceof GroupCreateSpan)) {
                ((GroupCreateSpan)paramView).updateColors();
              }
            }
          }
        }
      }
    }
  }
  
  public int getCurrentColor()
  {
    return this.currentColor;
  }
  
  public String getCurrentKey()
  {
    return this.currentKey;
  }
  
  public int getSetColor()
  {
    return Theme.getColor(this.currentKey);
  }
  
  public String getTitle()
  {
    return this.currentKey;
  }
  
  public void setColor(int paramInt, boolean paramBoolean)
  {
    setColor(paramInt, paramBoolean, true);
  }
  
  public void setColor(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2) {
      Theme.setColor(this.currentKey, paramInt, paramBoolean1);
    }
    int i;
    if (this.paintToUpdate != null)
    {
      i = 0;
      if (i < this.paintToUpdate.length)
      {
        if (((this.changeFlags & FLAG_LINKCOLOR) != 0) && ((this.paintToUpdate[i] instanceof TextPaint))) {
          ((TextPaint)this.paintToUpdate[i]).linkColor = paramInt;
        }
        for (;;)
        {
          i++;
          break;
          this.paintToUpdate[i].setColor(paramInt);
        }
      }
    }
    if (this.drawablesToUpdate != null)
    {
      i = 0;
      if (i < this.drawablesToUpdate.length)
      {
        if (this.drawablesToUpdate[i] == null) {}
        for (;;)
        {
          i++;
          break;
          if ((this.drawablesToUpdate[i] instanceof CombinedDrawable))
          {
            if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
              ((CombinedDrawable)this.drawablesToUpdate[i]).getBackground().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
            } else {
              ((CombinedDrawable)this.drawablesToUpdate[i]).getIcon().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
            }
          }
          else if ((this.drawablesToUpdate[i] instanceof AvatarDrawable)) {
            ((AvatarDrawable)this.drawablesToUpdate[i]).setColor(paramInt);
          } else {
            this.drawablesToUpdate[i].setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          }
        }
      }
    }
    if ((this.viewToInvalidate != null) && (this.listClasses == null) && (this.listClassesFieldName == null) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || (((this.changeFlags & FLAG_CHECKTAG) != 0) && (this.currentKey.equals(this.viewToInvalidate.getTag())))))
    {
      if ((this.changeFlags & FLAG_BACKGROUND) != 0) {
        this.viewToInvalidate.setBackgroundColor(paramInt);
      }
      if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0)
      {
        localObject1 = this.viewToInvalidate.getBackground();
        localObject2 = localObject1;
        if ((localObject1 instanceof CombinedDrawable))
        {
          if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0) {
            break label1259;
          }
          localObject2 = ((CombinedDrawable)localObject1).getBackground();
        }
        if (localObject2 != null)
        {
          if ((!(localObject2 instanceof StateListDrawable)) && ((Build.VERSION.SDK_INT < 21) || (!(localObject2 instanceof RippleDrawable)))) {
            break label1277;
          }
          if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0) {
            break label1272;
          }
          paramBoolean1 = true;
          label439:
          Theme.setSelectorDrawableColor((Drawable)localObject2, paramInt, paramBoolean1);
        }
      }
    }
    label446:
    if ((this.viewToInvalidate instanceof ActionBar))
    {
      if ((this.changeFlags & FLAG_AB_ITEMSCOLOR) != 0) {
        ((ActionBar)this.viewToInvalidate).setItemsColor(paramInt, false);
      }
      if ((this.changeFlags & FLAG_AB_TITLECOLOR) != 0) {
        ((ActionBar)this.viewToInvalidate).setTitleColor(paramInt);
      }
      if ((this.changeFlags & FLAG_AB_SELECTORCOLOR) != 0) {
        ((ActionBar)this.viewToInvalidate).setItemsBackgroundColor(paramInt, false);
      }
      if ((this.changeFlags & FLAG_AB_AM_SELECTORCOLOR) != 0) {
        ((ActionBar)this.viewToInvalidate).setItemsBackgroundColor(paramInt, true);
      }
      if ((this.changeFlags & FLAG_AB_AM_ITEMSCOLOR) != 0) {
        ((ActionBar)this.viewToInvalidate).setItemsColor(paramInt, true);
      }
      if ((this.changeFlags & FLAG_AB_SUBTITLECOLOR) != 0) {
        ((ActionBar)this.viewToInvalidate).setSubtitleColor(paramInt);
      }
      if ((this.changeFlags & FLAG_AB_AM_BACKGROUND) != 0) {
        ((ActionBar)this.viewToInvalidate).setActionModeColor(paramInt);
      }
      if ((this.changeFlags & FLAG_AB_AM_TOPBACKGROUND) != 0) {
        ((ActionBar)this.viewToInvalidate).setActionModeTopColor(paramInt);
      }
      if ((this.changeFlags & FLAG_AB_SEARCHPLACEHOLDER) != 0) {
        ((ActionBar)this.viewToInvalidate).setSearchTextColor(paramInt, true);
      }
      if ((this.changeFlags & FLAG_AB_SEARCH) != 0) {
        ((ActionBar)this.viewToInvalidate).setSearchTextColor(paramInt, false);
      }
      if ((this.changeFlags & FLAG_AB_SUBMENUITEM) != 0) {
        ((ActionBar)this.viewToInvalidate).setPopupItemsColor(paramInt);
      }
      if ((this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0) {
        ((ActionBar)this.viewToInvalidate).setPopupBackgroundColor(paramInt);
      }
    }
    if ((this.viewToInvalidate instanceof EmptyTextProgressView))
    {
      if ((this.changeFlags & FLAG_TEXTCOLOR) != 0) {
        ((EmptyTextProgressView)this.viewToInvalidate).setTextColor(paramInt);
      }
    }
    else
    {
      label758:
      if (!(this.viewToInvalidate instanceof RadialProgressView)) {
        break label1344;
      }
      ((RadialProgressView)this.viewToInvalidate).setProgressColor(paramInt);
      label779:
      if (((this.changeFlags & FLAG_TEXTCOLOR) != 0) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || ((this.viewToInvalidate != null) && ((this.changeFlags & FLAG_CHECKTAG) != 0) && (this.currentKey.equals(this.viewToInvalidate.getTag())))))
      {
        if (!(this.viewToInvalidate instanceof TextView)) {
          break label1416;
        }
        ((TextView)this.viewToInvalidate).setTextColor(paramInt);
      }
      label857:
      if (((this.changeFlags & FLAG_CURSORCOLOR) != 0) && ((this.viewToInvalidate instanceof EditTextBoldCursor))) {
        ((EditTextBoldCursor)this.viewToInvalidate).setCursorColor(paramInt);
      }
      if ((this.changeFlags & FLAG_HINTTEXTCOLOR) != 0)
      {
        if (!(this.viewToInvalidate instanceof EditTextBoldCursor)) {
          break label1488;
        }
        ((EditTextBoldCursor)this.viewToInvalidate).setHintColor(paramInt);
      }
      label921:
      if ((this.viewToInvalidate != null) && ((this.changeFlags & FLAG_SERVICEBACKGROUND) != 0))
      {
        localObject2 = this.viewToInvalidate.getBackground();
        if (localObject2 != null) {
          ((Drawable)localObject2).setColorFilter(Theme.colorFilter);
        }
      }
      if (((this.changeFlags & FLAG_IMAGECOLOR) != 0) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || (((this.changeFlags & FLAG_CHECKTAG) != 0) && (this.currentKey.equals(this.viewToInvalidate.getTag())))))
      {
        if (!(this.viewToInvalidate instanceof ImageView)) {
          break label1541;
        }
        if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) == 0) {
          break label1517;
        }
        localObject2 = ((ImageView)this.viewToInvalidate).getDrawable();
        if (((localObject2 instanceof StateListDrawable)) || ((Build.VERSION.SDK_INT >= 21) && ((localObject2 instanceof RippleDrawable))))
        {
          if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0) {
            break label1512;
          }
          paramBoolean1 = true;
          label1081:
          Theme.setSelectorDrawableColor((Drawable)localObject2, paramInt, paramBoolean1);
        }
      }
    }
    for (;;)
    {
      if (((this.viewToInvalidate instanceof ScrollView)) && ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0)) {
        AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView)this.viewToInvalidate, paramInt);
      }
      if (!(this.viewToInvalidate instanceof RecyclerListView)) {
        break label1688;
      }
      localObject2 = (RecyclerListView)this.viewToInvalidate;
      if (((this.changeFlags & FLAG_SELECTOR) != 0) && (this.currentKey.equals("listSelectorSDK21"))) {
        ((RecyclerListView)localObject2).setListSelectorColor(paramInt);
      }
      if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
        ((RecyclerListView)localObject2).updateFastScrollColors();
      }
      if ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
        ((RecyclerListView)localObject2).setGlowColor(paramInt);
      }
      if ((this.changeFlags & FLAG_SECTIONS) == 0) {
        break label1619;
      }
      localObject1 = ((RecyclerListView)localObject2).getHeaders();
      if (localObject1 == null) {
        break label1554;
      }
      for (i = 0; i < ((ArrayList)localObject1).size(); i++) {
        processViewColor((View)((ArrayList)localObject1).get(i), paramInt);
      }
      label1259:
      localObject2 = ((CombinedDrawable)localObject1).getIcon();
      break;
      label1272:
      paramBoolean1 = false;
      break label439;
      label1277:
      if ((localObject2 instanceof ShapeDrawable))
      {
        ((ShapeDrawable)localObject2).getPaint().setColor(paramInt);
        break label446;
      }
      ((Drawable)localObject2).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      break label446;
      if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
        break label758;
      }
      ((EmptyTextProgressView)this.viewToInvalidate).setProgressBarColor(paramInt);
      break label758;
      label1344:
      if ((this.viewToInvalidate instanceof LineProgressView))
      {
        if ((this.changeFlags & FLAG_PROGRESSBAR) != 0)
        {
          ((LineProgressView)this.viewToInvalidate).setProgressColor(paramInt);
          break label779;
        }
        ((LineProgressView)this.viewToInvalidate).setBackColor(paramInt);
        break label779;
      }
      if (!(this.viewToInvalidate instanceof ContextProgressView)) {
        break label779;
      }
      ((ContextProgressView)this.viewToInvalidate).updateColors();
      break label779;
      label1416:
      if ((this.viewToInvalidate instanceof NumberTextView))
      {
        ((NumberTextView)this.viewToInvalidate).setTextColor(paramInt);
        break label857;
      }
      if ((this.viewToInvalidate instanceof SimpleTextView))
      {
        ((SimpleTextView)this.viewToInvalidate).setTextColor(paramInt);
        break label857;
      }
      if (!(this.viewToInvalidate instanceof ChatBigEmptyView)) {
        break label857;
      }
      ((ChatBigEmptyView)this.viewToInvalidate).setTextColor(paramInt);
      break label857;
      label1488:
      if (!(this.viewToInvalidate instanceof EditText)) {
        break label921;
      }
      ((EditText)this.viewToInvalidate).setHintTextColor(paramInt);
      break label921;
      label1512:
      paramBoolean1 = false;
      break label1081;
      label1517:
      ((ImageView)this.viewToInvalidate).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      continue;
      label1541:
      if (!(this.viewToInvalidate instanceof BackupImageView)) {}
    }
    label1554:
    Object localObject1 = ((RecyclerListView)localObject2).getHeadersCache();
    if (localObject1 != null) {
      for (i = 0; i < ((ArrayList)localObject1).size(); i++) {
        processViewColor((View)((ArrayList)localObject1).get(i), paramInt);
      }
    }
    Object localObject2 = ((RecyclerListView)localObject2).getPinnedHeader();
    if (localObject2 != null) {
      processViewColor((View)localObject2, paramInt);
    }
    label1619:
    while (this.listClasses != null)
    {
      int j;
      if ((this.viewToInvalidate instanceof RecyclerListView))
      {
        localObject2 = (RecyclerListView)this.viewToInvalidate;
        ((RecyclerListView)localObject2).getRecycledViewPool().clear();
        j = ((RecyclerListView)localObject2).getHiddenChildCount();
        i = 0;
        for (;;)
        {
          if (i < j)
          {
            processViewColor(((RecyclerListView)localObject2).getHiddenChildAt(i), paramInt);
            i++;
            continue;
            label1688:
            if (this.viewToInvalidate == null) {
              break;
            }
            if ((this.changeFlags & FLAG_SELECTOR) != 0)
            {
              this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
              break;
            }
            if ((this.changeFlags & FLAG_SELECTORWHITE) == 0) {
              break;
            }
            this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            break;
          }
        }
        j = ((RecyclerListView)localObject2).getCachedChildCount();
        for (i = 0; i < j; i++) {
          processViewColor(((RecyclerListView)localObject2).getCachedChildAt(i), paramInt);
        }
        j = ((RecyclerListView)localObject2).getAttachedScrapChildCount();
        for (i = 0; i < j; i++) {
          processViewColor(((RecyclerListView)localObject2).getAttachedScrapChildAt(i), paramInt);
        }
      }
      if ((this.viewToInvalidate instanceof ViewGroup))
      {
        localObject2 = (ViewGroup)this.viewToInvalidate;
        j = ((ViewGroup)localObject2).getChildCount();
        for (i = 0; i < j; i++) {
          processViewColor(((ViewGroup)localObject2).getChildAt(i), paramInt);
        }
      }
      processViewColor(this.viewToInvalidate, paramInt);
    }
    this.currentColor = paramInt;
    if (this.delegate != null) {
      this.delegate.didSetColor();
    }
    if (this.viewToInvalidate != null) {
      this.viewToInvalidate.invalidate();
    }
  }
  
  public void setDefaultColor()
  {
    setColor(Theme.getDefaultColor(this.currentKey), true);
  }
  
  public ThemeDescriptionDelegate setDelegateDisabled()
  {
    ThemeDescriptionDelegate localThemeDescriptionDelegate = this.delegate;
    this.delegate = null;
    return localThemeDescriptionDelegate;
  }
  
  public void setPreviousColor()
  {
    setColor(this.previousColor, this.previousIsDefault[0]);
  }
  
  public void startEditing()
  {
    int i = Theme.getColor(this.currentKey, this.previousIsDefault);
    this.previousColor = i;
    this.currentColor = i;
  }
  
  public static abstract interface ThemeDescriptionDelegate
  {
    public abstract void didSetColor();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/ThemeDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */