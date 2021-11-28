package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
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
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertContactsLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private PhonebookShareAlertDelegate delegate;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    private FrameLayout frameLayout;
    private boolean ignoreLayout;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ShareAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ShareSearchAdapter searchAdapter;
    private SearchField searchField;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;

    public interface PhonebookShareAlertDelegate {
        void didSelectContact(TLRPC$User tLRPC$User, boolean z, int i);
    }

    public static class UserCell extends FrameLayout {
        private AvatarDrawable avatarDrawable;
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
        private final Theme.ResourcesProvider resourcesProvider;
        private SimpleTextView statusTextView;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public UserCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            int i = UserConfig.selectedAccount;
            this.resourcesProvider = resourcesProvider2;
            this.avatarDrawable = new AvatarDrawable(resourcesProvider2);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
            BackupImageView backupImageView2 = this.avatarImageView;
            boolean z = LocaleController.isRTL;
            int i2 = 5;
            addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 14.0f, 9.0f, z ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(getThemedColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView2 = this.nameTextView;
            boolean z2 = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 28.0f : 72.0f, 12.0f, z2 ? 72.0f : 28.0f, 0.0f));
            SimpleTextView simpleTextView3 = new SimpleTextView(context);
            this.statusTextView = simpleTextView3;
            simpleTextView3.setTextSize(13);
            this.statusTextView.setTextColor(getThemedColor("dialogTextGray2"));
            this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView4 = this.statusTextView;
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (!z3 ? 3 : i2) | 48, z3 ? 28.0f : 72.0f, 36.0f, z3 ? 72.0f : 28.0f, 0.0f));
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
            r2 = r0.photo;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x0068, code lost:
            if (r12.equals(r11.lastName) == false) goto L_0x006d;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void update(int r12) {
            /*
                r11 = this;
                org.telegram.tgnet.TLRPC$User r0 = r11.currentUser
                r1 = 0
                if (r0 == 0) goto L_0x000c
                org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r0.photo
                if (r2 == 0) goto L_0x000c
                org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
                goto L_0x000d
            L_0x000c:
                r2 = r1
            L_0x000d:
                r3 = 0
                if (r12 == 0) goto L_0x0070
                int r4 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
                r4 = r4 & r12
                r5 = 1
                if (r4 == 0) goto L_0x0034
                org.telegram.tgnet.TLRPC$FileLocation r4 = r11.lastAvatar
                if (r4 == 0) goto L_0x001c
                if (r2 == 0) goto L_0x0032
            L_0x001c:
                if (r4 != 0) goto L_0x0020
                if (r2 != 0) goto L_0x0032
            L_0x0020:
                if (r4 == 0) goto L_0x0034
                if (r2 == 0) goto L_0x0034
                long r6 = r4.volume_id
                long r8 = r2.volume_id
                int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r10 != 0) goto L_0x0032
                int r4 = r4.local_id
                int r6 = r2.local_id
                if (r4 == r6) goto L_0x0034
            L_0x0032:
                r4 = 1
                goto L_0x0035
            L_0x0034:
                r4 = 0
            L_0x0035:
                if (r0 == 0) goto L_0x004b
                if (r4 != 0) goto L_0x004b
                int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
                r6 = r6 & r12
                if (r6 == 0) goto L_0x004b
                org.telegram.tgnet.TLRPC$UserStatus r6 = r0.status
                if (r6 == 0) goto L_0x0045
                int r6 = r6.expires
                goto L_0x0046
            L_0x0045:
                r6 = 0
            L_0x0046:
                int r7 = r11.lastStatus
                if (r6 == r7) goto L_0x004b
                r4 = 1
            L_0x004b:
                if (r4 != 0) goto L_0x006b
                java.lang.CharSequence r6 = r11.currentName
                if (r6 != 0) goto L_0x006b
                java.lang.String r6 = r11.lastName
                if (r6 == 0) goto L_0x006b
                int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
                r12 = r12 & r6
                if (r12 == 0) goto L_0x006b
                if (r0 == 0) goto L_0x0061
                java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r0)
                goto L_0x0062
            L_0x0061:
                r12 = r1
            L_0x0062:
                java.lang.String r0 = r11.lastName
                boolean r0 = r12.equals(r0)
                if (r0 != 0) goto L_0x006c
                goto L_0x006d
            L_0x006b:
                r12 = r1
            L_0x006c:
                r5 = r4
            L_0x006d:
                if (r5 != 0) goto L_0x0071
                return
            L_0x0070:
                r12 = r1
            L_0x0071:
                org.telegram.tgnet.TLRPC$User r0 = r11.currentUser
                if (r0 == 0) goto L_0x0088
                org.telegram.ui.Components.AvatarDrawable r4 = r11.avatarDrawable
                r4.setInfo((org.telegram.tgnet.TLRPC$User) r0)
                org.telegram.tgnet.TLRPC$User r0 = r11.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r0 = r0.status
                if (r0 == 0) goto L_0x0085
                int r0 = r0.expires
                r11.lastStatus = r0
                goto L_0x00a3
            L_0x0085:
                r11.lastStatus = r3
                goto L_0x00a3
            L_0x0088:
                java.lang.CharSequence r0 = r11.currentName
                if (r0 == 0) goto L_0x0099
                org.telegram.ui.Components.AvatarDrawable r3 = r11.avatarDrawable
                int r4 = r11.currentId
                long r4 = (long) r4
                java.lang.String r0 = r0.toString()
                r3.setInfo(r4, r0, r1)
                goto L_0x00a3
            L_0x0099:
                org.telegram.ui.Components.AvatarDrawable r0 = r11.avatarDrawable
                int r3 = r11.currentId
                long r3 = (long) r3
                java.lang.String r5 = "#"
                r0.setInfo(r3, r5, r1)
            L_0x00a3:
                java.lang.CharSequence r0 = r11.currentName
                if (r0 == 0) goto L_0x00af
                r11.lastName = r1
                org.telegram.ui.ActionBar.SimpleTextView r12 = r11.nameTextView
                r12.setText(r0)
                goto L_0x00c7
            L_0x00af:
                org.telegram.tgnet.TLRPC$User r0 = r11.currentUser
                if (r0 == 0) goto L_0x00bc
                if (r12 != 0) goto L_0x00b9
                java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r0)
            L_0x00b9:
                r11.lastName = r12
                goto L_0x00c0
            L_0x00bc:
                java.lang.String r12 = ""
                r11.lastName = r12
            L_0x00c0:
                org.telegram.ui.ActionBar.SimpleTextView r12 = r11.nameTextView
                java.lang.String r0 = r11.lastName
                r12.setText(r0)
            L_0x00c7:
                java.lang.CharSequence r12 = r11.currentStatus
                if (r12 == 0) goto L_0x00d1
                org.telegram.ui.ActionBar.SimpleTextView r0 = r11.statusTextView
                r0.setText(r12)
                goto L_0x010e
            L_0x00d1:
                org.telegram.tgnet.TLRPC$User r12 = r11.currentUser
                if (r12 == 0) goto L_0x010e
                java.lang.String r12 = r12.phone
                boolean r12 = android.text.TextUtils.isEmpty(r12)
                if (r12 == 0) goto L_0x00ec
                org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
                r0 = 2131626743(0x7f0e0af7, float:1.888073E38)
                java.lang.String r1 = "NumberUnknown"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r12.setText(r0)
                goto L_0x010e
            L_0x00ec:
                org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
                org.telegram.PhoneFormat.PhoneFormat r0 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r3 = "+"
                r1.append(r3)
                org.telegram.tgnet.TLRPC$User r3 = r11.currentUser
                java.lang.String r3 = r3.phone
                r1.append(r3)
                java.lang.String r1 = r1.toString()
                java.lang.String r0 = r0.format(r1)
                r12.setText(r0)
            L_0x010e:
                r11.lastAvatar = r2
                org.telegram.tgnet.TLRPC$User r12 = r11.currentUser
                if (r12 == 0) goto L_0x011c
                org.telegram.ui.Components.BackupImageView r0 = r11.avatarImageView
                org.telegram.ui.Components.AvatarDrawable r1 = r11.avatarDrawable
                r0.setForUserOrChat(r12, r1)
                goto L_0x0123
            L_0x011c:
                org.telegram.ui.Components.BackupImageView r12 = r11.avatarImageView
                org.telegram.ui.Components.AvatarDrawable r0 = r11.avatarDrawable
                r12.setImageDrawable(r0)
            L_0x0123:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.update(int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    public ChatAttachAlertContactsLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.searchAdapter = new ShareSearchAdapter(context);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackgroundColor(getThemedColor("dialogBackground"));
        AnonymousClass1 r12 = new SearchField(context, false, resourcesProvider) {
            public void onTextChange(String str) {
                if (str.length() != 0) {
                    if (ChatAttachAlertContactsLayout.this.emptyView != null) {
                        ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                    }
                } else if (ChatAttachAlertContactsLayout.this.listView.getAdapter() != ChatAttachAlertContactsLayout.this.listAdapter) {
                    int access$300 = ChatAttachAlertContactsLayout.this.getCurrentTop();
                    ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
                    ChatAttachAlertContactsLayout.this.emptyView.showTextView();
                    ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.listAdapter);
                    ChatAttachAlertContactsLayout.this.listAdapter.notifyDataSetChanged();
                    if (access$300 > 0) {
                        ChatAttachAlertContactsLayout.this.layoutManager.scrollToPositionWithOffset(0, -access$300);
                    }
                }
                if (ChatAttachAlertContactsLayout.this.searchAdapter != null) {
                    ChatAttachAlertContactsLayout.this.searchAdapter.search(str);
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(motionEvent);
            }

            public void processTouchEvent(MotionEvent motionEvent) {
                MotionEvent obtain = MotionEvent.obtain(motionEvent);
                obtain.setLocation(obtain.getRawX(), (obtain.getRawY() - ChatAttachAlertContactsLayout.this.parentAlert.getSheetContainer().getTranslationY()) - ((float) AndroidUtilities.dp(58.0f)));
                ChatAttachAlertContactsLayout.this.listView.dispatchTouchEvent(obtain);
                obtain.recycle();
            }

            /* access modifiers changed from: protected */
            public void onFieldTouchUp(EditTextBoldCursor editTextBoldCursor) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(editTextBoldCursor, true);
            }
        };
        this.searchField = r12;
        r12.setHint(LocaleController.getString("SearchFriends", NUM));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, (View) null, resourcesProvider);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        AnonymousClass2 r122 = new RecyclerListView(context, resourcesProvider) {
            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) ((ChatAttachAlertContactsLayout.this.parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ChatAttachAlertContactsLayout.this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }
        };
        this.listView = r122;
        r122.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass3 r4 = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - (ChatAttachAlertContactsLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 2;
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }
        };
        this.layoutManager = r4;
        recyclerListView.setLayoutManager(r4);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ShareAdapter shareAdapter = new ShareAdapter(context);
        this.listAdapter = shareAdapter;
        recyclerListView2.setAdapter(shareAdapter);
        this.listView.setGlowColor(getThemedColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertContactsLayout$$ExternalSyntheticLambda2(this, resourcesProvider));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = ChatAttachAlertContactsLayout.this;
                chatAttachAlertContactsLayout.parentAlert.updateLayout(chatAttachAlertContactsLayout, true, i2);
                ChatAttachAlertContactsLayout.this.updateEmptyViewPosition();
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, layoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        updateEmptyView();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Theme.ResourcesProvider resourcesProvider, View view, int i) {
        Object obj;
        String str;
        String str2;
        ContactsController.Contact contact;
        String str3;
        String str4;
        int i2 = i;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (adapter == shareSearchAdapter) {
            obj = shareSearchAdapter.getItem(i2);
        } else {
            int sectionForPosition = this.listAdapter.getSectionForPosition(i2);
            int positionInSectionForPosition = this.listAdapter.getPositionInSectionForPosition(i2);
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
                if (tLRPC$User != null) {
                    str3 = tLRPC$User.first_name;
                    str4 = tLRPC$User.last_name;
                } else {
                    str3 = contact2.first_name;
                    str4 = contact2.last_name;
                }
                contact = contact2;
                str = str4;
                str2 = str3;
            } else {
                TLRPC$User tLRPC$User2 = (TLRPC$User) obj;
                ContactsController.Contact contact3 = new ContactsController.Contact();
                String str5 = tLRPC$User2.first_name;
                contact3.first_name = str5;
                String str6 = tLRPC$User2.last_name;
                contact3.last_name = str6;
                contact3.phones.add(tLRPC$User2.phone);
                contact3.user = tLRPC$User2;
                contact = contact3;
                str2 = str5;
                str = str6;
            }
            PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(this.parentAlert.baseFragment, contact, (TLRPC$User) null, (Uri) null, (File) null, str2, str, resourcesProvider);
            phonebookShareAlert.setDelegate(new ChatAttachAlertContactsLayout$$ExternalSyntheticLambda1(this));
            phonebookShareAlert.show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TLRPC$User tLRPC$User, boolean z, int i) {
        this.parentAlert.dismiss();
        this.delegate.didSelectContact(tLRPC$User, z, i);
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
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
        this.frameLayout.setTranslationY((float) top);
        return top + AndroidUtilities.dp(12.0f);
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    /* access modifiers changed from: package-private */
    public void onPreMeasure(int i, int i2) {
        int i3;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            i3 = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet()) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    i3 = (int) (((float) i2) / 3.5f);
                    this.parentAlert.setAllowNestedScroll(true);
                }
            }
            i3 = (i2 / 5) * 2;
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != i3) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, i3, 0, 0);
            this.ignoreLayout = false;
        }
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
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
                    if (ChatAttachAlertContactsLayout.this.shadowAnimation != null && ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            ChatAttachAlertContactsLayout.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlertContactsLayout.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlertContactsLayout.this.shadowAnimation != null && ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animator)) {
                        AnimatorSet unused = ChatAttachAlertContactsLayout.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
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

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
    }

    /* access modifiers changed from: package-private */
    public void onShow() {
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: private */
    public void updateEmptyViewPosition() {
        View childAt;
        if (this.emptyView.getVisibility() == 0 && (childAt = this.listView.getChildAt(0)) != null) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            emptyTextProgressView.setTranslationY((float) (((emptyTextProgressView.getMeasuredHeight() - getMeasuredHeight()) + childAt.getTop()) / 2));
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        int i = 0;
        boolean z = this.listView.getAdapter().getItemCount() == 2;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (!z) {
            i = 8;
        }
        emptyTextProgressView.setVisibility(i);
        updateEmptyViewPosition();
    }

    public class ShareAdapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public String getLetter(int i) {
            return null;
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            if (i == 0 || i == getSectionCount() - 1) {
                return false;
            }
            if (i2 < ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(i - 1)).size()) {
                return true;
            }
            return false;
        }

        public int getSectionCount() {
            return ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.size() + 2;
        }

        public int getCountForSection(int i) {
            if (i == 0 || i == getSectionCount() - 1) {
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
            if (i == 0) {
                view = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
            } else if (i != 1) {
                view = new View(this.mContext);
            } else {
                view = new View(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            TLRPC$User tLRPC$User;
            if (viewHolder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                Object item = getItem(i, i2);
                boolean z = true;
                if (i == getSectionCount() - 2 && i2 == getCountForSection(i) - 1) {
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

        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            return i == getSectionCount() - 1 ? 2 : 0;
        }

        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        private Context mContext;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

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
            ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1 chatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1 = new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1(this, str, i);
            this.searchRunnable = chatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(chatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$search$0(String str, int i) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2(this, str, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$2(String str, int i) {
            int i2 = UserConfig.selectedAccount;
            Utilities.searchQueue.postRunnable(new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda3(this, str, new ArrayList(ContactsController.getInstance(i2).contactsBook.values()), new ArrayList(ContactsController.getInstance(i2).contacts), i2, i));
        }

        /* JADX WARNING: type inference failed for: r1v4 */
        /* JADX WARNING: type inference failed for: r1v11 */
        /* JADX WARNING: type inference failed for: r1v14 */
        /* JADX WARNING: type inference failed for: r1v18 */
        /* JADX WARNING: type inference failed for: r1v25 */
        /* access modifiers changed from: private */
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
        public /* synthetic */ void lambda$processSearch$1(java.lang.String r19, java.util.ArrayList r20, java.util.ArrayList r21, int r22, int r23) {
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
                org.telegram.messenger.support.LongSparseIntArray r9 = new org.telegram.messenger.support.LongSparseIntArray
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
                long r0 = r0.id
                r4 = 1
                r9.put(r0, r4)
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
                long r5 = r4.user_id
                int r5 = r9.indexOfKey(r5)
                if (r5 < 0) goto L_0x01bd
            L_0x01b9:
                r12 = 0
                r15 = 1
                goto L_0x0276
            L_0x01bd:
                org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r22)
                long r10 = r4.user_id
                java.lang.Long r4 = java.lang.Long.valueOf(r10)
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
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertContactsLayout.ShareSearchAdapter.lambda$processSearch$1(java.lang.String, java.util.ArrayList, java.util.ArrayList, int, int):void");
        }

        private void updateSearchResults(String str, ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2, int i) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda0(this, i, arrayList, arrayList2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$3(int i, ArrayList arrayList, ArrayList arrayList2) {
            if (i == this.lastSearchId) {
                if (!(i == -1 || ChatAttachAlertContactsLayout.this.listView.getAdapter() == ChatAttachAlertContactsLayout.this.searchAdapter)) {
                    ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.searchAdapter);
                }
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            return this.searchResult.size() + 2;
        }

        public Object getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i2);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
            } else if (i != 1) {
                view = new View(this.mContext);
            } else {
                view = new View(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$User tLRPC$User;
            if (viewHolder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                boolean z = i != getItemCount() + -2;
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            return i == getItemCount() - 1 ? 2 : 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ChatAttachAlertContactsLayout$$ExternalSyntheticLambda0 chatAttachAlertContactsLayout$$ExternalSyntheticLambda0 = new ChatAttachAlertContactsLayout$$ExternalSyntheticLambda0(this);
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.shadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchBackground"));
        arrayList.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        arrayList.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchText"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchHint"));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) chatAttachAlertContactsLayout$$ExternalSyntheticLambda0, "dialogTextGray2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ChatAttachAlertContactsLayout$$ExternalSyntheticLambda0 chatAttachAlertContactsLayout$$ExternalSyntheticLambda02 = chatAttachAlertContactsLayout$$ExternalSyntheticLambda0;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertContactsLayout$$ExternalSyntheticLambda02, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertContactsLayout$$ExternalSyntheticLambda02, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertContactsLayout$$ExternalSyntheticLambda02, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertContactsLayout$$ExternalSyntheticLambda02, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertContactsLayout$$ExternalSyntheticLambda02, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertContactsLayout$$ExternalSyntheticLambda02, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertContactsLayout$$ExternalSyntheticLambda02, "avatar_backgroundPink"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$2() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }
}
