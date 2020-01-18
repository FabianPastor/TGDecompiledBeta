package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.VcardItem;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC.TL_restrictionReason;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.PhonebookSelectShareAlert.PhonebookShareAlertDelegate;

public class PhonebookShareAlert extends BottomSheet {
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private Paint backgroundPaint = new Paint(1);
    private TextView buttonTextView;
    private User currentUser;
    private PhonebookShareAlertDelegate delegate;
    private boolean inLayout;
    private boolean isImport;
    private LinearLayout linearLayout;
    private ListAdapter listAdapter;
    private ArrayList<VcardItem> other = new ArrayList();
    private BaseFragment parentFragment;
    private int phoneEndRow;
    private int phoneStartRow;
    private ArrayList<VcardItem> phones = new ArrayList();
    private int rowCount;
    private int scrollOffsetY;
    private NestedScrollView scrollView;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private int userRow;
    private int vcardEndRow;
    private int vcardStartRow;

    private class ListAdapter {
        private ListAdapter() {
        }

        /* synthetic */ ListAdapter(PhonebookShareAlert phonebookShareAlert, AnonymousClass1 anonymousClass1) {
            this();
        }

        public int getItemCount() {
            return PhonebookShareAlert.this.rowCount;
        }

        public void onBindViewHolder(View view, int i, int i2) {
            boolean z = true;
            if (i2 == 1) {
                VcardItem vcardItem;
                TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) view;
                int i3 = NUM;
                if (i < PhonebookShareAlert.this.phoneStartRow || i >= PhonebookShareAlert.this.phoneEndRow) {
                    vcardItem = (VcardItem) PhonebookShareAlert.this.other.get(i - PhonebookShareAlert.this.vcardStartRow);
                    int i4 = vcardItem.type;
                    if (i4 == 1) {
                        i3 = NUM;
                    } else if (i4 == 2) {
                        i3 = NUM;
                    } else if (i4 == 3) {
                        i3 = NUM;
                    } else if (i4 == 4) {
                        i3 = NUM;
                    } else if (i4 == 5) {
                        i3 = NUM;
                    } else if (i4 == 6) {
                        i3 = "ORG".equalsIgnoreCase(vcardItem.getRawType(true)) ? NUM : NUM;
                    }
                } else {
                    vcardItem = (VcardItem) PhonebookShareAlert.this.phones.get(i - PhonebookShareAlert.this.phoneStartRow);
                    i3 = NUM;
                }
                if (i == getItemCount() - 1) {
                    z = false;
                }
                textCheckBoxCell.setVCardItem(vcardItem, i3, z);
            }
        }

        public View createView(Context context, int i) {
            View textCheckBoxCell;
            int itemViewType = getItemViewType(i);
            if (itemViewType != 0) {
                textCheckBoxCell = new TextCheckBoxCell(PhonebookShareAlert.this, context);
            } else {
                textCheckBoxCell = new UserCell(PhonebookShareAlert.this, context);
            }
            onBindViewHolder(textCheckBoxCell, i, itemViewType);
            return textCheckBoxCell;
        }

        public int getItemViewType(int i) {
            return i == PhonebookShareAlert.this.userRow ? 0 : 1;
        }
    }

    public class TextCheckBoxCell extends FrameLayout {
        private Switch checkBox;
        private ImageView imageView;
        private boolean needDivider;
        private TextView textView;
        final /* synthetic */ PhonebookShareAlert this$0;
        private TextView valueTextView;

        public TextCheckBoxCell(PhonebookShareAlert phonebookShareAlert, Context context) {
            float f;
            float f2;
            Context context2 = context;
            this.this$0 = phonebookShareAlert;
            super(context2);
            this.textView = new TextView(context2);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setSingleLine(false);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.textView.setEllipsize(TruncateAt.END);
            TextView textView = this.textView;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 17;
            if (LocaleController.isRTL) {
                f = (float) (phonebookShareAlert.isImport ? 17 : 64);
            } else {
                f = 72.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 72.0f;
            } else {
                f2 = (float) (phonebookShareAlert.isImport ? 17 : 64);
            }
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2, f, 10.0f, f2, 0.0f));
            this.valueTextView = new TextView(context2);
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView = this.valueTextView;
            i2 = LocaleController.isRTL ? 5 : 3;
            if (LocaleController.isRTL) {
                f = (float) (phonebookShareAlert.isImport ? 17 : 64);
            } else {
                f = 72.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 72.0f;
            } else {
                if (!phonebookShareAlert.isImport) {
                    i3 = 64;
                }
                f2 = (float) i3;
            }
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, i2, f, 35.0f, f2, 0.0f));
            this.imageView = new ImageView(context2);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 20.0f, 20.0f, LocaleController.isRTL ? 20.0f : 0.0f, 0.0f));
            if (!phonebookShareAlert.isImport) {
                this.checkBox = new Switch(context2);
                String str = "windowBackgroundWhite";
                this.checkBox.setColors("switchTrack", "switchTrackChecked", str, str);
                Switch switchR = this.checkBox;
                if (LocaleController.isRTL) {
                    i = 3;
                }
                addView(switchR, LayoutHelper.createFrame(37, 40.0f, i | 16, 22.0f, 0.0f, 22.0f, 0.0f));
            }
        }

        public void invalidate() {
            super.invalidate();
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int i3 = i;
            int i4 = i2;
            measureChildWithMargins(this.textView, i3, 0, i4, 0);
            measureChildWithMargins(this.valueTextView, i, 0, i2, 0);
            measureChildWithMargins(this.imageView, i3, 0, i4, 0);
            Switch switchR = this.checkBox;
            if (switchR != null) {
                measureChildWithMargins(switchR, i, 0, i2, 0);
            }
            setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(64.0f), (this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight()) + AndroidUtilities.dp(20.0f)) + this.needDivider);
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int measuredHeight = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            TextView textView = this.valueTextView;
            textView.layout(textView.getLeft(), measuredHeight, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + measuredHeight);
        }

        public void setVCardItem(VcardItem vcardItem, int i, boolean z) {
            this.textView.setText(vcardItem.getValue(true));
            this.valueTextView.setText(vcardItem.getType());
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.setChecked(vcardItem.checked, false);
            }
            if (i != 0) {
                this.imageView.setImageResource(i);
            } else {
                this.imageView.setImageDrawable(null);
            }
            this.needDivider = z;
            setWillNotDraw(this.needDivider ^ 1);
        }

        public void setChecked(boolean z) {
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.setChecked(z, true);
            }
        }

        public boolean isChecked() {
            Switch switchR = this.checkBox;
            return switchR != null && switchR.isChecked();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public class UserCell extends LinearLayout {
        final /* synthetic */ PhonebookShareAlert this$0;

        public UserCell(PhonebookShareAlert phonebookShareAlert, Context context) {
            CharSequence value;
            Object obj;
            Context context2 = context;
            this.this$0 = phonebookShareAlert;
            super(context2);
            setOrientation(1);
            if (phonebookShareAlert.phones.size() == 1 && phonebookShareAlert.other.size() == 0) {
                value = ((VcardItem) phonebookShareAlert.phones.get(0)).getValue(true);
                obj = null;
            } else {
                value = (phonebookShareAlert.currentUser.status == null || phonebookShareAlert.currentUser.status.expires == 0) ? null : LocaleController.formatUserStatus(phonebookShareAlert.currentAccount, phonebookShareAlert.currentUser);
                obj = 1;
            }
            Drawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(30.0f));
            avatarDrawable.setInfo(phonebookShareAlert.currentUser);
            BackupImageView backupImageView = new BackupImageView(context2);
            backupImageView.setRoundRadius(AndroidUtilities.dp(40.0f));
            backupImageView.setImage(ImageLocation.getForUser(phonebookShareAlert.currentUser, false), "50_50", avatarDrawable, phonebookShareAlert.currentUser);
            addView(backupImageView, LayoutHelper.createLinear(80, 80, 49, 0, 32, 0, 0));
            TextView textView = new TextView(context2);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setTextSize(1, 17.0f);
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setSingleLine(true);
            textView.setEllipsize(TruncateAt.END);
            textView.setText(ContactsController.formatName(phonebookShareAlert.currentUser.first_name, phonebookShareAlert.currentUser.last_name));
            addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 10, 10, value != null ? 0 : 27));
            if (value != null) {
                TextView textView2 = new TextView(context2);
                textView2.setTextSize(1, 14.0f);
                textView2.setTextColor(Theme.getColor("dialogTextGray3"));
                textView2.setSingleLine(true);
                textView2.setEllipsize(TruncateAt.END);
                textView2.setText(value);
                addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 10, 3, 10, obj != null ? 27 : 11));
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x021b  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x02be  */
    public PhonebookShareAlert(org.telegram.ui.ActionBar.BaseFragment r20, org.telegram.messenger.ContactsController.Contact r21, org.telegram.tgnet.TLRPC.User r22, android.net.Uri r23, java.io.File r24, java.lang.String r25) {
        /*
        r19 = this;
        r0 = r19;
        r1 = r21;
        r2 = r23;
        r3 = r25;
        r4 = r20.getParentActivity();
        r5 = 0;
        r0.<init>(r4, r5);
        r4 = new android.graphics.Paint;
        r6 = 1;
        r4.<init>(r6);
        r0.backgroundPaint = r4;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0.other = r4;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r0.phones = r4;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r7 = 0;
        if (r2 == 0) goto L_0x0035;
    L_0x002e:
        r8 = r0.currentAccount;
        r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r8, r5, r4, r3);
        goto L_0x0081;
    L_0x0035:
        if (r24 == 0) goto L_0x0047;
    L_0x0037:
        r2 = android.net.Uri.fromFile(r24);
        r8 = r0.currentAccount;
        r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r8, r5, r4, r3);
        r24.delete();
        r0.isImport = r6;
        goto L_0x0081;
    L_0x0047:
        r2 = r1.key;
        if (r2 == 0) goto L_0x0058;
    L_0x004b:
        r8 = android.provider.ContactsContract.Contacts.CONTENT_VCARD_URI;
        r2 = android.net.Uri.withAppendedPath(r8, r2);
        r8 = r0.currentAccount;
        r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r8, r6, r4, r3);
        goto L_0x0081;
    L_0x0058:
        r2 = new org.telegram.messenger.AndroidUtilities$VcardItem;
        r2.<init>();
        r2.type = r5;
        r3 = r2.vcardData;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "TEL;MOBILE:+";
        r8.append(r9);
        r9 = r1.user;
        r9 = r9.phone;
        r8.append(r9);
        r8 = r8.toString();
        r2.fullData = r8;
        r3.add(r8);
        r3 = r0.phones;
        r3.add(r2);
        r2 = r7;
    L_0x0081:
        if (r22 != 0) goto L_0x0088;
    L_0x0083:
        if (r1 == 0) goto L_0x0088;
    L_0x0085:
        r1 = r1.user;
        goto L_0x008a;
    L_0x0088:
        r1 = r22;
    L_0x008a:
        if (r2 == 0) goto L_0x00e4;
    L_0x008c:
        r3 = 0;
    L_0x008d:
        r8 = r4.size();
        if (r3 >= r8) goto L_0x00d5;
    L_0x0093:
        r8 = r4.get(r3);
        r8 = (org.telegram.messenger.AndroidUtilities.VcardItem) r8;
        r9 = r8.type;
        if (r9 != 0) goto L_0x00cd;
    L_0x009d:
        r9 = 0;
    L_0x009e:
        r10 = r0.phones;
        r10 = r10.size();
        if (r9 >= r10) goto L_0x00c1;
    L_0x00a6:
        r10 = r0.phones;
        r10 = r10.get(r9);
        r10 = (org.telegram.messenger.AndroidUtilities.VcardItem) r10;
        r10 = r10.getValue(r5);
        r11 = r8.getValue(r5);
        r10 = r10.equals(r11);
        if (r10 == 0) goto L_0x00be;
    L_0x00bc:
        r9 = 1;
        goto L_0x00c2;
    L_0x00be:
        r9 = r9 + 1;
        goto L_0x009e;
    L_0x00c1:
        r9 = 0;
    L_0x00c2:
        if (r9 == 0) goto L_0x00c7;
    L_0x00c4:
        r8.checked = r5;
        goto L_0x00d2;
    L_0x00c7:
        r9 = r0.phones;
        r9.add(r8);
        goto L_0x00d2;
    L_0x00cd:
        r9 = r0.other;
        r9.add(r8);
    L_0x00d2:
        r3 = r3 + 1;
        goto L_0x008d;
    L_0x00d5:
        r3 = r2.isEmpty();
        if (r3 != 0) goto L_0x00e4;
    L_0x00db:
        r2 = r2.get(r5);
        r2 = (org.telegram.tgnet.TLRPC.User) r2;
        r2 = r2.restriction_reason;
        goto L_0x00e5;
    L_0x00e4:
        r2 = r7;
    L_0x00e5:
        r3 = new org.telegram.tgnet.TLRPC$TL_userContact_old2;
        r3.<init>();
        r0.currentUser = r3;
        if (r1 == 0) goto L_0x0110;
    L_0x00ee:
        r3 = r0.currentUser;
        r4 = r1.id;
        r3.id = r4;
        r8 = r1.access_hash;
        r3.access_hash = r8;
        r4 = r1.photo;
        r3.photo = r4;
        r4 = r1.status;
        r3.status = r4;
        r4 = r1.first_name;
        r3.first_name = r4;
        r4 = r1.last_name;
        r3.last_name = r4;
        r1 = r1.phone;
        r3.phone = r1;
        if (r2 == 0) goto L_0x0110;
    L_0x010e:
        r3.restriction_reason = r2;
    L_0x0110:
        r1 = r20;
        r0.parentFragment = r1;
        r1 = r0.parentFragment;
        r1 = r1.getParentActivity();
        r19.updateRows();
        r2 = new org.telegram.ui.Components.PhonebookShareAlert$1;
        r2.<init>(r1, r1);
        r2.setWillNotDraw(r5);
        r0.containerView = r2;
        r0.setApplyTopPadding(r5);
        r0.setApplyBottomPadding(r5);
        r3 = new org.telegram.ui.Components.PhonebookShareAlert$ListAdapter;
        r3.<init>(r0, r7);
        r0.listAdapter = r3;
        r3 = new org.telegram.ui.Components.PhonebookShareAlert$2;
        r3.<init>(r1);
        r0.scrollView = r3;
        r3 = r0.scrollView;
        r3.setClipToPadding(r5);
        r3 = r0.scrollView;
        r3.setVerticalScrollBarEnabled(r5);
        r3 = r0.scrollView;
        r7 = -1;
        r8 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r9 = 51;
        r10 = 0;
        r11 = 0;
        r12 = 0;
        r13 = NUM; // 0x429a0000 float:77.0 double:5.52063419E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13);
        r2.addView(r3, r4);
        r3 = new android.widget.LinearLayout;
        r3.<init>(r1);
        r0.linearLayout = r3;
        r3 = r0.linearLayout;
        r3.setOrientation(r6);
        r3 = r0.scrollView;
        r4 = r0.linearLayout;
        r7 = 51;
        r8 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createScroll(r8, r8, r7);
        r3.addView(r4, r7);
        r3 = r0.scrollView;
        r4 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$UnFjjPf4YUr9var_khYpf2fRUOgU;
        r4.<init>(r0);
        r3.setOnScrollChangeListener(r4);
        r3 = r0.listAdapter;
        r3 = r3.getItemCount();
        r4 = 0;
    L_0x0183:
        if (r4 >= r3) goto L_0x01bf;
    L_0x0185:
        r7 = r0.listAdapter;
        r7 = r7.createView(r1, r4);
        r9 = r0.linearLayout;
        r10 = -2;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r10);
        r9.addView(r7, r10);
        r9 = r0.phoneStartRow;
        if (r4 < r9) goto L_0x019d;
    L_0x0199:
        r9 = r0.phoneEndRow;
        if (r4 < r9) goto L_0x01a5;
    L_0x019d:
        r9 = r0.vcardStartRow;
        if (r4 < r9) goto L_0x01bc;
    L_0x01a1:
        r9 = r0.vcardEndRow;
        if (r4 >= r9) goto L_0x01bc;
    L_0x01a5:
        r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r7.setBackgroundDrawable(r9);
        r9 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$xYhjnG_zxNlx0UlxGUyPm9kg6jA;
        r9.<init>(r0, r4, r7);
        r7.setOnClickListener(r9);
        r9 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$xbmp1lL6aksgcnSgelcdZkldvDc;
        r9.<init>(r0, r4);
        r7.setOnLongClickListener(r9);
    L_0x01bc:
        r4 = r4 + 1;
        goto L_0x0183;
    L_0x01bf:
        r3 = new org.telegram.ui.Components.PhonebookShareAlert$3;
        r3.<init>(r1);
        r0.actionBar = r3;
        r3 = r0.actionBar;
        r4 = "dialogBackground";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setBackgroundColor(r4);
        r3 = r0.actionBar;
        r4 = NUM; // 0x7var_f3 float:1.794507E38 double:1.052935623E-314;
        r3.setBackButtonImage(r4);
        r3 = r0.actionBar;
        r4 = "dialogTextBlack";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setItemsColor(r7, r5);
        r3 = r0.actionBar;
        r7 = "dialogButtonSelector";
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r3.setItemsBackgroundColor(r7, r5);
        r3 = r0.actionBar;
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setTitleColor(r4);
        r3 = r0.actionBar;
        r3.setOccupyStatusBar(r5);
        r3 = r0.actionBar;
        r4 = 0;
        r3.setAlpha(r4);
        r3 = r0.isImport;
        r7 = NUM; // 0x7f0e00b0 float:1.8875395E38 double:1.0531622436E-314;
        r9 = "AddContactPhonebookTitle";
        r10 = NUM; // 0x7f0e0a4d float:1.8880386E38 double:1.0531634595E-314;
        r11 = "ShareContactTitle";
        if (r3 == 0) goto L_0x021b;
    L_0x0211:
        r3 = r0.actionBar;
        r12 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r3.setTitle(r12);
        goto L_0x0224;
    L_0x021b:
        r3 = r0.actionBar;
        r12 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r3.setTitle(r12);
    L_0x0224:
        r3 = r0.containerView;
        r12 = r0.actionBar;
        r13 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r13);
        r3.addView(r12, r13);
        r3 = r0.actionBar;
        r12 = new org.telegram.ui.Components.PhonebookShareAlert$4;
        r12.<init>();
        r3.setActionBarMenuOnItemClick(r12);
        r3 = new android.view.View;
        r3.<init>(r1);
        r0.actionBarShadow = r3;
        r3 = r0.actionBarShadow;
        r3.setAlpha(r4);
        r3 = r0.actionBarShadow;
        r12 = "dialogShadowLine";
        r13 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r3.setBackgroundColor(r13);
        r3 = r0.containerView;
        r13 = r0.actionBarShadow;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r14);
        r3.addView(r13, r8);
        r3 = new android.view.View;
        r3.<init>(r1);
        r0.shadow = r3;
        r3 = r0.shadow;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r12);
        r3.setBackgroundColor(r8);
        r3 = r0.shadow;
        r3.setAlpha(r4);
        r3 = r0.containerView;
        r4 = r0.shadow;
        r12 = -1;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14 = 83;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = NUM; // 0x429a0000 float:77.0 double:5.52063419E-315;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18);
        r3.addView(r4, r8);
        r3 = new android.widget.TextView;
        r3.<init>(r1);
        r0.buttonTextView = r3;
        r1 = r0.buttonTextView;
        r3 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.setPadding(r4, r5, r3, r5);
        r1 = r0.buttonTextView;
        r3 = 17;
        r1.setGravity(r3);
        r1 = r0.buttonTextView;
        r3 = "featuredStickers_buttonText";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setTextColor(r3);
        r1 = r0.buttonTextView;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r1.setTextSize(r6, r3);
        r1 = r0.isImport;
        if (r1 == 0) goto L_0x02c8;
    L_0x02be:
        r1 = r0.buttonTextView;
        r3 = org.telegram.messenger.LocaleController.getString(r9, r7);
        r1.setText(r3);
        goto L_0x02d1;
    L_0x02c8:
        r1 = r0.buttonTextView;
        r3 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r1.setText(r3);
    L_0x02d1:
        r1 = r0.buttonTextView;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = r0.buttonTextView;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = "featuredStickers_addButton";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r5 = "featuredStickers_addButtonPressed";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r3, r4, r5);
        r1.setBackground(r3);
        r1 = r0.buttonTextView;
        r3 = -1;
        r4 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
        r5 = 83;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r9 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9);
        r2.addView(r1, r3);
        r1 = r0.buttonTextView;
        r2 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$qATn_q-GXrzbrKnXSToIzpql_Ds;
        r2.<init>(r0);
        r1.setOnClickListener(r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.ContactsController$Contact, org.telegram.tgnet.TLRPC$User, android.net.Uri, java.io.File, java.lang.String):void");
    }

    public /* synthetic */ void lambda$new$0$PhonebookShareAlert(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
        updateLayout(this.inLayout ^ 1);
    }

    public /* synthetic */ void lambda$new$2$PhonebookShareAlert(int i, View view, View view2) {
        VcardItem vcardItem;
        int i2 = this.phoneStartRow;
        if (i < i2 || i >= this.phoneEndRow) {
            i2 = this.vcardStartRow;
            vcardItem = (i < i2 || i >= this.vcardEndRow) ? null : (VcardItem) this.other.get(i - i2);
        } else {
            vcardItem = (VcardItem) this.phones.get(i - i2);
        }
        if (vcardItem != null) {
            boolean z = true;
            if (this.isImport) {
                i = vcardItem.type;
                StringBuilder stringBuilder;
                if (i == 0) {
                    try {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("tel:");
                        stringBuilder2.append(vcardItem.getValue(false));
                        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse(stringBuilder2.toString()));
                        intent.addFlags(NUM);
                        this.parentFragment.getParentActivity().startActivityForResult(intent, 500);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (i == 1) {
                    Context parentActivity = this.parentFragment.getParentActivity();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("mailto:");
                    stringBuilder.append(vcardItem.getValue(false));
                    Browser.openUrl(parentActivity, stringBuilder.toString());
                } else if (i == 3) {
                    String value = vcardItem.getValue(false);
                    if (!value.startsWith("http")) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("http://");
                        stringBuilder.append(value);
                        value = stringBuilder.toString();
                    }
                    Browser.openUrl(this.parentFragment.getParentActivity(), value);
                } else {
                    Builder builder = new Builder(this.parentFragment.getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new -$$Lambda$PhonebookShareAlert$hhDW6LcXTbjZeBc7t6WBw5vGqm0(this, vcardItem));
                    builder.show();
                }
            } else {
                vcardItem.checked ^= 1;
                if (i >= this.phoneStartRow && i < this.phoneEndRow) {
                    for (i = 0; i < this.phones.size(); i++) {
                        if (((VcardItem) this.phones.get(i)).checked) {
                            break;
                        }
                    }
                    z = false;
                    i = Theme.getColor("featuredStickers_buttonText");
                    this.buttonTextView.setEnabled(z);
                    TextView textView = this.buttonTextView;
                    if (!z) {
                        i &= Integer.MAX_VALUE;
                    }
                    textView.setTextColor(i);
                }
                ((TextCheckBoxCell) view).setChecked(vcardItem.checked);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$PhonebookShareAlert(VcardItem vcardItem, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
                Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ boolean lambda$new$3$PhonebookShareAlert(int i, View view) {
        VcardItem vcardItem;
        int i2 = this.phoneStartRow;
        if (i < i2 || i >= this.phoneEndRow) {
            i2 = this.vcardStartRow;
            vcardItem = (i < i2 || i >= this.vcardEndRow) ? null : (VcardItem) this.other.get(i - i2);
        } else {
            vcardItem = (VcardItem) this.phones.get(i - i2);
        }
        if (vcardItem == null) {
            return false;
        }
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
        i = vcardItem.type;
        if (i == 0) {
            Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("PhoneCopied", NUM), 0).show();
        } else if (i == 1) {
            Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("EmailCopied", NUM), 0).show();
        } else if (i == 3) {
            Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
        } else {
            Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
        }
        return true;
    }

    public /* synthetic */ void lambda$new$5$PhonebookShareAlert(View view) {
        if (this.isImport) {
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("AddContactTitle", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            builder.setItems(new CharSequence[]{LocaleController.getString("CreateNewContact", NUM), LocaleController.getString("AddToExistingContact", NUM)}, new OnClickListener() {
                private void fillRowWithType(String str, ContentValues contentValues) {
                    boolean startsWith = str.startsWith("X-");
                    String str2 = "data3";
                    Integer valueOf = Integer.valueOf(0);
                    String str3 = "data2";
                    if (startsWith) {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str.substring(2));
                    } else if ("PREF".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(12));
                    } else if ("HOME".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(1));
                    } else if ("MOBILE".equalsIgnoreCase(str) || "CELL".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(2));
                    } else if ("OTHER".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(7));
                    } else if ("WORK".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(3));
                    } else if ("RADIO".equalsIgnoreCase(str) || "VOICE".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(14));
                    } else if ("PAGER".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(6));
                    } else if ("CALLBACK".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(8));
                    } else if ("CAR".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(9));
                    } else if ("ASSISTANT".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(19));
                    } else if ("MMS".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(20));
                    } else if (str.startsWith("FAX")) {
                        contentValues.put(str3, Integer.valueOf(4));
                    } else {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str);
                    }
                }

                private void fillUrlRowWithType(String str, ContentValues contentValues) {
                    boolean startsWith = str.startsWith("X-");
                    String str2 = "data3";
                    Integer valueOf = Integer.valueOf(0);
                    String str3 = "data2";
                    if (startsWith) {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str.substring(2));
                    } else if ("HOMEPAGE".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(1));
                    } else if ("BLOG".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(2));
                    } else if ("PROFILE".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(3));
                    } else if ("HOME".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(4));
                    } else if ("WORK".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(5));
                    } else if ("FTP".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(6));
                    } else if ("OTHER".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(7));
                    } else {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str);
                    }
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent;
                    String str;
                    String str2;
                    VcardItem vcardItem;
                    AnonymousClass5 anonymousClass5;
                    AnonymousClass5 anonymousClass52 = this;
                    int i2 = i;
                    int i3 = 1;
                    Integer valueOf = Integer.valueOf(1);
                    if (i2 == 0) {
                        intent = new Intent("android.intent.action.INSERT");
                        intent.setType("vnd.android.cursor.dir/raw_contact");
                    } else if (i2 == 1) {
                        intent = new Intent("android.intent.action.INSERT_OR_EDIT");
                        intent.setType("vnd.android.cursor.item/contact");
                    } else {
                        intent = null;
                    }
                    intent.putExtra("name", ContactsController.formatName(PhonebookShareAlert.this.currentUser.first_name, PhonebookShareAlert.this.currentUser.last_name));
                    ArrayList arrayList = new ArrayList();
                    boolean z = false;
                    int i4 = 0;
                    while (true) {
                        str = "data1";
                        str2 = "mimetype";
                        if (i4 >= PhonebookShareAlert.this.phones.size()) {
                            break;
                        }
                        vcardItem = (VcardItem) PhonebookShareAlert.this.phones.get(i4);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(str2, "vnd.android.cursor.item/phone_v2");
                        contentValues.put(str, vcardItem.getValue(false));
                        anonymousClass52.fillRowWithType(vcardItem.getRawType(false), contentValues);
                        arrayList.add(contentValues);
                        i4++;
                    }
                    i4 = 0;
                    Object obj = null;
                    while (i4 < PhonebookShareAlert.this.other.size()) {
                        Intent intent2;
                        int i5;
                        VcardItem vcardItem2 = (VcardItem) PhonebookShareAlert.this.other.get(i4);
                        int i6 = vcardItem2.type;
                        ContentValues contentValues2;
                        if (i6 == i3) {
                            contentValues2 = new ContentValues();
                            contentValues2.put(str2, "vnd.android.cursor.item/email_v2");
                            contentValues2.put(str, vcardItem2.getValue(z));
                            anonymousClass52.fillRowWithType(vcardItem2.getRawType(z), contentValues2);
                            arrayList.add(contentValues2);
                        } else if (i6 == 3) {
                            contentValues2 = new ContentValues();
                            contentValues2.put(str2, "vnd.android.cursor.item/website");
                            contentValues2.put(str, vcardItem2.getValue(z));
                            anonymousClass52.fillUrlRowWithType(vcardItem2.getRawType(z), contentValues2);
                            arrayList.add(contentValues2);
                        } else if (i6 == 4) {
                            contentValues2 = new ContentValues();
                            contentValues2.put(str2, "vnd.android.cursor.item/note");
                            contentValues2.put(str, vcardItem2.getValue(z));
                            arrayList.add(contentValues2);
                        } else {
                            String str3 = "data2";
                            if (i6 == 5) {
                                contentValues2 = new ContentValues();
                                contentValues2.put(str2, "vnd.android.cursor.item/contact_event");
                                contentValues2.put(str, vcardItem2.getValue(z));
                                contentValues2.put(str3, Integer.valueOf(3));
                                arrayList.add(contentValues2);
                            } else {
                                String str4 = "data6";
                                String str5 = "OTHER";
                                String str6 = "WORK";
                                String str7 = "data4";
                                intent2 = intent;
                                String str8 = "data5";
                                if (i6 == 2) {
                                    contentValues2 = new ContentValues();
                                    contentValues2.put(str2, "vnd.android.cursor.item/postal-address_v2");
                                    String[] rawValue = vcardItem2.getRawValue();
                                    i5 = i4;
                                    if (rawValue.length > 0) {
                                        contentValues2.put(str8, rawValue[0]);
                                    }
                                    if (rawValue.length > 1) {
                                        contentValues2.put(str4, rawValue[1]);
                                    }
                                    if (rawValue.length > 2) {
                                        contentValues2.put(str7, rawValue[2]);
                                    }
                                    if (rawValue.length > 3) {
                                        contentValues2.put("data7", rawValue[3]);
                                    }
                                    if (rawValue.length > 4) {
                                        contentValues2.put("data8", rawValue[4]);
                                    }
                                    if (rawValue.length > 5) {
                                        contentValues2.put("data9", rawValue[5]);
                                    }
                                    if (rawValue.length > 6) {
                                        contentValues2.put("data10", rawValue[6]);
                                    }
                                    String rawType = vcardItem2.getRawType(false);
                                    if ("HOME".equalsIgnoreCase(rawType)) {
                                        contentValues2.put(str3, valueOf);
                                    } else if (str6.equalsIgnoreCase(rawType)) {
                                        contentValues2.put(str3, Integer.valueOf(2));
                                    } else if (str5.equalsIgnoreCase(rawType)) {
                                        contentValues2.put(str3, Integer.valueOf(3));
                                    }
                                    arrayList.add(contentValues2);
                                } else {
                                    i5 = i4;
                                    ContentValues contentValues3;
                                    if (i6 == 20) {
                                        contentValues3 = new ContentValues();
                                        contentValues3.put(str2, "vnd.android.cursor.item/im");
                                        String rawType2 = vcardItem2.getRawType(true);
                                        String rawType3 = vcardItem2.getRawType(false);
                                        contentValues3.put(str, vcardItem2.getValue(false));
                                        if ("AIM".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(0));
                                        } else if ("MSN".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, valueOf);
                                        } else if ("YAHOO".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(2));
                                        } else if ("SKYPE".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(3));
                                        } else if ("QQ".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(4));
                                        } else if ("GOOGLE-TALK".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(5));
                                        } else if ("ICQ".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(6));
                                        } else if ("JABBER".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(7));
                                        } else if ("NETMEETING".equalsIgnoreCase(rawType2)) {
                                            contentValues3.put(str8, Integer.valueOf(8));
                                        } else {
                                            contentValues3.put(str8, Integer.valueOf(-1));
                                            contentValues3.put(str4, vcardItem2.getRawType(true));
                                        }
                                        if ("HOME".equalsIgnoreCase(rawType3)) {
                                            contentValues3.put(str3, valueOf);
                                        } else if (str6.equalsIgnoreCase(rawType3)) {
                                            contentValues3.put(str3, Integer.valueOf(2));
                                        } else if (str5.equalsIgnoreCase(rawType3)) {
                                            contentValues3.put(str3, Integer.valueOf(3));
                                        }
                                        arrayList.add(contentValues3);
                                    } else if (i6 == 6 && obj == null) {
                                        contentValues3 = new ContentValues();
                                        contentValues3.put(str2, "vnd.android.cursor.item/organization");
                                        anonymousClass5 = this;
                                        for (i4 = i5; i4 < PhonebookShareAlert.this.other.size(); i4++) {
                                            vcardItem = (VcardItem) PhonebookShareAlert.this.other.get(i4);
                                            if (vcardItem.type == 6) {
                                                str4 = vcardItem.getRawType(true);
                                                if ("ORG".equalsIgnoreCase(str4)) {
                                                    String[] rawValue2 = vcardItem.getRawValue();
                                                    if (rawValue2.length != 0) {
                                                        if (rawValue2.length >= 1) {
                                                            contentValues3.put(str, rawValue2[0]);
                                                        }
                                                        if (rawValue2.length >= 2) {
                                                            contentValues3.put(str8, rawValue2[1]);
                                                        }
                                                    }
                                                } else if ("TITLE".equalsIgnoreCase(str4)) {
                                                    contentValues3.put(str7, vcardItem.getValue(false));
                                                } else if ("ROLE".equalsIgnoreCase(str4)) {
                                                    contentValues3.put(str7, vcardItem.getValue(false));
                                                }
                                                String rawType4 = vcardItem.getRawType(true);
                                                if (str6.equalsIgnoreCase(rawType4)) {
                                                    contentValues3.put(str3, valueOf);
                                                } else if (str5.equalsIgnoreCase(rawType4)) {
                                                    contentValues3.put(str3, Integer.valueOf(2));
                                                }
                                            }
                                        }
                                        arrayList.add(contentValues3);
                                        obj = 1;
                                        i4 = i5 + 1;
                                        anonymousClass52 = anonymousClass5;
                                        intent = intent2;
                                        i3 = 1;
                                        z = false;
                                    }
                                }
                                anonymousClass5 = this;
                                i4 = i5 + 1;
                                anonymousClass52 = anonymousClass5;
                                intent = intent2;
                                i3 = 1;
                                z = false;
                            }
                        }
                        intent2 = intent;
                        anonymousClass5 = anonymousClass52;
                        i5 = i4;
                        i4 = i5 + 1;
                        anonymousClass52 = anonymousClass5;
                        intent = intent2;
                        i3 = 1;
                        z = false;
                    }
                    anonymousClass5 = anonymousClass52;
                    Intent intent3 = intent;
                    intent3.putExtra("finishActivityOnSaveCompleted", true);
                    intent3.putParcelableArrayListExtra("data", arrayList);
                    try {
                        PhonebookShareAlert.this.parentFragment.getParentActivity().startActivity(intent3);
                        PhonebookShareAlert.this.dismiss();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
            builder.show();
            return;
        }
        StringBuilder stringBuilder;
        if (this.currentUser.restriction_reason.isEmpty()) {
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            User user = this.currentUser;
            objArr[0] = ContactsController.formatName(user.first_name, user.last_name);
            stringBuilder = new StringBuilder(String.format(locale, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", objArr));
        } else {
            stringBuilder = new StringBuilder(((TL_restrictionReason) this.currentUser.restriction_reason.get(0)).text);
        }
        int lastIndexOf = stringBuilder.lastIndexOf("END:VCARD");
        if (lastIndexOf >= 0) {
            String str;
            VcardItem vcardItem;
            int i;
            StringBuilder stringBuilder2;
            this.currentUser.phone = null;
            int size = this.phones.size() - 1;
            while (true) {
                str = "\n";
                if (size < 0) {
                    break;
                }
                vcardItem = (VcardItem) this.phones.get(size);
                if (vcardItem.checked) {
                    User user2 = this.currentUser;
                    if (user2.phone == null) {
                        user2.phone = vcardItem.getValue(false);
                    }
                    for (i = 0; i < vcardItem.vcardData.size(); i++) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append((String) vcardItem.vcardData.get(i));
                        stringBuilder2.append(str);
                        stringBuilder.insert(lastIndexOf, stringBuilder2.toString());
                    }
                }
                size--;
            }
            for (size = this.other.size() - 1; size >= 0; size--) {
                vcardItem = (VcardItem) this.other.get(size);
                if (vcardItem.checked) {
                    for (i = vcardItem.vcardData.size() - 1; i >= 0; i--) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append((String) vcardItem.vcardData.get(i));
                        stringBuilder2.append(str);
                        stringBuilder.insert(lastIndexOf, stringBuilder2.toString());
                    }
                }
            }
            this.currentUser.restriction_reason.clear();
            TL_restrictionReason tL_restrictionReason = new TL_restrictionReason();
            tL_restrictionReason.text = stringBuilder.toString();
            String str2 = "";
            tL_restrictionReason.reason = str2;
            tL_restrictionReason.platform = str2;
            this.currentUser.restriction_reason.add(tL_restrictionReason);
        }
        BaseFragment baseFragment = this.parentFragment;
        if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentFragment).getDialogId(), new -$$Lambda$PhonebookShareAlert$vLjVUumASpET7c9XA-2F6lNn_CA(this));
            return;
        }
        this.delegate.didSelectContact(this.currentUser, true, 0);
        dismiss();
    }

    public /* synthetic */ void lambda$null$4$PhonebookShareAlert(boolean z, int i) {
        this.delegate.didSelectContact(this.currentUser, z, i);
        dismiss();
    }

    public void setDelegate(PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    private void updateLayout(boolean z) {
        View childAt = this.scrollView.getChildAt(0);
        int top = childAt.getTop() - this.scrollView.getScrollY();
        if (top < 0) {
            top = 0;
        }
        Object obj = top <= 0 ? 1 : null;
        float f = 1.0f;
        if ((obj != null && this.actionBar.getTag() == null) || (obj == null && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(obj != null ? Integer.valueOf(1) : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            if (z) {
                this.actionBarAnimation = new AnimatorSet();
                this.actionBarAnimation.setDuration(180);
                animatorSet = this.actionBarAnimation;
                Animator[] animatorArr = new Animator[2];
                ActionBar actionBar = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = obj != null ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property, fArr);
                View view = this.actionBarShadow;
                property = View.ALPHA;
                fArr = new float[1];
                fArr[0] = obj != null ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PhonebookShareAlert.this.actionBarAnimation = null;
                    }
                });
                this.actionBarAnimation.start();
            } else {
                this.actionBar.setAlpha(obj != null ? 1.0f : 0.0f);
                this.actionBarShadow.setAlpha(obj != null ? 1.0f : 0.0f);
            }
        }
        if (this.scrollOffsetY != top) {
            this.scrollOffsetY = top;
            this.containerView.invalidate();
        }
        childAt.getBottom();
        this.scrollView.getMeasuredHeight();
        Object obj2 = childAt.getBottom() - this.scrollView.getScrollY() > this.scrollView.getMeasuredHeight() ? 1 : null;
        if ((obj2 != null && this.shadow.getTag() == null) || (obj2 == null && this.shadow.getTag() != null)) {
            this.shadow.setTag(obj2 != null ? Integer.valueOf(1) : null);
            AnimatorSet animatorSet2 = this.shadowAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.shadowAnimation = null;
            }
            if (z) {
                this.shadowAnimation = new AnimatorSet();
                this.shadowAnimation.setDuration(180);
                animatorSet2 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                View view2 = this.shadow;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (obj2 == null) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(view2, property2, fArr2);
                animatorSet2.playTogether(animatorArr2);
                this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PhonebookShareAlert.this.shadowAnimation = null;
                    }
                });
                this.shadowAnimation.start();
                return;
            }
            View view3 = this.shadow;
            if (obj2 == null) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.userRow = i;
        if (this.phones.size() > 1 || !this.other.isEmpty()) {
            if (this.phones.isEmpty()) {
                this.phoneStartRow = -1;
                this.phoneEndRow = -1;
            } else {
                i = this.rowCount;
                this.phoneStartRow = i;
                this.rowCount = i + this.phones.size();
                this.phoneEndRow = this.rowCount;
            }
            if (this.other.isEmpty()) {
                this.vcardStartRow = -1;
                this.vcardEndRow = -1;
                return;
            }
            i = this.rowCount;
            this.vcardStartRow = i;
            this.rowCount = i + this.other.size();
            this.vcardEndRow = this.rowCount;
            return;
        }
        this.phoneStartRow = -1;
        this.phoneEndRow = -1;
        this.vcardStartRow = -1;
        this.vcardEndRow = -1;
    }
}
