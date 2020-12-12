package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.RecyclerListView;

public class GroupVoipInviteAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public int addNewRow;
    /* access modifiers changed from: private */
    public int backgroundColor;
    private float colorProgress;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$ChannelParticipant> contacts;
    private boolean contactsEndReached;
    /* access modifiers changed from: private */
    public int contactsEndRow;
    /* access modifiers changed from: private */
    public int contactsHeaderRow;
    private SparseArray<TLObject> contactsMap;
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    private int delayResults;
    /* access modifiers changed from: private */
    public GroupVoipInviteAlertDelegate delegate;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public boolean firstLoaded;
    /* access modifiers changed from: private */
    public FlickerLoadingView flickerLoadingView;
    private FrameLayout frameLayout;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC$TL_groupCallParticipant> ignoredUsers;
    private TLRPC$ChatFull info;
    /* access modifiers changed from: private */
    public HashSet<Integer> invitedUsers;
    /* access modifiers changed from: private */
    public int lastRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loadingUsers;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$ChannelParticipant> participants;
    /* access modifiers changed from: private */
    public int participantsEndRow;
    private SparseArray<TLObject> participantsMap;
    /* access modifiers changed from: private */
    public int participantsStartRow;
    private FrameLayout progressLayout;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    private SearchField searchView;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;

    public interface GroupVoipInviteAlertDelegate {
        void copyInviteLink();

        void inviteUser(int i);

        void needOpenSearch(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
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
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("voipgroup_listViewBackgroundUnscrolled")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_searchPlaceholder"), PorterDuff.Mode.MULTIPLY));
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
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_searchText"), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    GroupVoipInviteAlert.SearchField.this.lambda$new$0$GroupVoipInviteAlert$SearchField(view);
                }
            });
            AnonymousClass1 r0 = new EditTextBoldCursor(context, GroupVoipInviteAlert.this) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - GroupVoipInviteAlert.this.containerView.getTranslationY());
                    if (obtain.getAction() == 1) {
                        obtain.setAction(3);
                    }
                    GroupVoipInviteAlert.this.listView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor("voipgroup_searchPlaceholder"));
            this.searchEditText.setTextColor(Theme.getColor("voipgroup_searchText"));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("VoipGroupSearchMembers", NUM));
            this.searchEditText.setCursorColor(Theme.getColor("voipgroup_searchText"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(GroupVoipInviteAlert.this) {
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
                    int itemCount = GroupVoipInviteAlert.this.listView.getAdapter() == null ? 0 : GroupVoipInviteAlert.this.listView.getAdapter().getItemCount();
                    GroupVoipInviteAlert.this.searchListViewAdapter.searchUsers(obj);
                    if (!(!TextUtils.isEmpty(obj) || GroupVoipInviteAlert.this.listView == null || GroupVoipInviteAlert.this.listView.getAdapter() == GroupVoipInviteAlert.this.listViewAdapter)) {
                        GroupVoipInviteAlert.this.listView.setAdapter(GroupVoipInviteAlert.this.listViewAdapter);
                        if (itemCount == 0) {
                            GroupVoipInviteAlert.this.showItemsAnimated(0);
                        }
                    }
                    GroupVoipInviteAlert.this.flickerLoadingView.setVisibility(0);
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return GroupVoipInviteAlert.SearchField.this.lambda$new$1$GroupVoipInviteAlert$SearchField(textView, i, keyEvent);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$GroupVoipInviteAlert$SearchField(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
        public /* synthetic */ boolean lambda$new$1$GroupVoipInviteAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent == null) {
                return false;
            }
            if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            GroupVoipInviteAlert.this.delegate.needOpenSearch(motionEvent, this.searchEditText);
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    static {
        new AnimationProperties.FloatProperty<GroupVoipInviteAlert>("colorProgress") {
            public void setValue(GroupVoipInviteAlert groupVoipInviteAlert, float f) {
                groupVoipInviteAlert.setColorProgress(f);
            }

            public Float get(GroupVoipInviteAlert groupVoipInviteAlert) {
                return Float.valueOf(groupVoipInviteAlert.getColorProgress());
            }
        };
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public GroupVoipInviteAlert(android.content.Context r18, int r19, org.telegram.tgnet.TLRPC$Chat r20, org.telegram.tgnet.TLRPC$ChatFull r21) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = 0
            r0.<init>(r1, r2)
            android.graphics.RectF r3 = new android.graphics.RectF
            r3.<init>()
            r0.rect = r3
            android.graphics.Paint r3 = new android.graphics.Paint
            r4 = 1
            r3.<init>(r4)
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.participants = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.contacts = r3
            android.util.SparseArray r3 = new android.util.SparseArray
            r3.<init>()
            r0.participantsMap = r3
            android.util.SparseArray r3 = new android.util.SparseArray
            r3.<init>()
            r0.contactsMap = r3
            r3 = 75
            r0.setDimBehindAlpha(r3)
            r3 = r19
            r0.currentAccount = r3
            r3 = r20
            r0.currentChat = r3
            r3 = r21
            r0.info = r3
            android.content.res.Resources r3 = r18.getResources()
            r5 = 2131165979(0x7var_b, float:1.794619E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r5)
            android.graphics.drawable.Drawable r3 = r3.mutate()
            r0.shadowDrawable = r3
            org.telegram.ui.Components.GroupVoipInviteAlert$2 r3 = new org.telegram.ui.Components.GroupVoipInviteAlert$2
            r3.<init>(r1)
            r0.containerView = r3
            r3.setWillNotDraw(r2)
            android.view.ViewGroup r3 = r0.containerView
            r3.setClipChildren(r2)
            android.view.ViewGroup r3 = r0.containerView
            int r5 = r0.backgroundPaddingLeft
            r3.setPadding(r5, r2, r5, r2)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.frameLayout = r3
            org.telegram.ui.Components.GroupVoipInviteAlert$SearchField r3 = new org.telegram.ui.Components.GroupVoipInviteAlert$SearchField
            r3.<init>(r1)
            r0.searchView = r3
            android.widget.FrameLayout r5 = r0.frameLayout
            r6 = -1
            r7 = 51
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r7)
            r5.addView(r3, r8)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.progressLayout = r3
            org.telegram.ui.Components.FlickerLoadingView r3 = new org.telegram.ui.Components.FlickerLoadingView
            r3.<init>(r1)
            r0.flickerLoadingView = r3
            r5 = 6
            r3.setViewType(r5)
            org.telegram.ui.Components.FlickerLoadingView r3 = r0.flickerLoadingView
            r3.showDate(r2)
            org.telegram.ui.Components.FlickerLoadingView r3 = r0.flickerLoadingView
            r3.setUseHeaderOffset(r4)
            org.telegram.ui.Components.FlickerLoadingView r3 = r0.flickerLoadingView
            java.lang.String r5 = "voipgroup_inviteMembersBackground"
            java.lang.String r8 = "voipgroup_emptyView"
            r3.setColors(r5, r8)
            android.widget.FrameLayout r3 = r0.progressLayout
            org.telegram.ui.Components.FlickerLoadingView r9 = r0.flickerLoadingView
            r3.addView(r9)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r9 = r0.progressLayout
            r10 = -1
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = 51
            r13 = 0
            r14 = 1115160576(0x42780000, float:62.0)
            r15 = 0
            r16 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r3.addView(r9, r10)
            org.telegram.ui.Components.StickerEmptyView r3 = new org.telegram.ui.Components.StickerEmptyView
            android.widget.FrameLayout r9 = r0.progressLayout
            r3.<init>(r1, r9, r4)
            r0.emptyView = r3
            android.widget.TextView r3 = r3.title
            java.lang.String r9 = "NoResult"
            r10 = 2131626097(0x7f0e0871, float:1.887942E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r10)
            r3.setText(r9)
            org.telegram.ui.Components.StickerEmptyView r3 = r0.emptyView
            android.widget.TextView r3 = r3.subtitle
            java.lang.String r9 = "SearchEmptyViewFilteredSubtitle2"
            r10 = 2131627010(0x7f0e0CLASSNAME, float:1.8881272E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r10)
            r3.setText(r9)
            org.telegram.ui.Components.StickerEmptyView r3 = r0.emptyView
            r9 = 8
            r3.setVisibility(r9)
            org.telegram.ui.Components.StickerEmptyView r3 = r0.emptyView
            r3.setAnimateLayoutChange(r4)
            org.telegram.ui.Components.StickerEmptyView r3 = r0.emptyView
            r3.showProgress(r4, r2)
            org.telegram.ui.Components.StickerEmptyView r3 = r0.emptyView
            java.lang.String r9 = "voipgroup_nameText"
            java.lang.String r10 = "voipgroup_lastSeenText"
            r3.setColors(r9, r10, r5, r8)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.StickerEmptyView r5 = r0.emptyView
            r8 = -1
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = 51
            r11 = 0
            r12 = 1115160576(0x42780000, float:62.0)
            r14 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r3.addView(r5, r8)
            org.telegram.ui.Components.GroupVoipInviteAlert$SearchAdapter r3 = new org.telegram.ui.Components.GroupVoipInviteAlert$SearchAdapter
            r3.<init>(r1)
            r0.searchListViewAdapter = r3
            org.telegram.ui.Components.GroupVoipInviteAlert$3 r3 = new org.telegram.ui.Components.GroupVoipInviteAlert$3
            r3.<init>(r1)
            r0.listView = r3
            r5 = 13
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r3.setTag(r5)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r3.setPadding(r2, r2, r2, r5)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setClipToPadding(r2)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setHideIfEmpty(r2)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            java.lang.String r5 = "voipgroup_listSelector"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setSelectorDrawableColor(r5)
            org.telegram.ui.Components.FillLastLinearLayoutManager r3 = new org.telegram.ui.Components.FillLastLinearLayoutManager
            android.content.Context r9 = r17.getContext()
            r5 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
            org.telegram.ui.Components.RecyclerListView r13 = r0.listView
            r10 = 1
            r11 = 0
            r8 = r3
            r8.<init>(r9, r10, r11, r12, r13)
            r3.setBind(r2)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setLayoutManager(r3)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setHorizontalScrollBarEnabled(r2)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setVerticalScrollBarEnabled(r2)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r8 = -1
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = 51
            r11 = 0
            r12 = 0
            r13 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r3.addView(r5, r8)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            org.telegram.ui.Components.GroupVoipInviteAlert$ListAdapter r5 = new org.telegram.ui.Components.GroupVoipInviteAlert$ListAdapter
            r5.<init>(r1)
            r0.listViewAdapter = r5
            r3.setAdapter(r5)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            org.telegram.ui.Components.-$$Lambda$GroupVoipInviteAlert$6LuHPYYv5S7g_rtjCPTRmKTogW4 r5 = new org.telegram.ui.Components.-$$Lambda$GroupVoipInviteAlert$6LuHPYYv5S7g_rtjCPTRmKTogW4
            r5.<init>()
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r5)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            org.telegram.ui.Components.GroupVoipInviteAlert$4 r5 = new org.telegram.ui.Components.GroupVoipInviteAlert$4
            r5.<init>()
            r3.setOnScrollListener(r5)
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r5 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r3.<init>(r6, r5, r7)
            r5 = 1114112000(0x42680000, float:58.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r3.topMargin = r5
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            r0.shadow = r5
            java.lang.String r1 = "dialogShadowLine"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r5.setBackgroundColor(r1)
            android.view.View r1 = r0.shadow
            r5 = 0
            r1.setAlpha(r5)
            android.view.View r1 = r0.shadow
            java.lang.Integer r8 = java.lang.Integer.valueOf(r4)
            r1.setTag(r8)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r8 = r0.shadow
            r1.addView(r8, r3)
            android.view.ViewGroup r1 = r0.containerView
            android.widget.FrameLayout r3 = r0.frameLayout
            r8 = 58
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8, r7)
            r1.addView(r3, r6)
            r0.setColorProgress(r5)
            r1 = 200(0xc8, float:2.8E-43)
            r0.loadChatParticipants(r2, r1)
            r17.updateRows()
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            org.telegram.ui.Components.StickerEmptyView r3 = r0.emptyView
            r1.setEmptyView(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            r1.setAnimateEmptyView(r4, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.<init>(android.content.Context, int, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupVoipInviteAlert(View view, int i) {
        if (i == this.addNewRow) {
            this.delegate.copyInviteLink();
            dismiss();
        } else if (view instanceof ManageChatUserCell) {
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) view;
            if (!this.invitedUsers.contains(Integer.valueOf(manageChatUserCell.getUserId()))) {
                this.delegate.inviteUser(manageChatUserCell.getUserId());
            }
        }
    }

    /* access modifiers changed from: private */
    public float getColorProgress() {
        return this.colorProgress;
    }

    /* access modifiers changed from: private */
    public void setColorProgress(float f) {
        String str;
        this.colorProgress = f;
        this.backgroundColor = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_inviteMembersBackground"), Theme.getColor("voipgroup_listViewBackground"), f, 1.0f);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.frameLayout.setBackgroundColor(this.backgroundColor);
        int i = this.backgroundColor;
        this.navBarColor = i;
        this.listView.setGlowColor(i);
        int offsetColor = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_lastSeenTextUnscrolled"), Theme.getColor("voipgroup_lastSeenText"), f, 1.0f);
        int offsetColor2 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_mutedIconUnscrolled"), Theme.getColor("voipgroup_mutedIcon"), f, 1.0f);
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof GroupCallTextCell) {
                ((GroupCallTextCell) childAt).setColors(offsetColor, offsetColor);
            } else if (childAt instanceof GroupCallUserCell) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) childAt;
                if (this.shadow.getTag() != null) {
                    str = "voipgroup_mutedIcon";
                } else {
                    str = "voipgroup_mutedIconUnscrolled";
                }
                groupCallUserCell.setGrayIconColor(str, offsetColor2);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    public void setDelegate(GroupVoipInviteAlertDelegate groupVoipInviteAlertDelegate) {
        this.delegate = groupVoipInviteAlertDelegate;
    }

    public void setIgnoredUsers(SparseArray<TLRPC$TL_groupCallParticipant> sparseArray, HashSet<Integer> hashSet) {
        this.ignoredUsers = sparseArray;
        this.invitedUsers = hashSet;
    }

    private void updateRows() {
        this.addNewRow = -1;
        this.emptyRow = -1;
        this.participantsStartRow = -1;
        this.participantsEndRow = -1;
        this.contactsHeaderRow = -1;
        this.contactsStartRow = -1;
        this.contactsEndRow = -1;
        this.membersHeaderRow = -1;
        this.lastRow = -1;
        boolean z = false;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.emptyRow = 0;
        if (ChatObject.canAddUsers(this.currentChat)) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addNewRow = i;
        }
        if (!this.loadingUsers || this.firstLoaded) {
            if (!this.contacts.isEmpty()) {
                int i2 = this.rowCount;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.contactsHeaderRow = i2;
                this.contactsStartRow = i3;
                int size = i3 + this.contacts.size();
                this.rowCount = size;
                this.contactsEndRow = size;
                z = true;
            }
            if (!this.participants.isEmpty()) {
                if (z) {
                    int i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.membersHeaderRow = i4;
                }
                int i5 = this.rowCount;
                this.participantsStartRow = i5;
                int size2 = i5 + this.participants.size();
                this.rowCount = size2;
                this.participantsEndRow = size2;
            }
        }
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.lastRow = i6;
    }

    public void dismissInternal() {
        super.dismissInternal();
        AndroidUtilities.hideKeyboard(this.searchView.searchEditText);
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
                this.emptyView.setTranslationY((float) this.scrollOffsetY);
                this.progressLayout.setTranslationY((float) this.scrollOffsetY);
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
                    if (GroupVoipInviteAlert.this.shadowAnimation != null && GroupVoipInviteAlert.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            GroupVoipInviteAlert.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = GroupVoipInviteAlert.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (GroupVoipInviteAlert.this.shadowAnimation != null && GroupVoipInviteAlert.this.shadowAnimation.equals(animator)) {
                        AnimatorSet unused = GroupVoipInviteAlert.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public void showItemsAnimated(final int i) {
        if (isShowing()) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    GroupVoipInviteAlert.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = GroupVoipInviteAlert.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = GroupVoipInviteAlert.this.listView.getChildAt(i);
                        if (GroupVoipInviteAlert.this.listView.getChildAdapterPosition(childAt) >= i) {
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(GroupVoipInviteAlert.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) GroupVoipInviteAlert.this.listView.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    private void loadChatParticipants(int i, int i2) {
        if (!this.loadingUsers) {
            this.contactsEndReached = false;
            loadChatParticipants(i, i2, true);
        }
    }

    private void loadChatParticipants(int i, int i2, boolean z) {
        this.loadingUsers = true;
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(true, false);
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.participants_count <= 200) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
        } else if (!this.contactsEndReached) {
            this.delayResults = 2;
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsContacts();
            this.contactsEndReached = true;
            loadChatParticipants(0, 200, false);
        } else {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
        }
        tLRPC$TL_channels_getParticipants.filter.q = "";
        tLRPC$TL_channels_getParticipants.offset = i;
        tLRPC$TL_channels_getParticipants.limit = i2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate(tLRPC$TL_channels_getParticipants) {
            public final /* synthetic */ TLRPC$TL_channels_getParticipants f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                GroupVoipInviteAlert.this.lambda$loadChatParticipants$3$GroupVoipInviteAlert(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadChatParticipants$3 */
    public /* synthetic */ void lambda$loadChatParticipants$3$GroupVoipInviteAlert(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupVoipInviteAlert.this.lambda$null$2$GroupVoipInviteAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$GroupVoipInviteAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
        SparseArray<TLObject> sparseArray;
        ArrayList<TLRPC$ChannelParticipant> arrayList;
        boolean z;
        SparseArray<TLRPC$TL_groupCallParticipant> sparseArray2;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int i = 0;
            while (true) {
                if (i >= tLRPC$TL_channels_channelParticipants.participants.size()) {
                    break;
                } else if (tLRPC$TL_channels_channelParticipants.participants.get(i).user_id == clientUserId) {
                    tLRPC$TL_channels_channelParticipants.participants.remove(i);
                    break;
                } else {
                    i++;
                }
            }
            this.delayResults--;
            if (tLRPC$TL_channels_getParticipants.filter instanceof TLRPC$TL_channelParticipantsContacts) {
                arrayList = this.contacts;
                sparseArray = this.contactsMap;
            } else {
                arrayList = this.participants;
                sparseArray = this.participantsMap;
            }
            arrayList.clear();
            arrayList.addAll(tLRPC$TL_channels_channelParticipants.participants);
            int size = tLRPC$TL_channels_channelParticipants.participants.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i2);
                sparseArray.put(tLRPC$ChannelParticipant.user_id, tLRPC$ChannelParticipant);
            }
            int size2 = this.participants.size();
            int i3 = 0;
            while (i3 < size2) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = this.participants.get(i3);
                if (this.contactsMap.get(tLRPC$ChannelParticipant2.user_id) == null && ((sparseArray2 = this.ignoredUsers) == null || sparseArray2.indexOfKey(tLRPC$ChannelParticipant2.user_id) < 0)) {
                    z = false;
                } else {
                    z = true;
                }
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChannelParticipant2.user_id));
                if (user != null && user.bot) {
                    z = true;
                }
                if (z) {
                    this.participants.remove(i3);
                    this.participantsMap.remove(tLRPC$ChannelParticipant2.user_id);
                    i3--;
                    size2--;
                }
                i3++;
            }
            try {
                if (this.info.participants_count <= 200) {
                    Collections.sort(arrayList, new Object(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final int compare(Object obj, Object obj2) {
                            return GroupVoipInviteAlert.this.lambda$null$1$GroupVoipInviteAlert(this.f$1, (TLRPC$ChannelParticipant) obj, (TLRPC$ChannelParticipant) obj2);
                        }

                        public /* synthetic */ Comparator reversed() {
                            return Comparator.CC.$default$reversed(this);
                        }

                        public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                            return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                        }

                        public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                            return Comparator.CC.$default$thenComparing(this, function, comparator);
                        }

                        public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                            return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                        }

                        public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                            return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                        }

                        public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                            return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                        }

                        public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                            return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
            ListAdapter listAdapter = this.listViewAdapter;
            showItemsAnimated(listAdapter != null ? listAdapter.getItemCount() : 0);
        }
        updateRows();
        ListAdapter listAdapter2 = this.listViewAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
            if (this.emptyView != null && this.listViewAdapter.getItemCount() == 0 && this.firstLoaded) {
                this.emptyView.showProgress(false, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ int lambda$null$1$GroupVoipInviteAlert(int i, TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$ChannelParticipant tLRPC$ChannelParticipant2) {
        int i2;
        int i3;
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChannelParticipant.user_id));
        TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChannelParticipant2.user_id));
        if (user == null || (tLRPC$UserStatus2 = user.status) == null) {
            i2 = 0;
        } else {
            i2 = user.self ? i + 50000 : tLRPC$UserStatus2.expires;
        }
        if (user2 == null || (tLRPC$UserStatus = user2.status) == null) {
            i3 = 0;
        } else {
            i3 = user2.self ? i + 50000 : tLRPC$UserStatus.expires;
        }
        if (i2 <= 0 || i3 <= 0) {
            if (i2 >= 0 || i3 >= 0) {
                if ((i2 < 0 && i3 > 0) || (i2 == 0 && i3 != 0)) {
                    return -1;
                }
                if ((i3 >= 0 || i2 <= 0) && (i3 != 0 || i2 == 0)) {
                    return 0;
                }
                return 1;
            } else if (i2 > i3) {
                return 1;
            } else {
                if (i2 < i3) {
                    return -1;
                }
                return 0;
            }
        } else if (i2 > i3) {
            return 1;
        } else {
            if (i2 < i3) {
                return -1;
            }
            return 0;
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int emptyRow;
        private int globalStartRow;
        private int groupStartRow;
        private int lastRow;
        /* access modifiers changed from: private */
        public int lastSearchId;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate(GroupVoipInviteAlert.this) {
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                public /* synthetic */ SparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }

                public void onDataSetChanged(int i) {
                    if (i == SearchAdapter.this.lastSearchId) {
                        if (GroupVoipInviteAlert.this.listView.getAdapter() != GroupVoipInviteAlert.this.searchListViewAdapter) {
                            GroupVoipInviteAlert.this.listView.setAdapter(GroupVoipInviteAlert.this.searchListViewAdapter);
                        }
                        int itemCount = SearchAdapter.this.getItemCount();
                        SearchAdapter.this.notifyDataSetChanged();
                        if (SearchAdapter.this.getItemCount() > itemCount) {
                            GroupVoipInviteAlert.this.showItemsAnimated(itemCount);
                        }
                        if (SearchAdapter.this.getItemCount() <= 2) {
                            GroupVoipInviteAlert.this.emptyView.showProgress(false, true);
                        }
                    }
                }

                public SparseArray<TLRPC$TL_groupCallParticipant> getExcludeCallParticipants() {
                    return GroupVoipInviteAlert.this.ignoredUsers;
                }
            });
        }

        public void searchUsers(String str) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, true, false, false, GroupVoipInviteAlert.this.currentChat.id, false, 2, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(str)) {
                GroupVoipInviteAlert.this.emptyView.showProgress(true, true);
                int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                $$Lambda$GroupVoipInviteAlert$SearchAdapter$CjJVFO4QLy91LWrj5CbugY1KnwU r1 = new Runnable(str, i) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        GroupVoipInviteAlert.SearchAdapter.this.lambda$searchUsers$0$GroupVoipInviteAlert$SearchAdapter(this.f$1, this.f$2);
                    }
                };
                this.searchRunnable = r1;
                AndroidUtilities.runOnUIThread(r1, 300);
                return;
            }
            this.lastSearchId = -1;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$searchUsers$0 */
        public /* synthetic */ void lambda$searchUsers$0$GroupVoipInviteAlert$SearchAdapter(String str, int i) {
            if (this.searchRunnable != null) {
                this.searchRunnable = null;
                this.searchAdapterHelper.queryServerSearch(str, true, false, true, false, false, GroupVoipInviteAlert.this.currentChat.id, false, 2, i);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if ((!(view instanceof ManageChatUserCell) || !GroupVoipInviteAlert.this.invitedUsers.contains(Integer.valueOf(((ManageChatUserCell) view).getUserId()))) && viewHolder.getItemViewType() == 0) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return this.totalCount;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            this.totalCount = 0 + 1;
            this.emptyRow = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                int i = this.totalCount;
                this.groupStartRow = i;
                this.totalCount = i + size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size2 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + size2 + 1;
            } else {
                this.globalStartRow = -1;
            }
            int i3 = this.totalCount;
            this.totalCount = i3 + 1;
            this.lastRow = i3;
            super.notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int i2 = this.groupStartRow;
            if (i2 >= 0 && i > i2 && i < i2 + 1 + this.searchAdapterHelper.getGroupSearch().size()) {
                return this.searchAdapterHelper.getGroupSearch().get((i - this.groupStartRow) - 1);
            }
            int i3 = this.globalStartRow;
            if (i3 < 0 || i <= i3 || i >= i3 + 1 + this.searchAdapterHelper.getGlobalSearch().size()) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get((i - this.globalStartRow) - 1);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                java.lang.String r4 = "voipgroup_nameText"
                r0 = 2
                if (r5 == 0) goto L_0x003d
                r1 = 1
                if (r5 == r1) goto L_0x0029
                if (r5 == r0) goto L_0x0012
                android.view.View r4 = new android.view.View
                android.content.Context r5 = r3.mContext
                r4.<init>(r5)
                goto L_0x0065
            L_0x0012:
                android.view.View r4 = new android.view.View
                android.content.Context r5 = r3.mContext
                r4.<init>(r5)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r5 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = 1113587712(0x42600000, float:56.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r5.<init>((int) r0, (int) r1)
                r4.setLayoutParams(r5)
                goto L_0x0065
            L_0x0029:
                org.telegram.ui.Cells.GraySectionCell r5 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                java.lang.String r0 = "voipgroup_emptyView"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r5.setBackgroundColor(r0)
                r5.setTextColor(r4)
                goto L_0x0064
            L_0x003d:
                org.telegram.ui.Cells.ManageChatUserCell r5 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r3.mContext
                r2 = 0
                r5.<init>(r1, r0, r0, r2)
                r0 = 2131165743(0x7var_f, float:1.7945712E38)
                r5.setCustomRightImage(r0)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setNameColor(r0)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                java.lang.String r0 = "voipgroup_blueText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r5.setStatusColors(r4, r0)
                java.lang.String r4 = "voipgroup_listViewBackground"
                r5.setDividerColor(r4)
            L_0x0064:
                r4 = r5
            L_0x0065:
                org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                r5.<init>(r4)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
                r13 = this;
                int r0 = r14.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x0049
                if (r0 == r1) goto L_0x000b
                goto L_0x012a
            L_0x000b:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.GraySectionCell r14 = (org.telegram.ui.Cells.GraySectionCell) r14
                int r0 = r13.groupStartRow
                if (r15 != r0) goto L_0x0021
                r15 = 2131624661(0x7f0e02d5, float:1.8876508E38)
                java.lang.String r0 = "ChannelMembers"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x012a
            L_0x0021:
                int r0 = r13.globalStartRow
                if (r15 != r0) goto L_0x0033
                r15 = 2131625572(0x7f0e0664, float:1.8878356E38)
                java.lang.String r0 = "GlobalSearch"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x012a
            L_0x0033:
                org.telegram.ui.Components.GroupVoipInviteAlert r0 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r0 = r0.contactsStartRow
                if (r15 != r0) goto L_0x012a
                r15 = 2131624920(0x7f0e03d8, float:1.8877033E38)
                java.lang.String r0 = "Contacts"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x012a
            L_0x0049:
                org.telegram.tgnet.TLObject r0 = r13.getItem(r15)
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$User
                if (r2 == 0) goto L_0x0054
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
                goto L_0x006e
            L_0x0054:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r2 == 0) goto L_0x012a
                org.telegram.ui.Components.GroupVoipInviteAlert r2 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.tgnet.TLRPC$ChannelParticipant r0 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r0
                int r0 = r0.user_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            L_0x006e:
                java.lang.String r2 = r0.username
                org.telegram.ui.Adapters.SearchAdapterHelper r3 = r13.searchAdapterHelper
                java.util.ArrayList r3 = r3.getGroupSearch()
                int r3 = r3.size()
                r4 = 0
                r5 = 0
                if (r3 == 0) goto L_0x008a
                int r3 = r3 + r1
                if (r3 <= r15) goto L_0x0089
                org.telegram.ui.Adapters.SearchAdapterHelper r3 = r13.searchAdapterHelper
                java.lang.String r3 = r3.getLastFoundChannel()
                r6 = 1
                goto L_0x008c
            L_0x0089:
                int r15 = r15 - r3
            L_0x008a:
                r3 = r5
                r6 = 0
            L_0x008c:
                r7 = 33
                java.lang.String r8 = "voipgroup_blueText"
                r9 = -1
                if (r6 != 0) goto L_0x00e6
                if (r2 == 0) goto L_0x00e6
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r13.searchAdapterHelper
                java.util.ArrayList r6 = r6.getGlobalSearch()
                int r6 = r6.size()
                if (r6 == 0) goto L_0x00e6
                int r6 = r6 + r1
                if (r6 <= r15) goto L_0x00e6
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r13.searchAdapterHelper
                java.lang.String r6 = r6.getLastFoundUsername()
                java.lang.String r10 = "@"
                boolean r11 = r6.startsWith(r10)
                if (r11 == 0) goto L_0x00b6
                java.lang.String r6 = r6.substring(r1)
            L_0x00b6:
                android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x00e1 }
                r1.<init>()     // Catch:{ Exception -> 0x00e1 }
                r1.append(r10)     // Catch:{ Exception -> 0x00e1 }
                r1.append(r2)     // Catch:{ Exception -> 0x00e1 }
                int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r6)     // Catch:{ Exception -> 0x00e1 }
                if (r10 == r9) goto L_0x00df
                int r6 = r6.length()     // Catch:{ Exception -> 0x00e1 }
                if (r10 != 0) goto L_0x00d0
                int r6 = r6 + 1
                goto L_0x00d2
            L_0x00d0:
                int r10 = r10 + 1
            L_0x00d2:
                android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x00e1 }
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r8)     // Catch:{ Exception -> 0x00e1 }
                r11.<init>(r12)     // Catch:{ Exception -> 0x00e1 }
                int r6 = r6 + r10
                r1.setSpan(r11, r10, r6, r7)     // Catch:{ Exception -> 0x00e1 }
            L_0x00df:
                r2 = r1
                goto L_0x00e7
            L_0x00e1:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
                goto L_0x00e7
            L_0x00e6:
                r2 = r5
            L_0x00e7:
                if (r3 == 0) goto L_0x0109
                java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r0)
                android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
                r5.<init>(r1)
                int r1 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r1, r3)
                if (r1 == r9) goto L_0x0109
                android.text.style.ForegroundColorSpan r6 = new android.text.style.ForegroundColorSpan
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r6.<init>(r8)
                int r3 = r3.length()
                int r3 = r3 + r1
                r5.setSpan(r6, r1, r3, r7)
            L_0x0109:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.ManageChatUserCell r14 = (org.telegram.ui.Cells.ManageChatUserCell) r14
                java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                r14.setTag(r15)
                org.telegram.ui.Components.GroupVoipInviteAlert r15 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                java.util.HashSet r15 = r15.invitedUsers
                int r1 = r0.id
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                boolean r15 = r15.contains(r1)
                r14.setCustomImageVisible(r15)
                r14.setData(r0, r5, r2, r4)
            L_0x012a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == this.emptyRow) {
                return 2;
            }
            if (i == this.lastRow) {
                return 3;
            }
            return (i == this.globalStartRow || i == this.groupStartRow) ? 1 : 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if ((view instanceof ManageChatUserCell) && GroupVoipInviteAlert.this.invitedUsers.contains(Integer.valueOf(((ManageChatUserCell) view).getUserId()))) {
                return false;
            }
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0 || itemViewType == 1) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return GroupVoipInviteAlert.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                java.lang.String r7 = "voipgroup_actionBar"
                java.lang.String r0 = "voipgroup_nameText"
                r1 = 2
                java.lang.String r2 = "voipgroup_blueText"
                if (r8 == 0) goto L_0x0052
                r3 = 1
                if (r8 == r3) goto L_0x0044
                if (r8 == r1) goto L_0x0030
                r7 = 3
                if (r8 == r7) goto L_0x0019
                android.view.View r7 = new android.view.View
                android.content.Context r8 = r6.mContext
                r7.<init>(r8)
                goto L_0x0079
            L_0x0019:
                android.view.View r7 = new android.view.View
                android.content.Context r8 = r6.mContext
                r7.<init>(r8)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r8 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = 1113587712(0x42600000, float:56.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r8.<init>((int) r0, (int) r1)
                r7.setLayoutParams(r8)
                goto L_0x0079
            L_0x0030:
                org.telegram.ui.Cells.GraySectionCell r7 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r8 = r6.mContext
                r7.<init>(r8)
                java.lang.String r8 = "voipgroup_emptyView"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r7.setBackgroundColor(r8)
                r7.setTextColor(r0)
                goto L_0x0079
            L_0x0044:
                org.telegram.ui.Cells.ManageChatTextCell r8 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r0 = r6.mContext
                r8.<init>(r0)
                r8.setColors(r2, r2)
                r8.setDividerColor(r7)
                goto L_0x0078
            L_0x0052:
                org.telegram.ui.Cells.ManageChatUserCell r8 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r3 = r6.mContext
                r4 = 6
                r5 = 0
                r8.<init>(r3, r4, r1, r5)
                r1 = 2131165743(0x7var_f, float:1.7945712E38)
                r8.setCustomRightImage(r1)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r8.setNameColor(r0)
                java.lang.String r0 = "voipgroup_lastSeenText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r8.setStatusColors(r0, r1)
                r8.setDividerColor(r7)
            L_0x0078:
                r7 = r8
            L_0x0079:
                org.telegram.ui.Components.RecyclerListView$Holder r8 = new org.telegram.ui.Components.RecyclerListView$Holder
                r8.<init>(r7)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int i3;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                TLObject item = getItem(i);
                if (i < GroupVoipInviteAlert.this.participantsStartRow || i >= GroupVoipInviteAlert.this.participantsEndRow) {
                    i2 = GroupVoipInviteAlert.this.contactsEndRow;
                } else {
                    i2 = GroupVoipInviteAlert.this.participantsEndRow;
                }
                if (item instanceof TLRPC$ChannelParticipant) {
                    i3 = ((TLRPC$ChannelParticipant) item).user_id;
                } else {
                    i3 = ((TLRPC$ChatParticipant) item).user_id;
                }
                TLRPC$User user = MessagesController.getInstance(GroupVoipInviteAlert.this.currentAccount).getUser(Integer.valueOf(i3));
                if (user != null) {
                    manageChatUserCell.setCustomImageVisible(GroupVoipInviteAlert.this.invitedUsers.contains(Integer.valueOf(user.id)));
                    if (i != i2 - 1) {
                        z = true;
                    }
                    manageChatUserCell.setData(user, (CharSequence) null, (CharSequence) null, z);
                }
            } else if (itemViewType == 1) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                if (i == GroupVoipInviteAlert.this.addNewRow) {
                    manageChatTextCell.setText(LocaleController.getString("VoipGroupCopyInviteLink", NUM), (String) null, NUM, 7, (!GroupVoipInviteAlert.this.loadingUsers || GroupVoipInviteAlert.this.firstLoaded) && GroupVoipInviteAlert.this.membersHeaderRow == -1 && !GroupVoipInviteAlert.this.participants.isEmpty());
                }
            } else if (itemViewType == 2) {
                GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                if (i == GroupVoipInviteAlert.this.membersHeaderRow) {
                    graySectionCell.setText(LocaleController.getString("ChannelOtherMembers", NUM));
                } else if (i == GroupVoipInviteAlert.this.contactsHeaderRow) {
                    graySectionCell.setText(LocaleController.getString("GroupContacts", NUM));
                }
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if ((i >= GroupVoipInviteAlert.this.participantsStartRow && i < GroupVoipInviteAlert.this.participantsEndRow) || (i >= GroupVoipInviteAlert.this.contactsStartRow && i < GroupVoipInviteAlert.this.contactsEndRow)) {
                return 0;
            }
            if (i == GroupVoipInviteAlert.this.addNewRow) {
                return 1;
            }
            if (i == GroupVoipInviteAlert.this.membersHeaderRow || i == GroupVoipInviteAlert.this.contactsHeaderRow) {
                return 2;
            }
            if (i == GroupVoipInviteAlert.this.emptyRow) {
                return 3;
            }
            if (i == GroupVoipInviteAlert.this.lastRow) {
                return 4;
            }
            return 0;
        }

        public TLObject getItem(int i) {
            if (i >= GroupVoipInviteAlert.this.participantsStartRow && i < GroupVoipInviteAlert.this.participantsEndRow) {
                return (TLObject) GroupVoipInviteAlert.this.participants.get(i - GroupVoipInviteAlert.this.participantsStartRow);
            }
            if (i < GroupVoipInviteAlert.this.contactsStartRow || i >= GroupVoipInviteAlert.this.contactsEndRow) {
                return null;
            }
            return (TLObject) GroupVoipInviteAlert.this.contacts.get(i - GroupVoipInviteAlert.this.contactsStartRow);
        }
    }
}
