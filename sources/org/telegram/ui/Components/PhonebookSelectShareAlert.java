package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.PhonebookSelectShareAlert;
import org.telegram.ui.Components.RecyclerListView;

public class PhonebookSelectShareAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private ChatActivity chatActivity;
    private PhonebookShareAlertDelegate delegate;
    private FrameLayout frameLayout;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ShareAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public ShareSearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public int topBeforeSwitch;

    public interface PhonebookShareAlertDelegate {
        void didSelectContact(TLRPC$User tLRPC$User, boolean z, int i);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public class UserCell extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private BackupImageView avatarImageView;
        private int currentId;
        private CharSequence currentName;
        private CharSequence currentStatus;
        private TLRPC$User currentUser;
        private TLRPC$FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private SimpleTextView statusTextView;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public UserCell(PhonebookSelectShareAlert phonebookSelectShareAlert, Context context) {
            super(context);
            int i = UserConfig.selectedAccount;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
            int i2 = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 9.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 12.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.statusTextView = simpleTextView2;
            simpleTextView2.setTextSize(13);
            this.statusTextView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 36.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        }

        public void setCurrentId(int i) {
            this.currentId = i;
        }

        public void setData(TLRPC$User tLRPC$User, CharSequence charSequence, CharSequence charSequence2, boolean z) {
            if (tLRPC$User == null && charSequence == null && charSequence2 == null) {
                this.currentStatus = null;
                this.currentName = null;
                this.nameTextView.setText("");
                this.statusTextView.setText("");
                this.avatarImageView.setImageDrawable((Drawable) null);
                return;
            }
            this.currentStatus = charSequence2;
            this.currentName = charSequence;
            this.currentUser = tLRPC$User;
            this.needDivider = z;
            setWillNotDraw(!z);
            update(0);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
            r0 = r0.photo;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x006c, code lost:
            if (r11.equals(r10.lastName) == false) goto L_0x0071;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void update(int r11) {
            /*
                r10 = this;
                org.telegram.tgnet.TLRPC$User r0 = r10.currentUser
                r1 = 0
                if (r0 == 0) goto L_0x000c
                org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
                if (r0 == 0) goto L_0x000c
                org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
                goto L_0x000d
            L_0x000c:
                r0 = r1
            L_0x000d:
                r2 = 0
                if (r11 == 0) goto L_0x0074
                r3 = r11 & 2
                r4 = 1
                if (r3 == 0) goto L_0x0037
                org.telegram.tgnet.TLRPC$FileLocation r3 = r10.lastAvatar
                if (r3 == 0) goto L_0x001b
                if (r0 == 0) goto L_0x0035
            L_0x001b:
                org.telegram.tgnet.TLRPC$FileLocation r3 = r10.lastAvatar
                if (r3 != 0) goto L_0x0021
                if (r0 != 0) goto L_0x0035
            L_0x0021:
                org.telegram.tgnet.TLRPC$FileLocation r3 = r10.lastAvatar
                if (r3 == 0) goto L_0x0037
                if (r0 == 0) goto L_0x0037
                long r5 = r3.volume_id
                long r7 = r0.volume_id
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 != 0) goto L_0x0035
                int r3 = r3.local_id
                int r5 = r0.local_id
                if (r3 == r5) goto L_0x0037
            L_0x0035:
                r3 = 1
                goto L_0x0038
            L_0x0037:
                r3 = 0
            L_0x0038:
                org.telegram.tgnet.TLRPC$User r5 = r10.currentUser
                if (r5 == 0) goto L_0x004f
                if (r3 != 0) goto L_0x004f
                r6 = r11 & 4
                if (r6 == 0) goto L_0x004f
                org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
                if (r5 == 0) goto L_0x0049
                int r5 = r5.expires
                goto L_0x004a
            L_0x0049:
                r5 = 0
            L_0x004a:
                int r6 = r10.lastStatus
                if (r5 == r6) goto L_0x004f
                r3 = 1
            L_0x004f:
                if (r3 != 0) goto L_0x006f
                java.lang.CharSequence r5 = r10.currentName
                if (r5 != 0) goto L_0x006f
                java.lang.String r5 = r10.lastName
                if (r5 == 0) goto L_0x006f
                r11 = r11 & r4
                if (r11 == 0) goto L_0x006f
                org.telegram.tgnet.TLRPC$User r11 = r10.currentUser
                if (r11 == 0) goto L_0x0065
                java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r11)
                goto L_0x0066
            L_0x0065:
                r11 = r1
            L_0x0066:
                java.lang.String r5 = r10.lastName
                boolean r5 = r11.equals(r5)
                if (r5 != 0) goto L_0x0070
                goto L_0x0071
            L_0x006f:
                r11 = r1
            L_0x0070:
                r4 = r3
            L_0x0071:
                if (r4 != 0) goto L_0x0075
                return
            L_0x0074:
                r11 = r1
            L_0x0075:
                org.telegram.tgnet.TLRPC$User r3 = r10.currentUser
                if (r3 == 0) goto L_0x008c
                org.telegram.ui.Components.AvatarDrawable r4 = r10.avatarDrawable
                r4.setInfo((org.telegram.tgnet.TLRPC$User) r3)
                org.telegram.tgnet.TLRPC$User r3 = r10.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r3 = r3.status
                if (r3 == 0) goto L_0x0089
                int r3 = r3.expires
                r10.lastStatus = r3
                goto L_0x00a5
            L_0x0089:
                r10.lastStatus = r2
                goto L_0x00a5
            L_0x008c:
                java.lang.CharSequence r3 = r10.currentName
                if (r3 == 0) goto L_0x009c
                org.telegram.ui.Components.AvatarDrawable r4 = r10.avatarDrawable
                int r5 = r10.currentId
                java.lang.String r3 = r3.toString()
                r4.setInfo(r5, r3, r1)
                goto L_0x00a5
            L_0x009c:
                org.telegram.ui.Components.AvatarDrawable r3 = r10.avatarDrawable
                int r4 = r10.currentId
                java.lang.String r5 = "#"
                r3.setInfo(r4, r5, r1)
            L_0x00a5:
                java.lang.CharSequence r3 = r10.currentName
                if (r3 == 0) goto L_0x00b1
                r10.lastName = r1
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.nameTextView
                r11.setText(r3)
                goto L_0x00c9
            L_0x00b1:
                org.telegram.tgnet.TLRPC$User r1 = r10.currentUser
                if (r1 == 0) goto L_0x00be
                if (r11 != 0) goto L_0x00bb
                java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r1)
            L_0x00bb:
                r10.lastName = r11
                goto L_0x00c2
            L_0x00be:
                java.lang.String r11 = ""
                r10.lastName = r11
            L_0x00c2:
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.nameTextView
                java.lang.String r1 = r10.lastName
                r11.setText(r1)
            L_0x00c9:
                java.lang.CharSequence r11 = r10.currentStatus
                if (r11 == 0) goto L_0x00d3
                org.telegram.ui.ActionBar.SimpleTextView r1 = r10.statusTextView
                r1.setText(r11)
                goto L_0x0110
            L_0x00d3:
                org.telegram.tgnet.TLRPC$User r11 = r10.currentUser
                if (r11 == 0) goto L_0x0110
                java.lang.String r11 = r11.phone
                boolean r11 = android.text.TextUtils.isEmpty(r11)
                if (r11 == 0) goto L_0x00ee
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.statusTextView
                r1 = 2131625986(0x7f0e0802, float:1.8879195E38)
                java.lang.String r3 = "NumberUnknown"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r11.setText(r1)
                goto L_0x0110
            L_0x00ee:
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.statusTextView
                org.telegram.PhoneFormat.PhoneFormat r1 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "+"
                r3.append(r4)
                org.telegram.tgnet.TLRPC$User r4 = r10.currentUser
                java.lang.String r4 = r4.phone
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                java.lang.String r1 = r1.format(r3)
                r11.setText(r1)
            L_0x0110:
                r10.lastAvatar = r0
                org.telegram.tgnet.TLRPC$User r11 = r10.currentUser
                if (r11 == 0) goto L_0x0126
                org.telegram.ui.Components.BackupImageView r0 = r10.avatarImageView
                org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForUser(r11, r2)
                org.telegram.ui.Components.AvatarDrawable r1 = r10.avatarDrawable
                org.telegram.tgnet.TLRPC$User r2 = r10.currentUser
                java.lang.String r3 = "50_50"
                r0.setImage((org.telegram.messenger.ImageLocation) r11, (java.lang.String) r3, (android.graphics.drawable.Drawable) r1, (java.lang.Object) r2)
                goto L_0x012d
            L_0x0126:
                org.telegram.ui.Components.BackupImageView r11 = r10.avatarImageView
                org.telegram.ui.Components.AvatarDrawable r0 = r10.avatarDrawable
                r11.setImageDrawable(r0)
            L_0x012d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookSelectShareAlert.UserCell.update(int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    private class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhonebookSelectShareAlert.SearchField.this.lambda$new$0$PhonebookSelectShareAlert$SearchField(view);
                }
            });
            AnonymousClass1 r0 = new EditTextBoldCursor(context, PhonebookSelectShareAlert.this) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - PhonebookSelectShareAlert.this.containerView.getTranslationY());
                    PhonebookSelectShareAlert.this.listView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor("dialogSearchHint"));
            this.searchEditText.setTextColor(Theme.getColor("dialogSearchText"));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("SearchFriends", NUM));
            this.searchEditText.setCursorColor(Theme.getColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(PhonebookSelectShareAlert.this) {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    boolean z = true;
                    boolean z2 = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        z = false;
                    }
                    if (z2 != z) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z2) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    String obj = SearchField.this.searchEditText.getText().toString();
                    if (obj.length() != 0) {
                        if (PhonebookSelectShareAlert.this.searchEmptyView != null) {
                            PhonebookSelectShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                        }
                    } else if (PhonebookSelectShareAlert.this.listView.getAdapter() != PhonebookSelectShareAlert.this.listAdapter) {
                        int access$600 = PhonebookSelectShareAlert.this.getCurrentTop();
                        PhonebookSelectShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoContacts", NUM));
                        PhonebookSelectShareAlert.this.searchEmptyView.showTextView();
                        PhonebookSelectShareAlert.this.listView.setAdapter(PhonebookSelectShareAlert.this.listAdapter);
                        PhonebookSelectShareAlert.this.listAdapter.notifyDataSetChanged();
                        if (access$600 > 0) {
                            PhonebookSelectShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$600);
                        }
                    }
                    if (PhonebookSelectShareAlert.this.searchAdapter != null) {
                        PhonebookSelectShareAlert.this.searchAdapter.search(obj);
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return PhonebookSelectShareAlert.SearchField.this.lambda$new$1$PhonebookSelectShareAlert$SearchField(textView, i, keyEvent);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$PhonebookSelectShareAlert$SearchField(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public /* synthetic */ boolean lambda$new$1$PhonebookSelectShareAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent == null) {
                return false;
            }
            if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    public PhonebookSelectShareAlert(ChatActivity chatActivity2) {
        super(chatActivity2.getParentActivity(), true);
        this.chatActivity = chatActivity2;
        Activity parentActivity = chatActivity2.getParentActivity();
        Drawable mutate = parentActivity.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.searchAdapter = new ShareSearchAdapter(parentActivity);
        AnonymousClass1 r0 = new FrameLayout(parentActivity) {
            private boolean fullHeight;
            private boolean ignoreLayout = false;
            private RectF rect1 = new RectF();

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                int size = View.MeasureSpec.getSize(i2);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21 && !PhonebookSelectShareAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(PhonebookSelectShareAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, PhonebookSelectShareAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size - getPaddingTop();
                int max = Math.max(PhonebookSelectShareAlert.this.searchAdapter.getItemCount(), PhonebookSelectShareAlert.this.listAdapter.getItemCount());
                if (max > 0) {
                    max--;
                }
                int dp = (((AndroidUtilities.dp(91.0f) + (AndroidUtilities.dp(64.0f) * max)) + max) - 1) + PhonebookSelectShareAlert.this.backgroundPaddingTop;
                if (dp < paddingTop) {
                    i3 = 0;
                } else {
                    i3 = paddingTop - ((paddingTop / 5) * 3);
                }
                int dp2 = i3 + AndroidUtilities.dp(8.0f);
                if (PhonebookSelectShareAlert.this.listView.getPaddingTop() != dp2) {
                    this.ignoreLayout = true;
                    PhonebookSelectShareAlert.this.listView.setPadding(0, dp2, 0, 0);
                    this.ignoreLayout = false;
                }
                if (dp < size) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(dp, size), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                PhonebookSelectShareAlert.this.updateLayout();
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || PhonebookSelectShareAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) (PhonebookSelectShareAlert.this.scrollOffsetY - AndroidUtilities.dp(30.0f)))) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                PhonebookSelectShareAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !PhonebookSelectShareAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x014c  */
            /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDraw(android.graphics.Canvas r14) {
                /*
                    r13 = this;
                    org.telegram.ui.Components.PhonebookSelectShareAlert r0 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r0 = r0.scrollOffsetY
                    org.telegram.ui.Components.PhonebookSelectShareAlert r1 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r1 = r1.backgroundPaddingTop
                    int r0 = r0 - r1
                    r1 = 1086324736(0x40CLASSNAME, float:6.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r0 = r0 + r1
                    org.telegram.ui.Components.PhonebookSelectShareAlert r1 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r1 = r1.scrollOffsetY
                    org.telegram.ui.Components.PhonebookSelectShareAlert r2 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r2 = r2.backgroundPaddingTop
                    int r1 = r1 - r2
                    r2 = 1095761920(0x41500000, float:13.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r1 = r1 - r2
                    int r2 = r13.getMeasuredHeight()
                    r3 = 1106247680(0x41var_, float:30.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r2 = r2 + r3
                    org.telegram.ui.Components.PhonebookSelectShareAlert r3 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r3 = r3.backgroundPaddingTop
                    int r2 = r2 + r3
                    org.telegram.ui.Components.PhonebookSelectShareAlert r3 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    boolean r3 = r3.isFullscreen
                    r4 = 0
                    r5 = 1065353216(0x3var_, float:1.0)
                    if (r3 != 0) goto L_0x009a
                    int r3 = android.os.Build.VERSION.SDK_INT
                    r6 = 21
                    if (r3 < r6) goto L_0x009a
                    int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r1 = r1 + r3
                    int r0 = r0 + r3
                    int r2 = r2 - r3
                    boolean r3 = r13.fullHeight
                    if (r3 == 0) goto L_0x009a
                    org.telegram.ui.Components.PhonebookSelectShareAlert r3 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r3 = r3.backgroundPaddingTop
                    int r3 = r3 + r1
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r7 = r6 * 2
                    if (r3 >= r7) goto L_0x007f
                    int r3 = r6 * 2
                    int r3 = r3 - r1
                    org.telegram.ui.Components.PhonebookSelectShareAlert r7 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r7 = r7.backgroundPaddingTop
                    int r3 = r3 - r7
                    int r3 = java.lang.Math.min(r6, r3)
                    int r1 = r1 - r3
                    int r2 = r2 + r3
                    int r3 = r3 * 2
                    float r3 = (float) r3
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r6 = (float) r6
                    float r3 = r3 / r6
                    float r3 = java.lang.Math.min(r5, r3)
                    float r3 = r5 - r3
                    goto L_0x0081
                L_0x007f:
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x0081:
                    org.telegram.ui.Components.PhonebookSelectShareAlert r6 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r6 = r6.backgroundPaddingTop
                    int r6 = r6 + r1
                    int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    if (r6 >= r7) goto L_0x009c
                    int r6 = r7 - r1
                    org.telegram.ui.Components.PhonebookSelectShareAlert r8 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r8 = r8.backgroundPaddingTop
                    int r6 = r6 - r8
                    int r6 = java.lang.Math.min(r7, r6)
                    goto L_0x009d
                L_0x009a:
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x009c:
                    r6 = 0
                L_0x009d:
                    org.telegram.ui.Components.PhonebookSelectShareAlert r7 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    android.graphics.drawable.Drawable r7 = r7.shadowDrawable
                    int r8 = r13.getMeasuredWidth()
                    r7.setBounds(r4, r1, r8, r2)
                    org.telegram.ui.Components.PhonebookSelectShareAlert r2 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    android.graphics.drawable.Drawable r2 = r2.shadowDrawable
                    r2.draw(r14)
                    java.lang.String r2 = "dialogBackground"
                    int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                    if (r4 == 0) goto L_0x0108
                    android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r4.setColor(r5)
                    android.graphics.RectF r4 = r13.rect1
                    org.telegram.ui.Components.PhonebookSelectShareAlert r5 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r5 = r5.backgroundPaddingLeft
                    float r5 = (float) r5
                    org.telegram.ui.Components.PhonebookSelectShareAlert r7 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r7 = r7.backgroundPaddingTop
                    int r7 = r7 + r1
                    float r7 = (float) r7
                    int r8 = r13.getMeasuredWidth()
                    org.telegram.ui.Components.PhonebookSelectShareAlert r9 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r9 = r9.backgroundPaddingLeft
                    int r8 = r8 - r9
                    float r8 = (float) r8
                    org.telegram.ui.Components.PhonebookSelectShareAlert r9 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r9 = r9.backgroundPaddingTop
                    int r9 = r9 + r1
                    r1 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r9 = r9 + r1
                    float r1 = (float) r9
                    r4.set(r5, r7, r8, r1)
                    android.graphics.RectF r1 = r13.rect1
                    r4 = 1094713344(0x41400000, float:12.0)
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    float r5 = (float) r5
                    float r5 = r5 * r3
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    float r4 = (float) r4
                    float r4 = r4 * r3
                    android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r1, r5, r4, r3)
                L_0x0108:
                    r1 = 1108344832(0x42100000, float:36.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    android.graphics.RectF r3 = r13.rect1
                    int r4 = r13.getMeasuredWidth()
                    int r4 = r4 - r1
                    int r4 = r4 / 2
                    float r4 = (float) r4
                    float r5 = (float) r0
                    int r7 = r13.getMeasuredWidth()
                    int r7 = r7 + r1
                    int r7 = r7 / 2
                    float r1 = (float) r7
                    r7 = 1082130432(0x40800000, float:4.0)
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r0 = r0 + r7
                    float r0 = (float) r0
                    r3.set(r4, r5, r1, r0)
                    android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    java.lang.String r1 = "key_sheet_scrollUp"
                    int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                    r0.setColor(r1)
                    android.graphics.RectF r0 = r13.rect1
                    r1 = 1073741824(0x40000000, float:2.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r3 = (float) r3
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r1 = (float) r1
                    android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r14.drawRoundRect(r0, r3, r1, r4)
                    if (r6 <= 0) goto L_0x0196
                    int r0 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r1 = 255(0xff, float:3.57E-43)
                    int r2 = android.graphics.Color.red(r0)
                    float r2 = (float) r2
                    r3 = 1061997773(0x3f4ccccd, float:0.8)
                    float r2 = r2 * r3
                    int r2 = (int) r2
                    int r4 = android.graphics.Color.green(r0)
                    float r4 = (float) r4
                    float r4 = r4 * r3
                    int r4 = (int) r4
                    int r0 = android.graphics.Color.blue(r0)
                    float r0 = (float) r0
                    float r0 = r0 * r3
                    int r0 = (int) r0
                    int r0 = android.graphics.Color.argb(r1, r2, r4, r0)
                    android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r1.setColor(r0)
                    org.telegram.ui.Components.PhonebookSelectShareAlert r0 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r0 = r0.backgroundPaddingLeft
                    float r8 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r0 = r0 - r6
                    float r9 = (float) r0
                    int r0 = r13.getMeasuredWidth()
                    org.telegram.ui.Components.PhonebookSelectShareAlert r1 = org.telegram.ui.Components.PhonebookSelectShareAlert.this
                    int r1 = r1.backgroundPaddingLeft
                    int r0 = r0 - r1
                    float r10 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r11 = (float) r0
                    android.graphics.Paint r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r7 = r14
                    r7.drawRect(r8, r9, r10, r11, r12)
                L_0x0196:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookSelectShareAlert.AnonymousClass1.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView = r0;
        r0.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        FrameLayout frameLayout2 = new FrameLayout(parentActivity);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.frameLayout.addView(new SearchField(parentActivity), LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass2 r02 = new RecyclerListView(parentActivity) {
            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) ((PhonebookSelectShareAlert.this.scrollOffsetY + AndroidUtilities.dp(48.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }
        };
        this.listView = r02;
        r02.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ShareAdapter shareAdapter = new ShareAdapter(parentActivity);
        this.listAdapter = shareAdapter;
        recyclerListView2.setAdapter(shareAdapter);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PhonebookSelectShareAlert.this.lambda$new$1$PhonebookSelectShareAlert(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                PhonebookSelectShareAlert.this.updateLayout();
            }
        });
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(parentActivity);
        this.searchEmptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.searchEmptyView.showTextView();
        this.searchEmptyView.setText(LocaleController.getString("NoContacts", NUM));
        this.listView.setEmptyView(this.searchEmptyView);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(parentActivity);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        this.containerView.addView(this.shadow, layoutParams);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
    }

    public /* synthetic */ void lambda$new$1$PhonebookSelectShareAlert(View view, int i) {
        Object obj;
        String str;
        ContactsController.Contact contact;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (adapter == shareSearchAdapter) {
            obj = shareSearchAdapter.getItem(i);
        } else {
            int sectionForPosition = this.listAdapter.getSectionForPosition(i);
            int positionInSectionForPosition = this.listAdapter.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                obj = this.listAdapter.getItem(sectionForPosition, positionInSectionForPosition);
            } else {
                return;
            }
        }
        if (obj != null) {
            if (obj instanceof ContactsController.Contact) {
                ContactsController.Contact contact2 = (ContactsController.Contact) obj;
                TLRPC$User tLRPC$User = contact2.user;
                contact = contact2;
                str = tLRPC$User != null ? ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name) : "";
            } else {
                TLRPC$User tLRPC$User2 = (TLRPC$User) obj;
                ContactsController.Contact contact3 = new ContactsController.Contact();
                contact3.first_name = tLRPC$User2.first_name;
                contact3.last_name = tLRPC$User2.last_name;
                contact3.phones.add(tLRPC$User2.phone);
                contact3.user = tLRPC$User2;
                str = ContactsController.formatName(contact3.first_name, contact3.last_name);
                contact = contact3;
            }
            PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(this.chatActivity, contact, (TLRPC$User) null, (Uri) null, (File) null, str);
            phonebookShareAlert.setDelegate(new PhonebookShareAlertDelegate() {
                public final void didSelectContact(TLRPC$User tLRPC$User, boolean z, int i) {
                    PhonebookSelectShareAlert.this.lambda$null$0$PhonebookSelectShareAlert(tLRPC$User, z, i);
                }
            });
            phonebookShareAlert.show();
        }
    }

    public /* synthetic */ void lambda$null$0$PhonebookSelectShareAlert(TLRPC$User tLRPC$User, boolean z, int i) {
        dismiss();
        this.delegate.didSelectContact(tLRPC$User, z, i);
    }

    /* access modifiers changed from: private */
    public int getCurrentTop() {
        if (this.listView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.listView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
            i = childAt.getTop();
        }
        return paddingTop - i;
    }

    public void setDelegate(PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ShareAdapter shareAdapter;
        if (i == NotificationCenter.contactsDidLoad && (shareAdapter = this.listAdapter) != null) {
            shareAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
            int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(true);
                top = i;
            } else {
                runShadowAnimation(false);
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.listView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void runShadowAnimation(final boolean z) {
        if ((z && this.shadow.getTag() != null) || (!z && this.shadow.getTag() == null)) {
            this.shadow.setTag(z ? null : 1);
            if (z) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PhonebookSelectShareAlert.this.shadowAnimation != null && PhonebookSelectShareAlert.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            PhonebookSelectShareAlert.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = PhonebookSelectShareAlert.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PhonebookSelectShareAlert.this.shadowAnimation != null && PhonebookSelectShareAlert.this.shadowAnimation.equals(animator)) {
                        AnimatorSet unused = PhonebookSelectShareAlert.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
    }

    public class ShareAdapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public int getItemViewType(int i, int i2) {
            return i == 0 ? 1 : 0;
        }

        public String getLetter(int i) {
            return null;
        }

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public View getSectionHeaderView(int i, View view) {
            return null;
        }

        public ShareAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int i, int i2) {
            if (i == 0) {
                return null;
            }
            int i3 = i - 1;
            HashMap<String, ArrayList<Object>> hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (i3 < arrayList.size()) {
                ArrayList arrayList2 = hashMap.get(arrayList.get(i3));
                if (i2 < arrayList2.size()) {
                    return arrayList2.get(i2);
                }
            }
            return null;
        }

        public boolean isEnabled(int i, int i2) {
            if (i == 0) {
                return false;
            }
            if (i2 < ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(i - 1)).size()) {
                return true;
            }
            return false;
        }

        public int getSectionCount() {
            return ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.size() + 1;
        }

        public int getCountForSection(int i) {
            if (i == 0) {
                return 1;
            }
            int i2 = i - 1;
            HashMap<String, ArrayList<Object>> hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (i2 < arrayList.size()) {
                return hashMap.get(arrayList.get(i2)).size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new UserCell(PhonebookSelectShareAlert.this, this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            TLRPC$User tLRPC$User;
            if (viewHolder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                Object item = getItem(i, i2);
                boolean z = true;
                if (i == getSectionCount() - 1 && i2 == getCountForSection(i) - 1) {
                    z = false;
                }
                if (item instanceof ContactsController.Contact) {
                    ContactsController.Contact contact = (ContactsController.Contact) item;
                    tLRPC$User = contact.user;
                    if (tLRPC$User == null) {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC$User) null, ContactsController.formatName(contact.first_name, contact.last_name), contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0)), z);
                        tLRPC$User = null;
                    }
                } else {
                    tLRPC$User = (TLRPC$User) item;
                }
                if (tLRPC$User != null) {
                    userCell.setData(tLRPC$User, (CharSequence) null, PhoneFormat.getInstance().format("+" + tLRPC$User.phone), z);
                }
            }
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        private Context mContext;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ShareSearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$PhonebookSelectShareAlert$ShareSearchAdapter$_WGUIHYttDkAPmlZI1eNbtmVN5Q r2 = new Runnable(str, i) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PhonebookSelectShareAlert.ShareSearchAdapter.this.lambda$search$0$PhonebookSelectShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            };
            this.searchRunnable = r2;
            dispatchQueue.postRunnable(r2, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$search$0$PhonebookSelectShareAlert$ShareSearchAdapter(String str, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(str, i) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PhonebookSelectShareAlert.ShareSearchAdapter.this.lambda$processSearch$2$PhonebookSelectShareAlert$ShareSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$2$PhonebookSelectShareAlert$ShareSearchAdapter(String str, int i) {
            int i2 = UserConfig.selectedAccount;
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(ContactsController.getInstance(i2).contactsBook.values()), new ArrayList(ContactsController.getInstance(i2).contacts), i2, i) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ int f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    PhonebookSelectShareAlert.ShareSearchAdapter.this.lambda$null$1$PhonebookSelectShareAlert$ShareSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        }

        /* JADX WARNING: type inference failed for: r1v4 */
        /* JADX WARNING: type inference failed for: r1v12 */
        /* JADX WARNING: type inference failed for: r1v15 */
        /* JADX WARNING: type inference failed for: r1v19 */
        /* JADX WARNING: type inference failed for: r1v26 */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c4, code lost:
            if (r5.contains(" " + r0) == false) goto L_0x00c6;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00e1, code lost:
            if (r6.contains(" " + r0) != false) goto L_0x00e3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x012b, code lost:
            if (r12.contains(" " + r0) != false) goto L_0x0131;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:0x0222, code lost:
            if (r6.contains(" " + r12) != false) goto L_0x0231;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x0270 A[LOOP:3: B:82:0x01e8->B:104:0x0270, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x0134 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:116:0x0234 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x018c A[LOOP:1: B:27:0x00a5->B:70:0x018c, LOOP_END] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$1$PhonebookSelectShareAlert$ShareSearchAdapter(java.lang.String r19, java.util.ArrayList r20, java.util.ArrayList r21, int r22, int r23) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                java.lang.String r2 = r19.trim()
                java.lang.String r2 = r2.toLowerCase()
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0025
                r2 = -1
                r0.lastSearchId = r2
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                int r4 = r0.lastSearchId
                r0.updateSearchResults(r1, r2, r3, r4)
                return
            L_0x0025:
                org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r3 = r3.getTranslitString(r2)
                boolean r4 = r2.equals(r3)
                if (r4 != 0) goto L_0x0039
                int r4 = r3.length()
                if (r4 != 0) goto L_0x003a
            L_0x0039:
                r3 = 0
            L_0x003a:
                r4 = 0
                r6 = 1
                if (r3 == 0) goto L_0x0040
                r7 = 1
                goto L_0x0041
            L_0x0040:
                r7 = 0
            L_0x0041:
                int r7 = r7 + r6
                java.lang.String[] r8 = new java.lang.String[r7]
                r8[r4] = r2
                if (r3 == 0) goto L_0x004a
                r8[r6] = r3
            L_0x004a:
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                android.util.SparseBooleanArray r9 = new android.util.SparseBooleanArray
                r9.<init>()
                r10 = 0
            L_0x005a:
                int r11 = r20.size()
                java.lang.String r13 = "@"
                java.lang.String r14 = " "
                if (r10 >= r11) goto L_0x01a2
                r11 = r20
                java.lang.Object r15 = r11.get(r10)
                org.telegram.messenger.ContactsController$Contact r15 = (org.telegram.messenger.ContactsController.Contact) r15
                java.lang.String r4 = r15.first_name
                java.lang.String r12 = r15.last_name
                java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r4, r12)
                java.lang.String r4 = r4.toLowerCase()
                org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r12 = r12.getTranslitString(r4)
                org.telegram.tgnet.TLRPC$User r5 = r15.user
                if (r5 == 0) goto L_0x0099
                java.lang.String r6 = r5.first_name
                java.lang.String r5 = r5.last_name
                java.lang.String r5 = org.telegram.messenger.ContactsController.formatName(r6, r5)
                java.lang.String r5 = r5.toLowerCase()
                org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r6 = r6.getTranslitString(r4)
                goto L_0x009b
            L_0x0099:
                r5 = 0
                r6 = 0
            L_0x009b:
                boolean r16 = r4.equals(r12)
                if (r16 == 0) goto L_0x00a2
                r12 = 0
            L_0x00a2:
                r11 = 0
                r16 = 0
            L_0x00a5:
                if (r11 >= r7) goto L_0x0198
                r0 = r8[r11]
                if (r5 == 0) goto L_0x00c6
                boolean r17 = r5.startsWith(r0)
                if (r17 != 0) goto L_0x00e3
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r14)
                r1.append(r0)
                java.lang.String r1 = r1.toString()
                boolean r1 = r5.contains(r1)
                if (r1 != 0) goto L_0x00e3
            L_0x00c6:
                if (r6 == 0) goto L_0x00e5
                boolean r1 = r6.startsWith(r0)
                if (r1 != 0) goto L_0x00e3
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r14)
                r1.append(r0)
                java.lang.String r1 = r1.toString()
                boolean r1 = r6.contains(r1)
                if (r1 == 0) goto L_0x00e5
            L_0x00e3:
                r1 = 1
                goto L_0x0132
            L_0x00e5:
                org.telegram.tgnet.TLRPC$User r1 = r15.user
                if (r1 == 0) goto L_0x00f5
                java.lang.String r1 = r1.username
                if (r1 == 0) goto L_0x00f5
                boolean r1 = r1.startsWith(r0)
                if (r1 == 0) goto L_0x00f5
                r1 = 2
                goto L_0x0132
            L_0x00f5:
                boolean r1 = r4.startsWith(r0)
                if (r1 != 0) goto L_0x0131
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r14)
                r1.append(r0)
                java.lang.String r1 = r1.toString()
                boolean r1 = r4.contains(r1)
                if (r1 != 0) goto L_0x0131
                if (r12 == 0) goto L_0x012e
                boolean r1 = r12.startsWith(r0)
                if (r1 != 0) goto L_0x0131
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r14)
                r1.append(r0)
                java.lang.String r1 = r1.toString()
                boolean r1 = r12.contains(r1)
                if (r1 == 0) goto L_0x012e
                goto L_0x0131
            L_0x012e:
                r1 = r16
                goto L_0x0132
            L_0x0131:
                r1 = 3
            L_0x0132:
                if (r1 == 0) goto L_0x018c
                r4 = 3
                if (r1 != r4) goto L_0x0143
                java.lang.String r1 = r15.first_name
                java.lang.String r4 = r15.last_name
                java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r1, r4, r0)
                r3.add(r0)
                goto L_0x017e
            L_0x0143:
                r4 = 1
                if (r1 != r4) goto L_0x0154
                org.telegram.tgnet.TLRPC$User r1 = r15.user
                java.lang.String r4 = r1.first_name
                java.lang.String r1 = r1.last_name
                java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r1, r0)
                r3.add(r0)
                goto L_0x017e
            L_0x0154:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r13)
                org.telegram.tgnet.TLRPC$User r4 = r15.user
                java.lang.String r4 = r4.username
                r1.append(r4)
                java.lang.String r1 = r1.toString()
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r13)
                r4.append(r0)
                java.lang.String r0 = r4.toString()
                r4 = 0
                java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r1, r4, r0)
                r3.add(r0)
            L_0x017e:
                org.telegram.tgnet.TLRPC$User r0 = r15.user
                if (r0 == 0) goto L_0x0188
                int r0 = r0.id
                r1 = 1
                r9.put(r0, r1)
            L_0x0188:
                r2.add(r15)
                goto L_0x0198
            L_0x018c:
                r16 = r4
                int r11 = r11 + 1
                r0 = r18
                r16 = r1
                r1 = r19
                goto L_0x00a5
            L_0x0198:
                int r10 = r10 + 1
                r4 = 0
                r6 = 1
                r0 = r18
                r1 = r19
                goto L_0x005a
            L_0x01a2:
                r0 = 0
            L_0x01a3:
                int r1 = r21.size()
                if (r0 >= r1) goto L_0x027a
                r1 = r21
                java.lang.Object r4 = r1.get(r0)
                org.telegram.tgnet.TLRPC$TL_contact r4 = (org.telegram.tgnet.TLRPC$TL_contact) r4
                int r5 = r4.user_id
                int r5 = r9.indexOfKey(r5)
                if (r5 < 0) goto L_0x01bd
            L_0x01b9:
                r12 = 0
                r15 = 1
                goto L_0x0276
            L_0x01bd:
                org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r22)
                int r4 = r4.user_id
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                org.telegram.tgnet.TLRPC$User r4 = r5.getUser(r4)
                java.lang.String r5 = r4.first_name
                java.lang.String r6 = r4.last_name
                java.lang.String r5 = org.telegram.messenger.ContactsController.formatName(r5, r6)
                java.lang.String r5 = r5.toLowerCase()
                org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r6 = r6.getTranslitString(r5)
                boolean r10 = r5.equals(r6)
                if (r10 == 0) goto L_0x01e6
                r6 = 0
            L_0x01e6:
                r10 = 0
                r11 = 0
            L_0x01e8:
                if (r11 >= r7) goto L_0x01b9
                r12 = r8[r11]
                boolean r15 = r5.startsWith(r12)
                if (r15 != 0) goto L_0x0231
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r14)
                r15.append(r12)
                java.lang.String r15 = r15.toString()
                boolean r15 = r5.contains(r15)
                if (r15 != 0) goto L_0x0231
                if (r6 == 0) goto L_0x0225
                boolean r15 = r6.startsWith(r12)
                if (r15 != 0) goto L_0x0231
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r14)
                r15.append(r12)
                java.lang.String r15 = r15.toString()
                boolean r15 = r6.contains(r15)
                if (r15 == 0) goto L_0x0225
                goto L_0x0231
            L_0x0225:
                java.lang.String r15 = r4.username
                if (r15 == 0) goto L_0x0232
                boolean r15 = r15.startsWith(r12)
                if (r15 == 0) goto L_0x0232
                r10 = 2
                goto L_0x0232
            L_0x0231:
                r10 = 1
            L_0x0232:
                if (r10 == 0) goto L_0x0270
                r15 = 1
                if (r10 != r15) goto L_0x0244
                java.lang.String r5 = r4.first_name
                java.lang.String r6 = r4.last_name
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r6, r12)
                r3.add(r5)
                r12 = 0
                goto L_0x026c
            L_0x0244:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r13)
                java.lang.String r6 = r4.username
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r13)
                r6.append(r12)
                java.lang.String r6 = r6.toString()
                r12 = 0
                java.lang.CharSequence r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r12, r6)
                r3.add(r5)
            L_0x026c:
                r2.add(r4)
                goto L_0x0276
            L_0x0270:
                r12 = 0
                r15 = 1
                int r11 = r11 + 1
                goto L_0x01e8
            L_0x0276:
                int r0 = r0 + 1
                goto L_0x01a3
            L_0x027a:
                r0 = r18
                r4 = r19
                r5 = r23
                r0.updateSearchResults(r4, r2, r3, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookSelectShareAlert.ShareSearchAdapter.lambda$null$1$PhonebookSelectShareAlert$ShareSearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList, int, int):void");
        }

        private void updateSearchResults(String str, ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(i, arrayList, arrayList2) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    PhonebookSelectShareAlert.ShareSearchAdapter.this.lambda$updateSearchResults$3$PhonebookSelectShareAlert$ShareSearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3$PhonebookSelectShareAlert$ShareSearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2) {
            if (i == this.lastSearchId) {
                if (!(i == -1 || PhonebookSelectShareAlert.this.listView.getAdapter() == PhonebookSelectShareAlert.this.searchAdapter)) {
                    PhonebookSelectShareAlert phonebookSelectShareAlert = PhonebookSelectShareAlert.this;
                    int unused = phonebookSelectShareAlert.topBeforeSwitch = phonebookSelectShareAlert.getCurrentTop();
                    PhonebookSelectShareAlert.this.listView.setAdapter(PhonebookSelectShareAlert.this.searchAdapter);
                }
                boolean z = true;
                boolean z2 = !this.searchResult.isEmpty() && arrayList.isEmpty();
                if (!this.searchResult.isEmpty() || !arrayList.isEmpty()) {
                    z = false;
                }
                if (z2) {
                    PhonebookSelectShareAlert phonebookSelectShareAlert2 = PhonebookSelectShareAlert.this;
                    int unused2 = phonebookSelectShareAlert2.topBeforeSwitch = phonebookSelectShareAlert2.getCurrentTop();
                }
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                notifyDataSetChanged();
                if (!z && !z2 && PhonebookSelectShareAlert.this.topBeforeSwitch > 0) {
                    PhonebookSelectShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -PhonebookSelectShareAlert.this.topBeforeSwitch);
                    int unused3 = PhonebookSelectShareAlert.this.topBeforeSwitch = -1000;
                }
            }
        }

        public int getItemCount() {
            if (this.searchResult.isEmpty()) {
                return 0;
            }
            return this.searchResult.size() + 1;
        }

        public Object getItem(int i) {
            if (i == 0) {
                return null;
            }
            return this.searchResult.get(i - 1);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new UserCell(PhonebookSelectShareAlert.this, this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$User tLRPC$User;
            if (viewHolder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                boolean z = i != getItemCount() - 1;
                Object item = getItem(i);
                if (item instanceof ContactsController.Contact) {
                    ContactsController.Contact contact = (ContactsController.Contact) item;
                    tLRPC$User = contact.user;
                    if (tLRPC$User == null) {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC$User) null, this.searchResultNames.get(i - 1), contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0)), z);
                        tLRPC$User = null;
                    }
                } else {
                    tLRPC$User = (TLRPC$User) item;
                }
                if (tLRPC$User != null) {
                    userCell.setData(tLRPC$User, this.searchResultNames.get(i - 1), PhoneFormat.getInstance().format("+" + tLRPC$User.phone), z);
                }
            }
        }
    }
}
