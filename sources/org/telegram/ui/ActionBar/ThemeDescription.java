package org.telegram.ui.ActionBar;

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

public class ThemeDescription {
    public static int FLAG_AB_AM_BACKGROUND = 1048576;
    public static int FLAG_AB_AM_ITEMSCOLOR = 512;
    public static int FLAG_AB_AM_SELECTORCOLOR = 4194304;
    public static int FLAG_AB_AM_TOPBACKGROUND = 2097152;
    public static int FLAG_AB_ITEMSCOLOR = 64;
    public static int FLAG_AB_SEARCH = 134217728;
    public static int FLAG_AB_SEARCHPLACEHOLDER = 67108864;
    public static int FLAG_AB_SELECTORCOLOR = 256;
    public static int FLAG_AB_SUBMENUBACKGROUND = Integer.MIN_VALUE;
    public static int FLAG_AB_SUBMENUITEM = NUM;
    public static int FLAG_AB_SUBTITLECOLOR = 1024;
    public static int FLAG_AB_TITLECOLOR = 128;
    public static int FLAG_BACKGROUND = 1;
    public static int FLAG_BACKGROUNDFILTER = 32;
    public static int FLAG_CELLBACKGROUNDCOLOR = 16;
    public static int FLAG_CHECKBOX = 8192;
    public static int FLAG_CHECKBOXCHECK = 16384;
    public static int FLAG_CHECKTAG = 262144;
    public static int FLAG_CURSORCOLOR = 16777216;
    public static int FLAG_DRAWABLESELECTEDSTATE = 65536;
    public static int FLAG_FASTSCROLL = 33554432;
    public static int FLAG_HINTTEXTCOLOR = 8388608;
    public static int FLAG_IMAGECOLOR = 8;
    public static int FLAG_LINKCOLOR = 2;
    public static int FLAG_LISTGLOWCOLOR = 32768;
    public static int FLAG_PROGRESSBAR = 2048;
    public static int FLAG_SECTIONS = 524288;
    public static int FLAG_SELECTOR = 4096;
    public static int FLAG_SELECTORWHITE = 268435456;
    public static int FLAG_SERVICEBACKGROUND = 536870912;
    public static int FLAG_TEXTCOLOR = 4;
    public static int FLAG_USEBACKGROUNDDRAWABLE = 131072;
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
    private boolean[] previousIsDefault;
    private View viewToInvalidate;

    public interface ThemeDescriptionDelegate {
        void didSetColor();
    }

    public ThemeDescription(View view, int i, Class[] clsArr, Paint[] paintArr, Drawable[] drawableArr, ThemeDescriptionDelegate themeDescriptionDelegate, String str, Object obj) {
        this.previousIsDefault = new boolean[1];
        this.currentKey = str;
        this.paintToUpdate = paintArr;
        this.drawablesToUpdate = drawableArr;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        this.delegate = themeDescriptionDelegate;
    }

    public ThemeDescription(View view, int i, Class[] clsArr, Paint paint, Drawable[] drawableArr, ThemeDescriptionDelegate themeDescriptionDelegate, String str) {
        this.previousIsDefault = new boolean[1];
        this.currentKey = str;
        if (paint != null) {
            this.paintToUpdate = new Paint[]{paint};
        }
        this.drawablesToUpdate = drawableArr;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        this.delegate = themeDescriptionDelegate;
    }

    public ThemeDescription(View view, int i, Class[] clsArr, String[] strArr, Paint[] paintArr, Drawable[] drawableArr, ThemeDescriptionDelegate themeDescriptionDelegate, String str) {
        this.previousIsDefault = new boolean[1];
        this.currentKey = str;
        this.paintToUpdate = paintArr;
        this.drawablesToUpdate = drawableArr;
        this.viewToInvalidate = view;
        this.changeFlags = i;
        this.listClasses = clsArr;
        this.listClassesFieldName = strArr;
        this.delegate = themeDescriptionDelegate;
        this.cachedFields = new HashMap();
        this.notFoundCachedFields = new HashMap();
    }

    public ThemeDescriptionDelegate setDelegateDisabled() {
        ThemeDescriptionDelegate themeDescriptionDelegate = this.delegate;
        this.delegate = null;
        return themeDescriptionDelegate;
    }

    public void setColor(int i, boolean z) {
        setColor(i, z, true);
    }

    public void setColor(int i, boolean z, boolean z2) {
        RecyclerListView recyclerListView;
        int i2;
        if (z2) {
            Theme.setColor(this.currentKey, i, z);
        }
        z2 = false;
        if (this.paintToUpdate) {
            z = false;
            while (z < this.paintToUpdate.length) {
                if ((this.changeFlags & FLAG_LINKCOLOR) == 0 || !(this.paintToUpdate[z] instanceof TextPaint)) {
                    this.paintToUpdate[z].setColor(i);
                } else {
                    ((TextPaint) this.paintToUpdate[z]).linkColor = i;
                }
                z++;
            }
        }
        if (this.drawablesToUpdate) {
            for (z = false; z < this.drawablesToUpdate.length; z++) {
                if (this.drawablesToUpdate[z] != null) {
                    if (this.drawablesToUpdate[z] instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                            ((CombinedDrawable) this.drawablesToUpdate[z]).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        } else {
                            ((CombinedDrawable) this.drawablesToUpdate[z]).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        }
                    } else if (this.drawablesToUpdate[z] instanceof AvatarDrawable) {
                        ((AvatarDrawable) this.drawablesToUpdate[z]).setColor(i);
                    } else {
                        this.drawablesToUpdate[z].setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                    }
                }
            }
        }
        if (this.viewToInvalidate && !this.listClasses && !this.listClassesFieldName && (!(this.changeFlags & FLAG_CHECKTAG) || ((this.changeFlags & FLAG_CHECKTAG) && this.currentKey.equals(this.viewToInvalidate.getTag())))) {
            if (this.changeFlags & FLAG_BACKGROUND) {
                this.viewToInvalidate.setBackgroundColor(i);
            }
            if (this.changeFlags & FLAG_BACKGROUNDFILTER) {
                z = this.viewToInvalidate.getBackground();
                if (z instanceof CombinedDrawable) {
                    if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                        z = ((CombinedDrawable) z).getBackground();
                    } else {
                        z = ((CombinedDrawable) z).getIcon();
                    }
                }
                if (z) {
                    if (!(z instanceof StateListDrawable)) {
                        if (VERSION.SDK_INT < 21 || !(z instanceof RippleDrawable)) {
                            if (z instanceof ShapeDrawable) {
                                ((ShapeDrawable) z).getPaint().setColor(i);
                            } else {
                                z.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                            }
                        }
                    }
                    Theme.setSelectorDrawableColor(z, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                }
            }
        }
        if (this.viewToInvalidate instanceof ActionBar) {
            if (this.changeFlags & FLAG_AB_ITEMSCOLOR) {
                ((ActionBar) this.viewToInvalidate).setItemsColor(i, false);
            }
            if (this.changeFlags & FLAG_AB_TITLECOLOR) {
                ((ActionBar) this.viewToInvalidate).setTitleColor(i);
            }
            if (this.changeFlags & FLAG_AB_SELECTORCOLOR) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(i, false);
            }
            if (this.changeFlags & FLAG_AB_AM_SELECTORCOLOR) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(i, true);
            }
            if (this.changeFlags & FLAG_AB_AM_ITEMSCOLOR) {
                ((ActionBar) this.viewToInvalidate).setItemsColor(i, true);
            }
            if (this.changeFlags & FLAG_AB_SUBTITLECOLOR) {
                ((ActionBar) this.viewToInvalidate).setSubtitleColor(i);
            }
            if (this.changeFlags & FLAG_AB_AM_BACKGROUND) {
                ((ActionBar) this.viewToInvalidate).setActionModeColor(i);
            }
            if (this.changeFlags & FLAG_AB_AM_TOPBACKGROUND) {
                ((ActionBar) this.viewToInvalidate).setActionModeTopColor(i);
            }
            if (this.changeFlags & FLAG_AB_SEARCHPLACEHOLDER) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(i, true);
            }
            if (this.changeFlags & FLAG_AB_SEARCH) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(i, false);
            }
            if (this.changeFlags & FLAG_AB_SUBMENUITEM) {
                ((ActionBar) this.viewToInvalidate).setPopupItemsColor(i);
            }
            if (this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) {
                ((ActionBar) this.viewToInvalidate).setPopupBackgroundColor(i);
            }
        }
        if (this.viewToInvalidate instanceof EmptyTextProgressView) {
            if (this.changeFlags & FLAG_TEXTCOLOR) {
                ((EmptyTextProgressView) this.viewToInvalidate).setTextColor(i);
            } else if (this.changeFlags & FLAG_PROGRESSBAR) {
                ((EmptyTextProgressView) this.viewToInvalidate).setProgressBarColor(i);
            }
        }
        if (this.viewToInvalidate instanceof RadialProgressView) {
            ((RadialProgressView) this.viewToInvalidate).setProgressColor(i);
        } else if (this.viewToInvalidate instanceof LineProgressView) {
            if (this.changeFlags & FLAG_PROGRESSBAR) {
                ((LineProgressView) this.viewToInvalidate).setProgressColor(i);
            } else {
                ((LineProgressView) this.viewToInvalidate).setBackColor(i);
            }
        } else if (this.viewToInvalidate instanceof ContextProgressView) {
            ((ContextProgressView) this.viewToInvalidate).updateColors();
        }
        if ((this.changeFlags & FLAG_TEXTCOLOR) && (!(this.changeFlags & FLAG_CHECKTAG) || (this.viewToInvalidate && (this.changeFlags & FLAG_CHECKTAG) && this.currentKey.equals(this.viewToInvalidate.getTag())))) {
            if (this.viewToInvalidate instanceof TextView) {
                ((TextView) this.viewToInvalidate).setTextColor(i);
            } else if (this.viewToInvalidate instanceof NumberTextView) {
                ((NumberTextView) this.viewToInvalidate).setTextColor(i);
            } else if (this.viewToInvalidate instanceof SimpleTextView) {
                ((SimpleTextView) this.viewToInvalidate).setTextColor(i);
            } else if (this.viewToInvalidate instanceof ChatBigEmptyView) {
                ((ChatBigEmptyView) this.viewToInvalidate).setTextColor(i);
            }
        }
        if ((this.changeFlags & FLAG_CURSORCOLOR) && (this.viewToInvalidate instanceof EditTextBoldCursor)) {
            ((EditTextBoldCursor) this.viewToInvalidate).setCursorColor(i);
        }
        if (this.changeFlags & FLAG_HINTTEXTCOLOR) {
            if (this.viewToInvalidate instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) this.viewToInvalidate).setHintColor(i);
            } else if (this.viewToInvalidate instanceof EditText) {
                ((EditText) this.viewToInvalidate).setHintTextColor(i);
            }
        }
        if (this.viewToInvalidate && (this.changeFlags & FLAG_SERVICEBACKGROUND)) {
            z = this.viewToInvalidate.getBackground();
            if (z) {
                z.setColorFilter(Theme.colorFilter);
            }
        }
        if ((this.changeFlags & FLAG_IMAGECOLOR) && (!(this.changeFlags & FLAG_CHECKTAG) || ((this.changeFlags & FLAG_CHECKTAG) && this.currentKey.equals(this.viewToInvalidate.getTag())))) {
            if (!(this.viewToInvalidate instanceof ImageView)) {
                z = this.viewToInvalidate instanceof BackupImageView;
            } else if (this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) {
                z = ((ImageView) this.viewToInvalidate).getDrawable();
                if ((z instanceof StateListDrawable) || (VERSION.SDK_INT >= 21 && (z instanceof RippleDrawable))) {
                    Theme.setSelectorDrawableColor(z, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                }
            } else {
                ((ImageView) this.viewToInvalidate).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
            }
        }
        if ((this.viewToInvalidate instanceof ScrollView) && (this.changeFlags & FLAG_LISTGLOWCOLOR)) {
            AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) this.viewToInvalidate, i);
        }
        if (this.viewToInvalidate instanceof RecyclerListView) {
            recyclerListView = (RecyclerListView) this.viewToInvalidate;
            if ((this.changeFlags & FLAG_SELECTOR) != 0 && this.currentKey.equals(Theme.key_listSelector)) {
                recyclerListView.setListSelectorColor(i);
            }
            if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                recyclerListView.updateFastScrollColors();
            }
            if ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
                recyclerListView.setGlowColor(i);
            }
            if ((this.changeFlags & FLAG_SECTIONS) != 0) {
                ArrayList headers = recyclerListView.getHeaders();
                if (headers != null) {
                    for (i2 = 0; i2 < headers.size(); i2++) {
                        processViewColor((View) headers.get(i2), i);
                    }
                }
                headers = recyclerListView.getHeadersCache();
                if (headers != null) {
                    for (i2 = 0; i2 < headers.size(); i2++) {
                        processViewColor((View) headers.get(i2), i);
                    }
                }
                z = recyclerListView.getPinnedHeader();
                if (z) {
                    processViewColor(z, i);
                }
            }
        } else if (this.viewToInvalidate) {
            if (this.changeFlags & FLAG_SELECTOR) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if (this.changeFlags & FLAG_SELECTORWHITE) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            }
        }
        if (this.listClasses) {
            if (this.viewToInvalidate instanceof RecyclerListView) {
                recyclerListView = (RecyclerListView) this.viewToInvalidate;
                recyclerListView.getRecycledViewPool().clear();
                int hiddenChildCount = recyclerListView.getHiddenChildCount();
                for (i2 = 0; i2 < hiddenChildCount; i2++) {
                    processViewColor(recyclerListView.getHiddenChildAt(i2), i);
                }
                hiddenChildCount = recyclerListView.getCachedChildCount();
                for (i2 = 0; i2 < hiddenChildCount; i2++) {
                    processViewColor(recyclerListView.getCachedChildAt(i2), i);
                }
                hiddenChildCount = recyclerListView.getAttachedScrapChildCount();
                for (i2 = 0; i2 < hiddenChildCount; i2++) {
                    processViewColor(recyclerListView.getAttachedScrapChildAt(i2), i);
                }
            }
            if (this.viewToInvalidate instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) this.viewToInvalidate;
                boolean childCount = viewGroup.getChildCount();
                while (z2 < childCount) {
                    processViewColor(viewGroup.getChildAt(z2), i);
                    z2++;
                }
            }
            processViewColor(this.viewToInvalidate, i);
        }
        this.currentColor = i;
        if (this.delegate != 0) {
            this.delegate.didSetColor();
        }
        if (this.viewToInvalidate != 0) {
            this.viewToInvalidate.invalidate();
        }
    }

    private void processViewColor(View view, int i) {
        for (int i2 = 0; i2 < this.listClasses.length; i2++) {
            if (this.listClasses[i2].isInstance(view)) {
                int i3;
                StringBuilder stringBuilder;
                String stringBuilder2;
                Field field;
                Object obj;
                TextView textView;
                Drawable[] compoundDrawables;
                int i4;
                CharSequence text;
                TypefaceSpan[] typefaceSpanArr;
                Drawable staticThumb;
                view.invalidate();
                if ((this.changeFlags & FLAG_CHECKTAG) != 0) {
                    if ((this.changeFlags & FLAG_CHECKTAG) == 0 || !this.currentKey.equals(view.getTag())) {
                        i3 = 0;
                        if (this.listClassesFieldName != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(this.listClasses[i2]);
                            stringBuilder.append("_");
                            stringBuilder.append(this.listClassesFieldName[i2]);
                            stringBuilder2 = stringBuilder.toString();
                            if (this.notFoundCachedFields != null || !this.notFoundCachedFields.containsKey(stringBuilder2)) {
                                try {
                                    field = (Field) this.cachedFields.get(stringBuilder2);
                                    if (field == null) {
                                        field = this.listClasses[i2].getDeclaredField(this.listClassesFieldName[i2]);
                                        if (field != null) {
                                            field.setAccessible(true);
                                            this.cachedFields.put(stringBuilder2, field);
                                        }
                                    }
                                    if (field != null) {
                                        obj = field.get(view);
                                        if (obj == null) {
                                            if (i3 == 0 || !(obj instanceof View) || this.currentKey.equals(((View) obj).getTag())) {
                                                if (obj instanceof View) {
                                                    ((View) obj).invalidate();
                                                }
                                                if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0 && (obj instanceof View)) {
                                                    obj = ((View) obj).getBackground();
                                                }
                                                if ((this.changeFlags & FLAG_BACKGROUND) == 0 && (obj instanceof View)) {
                                                    ((View) obj).setBackgroundColor(i);
                                                } else if (obj instanceof Switch) {
                                                    ((Switch) obj).checkColorFilters();
                                                } else if (obj instanceof EditTextCaption) {
                                                    if (obj instanceof SimpleTextView) {
                                                        if (obj instanceof TextView) {
                                                            textView = (TextView) obj;
                                                            if ((this.changeFlags & FLAG_IMAGECOLOR) != 0) {
                                                                compoundDrawables = textView.getCompoundDrawables();
                                                                if (compoundDrawables != null) {
                                                                    for (i4 = 0; i4 < compoundDrawables.length; i4++) {
                                                                        if (compoundDrawables[i4] != null) {
                                                                            compoundDrawables[i4].setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                                        }
                                                                    }
                                                                }
                                                            } else if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                                textView.getPaint().linkColor = i;
                                                                textView.invalidate();
                                                            } else if ((this.changeFlags & FLAG_FASTSCROLL) == 0) {
                                                                text = textView.getText();
                                                                if (text instanceof SpannedString) {
                                                                    typefaceSpanArr = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class);
                                                                    if (typefaceSpanArr != null && typefaceSpanArr.length > 0) {
                                                                        for (TypefaceSpan color : typefaceSpanArr) {
                                                                            color.setColor(i);
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                textView.setTextColor(i);
                                                            }
                                                        } else if (obj instanceof ImageView) {
                                                            ((ImageView) obj).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                        } else if (obj instanceof BackupImageView) {
                                                            staticThumb = ((BackupImageView) obj).getImageReceiver().getStaticThumb();
                                                            if (staticThumb instanceof CombinedDrawable) {
                                                                if (staticThumb != null) {
                                                                    staticThumb.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                                }
                                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                                                ((CombinedDrawable) staticThumb).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                            } else {
                                                                ((CombinedDrawable) staticThumb).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                            }
                                                        } else if (obj instanceof Drawable) {
                                                            if (obj instanceof CheckBox) {
                                                                if (obj instanceof GroupCreateCheckBox) {
                                                                    ((GroupCreateCheckBox) obj).updateColors();
                                                                } else if (obj instanceof Integer) {
                                                                    field.set(view, Integer.valueOf(i));
                                                                } else if (obj instanceof RadioButton) {
                                                                    if (obj instanceof TextPaint) {
                                                                        if (obj instanceof LineProgressView) {
                                                                            if (obj instanceof Paint) {
                                                                                ((Paint) obj).setColor(i);
                                                                            } else if (!(obj instanceof SeekBarView)) {
                                                                                if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                                                    ((SeekBarView) obj).setOuterColor(i);
                                                                                } else {
                                                                                    ((SeekBarView) obj).setInnerColor(i);
                                                                                }
                                                                            }
                                                                        } else if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                                            ((LineProgressView) obj).setProgressColor(i);
                                                                        } else {
                                                                            ((LineProgressView) obj).setBackColor(i);
                                                                        }
                                                                    } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                                                        ((TextPaint) obj).linkColor = i;
                                                                    } else {
                                                                        ((TextPaint) obj).setColor(i);
                                                                    }
                                                                } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                                    ((RadioButton) obj).setBackgroundColor(i);
                                                                    ((RadioButton) obj).invalidate();
                                                                } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                                    ((RadioButton) obj).setCheckedColor(i);
                                                                    ((RadioButton) obj).invalidate();
                                                                }
                                                            } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                                ((CheckBox) obj).setBackgroundColor(i);
                                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                                ((CheckBox) obj).setCheckColor(i);
                                                            }
                                                        } else if (obj instanceof LetterDrawable) {
                                                            if (obj instanceof CombinedDrawable) {
                                                                if (!(obj instanceof StateListDrawable)) {
                                                                    if (VERSION.SDK_INT >= 21 || !(obj instanceof RippleDrawable)) {
                                                                        ((Drawable) obj).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                                    }
                                                                }
                                                                Theme.setSelectorDrawableColor((Drawable) obj, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0);
                                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                                                ((CombinedDrawable) obj).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                            } else {
                                                                ((CombinedDrawable) obj).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                            }
                                                        } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                                            ((LetterDrawable) obj).setBackgroundColor(i);
                                                        } else {
                                                            ((LetterDrawable) obj).setColor(i);
                                                        }
                                                    } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                                        ((SimpleTextView) obj).setLinkTextColor(i);
                                                    } else {
                                                        ((SimpleTextView) obj).setTextColor(i);
                                                    }
                                                } else if ((this.changeFlags & FLAG_HINTTEXTCOLOR) == 0) {
                                                    ((EditTextCaption) obj).setHintColor(i);
                                                    ((EditTextCaption) obj).setHintTextColor(i);
                                                } else {
                                                    ((EditTextCaption) obj).setTextColor(i);
                                                }
                                            }
                                        }
                                    }
                                } catch (Throwable th) {
                                    FileLog.m3e(th);
                                    this.notFoundCachedFields.put(stringBuilder2, Boolean.valueOf(true));
                                }
                            }
                        } else if (view instanceof GroupCreateSpan) {
                            ((GroupCreateSpan) view).updateColors();
                        }
                    }
                }
                view.invalidate();
                if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                    staticThumb = view.getBackground();
                    if (staticThumb != null) {
                        if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0) {
                            if (staticThumb instanceof CombinedDrawable) {
                                staticThumb = ((CombinedDrawable) staticThumb).getIcon();
                            }
                            staticThumb.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        } else if (staticThumb instanceof CombinedDrawable) {
                            staticThumb = ((CombinedDrawable) staticThumb).getBackground();
                            if (staticThumb instanceof ColorDrawable) {
                                ((ColorDrawable) staticThumb).setColor(i);
                            }
                        }
                    }
                } else if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) != 0) {
                    view.setBackgroundColor(i);
                } else if ((this.changeFlags & FLAG_TEXTCOLOR) != 0) {
                    if (view instanceof TextView) {
                        ((TextView) view).setTextColor(i);
                    }
                } else if ((this.changeFlags & FLAG_SERVICEBACKGROUND) != 0) {
                    staticThumb = view.getBackground();
                    if (staticThumb != null) {
                        staticThumb.setColorFilter(Theme.colorFilter);
                    }
                }
                i3 = true;
                if (this.listClassesFieldName != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.listClasses[i2]);
                    stringBuilder.append("_");
                    stringBuilder.append(this.listClassesFieldName[i2]);
                    stringBuilder2 = stringBuilder.toString();
                    if (this.notFoundCachedFields != null) {
                    }
                    field = (Field) this.cachedFields.get(stringBuilder2);
                    if (field == null) {
                        field = this.listClasses[i2].getDeclaredField(this.listClassesFieldName[i2]);
                        if (field != null) {
                            field.setAccessible(true);
                            this.cachedFields.put(stringBuilder2, field);
                        }
                    }
                    if (field != null) {
                        obj = field.get(view);
                        if (obj == null) {
                            if (i3 == 0) {
                            }
                            if (obj instanceof View) {
                                ((View) obj).invalidate();
                            }
                            obj = ((View) obj).getBackground();
                            if ((this.changeFlags & FLAG_BACKGROUND) == 0) {
                            }
                            if (obj instanceof Switch) {
                                ((Switch) obj).checkColorFilters();
                            } else if (obj instanceof EditTextCaption) {
                                if (obj instanceof SimpleTextView) {
                                    if (obj instanceof TextView) {
                                        textView = (TextView) obj;
                                        if ((this.changeFlags & FLAG_IMAGECOLOR) != 0) {
                                            compoundDrawables = textView.getCompoundDrawables();
                                            if (compoundDrawables != null) {
                                                for (i4 = 0; i4 < compoundDrawables.length; i4++) {
                                                    if (compoundDrawables[i4] != null) {
                                                        compoundDrawables[i4].setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                                    }
                                                }
                                            }
                                        } else if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                            textView.getPaint().linkColor = i;
                                            textView.invalidate();
                                        } else if ((this.changeFlags & FLAG_FASTSCROLL) == 0) {
                                            textView.setTextColor(i);
                                        } else {
                                            text = textView.getText();
                                            if (text instanceof SpannedString) {
                                                typefaceSpanArr = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class);
                                                while (i4 < typefaceSpanArr.length) {
                                                    color.setColor(i);
                                                }
                                            }
                                        }
                                    } else if (obj instanceof ImageView) {
                                        ((ImageView) obj).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                    } else if (obj instanceof BackupImageView) {
                                        staticThumb = ((BackupImageView) obj).getImageReceiver().getStaticThumb();
                                        if (staticThumb instanceof CombinedDrawable) {
                                            if (staticThumb != null) {
                                                staticThumb.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                            }
                                        } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                            ((CombinedDrawable) staticThumb).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                        } else {
                                            ((CombinedDrawable) staticThumb).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                        }
                                    } else if (obj instanceof Drawable) {
                                        if (obj instanceof CheckBox) {
                                            if (obj instanceof GroupCreateCheckBox) {
                                                ((GroupCreateCheckBox) obj).updateColors();
                                            } else if (obj instanceof Integer) {
                                                field.set(view, Integer.valueOf(i));
                                            } else if (obj instanceof RadioButton) {
                                                if (obj instanceof TextPaint) {
                                                    if (obj instanceof LineProgressView) {
                                                        if (obj instanceof Paint) {
                                                            ((Paint) obj).setColor(i);
                                                        } else if (!(obj instanceof SeekBarView)) {
                                                            if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                                ((SeekBarView) obj).setInnerColor(i);
                                                            } else {
                                                                ((SeekBarView) obj).setOuterColor(i);
                                                            }
                                                        }
                                                    } else if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                        ((LineProgressView) obj).setBackColor(i);
                                                    } else {
                                                        ((LineProgressView) obj).setProgressColor(i);
                                                    }
                                                } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                                    ((TextPaint) obj).setColor(i);
                                                } else {
                                                    ((TextPaint) obj).linkColor = i;
                                                }
                                            } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                ((RadioButton) obj).setBackgroundColor(i);
                                                ((RadioButton) obj).invalidate();
                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                ((RadioButton) obj).setCheckedColor(i);
                                                ((RadioButton) obj).invalidate();
                                            }
                                        } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                            ((CheckBox) obj).setBackgroundColor(i);
                                        } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                            ((CheckBox) obj).setCheckColor(i);
                                        }
                                    } else if (obj instanceof LetterDrawable) {
                                        if (obj instanceof CombinedDrawable) {
                                            if (obj instanceof StateListDrawable) {
                                                if (VERSION.SDK_INT >= 21) {
                                                }
                                                ((Drawable) obj).setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                            }
                                            if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0) {
                                            }
                                            Theme.setSelectorDrawableColor((Drawable) obj, i, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0);
                                        } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                            ((CombinedDrawable) obj).getIcon().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                        } else {
                                            ((CombinedDrawable) obj).getBackground().setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                                        }
                                    } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                        ((LetterDrawable) obj).setColor(i);
                                    } else {
                                        ((LetterDrawable) obj).setBackgroundColor(i);
                                    }
                                } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                    ((SimpleTextView) obj).setTextColor(i);
                                } else {
                                    ((SimpleTextView) obj).setLinkTextColor(i);
                                }
                            } else if ((this.changeFlags & FLAG_HINTTEXTCOLOR) == 0) {
                                ((EditTextCaption) obj).setTextColor(i);
                            } else {
                                ((EditTextCaption) obj).setHintColor(i);
                                ((EditTextCaption) obj).setHintTextColor(i);
                            }
                        }
                    }
                } else if (view instanceof GroupCreateSpan) {
                    ((GroupCreateSpan) view).updateColors();
                }
            }
        }
    }

    public String getCurrentKey() {
        return this.currentKey;
    }

    public void startEditing() {
        int color = Theme.getColor(this.currentKey, this.previousIsDefault);
        this.previousColor = color;
        this.currentColor = color;
    }

    public int getCurrentColor() {
        return this.currentColor;
    }

    public int getSetColor() {
        return Theme.getColor(this.currentKey);
    }

    public void setDefaultColor() {
        setColor(Theme.getDefaultColor(this.currentKey), true);
    }

    public void setPreviousColor() {
        setColor(this.previousColor, this.previousIsDefault[0]);
    }

    public String getTitle() {
        return this.currentKey;
    }
}
