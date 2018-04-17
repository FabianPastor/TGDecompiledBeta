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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.telegram.tgnet.ConnectionsManager;
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
    public static int FLAG_AB_AM_BACKGROUND = ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES;
    public static int FLAG_AB_AM_ITEMSCOLOR = 512;
    public static int FLAG_AB_AM_SELECTORCOLOR = 4194304;
    public static int FLAG_AB_AM_TOPBACKGROUND = 2097152;
    public static int FLAG_AB_ITEMSCOLOR = 64;
    public static int FLAG_AB_SEARCH = 134217728;
    public static int FLAG_AB_SEARCHPLACEHOLDER = ConnectionsManager.FileTypeFile;
    public static int FLAG_AB_SELECTORCOLOR = 256;
    public static int FLAG_AB_SUBMENUBACKGROUND = Integer.MIN_VALUE;
    public static int FLAG_AB_SUBMENUITEM = NUM;
    public static int FLAG_AB_SUBTITLECOLOR = 1024;
    public static int FLAG_AB_TITLECOLOR = 128;
    public static int FLAG_BACKGROUND = 1;
    public static int FLAG_BACKGROUNDFILTER = 32;
    public static int FLAG_CELLBACKGROUNDCOLOR = 16;
    public static int FLAG_CHECKBOX = MessagesController.UPDATE_MASK_CHANNEL;
    public static int FLAG_CHECKBOXCHECK = MessagesController.UPDATE_MASK_CHAT_ADMINS;
    public static int FLAG_CHECKTAG = 262144;
    public static int FLAG_CURSORCOLOR = 16777216;
    public static int FLAG_DRAWABLESELECTEDSTATE = C0542C.DEFAULT_BUFFER_SEGMENT_SIZE;
    public static int FLAG_FASTSCROLL = ConnectionsManager.FileTypeVideo;
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

    public ThemeDescription(View view, int flags, Class[] classes, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key, Object unused) {
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.paintToUpdate = paint;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.delegate = themeDescriptionDelegate;
    }

    public ThemeDescription(View view, int flags, Class[] classes, Paint paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        if (paint != null) {
            this.paintToUpdate = new Paint[]{paint};
        }
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.delegate = themeDescriptionDelegate;
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.paintToUpdate = paint;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.listClassesFieldName = classesFields;
        this.delegate = themeDescriptionDelegate;
        this.cachedFields = new HashMap();
        this.notFoundCachedFields = new HashMap();
    }

    public ThemeDescriptionDelegate setDelegateDisabled() {
        ThemeDescriptionDelegate oldDelegate = this.delegate;
        this.delegate = null;
        return oldDelegate;
    }

    public void setColor(int color, boolean useDefault) {
        setColor(color, useDefault, true);
    }

    public void setColor(int color, boolean useDefault, boolean save) {
        int a;
        Drawable drawable;
        RecyclerListView recyclerListView;
        int a2;
        if (save) {
            Theme.setColor(this.currentKey, color, useDefault);
        }
        int a3 = 0;
        if (this.paintToUpdate != null) {
            a = 0;
            while (a < this.paintToUpdate.length) {
                if ((this.changeFlags & FLAG_LINKCOLOR) == 0 || !(this.paintToUpdate[a] instanceof TextPaint)) {
                    this.paintToUpdate[a].setColor(color);
                } else {
                    ((TextPaint) this.paintToUpdate[a]).linkColor = color;
                }
                a++;
            }
        }
        if (this.drawablesToUpdate != null) {
            for (a = 0; a < this.drawablesToUpdate.length; a++) {
                if (this.drawablesToUpdate[a] != null) {
                    if (this.drawablesToUpdate[a] instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                            ((CombinedDrawable) this.drawablesToUpdate[a]).getBackground().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                        } else {
                            ((CombinedDrawable) this.drawablesToUpdate[a]).getIcon().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                        }
                    } else if (this.drawablesToUpdate[a] instanceof AvatarDrawable) {
                        ((AvatarDrawable) this.drawablesToUpdate[a]).setColor(color);
                    } else {
                        this.drawablesToUpdate[a].setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                    }
                }
            }
        }
        if (this.viewToInvalidate != null && this.listClasses == null && this.listClassesFieldName == null && ((this.changeFlags & FLAG_CHECKTAG) == 0 || ((this.changeFlags & FLAG_CHECKTAG) != 0 && this.currentKey.equals(this.viewToInvalidate.getTag())))) {
            if ((this.changeFlags & FLAG_BACKGROUND) != 0) {
                this.viewToInvalidate.setBackgroundColor(color);
            }
            if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                drawable = this.viewToInvalidate.getBackground();
                if (drawable instanceof CombinedDrawable) {
                    if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                        drawable = ((CombinedDrawable) drawable).getBackground();
                    } else {
                        drawable = ((CombinedDrawable) drawable).getIcon();
                    }
                }
                if (drawable != null) {
                    if (!(drawable instanceof StateListDrawable)) {
                        if (VERSION.SDK_INT < 21 || !(drawable instanceof RippleDrawable)) {
                            if (drawable instanceof ShapeDrawable) {
                                ((ShapeDrawable) drawable).getPaint().setColor(color);
                            } else {
                                drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                            }
                        }
                    }
                    Theme.setSelectorDrawableColor(drawable, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                }
            }
        }
        if (this.viewToInvalidate instanceof ActionBar) {
            if ((this.changeFlags & FLAG_AB_ITEMSCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsColor(color, false);
            }
            if ((this.changeFlags & FLAG_AB_TITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setTitleColor(color);
            }
            if ((this.changeFlags & FLAG_AB_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(color, false);
            }
            if ((this.changeFlags & FLAG_AB_AM_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_AM_ITEMSCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_SUBTITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setSubtitleColor(color);
            }
            if ((this.changeFlags & FLAG_AB_AM_BACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeColor(color);
            }
            if ((this.changeFlags & FLAG_AB_AM_TOPBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeTopColor(color);
            }
            if ((this.changeFlags & FLAG_AB_SEARCHPLACEHOLDER) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_SEARCH) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(color, false);
            }
            if ((this.changeFlags & FLAG_AB_SUBMENUITEM) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupItemsColor(color);
            }
            if ((this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupBackgroundColor(color);
            }
        }
        if (this.viewToInvalidate instanceof EmptyTextProgressView) {
            if ((this.changeFlags & FLAG_TEXTCOLOR) != 0) {
                ((EmptyTextProgressView) this.viewToInvalidate).setTextColor(color);
            } else if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                ((EmptyTextProgressView) this.viewToInvalidate).setProgressBarColor(color);
            }
        }
        if (this.viewToInvalidate instanceof RadialProgressView) {
            ((RadialProgressView) this.viewToInvalidate).setProgressColor(color);
        } else if (this.viewToInvalidate instanceof LineProgressView) {
            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                ((LineProgressView) this.viewToInvalidate).setProgressColor(color);
            } else {
                ((LineProgressView) this.viewToInvalidate).setBackColor(color);
            }
        } else if (this.viewToInvalidate instanceof ContextProgressView) {
            ((ContextProgressView) this.viewToInvalidate).updateColors();
        }
        if ((this.changeFlags & FLAG_TEXTCOLOR) != 0 && ((this.changeFlags & FLAG_CHECKTAG) == 0 || !(this.viewToInvalidate == null || (this.changeFlags & FLAG_CHECKTAG) == 0 || !this.currentKey.equals(this.viewToInvalidate.getTag())))) {
            if (this.viewToInvalidate instanceof TextView) {
                ((TextView) this.viewToInvalidate).setTextColor(color);
            } else if (this.viewToInvalidate instanceof NumberTextView) {
                ((NumberTextView) this.viewToInvalidate).setTextColor(color);
            } else if (this.viewToInvalidate instanceof SimpleTextView) {
                ((SimpleTextView) this.viewToInvalidate).setTextColor(color);
            } else if (this.viewToInvalidate instanceof ChatBigEmptyView) {
                ((ChatBigEmptyView) this.viewToInvalidate).setTextColor(color);
            }
        }
        if ((this.changeFlags & FLAG_CURSORCOLOR) != 0 && (this.viewToInvalidate instanceof EditTextBoldCursor)) {
            ((EditTextBoldCursor) this.viewToInvalidate).setCursorColor(color);
        }
        if ((this.changeFlags & FLAG_HINTTEXTCOLOR) != 0) {
            if (this.viewToInvalidate instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) this.viewToInvalidate).setHintColor(color);
            } else if (this.viewToInvalidate instanceof EditText) {
                ((EditText) this.viewToInvalidate).setHintTextColor(color);
            }
        }
        if (!(this.viewToInvalidate == null || (this.changeFlags & FLAG_SERVICEBACKGROUND) == 0)) {
            drawable = this.viewToInvalidate.getBackground();
            if (drawable != null) {
                drawable.setColorFilter(Theme.colorFilter);
            }
        }
        if ((this.changeFlags & FLAG_IMAGECOLOR) != 0 && ((this.changeFlags & FLAG_CHECKTAG) == 0 || ((this.changeFlags & FLAG_CHECKTAG) != 0 && this.currentKey.equals(this.viewToInvalidate.getTag())))) {
            if (!(this.viewToInvalidate instanceof ImageView)) {
                boolean z = this.viewToInvalidate instanceof BackupImageView;
            } else if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                drawable = ((ImageView) this.viewToInvalidate).getDrawable();
                if ((drawable instanceof StateListDrawable) || (VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                    Theme.setSelectorDrawableColor(drawable, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                }
            } else {
                ((ImageView) this.viewToInvalidate).setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
            }
        }
        if ((this.viewToInvalidate instanceof ScrollView) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) this.viewToInvalidate, color);
        }
        if (this.viewToInvalidate instanceof RecyclerListView) {
            recyclerListView = this.viewToInvalidate;
            if ((this.changeFlags & FLAG_SELECTOR) != 0 && this.currentKey.equals(Theme.key_listSelector)) {
                recyclerListView.setListSelectorColor(color);
            }
            if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                recyclerListView.updateFastScrollColors();
            }
            if ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
                recyclerListView.setGlowColor(color);
            }
            if ((this.changeFlags & FLAG_SECTIONS) != 0) {
                ArrayList<View> headers = recyclerListView.getHeaders();
                if (headers != null) {
                    for (a2 = 0; a2 < headers.size(); a2++) {
                        processViewColor((View) headers.get(a2), color);
                    }
                }
                headers = recyclerListView.getHeadersCache();
                if (headers != null) {
                    for (a2 = 0; a2 < headers.size(); a2++) {
                        processViewColor((View) headers.get(a2), color);
                    }
                }
                View header = recyclerListView.getPinnedHeader();
                if (header != null) {
                    processViewColor(header, color);
                }
            }
        } else if (this.viewToInvalidate != null) {
            if ((this.changeFlags & FLAG_SELECTOR) != 0) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if ((this.changeFlags & FLAG_SELECTORWHITE) != 0) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            }
        }
        if (this.listClasses != null) {
            int count;
            if (this.viewToInvalidate instanceof RecyclerListView) {
                recyclerListView = (RecyclerListView) this.viewToInvalidate;
                recyclerListView.getRecycledViewPool().clear();
                count = recyclerListView.getHiddenChildCount();
                for (a2 = 0; a2 < count; a2++) {
                    processViewColor(recyclerListView.getHiddenChildAt(a2), color);
                }
                count = recyclerListView.getCachedChildCount();
                for (a2 = 0; a2 < count; a2++) {
                    processViewColor(recyclerListView.getCachedChildAt(a2), color);
                }
                count = recyclerListView.getAttachedScrapChildCount();
                for (a2 = 0; a2 < count; a2++) {
                    processViewColor(recyclerListView.getAttachedScrapChildAt(a2), color);
                }
            }
            if (this.viewToInvalidate instanceof ViewGroup) {
                ViewGroup viewGroup = this.viewToInvalidate;
                count = viewGroup.getChildCount();
                while (a3 < count) {
                    processViewColor(viewGroup.getChildAt(a3), color);
                    a3++;
                }
            }
            processViewColor(this.viewToInvalidate, color);
        }
        this.currentColor = color;
        if (this.delegate != null) {
            this.delegate.didSetColor();
        }
        if (this.viewToInvalidate != null) {
            this.viewToInvalidate.invalidate();
        }
    }

    private void processViewColor(View child, int color) {
        for (int b = 0; b < this.listClasses.length; b++) {
            if (this.listClasses[b].isInstance(child)) {
                boolean passedCheck;
                String key;
                Field field;
                TextView object;
                TextView textView;
                Drawable[] drawables;
                int a;
                CharSequence text;
                TypefaceSpan[] spans;
                Drawable drawable;
                child.invalidate();
                if ((this.changeFlags & FLAG_CHECKTAG) != 0) {
                    if ((this.changeFlags & FLAG_CHECKTAG) == 0 || !this.currentKey.equals(child.getTag())) {
                        passedCheck = false;
                        if (this.listClassesFieldName != null) {
                            key = new StringBuilder();
                            key.append(this.listClasses[b]);
                            key.append("_");
                            key.append(this.listClassesFieldName[b]);
                            key = key.toString();
                            if (this.notFoundCachedFields != null || !this.notFoundCachedFields.containsKey(key)) {
                                try {
                                    field = (Field) this.cachedFields.get(key);
                                    if (field == null) {
                                        field = this.listClasses[b].getDeclaredField(this.listClassesFieldName[b]);
                                        if (field != null) {
                                            field.setAccessible(true);
                                            this.cachedFields.put(key, field);
                                        }
                                    }
                                    if (field != null) {
                                        object = field.get(child);
                                        if (object != null) {
                                            if (passedCheck || !(object instanceof View) || this.currentKey.equals(object.getTag())) {
                                                if (object instanceof View) {
                                                    object.invalidate();
                                                }
                                                if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0 && (object instanceof View)) {
                                                    object = object.getBackground();
                                                }
                                                if ((this.changeFlags & FLAG_BACKGROUND) == 0 && (object instanceof View)) {
                                                    object.setBackgroundColor(color);
                                                } else if (object instanceof Switch) {
                                                    ((Switch) object).checkColorFilters();
                                                } else if (object instanceof EditTextCaption) {
                                                    if (object instanceof SimpleTextView) {
                                                        if (object instanceof TextView) {
                                                            textView = object;
                                                            if ((this.changeFlags & FLAG_IMAGECOLOR) != 0) {
                                                                drawables = textView.getCompoundDrawables();
                                                                if (drawables != null) {
                                                                    for (a = 0; a < drawables.length; a++) {
                                                                        if (drawables[a] != null) {
                                                                            drawables[a].setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                                        }
                                                                    }
                                                                }
                                                            } else if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                                textView.getPaint().linkColor = color;
                                                                textView.invalidate();
                                                            } else if ((this.changeFlags & FLAG_FASTSCROLL) == 0) {
                                                                text = textView.getText();
                                                                if (text instanceof SpannedString) {
                                                                    spans = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class);
                                                                    if (spans != null && spans.length > 0) {
                                                                        for (TypefaceSpan color2 : spans) {
                                                                            color2.setColor(color);
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                textView.setTextColor(color);
                                                            }
                                                        } else if (object instanceof ImageView) {
                                                            ((ImageView) object).setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                        } else if (object instanceof BackupImageView) {
                                                            drawable = ((BackupImageView) object).getImageReceiver().getStaticThumb();
                                                            if (drawable instanceof CombinedDrawable) {
                                                                if (drawable != null) {
                                                                    drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                                }
                                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                                                ((CombinedDrawable) drawable).getBackground().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                            } else {
                                                                ((CombinedDrawable) drawable).getIcon().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                            }
                                                        } else if (object instanceof Drawable) {
                                                            if (object instanceof CheckBox) {
                                                                if (object instanceof GroupCreateCheckBox) {
                                                                    ((GroupCreateCheckBox) object).updateColors();
                                                                } else if (object instanceof Integer) {
                                                                    field.set(child, Integer.valueOf(color));
                                                                } else if (object instanceof RadioButton) {
                                                                    if (object instanceof TextPaint) {
                                                                        if (object instanceof LineProgressView) {
                                                                            if (object instanceof Paint) {
                                                                                ((Paint) object).setColor(color);
                                                                            } else if (object instanceof SeekBarView) {
                                                                                if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                                                    ((SeekBarView) object).setOuterColor(color);
                                                                                } else {
                                                                                    ((SeekBarView) object).setInnerColor(color);
                                                                                }
                                                                            }
                                                                        } else if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                                            ((LineProgressView) object).setProgressColor(color);
                                                                        } else {
                                                                            ((LineProgressView) object).setBackColor(color);
                                                                        }
                                                                    } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                                                        ((TextPaint) object).linkColor = color;
                                                                    } else {
                                                                        ((TextPaint) object).setColor(color);
                                                                    }
                                                                } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                                    ((RadioButton) object).setBackgroundColor(color);
                                                                    ((RadioButton) object).invalidate();
                                                                } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                                    ((RadioButton) object).setCheckedColor(color);
                                                                    ((RadioButton) object).invalidate();
                                                                }
                                                            } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                                ((CheckBox) object).setBackgroundColor(color);
                                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                                ((CheckBox) object).setCheckColor(color);
                                                            }
                                                        } else if (object instanceof LetterDrawable) {
                                                            if (object instanceof CombinedDrawable) {
                                                                if (!(object instanceof StateListDrawable)) {
                                                                    if (VERSION.SDK_INT >= 21 || !(object instanceof RippleDrawable)) {
                                                                        ((Drawable) object).setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                                    }
                                                                }
                                                                Theme.setSelectorDrawableColor((Drawable) object, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0);
                                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                                                ((CombinedDrawable) object).getBackground().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                            } else {
                                                                ((CombinedDrawable) object).getIcon().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                            }
                                                        } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                                            ((LetterDrawable) object).setBackgroundColor(color);
                                                        } else {
                                                            ((LetterDrawable) object).setColor(color);
                                                        }
                                                    } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                                        ((SimpleTextView) object).setLinkTextColor(color);
                                                    } else {
                                                        ((SimpleTextView) object).setTextColor(color);
                                                    }
                                                } else if ((this.changeFlags & FLAG_HINTTEXTCOLOR) == 0) {
                                                    ((EditTextCaption) object).setHintColor(color);
                                                    ((EditTextCaption) object).setHintTextColor(color);
                                                } else {
                                                    ((EditTextCaption) object).setTextColor(color);
                                                }
                                            }
                                        }
                                    }
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                    this.notFoundCachedFields.put(key, Boolean.valueOf(true));
                                }
                            }
                        } else if (child instanceof GroupCreateSpan) {
                            ((GroupCreateSpan) child).updateColors();
                        }
                    }
                }
                passedCheck = true;
                child.invalidate();
                Drawable drawable2;
                if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                    drawable2 = child.getBackground();
                    if (drawable2 != null) {
                        if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0) {
                            if (drawable2 instanceof CombinedDrawable) {
                                drawable2 = ((CombinedDrawable) drawable2).getIcon();
                            }
                            drawable2.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                        } else if (drawable2 instanceof CombinedDrawable) {
                            Drawable back = ((CombinedDrawable) drawable2).getBackground();
                            if (back instanceof ColorDrawable) {
                                ((ColorDrawable) back).setColor(color);
                            }
                        }
                    }
                } else if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) != 0) {
                    child.setBackgroundColor(color);
                } else if ((this.changeFlags & FLAG_TEXTCOLOR) != 0) {
                    if (child instanceof TextView) {
                        ((TextView) child).setTextColor(color);
                    }
                } else if ((this.changeFlags & FLAG_SERVICEBACKGROUND) != 0) {
                    drawable2 = child.getBackground();
                    if (drawable2 != null) {
                        drawable2.setColorFilter(Theme.colorFilter);
                    }
                }
                if (this.listClassesFieldName != null) {
                    key = new StringBuilder();
                    key.append(this.listClasses[b]);
                    key.append("_");
                    key.append(this.listClassesFieldName[b]);
                    key = key.toString();
                    if (this.notFoundCachedFields != null) {
                    }
                    field = (Field) this.cachedFields.get(key);
                    if (field == null) {
                        field = this.listClasses[b].getDeclaredField(this.listClassesFieldName[b]);
                        if (field != null) {
                            field.setAccessible(true);
                            this.cachedFields.put(key, field);
                        }
                    }
                    if (field != null) {
                        object = field.get(child);
                        if (object != null) {
                            if (!passedCheck) {
                            }
                            if (object instanceof View) {
                                object.invalidate();
                            }
                            object = object.getBackground();
                            if ((this.changeFlags & FLAG_BACKGROUND) == 0) {
                            }
                            if (object instanceof Switch) {
                                ((Switch) object).checkColorFilters();
                            } else if (object instanceof EditTextCaption) {
                                if (object instanceof SimpleTextView) {
                                    if (object instanceof TextView) {
                                        textView = object;
                                        if ((this.changeFlags & FLAG_IMAGECOLOR) != 0) {
                                            drawables = textView.getCompoundDrawables();
                                            if (drawables != null) {
                                                for (a = 0; a < drawables.length; a++) {
                                                    if (drawables[a] != null) {
                                                        drawables[a].setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                                    }
                                                }
                                            }
                                        } else if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                            textView.getPaint().linkColor = color;
                                            textView.invalidate();
                                        } else if ((this.changeFlags & FLAG_FASTSCROLL) == 0) {
                                            textView.setTextColor(color);
                                        } else {
                                            text = textView.getText();
                                            if (text instanceof SpannedString) {
                                                spans = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class);
                                                while (i < spans.length) {
                                                    color2.setColor(color);
                                                }
                                            }
                                        }
                                    } else if (object instanceof ImageView) {
                                        ((ImageView) object).setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                    } else if (object instanceof BackupImageView) {
                                        drawable = ((BackupImageView) object).getImageReceiver().getStaticThumb();
                                        if (drawable instanceof CombinedDrawable) {
                                            if (drawable != null) {
                                                drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                            }
                                        } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                            ((CombinedDrawable) drawable).getIcon().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                        } else {
                                            ((CombinedDrawable) drawable).getBackground().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                        }
                                    } else if (object instanceof Drawable) {
                                        if (object instanceof CheckBox) {
                                            if (object instanceof GroupCreateCheckBox) {
                                                ((GroupCreateCheckBox) object).updateColors();
                                            } else if (object instanceof Integer) {
                                                field.set(child, Integer.valueOf(color));
                                            } else if (object instanceof RadioButton) {
                                                if (object instanceof TextPaint) {
                                                    if (object instanceof LineProgressView) {
                                                        if (object instanceof Paint) {
                                                            ((Paint) object).setColor(color);
                                                        } else if (object instanceof SeekBarView) {
                                                            if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                                ((SeekBarView) object).setInnerColor(color);
                                                            } else {
                                                                ((SeekBarView) object).setOuterColor(color);
                                                            }
                                                        }
                                                    } else if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                                                        ((LineProgressView) object).setBackColor(color);
                                                    } else {
                                                        ((LineProgressView) object).setProgressColor(color);
                                                    }
                                                } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                                    ((TextPaint) object).setColor(color);
                                                } else {
                                                    ((TextPaint) object).linkColor = color;
                                                }
                                            } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                ((RadioButton) object).setBackgroundColor(color);
                                                ((RadioButton) object).invalidate();
                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                ((RadioButton) object).setCheckedColor(color);
                                                ((RadioButton) object).invalidate();
                                            }
                                        } else if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                            ((CheckBox) object).setBackgroundColor(color);
                                        } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                            ((CheckBox) object).setCheckColor(color);
                                        }
                                    } else if (object instanceof LetterDrawable) {
                                        if (object instanceof CombinedDrawable) {
                                            if (object instanceof StateListDrawable) {
                                                if (VERSION.SDK_INT >= 21) {
                                                }
                                                ((Drawable) object).setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                            }
                                            if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0) {
                                            }
                                            Theme.setSelectorDrawableColor((Drawable) object, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0);
                                        } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                            ((CombinedDrawable) object).getIcon().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                        } else {
                                            ((CombinedDrawable) object).getBackground().setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                                        }
                                    } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0) {
                                        ((LetterDrawable) object).setColor(color);
                                    } else {
                                        ((LetterDrawable) object).setBackgroundColor(color);
                                    }
                                } else if ((this.changeFlags & FLAG_LINKCOLOR) == 0) {
                                    ((SimpleTextView) object).setTextColor(color);
                                } else {
                                    ((SimpleTextView) object).setLinkTextColor(color);
                                }
                            } else if ((this.changeFlags & FLAG_HINTTEXTCOLOR) == 0) {
                                ((EditTextCaption) object).setTextColor(color);
                            } else {
                                ((EditTextCaption) object).setHintColor(color);
                                ((EditTextCaption) object).setHintTextColor(color);
                            }
                        }
                    }
                } else if (child instanceof GroupCreateSpan) {
                    ((GroupCreateSpan) child).updateColors();
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
