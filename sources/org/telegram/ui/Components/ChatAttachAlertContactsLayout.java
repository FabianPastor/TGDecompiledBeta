package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
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
    public FillLastLinearLayoutManager layoutManager;
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
        void didSelectContact(TLRPC.User user, boolean z, int i);
    }

    public static class UserCell extends FrameLayout {
        private AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        private int currentAccount = UserConfig.selectedAccount;
        private int currentId;
        private CharSequence currentName;
        private CharSequence currentStatus;
        private TLRPC.User currentUser;
        private CharSequence formattedPhoneNumber;
        private TLRPC.User formattedPhoneNumberUser;
        private TLRPC.FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private SimpleTextView statusTextView;

        public interface CharSequenceCallback {
            CharSequence run();
        }

        public UserCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            this.avatarDrawable = new AvatarDrawable(resourcesProvider2);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
            int i = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 9.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.nameTextView = simpleTextView;
            simpleTextView.setTextColor(getThemedColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 12.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.statusTextView = simpleTextView2;
            simpleTextView2.setTextSize(13);
            this.statusTextView.setTextColor(getThemedColor("dialogTextGray2"));
            this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 36.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        }

        public void setCurrentId(int id) {
            this.currentId = id;
        }

        public void setData(TLRPC.User user, CharSequence name, CharSequence status, boolean divider) {
            if (user == null && name == null && status == null) {
                this.currentStatus = null;
                this.currentName = null;
                this.nameTextView.setText("");
                this.statusTextView.setText("");
                this.avatarImageView.setImageDrawable((Drawable) null);
                return;
            }
            this.currentStatus = status;
            this.currentName = name;
            this.currentUser = user;
            this.needDivider = divider;
            setWillNotDraw(!divider);
            update(0);
        }

        public void setData(TLRPC.User user, CharSequence name, CharSequenceCallback status, boolean divider) {
            setData(user, name, (CharSequence) null, divider);
            Utilities.globalQueue.postRunnable(new ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda3(this, status));
        }

        /* renamed from: lambda$setData$1$org-telegram-ui-Components-ChatAttachAlertContactsLayout$UserCell  reason: not valid java name */
        public /* synthetic */ void m783x2582dee(CharSequenceCallback status) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda2(this, status.run()));
        }

        /* renamed from: setStatus */
        public void m782x2696b22d(CharSequence status) {
            CharSequence charSequence;
            this.currentStatus = status;
            if (status != null) {
                this.statusTextView.setText(status);
                return;
            }
            TLRPC.User user = this.currentUser;
            if (user == null) {
                return;
            }
            if (TextUtils.isEmpty(user.phone)) {
                this.statusTextView.setText(LocaleController.getString("NumberUnknown", NUM));
            } else if (this.formattedPhoneNumberUser == this.currentUser || (charSequence = this.formattedPhoneNumber) == null) {
                this.statusTextView.setText("");
                Utilities.globalQueue.postRunnable(new ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda1(this));
            } else {
                this.statusTextView.setText(charSequence);
            }
        }

        /* renamed from: lambda$setStatus$3$org-telegram-ui-Components-ChatAttachAlertContactsLayout$UserCell  reason: not valid java name */
        public /* synthetic */ void m785x8718aCLASSNAME() {
            if (this.currentUser != null) {
                PhoneFormat instance = PhoneFormat.getInstance();
                this.formattedPhoneNumber = instance.format("+" + this.currentUser.phone);
                this.formattedPhoneNumberUser = this.currentUser;
                AndroidUtilities.runOnUIThread(new ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda0(this));
            }
        }

        /* renamed from: lambda$setStatus$2$org-telegram-ui-Components-ChatAttachAlertContactsLayout$UserCell  reason: not valid java name */
        public /* synthetic */ void m784xab5730b7() {
            this.statusTextView.setText(this.formattedPhoneNumber);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        public void update(int mask) {
            TLRPC.FileLocation fileLocation;
            TLRPC.FileLocation photo = null;
            String newName = null;
            TLRPC.User user = this.currentUser;
            if (!(user == null || user.photo == null)) {
                photo = this.currentUser.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || !(fileLocation == null || photo == null || (fileLocation.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                    continueUpdate = true;
                }
                if (!(this.currentUser == null || continueUpdate || (MessagesController.UPDATE_MASK_STATUS & mask) == 0)) {
                    int newStatus = 0;
                    if (this.currentUser.status != null) {
                        newStatus = this.currentUser.status.expires;
                    }
                    if (newStatus != this.lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate && this.currentName == null && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                    TLRPC.User user2 = this.currentUser;
                    if (user2 != null) {
                        newName = UserObject.getUserName(user2);
                    }
                    if (!newName.equals(this.lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            TLRPC.User user3 = this.currentUser;
            if (user3 != null) {
                this.avatarDrawable.setInfo(user3);
                if (this.currentUser.status != null) {
                    this.lastStatus = this.currentUser.status.expires;
                } else {
                    this.lastStatus = 0;
                }
            } else {
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.avatarDrawable.setInfo((long) this.currentId, charSequence.toString(), (String) null);
                } else {
                    this.avatarDrawable.setInfo((long) this.currentId, "#", (String) null);
                }
            }
            CharSequence charSequence2 = this.currentName;
            if (charSequence2 != null) {
                this.lastName = null;
                this.nameTextView.setText(charSequence2);
            } else {
                TLRPC.User user4 = this.currentUser;
                if (user4 != null) {
                    this.lastName = newName == null ? UserObject.getUserName(user4) : newName;
                } else {
                    this.lastName = "";
                }
                this.nameTextView.setText(this.lastName);
            }
            m782x2696b22d(this.currentStatus);
            this.lastAvatar = photo;
            TLRPC.User user5 = this.currentUser;
            if (user5 != null) {
                this.avatarImageView.setForUserOrChat(user5, this.avatarDrawable);
            } else {
                this.avatarImageView.setImageDrawable(this.avatarDrawable);
            }
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public ChatAttachAlertContactsLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        this.searchAdapter = new ShareSearchAdapter(context);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackgroundColor(getThemedColor("dialogBackground"));
        AnonymousClass1 r0 = new SearchField(context, false, resourcesProvider) {
            public void onTextChange(String text) {
                if (text.length() != 0) {
                    if (ChatAttachAlertContactsLayout.this.emptyView != null) {
                        ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                    }
                } else if (ChatAttachAlertContactsLayout.this.listView.getAdapter() != ChatAttachAlertContactsLayout.this.listAdapter) {
                    int top = ChatAttachAlertContactsLayout.this.getCurrentTop();
                    ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
                    ChatAttachAlertContactsLayout.this.emptyView.showTextView();
                    ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.listAdapter);
                    ChatAttachAlertContactsLayout.this.listAdapter.notifyDataSetChanged();
                    if (top > 0) {
                        ChatAttachAlertContactsLayout.this.layoutManager.scrollToPositionWithOffset(0, -top);
                    }
                }
                if (ChatAttachAlertContactsLayout.this.searchAdapter != null) {
                    ChatAttachAlertContactsLayout.this.searchAdapter.search(text);
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(ev);
            }

            public void processTouchEvent(MotionEvent event) {
                MotionEvent e = MotionEvent.obtain(event);
                e.setLocation(e.getRawX(), (e.getRawY() - ChatAttachAlertContactsLayout.this.parentAlert.getSheetContainer().getTranslationY()) - ((float) AndroidUtilities.dp(58.0f)));
                ChatAttachAlertContactsLayout.this.listView.dispatchTouchEvent(e);
                e.recycle();
            }

            /* access modifiers changed from: protected */
            public void onFieldTouchUp(EditTextBoldCursor editText) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(editText, true);
            }
        };
        this.searchField = r0;
        r0.setHint(LocaleController.getString("SearchFriends", NUM));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, (View) null, resourcesProvider);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        AnonymousClass2 r02 = new RecyclerListView(context, resourcesProvider) {
            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) ((ChatAttachAlertContactsLayout.this.parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ChatAttachAlertContactsLayout.this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }
        };
        this.listView = r02;
        r02.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass3 r5 = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        return super.calculateDyToMakeVisible(view, snapPreference) - (ChatAttachAlertContactsLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = r5;
        recyclerListView.setLayoutManager(r5);
        this.layoutManager.setBind(false);
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertContactsLayout.this.parentAlert.updateLayout(ChatAttachAlertContactsLayout.this, true, dy);
                ChatAttachAlertContactsLayout.this.updateEmptyViewPosition();
            }
        });
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(getThemedColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, frameLayoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        updateEmptyView();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertContactsLayout  reason: not valid java name */
    public /* synthetic */ void m777x9bcdCLASSNAMEf(Theme.ResourcesProvider resourcesProvider, View view, int position) {
        Object object;
        String firstName;
        String firstName2;
        ContactsController.Contact contact;
        int i = position;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (adapter == shareSearchAdapter) {
            object = shareSearchAdapter.getItem(i);
        } else {
            int section = this.listAdapter.getSectionForPosition(i);
            int row = this.listAdapter.getPositionInSectionForPosition(i);
            if (row >= 0 && section >= 0) {
                object = this.listAdapter.getItem(section, row);
            } else {
                return;
            }
        }
        if (object != null) {
            if (object instanceof ContactsController.Contact) {
                contact = object;
                if (contact.user != null) {
                    firstName2 = contact.user.first_name;
                    firstName = contact.user.last_name;
                } else {
                    firstName2 = contact.first_name;
                    firstName = contact.last_name;
                }
            } else {
                TLRPC.User user = object;
                ContactsController.Contact contact2 = new ContactsController.Contact();
                String firstName3 = user.first_name;
                contact2.first_name = firstName3;
                String lastName = user.last_name;
                contact2.last_name = lastName;
                contact2.phones.add(user.phone);
                contact2.user = user;
                contact = contact2;
                firstName2 = firstName3;
                firstName = lastName;
            }
            PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(this.parentAlert.baseFragment, contact, (TLRPC.User) null, (Uri) null, (File) null, firstName2, firstName, resourcesProvider);
            phonebookShareAlert.setDelegate(new ChatAttachAlertContactsLayout$$ExternalSyntheticLambda1(this));
            phonebookShareAlert.show();
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertContactsLayout  reason: not valid java name */
    public /* synthetic */ void m776xee0adf0(TLRPC.User user, boolean notify, int scheduleDate) {
        this.parentAlert.dismiss(true);
        this.delegate.didSelectContact(user, notify, scheduleDate);
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
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
        } else {
            newOffset = top;
            runShadowAnimation(false);
        }
        this.frameLayout.setTranslationY((float) newOffset);
        return AndroidUtilities.dp(12.0f) + newOffset;
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    /* access modifiers changed from: package-private */
    public void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (AndroidUtilities.isTablet() != 0 || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                padding = (availableHeight / 5) * 2;
            } else {
                padding = (int) (((float) availableHeight) / 3.5f);
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, 0);
            this.ignoreLayout = false;
        }
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
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
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatAttachAlertContactsLayout.this.shadowAnimation != null && ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            ChatAttachAlertContactsLayout.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlertContactsLayout.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatAttachAlertContactsLayout.this.shadowAnimation != null && ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animation)) {
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
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.listView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
            i = child.getTop();
        }
        return paddingTop - i;
    }

    public void setDelegate(PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ShareAdapter shareAdapter;
        if (id == NotificationCenter.contactsDidLoad && (shareAdapter = this.listAdapter) != null) {
            shareAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: private */
    public void updateEmptyViewPosition() {
        View child;
        if (this.emptyView.getVisibility() == 0 && (child = this.listView.getChildAt(0)) != null) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            emptyTextProgressView.setTranslationY((float) (((emptyTextProgressView.getMeasuredHeight() - getMeasuredHeight()) + child.getTop()) / 2));
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        int i = 0;
        boolean visible = this.listView.getAdapter().getItemCount() == 2;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (!visible) {
            i = 8;
        }
        emptyTextProgressView.setVisibility(i);
        updateEmptyViewPosition();
    }

    public class ShareAdapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public ShareAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            if (section == 0) {
                return null;
            }
            int section2 = section - 1;
            HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (section2 < sortedUsersSectionsArray.size()) {
                ArrayList<Object> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section2));
                if (position < arr.size()) {
                    return arr.get(position);
                }
            }
            return null;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            if (section == 0 || section == getSectionCount() - 1) {
                return false;
            }
            if (row < ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(section - 1)).size()) {
                return true;
            }
            return false;
        }

        public int getSectionCount() {
            return ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.size() + 2;
        }

        public int getCountForSection(int section) {
            if (section == 0 || section == getSectionCount() - 1) {
                return 1;
            }
            int section2 = section - 1;
            HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (section2 < sortedUsersSectionsArray.size()) {
                return usersSectionsDict.get(sortedUsersSectionsArray.get(section2)).size();
            }
            return 0;
        }

        public View getSectionHeaderView(int section, View view) {
            return null;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new View(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) holder.itemView;
                Object object = getItem(section, position);
                TLRPC.User user = null;
                boolean z = true;
                if (section == getSectionCount() - 2 && position == getCountForSection(section) - 1) {
                    z = false;
                }
                boolean divider = z;
                if (object instanceof ContactsController.Contact) {
                    ContactsController.Contact contact = (ContactsController.Contact) object;
                    if (contact.user != null) {
                        user = contact.user;
                    } else {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC.User) null, (CharSequence) ContactsController.formatName(contact.first_name, contact.last_name), (UserCell.CharSequenceCallback) new ChatAttachAlertContactsLayout$ShareAdapter$$ExternalSyntheticLambda0(contact), divider);
                    }
                } else {
                    user = (TLRPC.User) object;
                }
                if (user != null) {
                    userCell.setData(user, (CharSequence) null, (UserCell.CharSequenceCallback) new ChatAttachAlertContactsLayout$ShareAdapter$$ExternalSyntheticLambda1(user), divider);
                }
            }
        }

        static /* synthetic */ CharSequence lambda$onBindViewHolder$0(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        public int getItemViewType(int section, int position) {
            if (section == 0) {
                return 1;
            }
            if (section == getSectionCount() - 1) {
                return 2;
            }
            return 0;
        }

        public String getLetter(int position) {
            return null;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
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

        public void search(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2 chatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2 = new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2(this, query, searchId);
            this.searchRunnable = chatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2;
            dispatchQueue.postRunnable(chatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void m780x8377var_e(String query, int searchId) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1(this, query, searchId));
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-ChatAttachAlertContactsLayout$ShareSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m779xcCLASSNAMEea95(String query, int searchId) {
            int currentAccount = UserConfig.selectedAccount;
            Utilities.searchQueue.postRunnable(new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda3(this, query, new ArrayList<>(ContactsController.getInstance(currentAccount).contactsBook.values()), new ArrayList<>(ContactsController.getInstance(currentAccount).contacts), currentAccount, searchId));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00d1, code lost:
            if (r2.contains(" " + r3) == false) goto L_0x00d3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ee, code lost:
            if (r5.contains(" " + r3) != false) goto L_0x00f0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x013e, code lost:
            if (r12.contains(" " + r3) != false) goto L_0x0144;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:0x023f, code lost:
            if (r10.contains(" " + r2) != false) goto L_0x0253;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:105:0x0293 A[LOOP:3: B:82:0x0203->B:105:0x0293, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x0147 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:119:0x0257 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x01a3 A[LOOP:1: B:27:0x00b0->B:70:0x01a3, LOOP_END] */
        /* renamed from: lambda$processSearch$1$org-telegram-ui-Components-ChatAttachAlertContactsLayout$ShareSearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m778xcb356CLASSNAME(java.lang.String r22, java.util.ArrayList r23, java.util.ArrayList r24, int r25, int r26) {
            /*
                r21 = this;
                r0 = r21
                r1 = r22
                java.lang.String r2 = r22.trim()
                java.lang.String r2 = r2.toLowerCase()
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0025
                r3 = -1
                r0.lastSearchId = r3
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                int r5 = r0.lastSearchId
                r0.updateSearchResults(r1, r3, r4, r5)
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
                r5 = 1
                if (r3 == 0) goto L_0x0040
                r6 = 1
                goto L_0x0041
            L_0x0040:
                r6 = 0
            L_0x0041:
                int r6 = r6 + r5
                java.lang.String[] r6 = new java.lang.String[r6]
                r6[r4] = r2
                if (r3 == 0) goto L_0x004a
                r6[r5] = r3
            L_0x004a:
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                org.telegram.messenger.support.LongSparseIntArray r9 = new org.telegram.messenger.support.LongSparseIntArray
                r9.<init>()
                r10 = 0
            L_0x005a:
                int r11 = r23.size()
                java.lang.String r13 = "@"
                java.lang.String r14 = " "
                if (r10 >= r11) goto L_0x01b9
                r11 = r23
                java.lang.Object r15 = r11.get(r10)
                org.telegram.messenger.ContactsController$Contact r15 = (org.telegram.messenger.ContactsController.Contact) r15
                java.lang.String r4 = r15.first_name
                java.lang.String r12 = r15.last_name
                java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r4, r12)
                java.lang.String r4 = r4.toLowerCase()
                org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r12 = r12.getTranslitString(r4)
                org.telegram.tgnet.TLRPC$User r5 = r15.user
                if (r5 == 0) goto L_0x009f
                org.telegram.tgnet.TLRPC$User r5 = r15.user
                java.lang.String r5 = r5.first_name
                r16 = r2
                org.telegram.tgnet.TLRPC$User r2 = r15.user
                java.lang.String r2 = r2.last_name
                java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r5, r2)
                java.lang.String r2 = r2.toLowerCase()
                org.telegram.messenger.LocaleController r5 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r5 = r5.getTranslitString(r4)
                goto L_0x00a3
            L_0x009f:
                r16 = r2
                r2 = 0
                r5 = 0
            L_0x00a3:
                boolean r17 = r4.equals(r12)
                if (r17 == 0) goto L_0x00aa
                r12 = 0
            L_0x00aa:
                r17 = 0
                r18 = r3
                int r3 = r6.length
                r11 = 0
            L_0x00b0:
                if (r11 >= r3) goto L_0x01ad
                r19 = r3
                r3 = r6[r11]
                if (r2 == 0) goto L_0x00d3
                boolean r20 = r2.startsWith(r3)
                if (r20 != 0) goto L_0x00f0
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r14)
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                boolean r0 = r2.contains(r0)
                if (r0 != 0) goto L_0x00f0
            L_0x00d3:
                if (r5 == 0) goto L_0x00f2
                boolean r0 = r5.startsWith(r3)
                if (r0 != 0) goto L_0x00f0
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r14)
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                boolean r0 = r5.contains(r0)
                if (r0 == 0) goto L_0x00f2
            L_0x00f0:
                r0 = 1
                goto L_0x0145
            L_0x00f2:
                org.telegram.tgnet.TLRPC$User r0 = r15.user
                if (r0 == 0) goto L_0x0108
                org.telegram.tgnet.TLRPC$User r0 = r15.user
                java.lang.String r0 = r0.username
                if (r0 == 0) goto L_0x0108
                org.telegram.tgnet.TLRPC$User r0 = r15.user
                java.lang.String r0 = r0.username
                boolean r0 = r0.startsWith(r3)
                if (r0 == 0) goto L_0x0108
                r0 = 2
                goto L_0x0145
            L_0x0108:
                boolean r0 = r4.startsWith(r3)
                if (r0 != 0) goto L_0x0144
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r14)
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                boolean r0 = r4.contains(r0)
                if (r0 != 0) goto L_0x0144
                if (r12 == 0) goto L_0x0141
                boolean r0 = r12.startsWith(r3)
                if (r0 != 0) goto L_0x0144
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r14)
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                boolean r0 = r12.contains(r0)
                if (r0 == 0) goto L_0x0141
                goto L_0x0144
            L_0x0141:
                r0 = r17
                goto L_0x0145
            L_0x0144:
                r0 = 3
            L_0x0145:
                if (r0 == 0) goto L_0x01a3
                r11 = 3
                if (r0 != r11) goto L_0x0156
                java.lang.String r11 = r15.first_name
                java.lang.String r13 = r15.last_name
                java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.generateSearchName(r11, r13, r3)
                r8.add(r11)
                goto L_0x0193
            L_0x0156:
                r11 = 1
                if (r0 != r11) goto L_0x0169
                org.telegram.tgnet.TLRPC$User r11 = r15.user
                java.lang.String r11 = r11.first_name
                org.telegram.tgnet.TLRPC$User r13 = r15.user
                java.lang.String r13 = r13.last_name
                java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.generateSearchName(r11, r13, r3)
                r8.add(r11)
                goto L_0x0193
            L_0x0169:
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r13)
                org.telegram.tgnet.TLRPC$User r14 = r15.user
                java.lang.String r14 = r14.username
                r11.append(r14)
                java.lang.String r11 = r11.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r13)
                r14.append(r3)
                java.lang.String r13 = r14.toString()
                r14 = 0
                java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.generateSearchName(r11, r14, r13)
                r8.add(r11)
            L_0x0193:
                org.telegram.tgnet.TLRPC$User r11 = r15.user
                if (r11 == 0) goto L_0x019f
                org.telegram.tgnet.TLRPC$User r11 = r15.user
                long r13 = r11.id
                r11 = 1
                r9.put(r13, r11)
            L_0x019f:
                r7.add(r15)
                goto L_0x01ad
            L_0x01a3:
                int r11 = r11 + 1
                r17 = r0
                r3 = r19
                r0 = r21
                goto L_0x00b0
            L_0x01ad:
                int r10 = r10 + 1
                r0 = r21
                r2 = r16
                r3 = r18
                r4 = 0
                r5 = 1
                goto L_0x005a
            L_0x01b9:
                r16 = r2
                r18 = r3
                r0 = 0
            L_0x01be:
                int r2 = r24.size()
                if (r0 >= r2) goto L_0x02a3
                r2 = r24
                java.lang.Object r3 = r2.get(r0)
                org.telegram.tgnet.TLRPC$TL_contact r3 = (org.telegram.tgnet.TLRPC.TL_contact) r3
                long r4 = r3.user_id
                int r4 = r9.indexOfKey(r4)
                if (r4 < 0) goto L_0x01d7
                r3 = 0
                goto L_0x029f
            L_0x01d7:
                org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r25)
                long r10 = r3.user_id
                java.lang.Long r5 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
                java.lang.String r5 = r4.first_name
                java.lang.String r10 = r4.last_name
                java.lang.String r5 = org.telegram.messenger.ContactsController.formatName(r5, r10)
                java.lang.String r5 = r5.toLowerCase()
                org.telegram.messenger.LocaleController r10 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r10 = r10.getTranslitString(r5)
                boolean r11 = r5.equals(r10)
                if (r11 == 0) goto L_0x0200
                r10 = 0
            L_0x0200:
                r11 = 0
                int r12 = r6.length
                r15 = 0
            L_0x0203:
                if (r15 >= r12) goto L_0x029c
                r2 = r6[r15]
                boolean r17 = r5.startsWith(r2)
                if (r17 != 0) goto L_0x0251
                r17 = r3
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r14)
                r3.append(r2)
                java.lang.String r3 = r3.toString()
                boolean r3 = r5.contains(r3)
                if (r3 != 0) goto L_0x0253
                if (r10 == 0) goto L_0x0242
                boolean r3 = r10.startsWith(r2)
                if (r3 != 0) goto L_0x0253
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r14)
                r3.append(r2)
                java.lang.String r3 = r3.toString()
                boolean r3 = r10.contains(r3)
                if (r3 == 0) goto L_0x0242
                goto L_0x0253
            L_0x0242:
                java.lang.String r3 = r4.username
                if (r3 == 0) goto L_0x0255
                java.lang.String r3 = r4.username
                boolean r3 = r3.startsWith(r2)
                if (r3 == 0) goto L_0x0255
                r3 = 2
                r11 = r3
                goto L_0x0255
            L_0x0251:
                r17 = r3
            L_0x0253:
                r3 = 1
                r11 = r3
            L_0x0255:
                if (r11 == 0) goto L_0x0293
                r3 = 1
                if (r11 != r3) goto L_0x0267
                java.lang.String r12 = r4.first_name
                java.lang.String r15 = r4.last_name
                java.lang.CharSequence r12 = org.telegram.messenger.AndroidUtilities.generateSearchName(r12, r15, r2)
                r8.add(r12)
                r3 = 0
                goto L_0x028f
            L_0x0267:
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r13)
                java.lang.String r15 = r4.username
                r12.append(r15)
                java.lang.String r12 = r12.toString()
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r13)
                r15.append(r2)
                java.lang.String r15 = r15.toString()
                r3 = 0
                java.lang.CharSequence r12 = org.telegram.messenger.AndroidUtilities.generateSearchName(r12, r3, r15)
                r8.add(r12)
            L_0x028f:
                r7.add(r4)
                goto L_0x029f
            L_0x0293:
                r3 = 0
                int r15 = r15 + 1
                r2 = r24
                r3 = r17
                goto L_0x0203
            L_0x029c:
                r17 = r3
                r3 = 0
            L_0x029f:
                int r0 = r0 + 1
                goto L_0x01be
            L_0x02a3:
                r0 = r21
                r2 = r26
                r0.updateSearchResults(r1, r7, r8, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertContactsLayout.ShareSearchAdapter.m778xcb356CLASSNAME(java.lang.String, java.util.ArrayList, java.util.ArrayList, int, int):void");
        }

        private void updateSearchResults(String query, ArrayList<Object> users, ArrayList<CharSequence> names, int searchId) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda0(this, searchId, users, names));
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-Components-ChatAttachAlertContactsLayout$ShareSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m781xcea3a044(int searchId, ArrayList users, ArrayList names) {
            if (searchId == this.lastSearchId) {
                if (!(searchId == -1 || ChatAttachAlertContactsLayout.this.listView.getAdapter() == ChatAttachAlertContactsLayout.this.searchAdapter)) {
                    ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.searchAdapter);
                }
                this.searchResult = users;
                this.searchResultNames = names;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            return this.searchResult.size() + 2;
        }

        public Object getItem(int position) {
            int position2 = position - 1;
            if (position2 < 0 || position2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(position2);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new View(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) holder.itemView;
                boolean divider = position != getItemCount() + -2;
                Object object = getItem(position);
                TLRPC.User user = null;
                if (object instanceof ContactsController.Contact) {
                    ContactsController.Contact contact = (ContactsController.Contact) object;
                    if (contact.user != null) {
                        user = contact.user;
                    } else {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC.User) null, this.searchResultNames.get(position - 1), (UserCell.CharSequenceCallback) new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda4(contact), divider);
                    }
                } else {
                    user = (TLRPC.User) object;
                }
                if (user != null) {
                    userCell.setData(user, this.searchResultNames.get(position - 1), (UserCell.CharSequenceCallback) new ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda5(user), divider);
                }
            }
        }

        static /* synthetic */ CharSequence lambda$onBindViewHolder$4(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            if (position == getItemCount() - 1) {
                return 2;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatAttachAlertContactsLayout$$ExternalSyntheticLambda0(this);
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.shadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchText"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchHint"));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "dialogTextGray2"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundPink"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$2$org-telegram-ui-Components-ChatAttachAlertContactsLayout  reason: not valid java name */
    public /* synthetic */ void m775x637b688a() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
