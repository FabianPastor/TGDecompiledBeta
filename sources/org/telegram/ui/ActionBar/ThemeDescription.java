package org.telegram.ui.ActionBar;

import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;

public class ThemeDescription {
    public static int FLAG_AB_AM_BACKGROUND = 1048576;
    public static int FLAG_AB_AM_ITEMSCOLOR = 512;
    public static int FLAG_AB_AM_SELECTORCOLOR = 4194304;
    public static int FLAG_AB_AM_TOPBACKGROUND = 2097152;
    public static int FLAG_AB_ITEMSCOLOR = 64;
    public static int FLAG_AB_SEARCH = NUM;
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
    public static int FLAG_SELECTORWHITE = NUM;
    public static int FLAG_SERVICEBACKGROUND = NUM;
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
        if (this.viewToInvalidate instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) this.viewToInvalidate).getEditText();
        }
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
        if (this.viewToInvalidate instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) this.viewToInvalidate).getEditText();
        }
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
        if (this.viewToInvalidate instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) this.viewToInvalidate).getEditText();
        }
    }

    public ThemeDescriptionDelegate setDelegateDisabled() {
        ThemeDescriptionDelegate oldDelegate = this.delegate;
        this.delegate = null;
        return oldDelegate;
    }

    public void setColor(int color, boolean useDefault) {
        setColor(color, useDefault, true);
    }

    private boolean checkTag(String key, View view) {
        if (key == null || view == null) {
            return false;
        }
        Object viewTag = view.getTag();
        if (viewTag instanceof String) {
            return ((String) viewTag).contains(key);
        }
        return false;
    }

    public void setColor(int color, boolean useDefault, boolean save) {
        int a;
        Drawable drawable;
        RecyclerListView recyclerListView;
        if (save) {
            Theme.setColor(this.currentKey, color, useDefault);
        }
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
        if (this.viewToInvalidate != null && this.listClasses == null && this.listClassesFieldName == null && ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            if ((this.changeFlags & FLAG_BACKGROUND) != 0) {
                this.viewToInvalidate.setBackgroundColor(color);
            }
            if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                if ((this.changeFlags & FLAG_PROGRESSBAR) == 0) {
                    drawable = this.viewToInvalidate.getBackground();
                    if (drawable instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                            drawable = ((CombinedDrawable) drawable).getBackground();
                        } else {
                            drawable = ((CombinedDrawable) drawable).getIcon();
                        }
                    }
                    if (drawable != null) {
                        if ((drawable instanceof StateListDrawable) || (VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                            boolean z;
                            if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                                z = true;
                            } else {
                                z = false;
                            }
                            Theme.setSelectorDrawableColor(drawable, color, z);
                        } else if (drawable instanceof ShapeDrawable) {
                            ((ShapeDrawable) drawable).getPaint().setColor(color);
                        } else {
                            drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                        }
                    }
                } else if (this.viewToInvalidate instanceof EditTextBoldCursor) {
                    ((EditTextBoldCursor) this.viewToInvalidate).setErrorLineColor(color);
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
        if ((this.changeFlags & FLAG_TEXTCOLOR) != 0 && ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
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
                if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                    ((EditTextBoldCursor) this.viewToInvalidate).setHeaderHintColor(color);
                } else {
                    ((EditTextBoldCursor) this.viewToInvalidate).setHintColor(color);
                }
            } else if (this.viewToInvalidate instanceof EditText) {
                ((EditText) this.viewToInvalidate).setHintTextColor(color);
            }
        }
        if (!(this.viewToInvalidate == null || (this.changeFlags & FLAG_SERVICEBACKGROUND) == 0)) {
            Drawable background = this.viewToInvalidate.getBackground();
            if (background != null) {
                background.setColorFilter(Theme.colorFilter);
            }
        }
        if ((this.changeFlags & FLAG_IMAGECOLOR) != 0 && ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            if (this.viewToInvalidate instanceof ImageView) {
                if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                    drawable = ((ImageView) this.viewToInvalidate).getDrawable();
                    if ((drawable instanceof StateListDrawable) || (VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                        Theme.setSelectorDrawableColor(drawable, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                    }
                } else {
                    ((ImageView) this.viewToInvalidate).setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                }
            } else if (this.viewToInvalidate instanceof BackupImageView) {
            }
        }
        if ((this.viewToInvalidate instanceof ScrollView) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) this.viewToInvalidate, color);
        }
        if (this.viewToInvalidate instanceof RecyclerListView) {
            recyclerListView = this.viewToInvalidate;
            if ((this.changeFlags & FLAG_SELECTOR) != 0 && this.currentKey.equals("listSelectorSDK21")) {
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
                    for (a = 0; a < headers.size(); a++) {
                        processViewColor((View) headers.get(a), color);
                    }
                }
                headers = recyclerListView.getHeadersCache();
                if (headers != null) {
                    for (a = 0; a < headers.size(); a++) {
                        processViewColor((View) headers.get(a), color);
                    }
                }
                View header = recyclerListView.getPinnedHeader();
                if (header != null) {
                    processViewColor(header, color);
                }
            }
        } else if (this.viewToInvalidate != null && (this.listClasses == null || this.listClasses.length == 0)) {
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
                for (a = 0; a < count; a++) {
                    processViewColor(recyclerListView.getHiddenChildAt(a), color);
                }
                count = recyclerListView.getCachedChildCount();
                for (a = 0; a < count; a++) {
                    processViewColor(recyclerListView.getCachedChildAt(a), color);
                }
                count = recyclerListView.getAttachedScrapChildCount();
                for (a = 0; a < count; a++) {
                    processViewColor(recyclerListView.getAttachedScrapChildAt(a), color);
                }
            }
            if (this.viewToInvalidate instanceof ViewGroup) {
                ViewGroup viewGroup = this.viewToInvalidate;
                count = viewGroup.getChildCount();
                for (a = 0; a < count; a++) {
                    processViewColor(viewGroup.getChildAt(a), color);
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

    /* JADX WARNING: Removed duplicated region for block: B:215:0x059c  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0088  */
    private void processViewColor(android.view.View r23, int r24) {
        /*
        r22 = this;
        r4 = 0;
    L_0x0001:
        r0 = r22;
        r0 = r0.listClasses;
        r18 = r0;
        r0 = r18;
        r0 = r0.length;
        r18 = r0;
        r0 = r18;
        if (r4 >= r0) goto L_0x05ad;
    L_0x0010:
        r0 = r22;
        r0 = r0.listClasses;
        r18 = r0;
        r18 = r18[r4];
        r0 = r18;
        r1 = r23;
        r18 = r0.isInstance(r1);
        if (r18 == 0) goto L_0x00c6;
    L_0x0022:
        r23.invalidate();
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_CHECKTAG;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0043;
    L_0x0031:
        r0 = r22;
        r0 = r0.currentKey;
        r18 = r0;
        r0 = r22;
        r1 = r18;
        r2 = r23;
        r18 = r0.checkTag(r1, r2);
        if (r18 == 0) goto L_0x019b;
    L_0x0043:
        r14 = 1;
        r23.invalidate();
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_BACKGROUNDFILTER;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0118;
    L_0x0053:
        r7 = r23.getBackground();
        if (r7 == 0) goto L_0x0080;
    L_0x0059:
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_CELLBACKGROUNDCOLOR;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x00ca;
    L_0x0065:
        r0 = r7 instanceof org.telegram.ui.Components.CombinedDrawable;
        r18 = r0;
        if (r18 == 0) goto L_0x0080;
    L_0x006b:
        r18 = r7;
        r18 = (org.telegram.ui.Components.CombinedDrawable) r18;
        r5 = r18.getBackground();
        r0 = r5 instanceof android.graphics.drawable.ColorDrawable;
        r18 = r0;
        if (r18 == 0) goto L_0x0080;
    L_0x0079:
        r5 = (android.graphics.drawable.ColorDrawable) r5;
        r0 = r24;
        r5.setColor(r0);
    L_0x0080:
        r0 = r22;
        r0 = r0.listClassesFieldName;
        r18 = r0;
        if (r18 == 0) goto L_0x059c;
    L_0x0088:
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r0 = r22;
        r0 = r0.listClasses;
        r19 = r0;
        r19 = r19[r4];
        r18 = r18.append(r19);
        r19 = "_";
        r18 = r18.append(r19);
        r0 = r22;
        r0 = r0.listClassesFieldName;
        r19 = r0;
        r19 = r19[r4];
        r18 = r18.append(r19);
        r12 = r18.toString();
        r0 = r22;
        r0 = r0.notFoundCachedFields;
        r18 = r0;
        if (r18 == 0) goto L_0x019e;
    L_0x00b8:
        r0 = r22;
        r0 = r0.notFoundCachedFields;
        r18 = r0;
        r0 = r18;
        r18 = r0.containsKey(r12);
        if (r18 == 0) goto L_0x019e;
    L_0x00c6:
        r4 = r4 + 1;
        goto L_0x0001;
    L_0x00ca:
        r0 = r7 instanceof org.telegram.ui.Components.CombinedDrawable;
        r18 = r0;
        if (r18 == 0) goto L_0x00e9;
    L_0x00d0:
        r7 = (org.telegram.ui.Components.CombinedDrawable) r7;
        r7 = r7.getIcon();
    L_0x00d6:
        r18 = new android.graphics.PorterDuffColorFilter;
        r19 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r0 = r18;
        r1 = r24;
        r2 = r19;
        r0.<init>(r1, r2);
        r0 = r18;
        r7.setColorFilter(r0);
        goto L_0x0080;
    L_0x00e9:
        r0 = r7 instanceof android.graphics.drawable.StateListDrawable;
        r18 = r0;
        if (r18 != 0) goto L_0x00ff;
    L_0x00ef:
        r18 = android.os.Build.VERSION.SDK_INT;
        r19 = 21;
        r0 = r18;
        r1 = r19;
        if (r0 < r1) goto L_0x00d6;
    L_0x00f9:
        r0 = r7 instanceof android.graphics.drawable.RippleDrawable;
        r18 = r0;
        if (r18 == 0) goto L_0x00d6;
    L_0x00ff:
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_DRAWABLESELECTEDSTATE;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0115;
    L_0x010b:
        r18 = 1;
    L_0x010d:
        r0 = r24;
        r1 = r18;
        org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r7, r0, r1);
        goto L_0x00d6;
    L_0x0115:
        r18 = 0;
        goto L_0x010d;
    L_0x0118:
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_CELLBACKGROUNDCOLOR;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0129;
    L_0x0124:
        r23.setBackgroundColor(r24);
        goto L_0x0080;
    L_0x0129:
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_TEXTCOLOR;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x014a;
    L_0x0135:
        r0 = r23;
        r0 = r0 instanceof android.widget.TextView;
        r18 = r0;
        if (r18 == 0) goto L_0x0080;
    L_0x013d:
        r18 = r23;
        r18 = (android.widget.TextView) r18;
        r0 = r18;
        r1 = r24;
        r0.setTextColor(r1);
        goto L_0x0080;
    L_0x014a:
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_SERVICEBACKGROUND;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0165;
    L_0x0156:
        r6 = r23.getBackground();
        if (r6 == 0) goto L_0x0080;
    L_0x015c:
        r18 = org.telegram.ui.ActionBar.Theme.colorFilter;
        r0 = r18;
        r6.setColorFilter(r0);
        goto L_0x0080;
    L_0x0165:
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_SELECTOR;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0180;
    L_0x0171:
        r18 = 0;
        r18 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r18);
        r0 = r23;
        r1 = r18;
        r0.setBackgroundDrawable(r1);
        goto L_0x0080;
    L_0x0180:
        r0 = r22;
        r0 = r0.changeFlags;
        r18 = r0;
        r19 = FLAG_SELECTORWHITE;
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0080;
    L_0x018c:
        r18 = 1;
        r18 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r18);
        r0 = r23;
        r1 = r18;
        r0.setBackgroundDrawable(r1);
        goto L_0x0080;
    L_0x019b:
        r14 = 0;
        goto L_0x0080;
    L_0x019e:
        r0 = r22;
        r0 = r0.cachedFields;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r18;
        r10 = r0.get(r12);	 Catch:{ Throwable -> 0x0240 }
        r10 = (java.lang.reflect.Field) r10;	 Catch:{ Throwable -> 0x0240 }
        if (r10 != 0) goto L_0x01d6;
    L_0x01ae:
        r0 = r22;
        r0 = r0.listClasses;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r18 = r18[r4];	 Catch:{ Throwable -> 0x0240 }
        r0 = r22;
        r0 = r0.listClassesFieldName;	 Catch:{ Throwable -> 0x0240 }
        r19 = r0;
        r19 = r19[r4];	 Catch:{ Throwable -> 0x0240 }
        r10 = r18.getDeclaredField(r19);	 Catch:{ Throwable -> 0x0240 }
        if (r10 == 0) goto L_0x01d6;
    L_0x01c4:
        r18 = 1;
        r0 = r18;
        r10.setAccessible(r0);	 Catch:{ Throwable -> 0x0240 }
        r0 = r22;
        r0 = r0.cachedFields;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r18;
        r0.put(r12, r10);	 Catch:{ Throwable -> 0x0240 }
    L_0x01d6:
        if (r10 == 0) goto L_0x00c6;
    L_0x01d8:
        r0 = r23;
        r13 = r10.get(r0);	 Catch:{ Throwable -> 0x0240 }
        if (r13 == 0) goto L_0x00c6;
    L_0x01e0:
        if (r14 != 0) goto L_0x01ff;
    L_0x01e2:
        r0 = r13 instanceof android.view.View;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x01ff;
    L_0x01e8:
        r0 = r22;
        r0 = r0.currentKey;	 Catch:{ Throwable -> 0x0240 }
        r19 = r0;
        r0 = r13;
        r0 = (android.view.View) r0;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r22;
        r1 = r19;
        r2 = r18;
        r18 = r0.checkTag(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        if (r18 == 0) goto L_0x00c6;
    L_0x01ff:
        r0 = r13 instanceof android.view.View;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x020d;
    L_0x0205:
        r0 = r13;
        r0 = (android.view.View) r0;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r18.invalidate();	 Catch:{ Throwable -> 0x0240 }
    L_0x020d:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_USEBACKGROUNDDRAWABLE;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0225;
    L_0x0219:
        r0 = r13 instanceof android.view.View;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0225;
    L_0x021f:
        r13 = (android.view.View) r13;	 Catch:{ Throwable -> 0x0240 }
        r13 = r13.getBackground();	 Catch:{ Throwable -> 0x0240 }
    L_0x0225:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_BACKGROUND;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0259;
    L_0x0231:
        r0 = r13 instanceof android.view.View;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0259;
    L_0x0237:
        r13 = (android.view.View) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setBackgroundColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0240:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        r0 = r22;
        r0 = r0.notFoundCachedFields;
        r18 = r0;
        r19 = 1;
        r19 = java.lang.Boolean.valueOf(r19);
        r0 = r18;
        r1 = r19;
        r0.put(r12, r1);
        goto L_0x00c6;
    L_0x0259:
        r0 = r13 instanceof org.telegram.ui.Components.EditTextCaption;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0289;
    L_0x025f:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_HINTTEXTCOLOR;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0280;
    L_0x026b:
        r0 = r13;
        r0 = (org.telegram.ui.Components.EditTextCaption) r0;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r18;
        r1 = r24;
        r0.setHintColor(r1);	 Catch:{ Throwable -> 0x0240 }
        r13 = (org.telegram.ui.Components.EditTextCaption) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setHintTextColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0280:
        r13 = (org.telegram.ui.Components.EditTextCaption) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setTextColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0289:
        r0 = r13 instanceof org.telegram.ui.ActionBar.SimpleTextView;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x02ad;
    L_0x028f:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_LINKCOLOR;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x02a4;
    L_0x029b:
        r13 = (org.telegram.ui.ActionBar.SimpleTextView) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setLinkTextColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x02a4:
        r13 = (org.telegram.ui.ActionBar.SimpleTextView) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setTextColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x02ad:
        r0 = r13 instanceof android.widget.TextView;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0356;
    L_0x02b3:
        r0 = r13;
        r0 = (android.widget.TextView) r0;	 Catch:{ Throwable -> 0x0240 }
        r17 = r0;
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_IMAGECOLOR;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x02eb;
    L_0x02c4:
        r8 = r17.getCompoundDrawables();	 Catch:{ Throwable -> 0x0240 }
        if (r8 == 0) goto L_0x00c6;
    L_0x02ca:
        r3 = 0;
    L_0x02cb:
        r0 = r8.length;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r18;
        if (r3 >= r0) goto L_0x00c6;
    L_0x02d2:
        r18 = r8[r3];	 Catch:{ Throwable -> 0x0240 }
        if (r18 == 0) goto L_0x02e8;
    L_0x02d6:
        r18 = r8[r3];	 Catch:{ Throwable -> 0x0240 }
        r19 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r20 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r19;
        r1 = r24;
        r2 = r20;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r18.setColorFilter(r19);	 Catch:{ Throwable -> 0x0240 }
    L_0x02e8:
        r3 = r3 + 1;
        goto L_0x02cb;
    L_0x02eb:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_LINKCOLOR;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0306;
    L_0x02f7:
        r18 = r17.getPaint();	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r1 = r18;
        r1.linkColor = r0;	 Catch:{ Throwable -> 0x0240 }
        r17.invalidate();	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0306:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_FASTSCROLL;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x034d;
    L_0x0312:
        r16 = r17.getText();	 Catch:{ Throwable -> 0x0240 }
        r0 = r16;
        r0 = r0 instanceof android.text.SpannedString;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x00c6;
    L_0x031e:
        r0 = r16;
        r0 = (android.text.SpannedString) r0;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = 0;
        r20 = r16.length();	 Catch:{ Throwable -> 0x0240 }
        r21 = org.telegram.ui.Components.TypefaceSpan.class;
        r15 = r18.getSpans(r19, r20, r21);	 Catch:{ Throwable -> 0x0240 }
        r15 = (org.telegram.ui.Components.TypefaceSpan[]) r15;	 Catch:{ Throwable -> 0x0240 }
        if (r15 == 0) goto L_0x00c6;
    L_0x0334:
        r0 = r15.length;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 <= 0) goto L_0x00c6;
    L_0x0339:
        r11 = 0;
    L_0x033a:
        r0 = r15.length;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r18;
        if (r11 >= r0) goto L_0x00c6;
    L_0x0341:
        r18 = r15[r11];	 Catch:{ Throwable -> 0x0240 }
        r0 = r18;
        r1 = r24;
        r0.setColor(r1);	 Catch:{ Throwable -> 0x0240 }
        r11 = r11 + 1;
        goto L_0x033a;
    L_0x034d:
        r0 = r17;
        r1 = r24;
        r0.setTextColor(r1);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0356:
        r0 = r13 instanceof android.widget.ImageView;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0372;
    L_0x035c:
        r13 = (android.widget.ImageView) r13;	 Catch:{ Throwable -> 0x0240 }
        r18 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r19 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r18;
        r1 = r24;
        r2 = r19;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r0 = r18;
        r13.setColorFilter(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0372:
        r0 = r13 instanceof org.telegram.ui.Components.BackupImageView;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x03da;
    L_0x0378:
        r13 = (org.telegram.ui.Components.BackupImageView) r13;	 Catch:{ Throwable -> 0x0240 }
        r18 = r13.getImageReceiver();	 Catch:{ Throwable -> 0x0240 }
        r7 = r18.getStaticThumb();	 Catch:{ Throwable -> 0x0240 }
        r0 = r7 instanceof org.telegram.ui.Components.CombinedDrawable;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x03c4;
    L_0x0388:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_BACKGROUNDFILTER;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x03ac;
    L_0x0394:
        r7 = (org.telegram.ui.Components.CombinedDrawable) r7;	 Catch:{ Throwable -> 0x0240 }
        r18 = r7.getBackground();	 Catch:{ Throwable -> 0x0240 }
        r19 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r20 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r19;
        r1 = r24;
        r2 = r20;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r18.setColorFilter(r19);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x03ac:
        r7 = (org.telegram.ui.Components.CombinedDrawable) r7;	 Catch:{ Throwable -> 0x0240 }
        r18 = r7.getIcon();	 Catch:{ Throwable -> 0x0240 }
        r19 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r20 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r19;
        r1 = r24;
        r2 = r20;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r18.setColorFilter(r19);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x03c4:
        if (r7 == 0) goto L_0x00c6;
    L_0x03c6:
        r18 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r19 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r18;
        r1 = r24;
        r2 = r19;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r0 = r18;
        r7.setColorFilter(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x03da:
        r0 = r13 instanceof android.graphics.drawable.Drawable;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x048e;
    L_0x03e0:
        r0 = r13 instanceof org.telegram.ui.Components.LetterDrawable;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0404;
    L_0x03e6:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_BACKGROUNDFILTER;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x03fb;
    L_0x03f2:
        r13 = (org.telegram.ui.Components.LetterDrawable) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setBackgroundColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x03fb:
        r13 = (org.telegram.ui.Components.LetterDrawable) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0404:
        r0 = r13 instanceof org.telegram.ui.Components.CombinedDrawable;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0446;
    L_0x040a:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_BACKGROUNDFILTER;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x042e;
    L_0x0416:
        r13 = (org.telegram.ui.Components.CombinedDrawable) r13;	 Catch:{ Throwable -> 0x0240 }
        r18 = r13.getBackground();	 Catch:{ Throwable -> 0x0240 }
        r19 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r20 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r19;
        r1 = r24;
        r2 = r20;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r18.setColorFilter(r19);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x042e:
        r13 = (org.telegram.ui.Components.CombinedDrawable) r13;	 Catch:{ Throwable -> 0x0240 }
        r18 = r13.getIcon();	 Catch:{ Throwable -> 0x0240 }
        r19 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r20 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r19;
        r1 = r24;
        r2 = r20;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r18.setColorFilter(r19);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0446:
        r0 = r13 instanceof android.graphics.drawable.StateListDrawable;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 != 0) goto L_0x045c;
    L_0x044c:
        r18 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0240 }
        r19 = 21;
        r0 = r18;
        r1 = r19;
        if (r0 < r1) goto L_0x0478;
    L_0x0456:
        r0 = r13 instanceof android.graphics.drawable.RippleDrawable;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0478;
    L_0x045c:
        r13 = (android.graphics.drawable.Drawable) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_DRAWABLESELECTEDSTATE;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0475;
    L_0x046a:
        r18 = 1;
    L_0x046c:
        r0 = r24;
        r1 = r18;
        org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r13, r0, r1);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0475:
        r18 = 0;
        goto L_0x046c;
    L_0x0478:
        r13 = (android.graphics.drawable.Drawable) r13;	 Catch:{ Throwable -> 0x0240 }
        r18 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0240 }
        r19 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0240 }
        r0 = r18;
        r1 = r24;
        r2 = r19;
        r0.<init>(r1, r2);	 Catch:{ Throwable -> 0x0240 }
        r0 = r18;
        r13.setColorFilter(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x048e:
        r0 = r13 instanceof org.telegram.ui.Components.CheckBox;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x04be;
    L_0x0494:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_CHECKBOX;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x04a9;
    L_0x04a0:
        r13 = (org.telegram.ui.Components.CheckBox) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setBackgroundColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x04a9:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_CHECKBOXCHECK;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x00c6;
    L_0x04b5:
        r13 = (org.telegram.ui.Components.CheckBox) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setCheckColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x04be:
        r0 = r13 instanceof org.telegram.ui.Components.GroupCreateCheckBox;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x04cb;
    L_0x04c4:
        r13 = (org.telegram.ui.Components.GroupCreateCheckBox) r13;	 Catch:{ Throwable -> 0x0240 }
        r13.updateColors();	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x04cb:
        r0 = r13 instanceof java.lang.Integer;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x04de;
    L_0x04d1:
        r18 = java.lang.Integer.valueOf(r24);	 Catch:{ Throwable -> 0x0240 }
        r0 = r23;
        r1 = r18;
        r10.set(r0, r1);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x04de:
        r0 = r13 instanceof org.telegram.ui.Components.RadioButton;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0522;
    L_0x04e4:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_CHECKBOX;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0503;
    L_0x04f0:
        r0 = r13;
        r0 = (org.telegram.ui.Components.RadioButton) r0;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r18;
        r1 = r24;
        r0.setBackgroundColor(r1);	 Catch:{ Throwable -> 0x0240 }
        r13 = (org.telegram.ui.Components.RadioButton) r13;	 Catch:{ Throwable -> 0x0240 }
        r13.invalidate();	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0503:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_CHECKBOXCHECK;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x00c6;
    L_0x050f:
        r0 = r13;
        r0 = (org.telegram.ui.Components.RadioButton) r0;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r0 = r18;
        r1 = r24;
        r0.setCheckedColor(r1);	 Catch:{ Throwable -> 0x0240 }
        r13 = (org.telegram.ui.Components.RadioButton) r13;	 Catch:{ Throwable -> 0x0240 }
        r13.invalidate();	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0522:
        r0 = r13 instanceof android.text.TextPaint;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0545;
    L_0x0528:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_LINKCOLOR;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x053c;
    L_0x0534:
        r13 = (android.text.TextPaint) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.linkColor = r0;	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x053c:
        r13 = (android.text.TextPaint) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0545:
        r0 = r13 instanceof org.telegram.ui.Components.LineProgressView;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0569;
    L_0x054b:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_PROGRESSBAR;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0560;
    L_0x0557:
        r13 = (org.telegram.ui.Components.LineProgressView) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setProgressColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0560:
        r13 = (org.telegram.ui.Components.LineProgressView) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setBackColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0569:
        r0 = r13 instanceof android.graphics.Paint;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x0578;
    L_0x056f:
        r13 = (android.graphics.Paint) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0578:
        r0 = r13 instanceof org.telegram.ui.Components.SeekBarView;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        if (r18 == 0) goto L_0x00c6;
    L_0x057e:
        r0 = r22;
        r0 = r0.changeFlags;	 Catch:{ Throwable -> 0x0240 }
        r18 = r0;
        r19 = FLAG_PROGRESSBAR;	 Catch:{ Throwable -> 0x0240 }
        r18 = r18 & r19;
        if (r18 == 0) goto L_0x0593;
    L_0x058a:
        r13 = (org.telegram.ui.Components.SeekBarView) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setOuterColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x0593:
        r13 = (org.telegram.ui.Components.SeekBarView) r13;	 Catch:{ Throwable -> 0x0240 }
        r0 = r24;
        r13.setInnerColor(r0);	 Catch:{ Throwable -> 0x0240 }
        goto L_0x00c6;
    L_0x059c:
        r0 = r23;
        r0 = r0 instanceof org.telegram.ui.Components.GroupCreateSpan;
        r18 = r0;
        if (r18 == 0) goto L_0x00c6;
    L_0x05a4:
        r18 = r23;
        r18 = (org.telegram.ui.Components.GroupCreateSpan) r18;
        r18.updateColors();
        goto L_0x00c6;
    L_0x05ad:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ThemeDescription.processViewColor(android.view.View, int):void");
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
