package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UsersAlertBase;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.LaunchActivity;

public class InviteMembersBottomSheet extends UsersAlertBase implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int additionalHeight;
    private long chatId;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> contacts = new ArrayList<>();
    /* access modifiers changed from: private */
    public int contactsEndRow;
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public int copyLinkRow;
    /* access modifiers changed from: private */
    public AnimatorSet currentAnimation;
    /* access modifiers changed from: private */
    public GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivity.ContactsAddActivityDelegate delegate;
    /* access modifiers changed from: private */
    public InviteMembersBottomSheetDelegate dialogsDelegate;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Dialog> dialogsServerOnly;
    /* access modifiers changed from: private */
    public int emptyRow;
    boolean enterEventSent;
    /* access modifiers changed from: private */
    public final ImageView floatingButton;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> ignoreUsers;
    TLRPC.TL_chatInviteExported invite;
    /* access modifiers changed from: private */
    public int lastRow;
    boolean linkGenerating;
    /* access modifiers changed from: private */
    public int maxSize;
    /* access modifiers changed from: private */
    public int noContactsStubRow;
    private BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int scrollViewH;
    private SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public int searchAdditionalHeight;
    /* access modifiers changed from: private */
    public LongSparseArray<GroupCreateSpan> selectedContacts = new LongSparseArray<>();
    private View.OnClickListener spanClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            GroupCreateSpan span = (GroupCreateSpan) v;
            if (span.isDeleting()) {
                GroupCreateSpan unused = InviteMembersBottomSheet.this.currentDeletingSpan = null;
                InviteMembersBottomSheet.this.selectedContacts.remove(span.getUid());
                InviteMembersBottomSheet.this.spansContainer.removeSpan(span);
                InviteMembersBottomSheet.this.spansCountChanged(true);
                AndroidUtilities.updateVisibleRows(InviteMembersBottomSheet.this.listView);
                return;
            }
            if (InviteMembersBottomSheet.this.currentDeletingSpan != null) {
                InviteMembersBottomSheet.this.currentDeletingSpan.cancelDeleteAnimation();
            }
            GroupCreateSpan unused2 = InviteMembersBottomSheet.this.currentDeletingSpan = span;
            span.startDeleteAnimation();
        }
    };
    /* access modifiers changed from: private */
    public boolean spanEnter;
    /* access modifiers changed from: private */
    public final SpansContainer spansContainer;
    private ValueAnimator spansEnterAnimator;
    /* access modifiers changed from: private */
    public float spansEnterProgress = 0.0f;
    /* access modifiers changed from: private */
    public final ScrollView spansScrollView;
    private float touchSlop;
    float y;

    public interface InviteMembersBottomSheetDelegate {
        void didSelectDialogs(ArrayList<Long> arrayList);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public InviteMembersBottomSheet(android.content.Context r27, int r28, androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r29, long r30, org.telegram.ui.ActionBar.BaseFragment r32, org.telegram.ui.ActionBar.Theme.ResourcesProvider r33) {
        /*
            r26 = this;
            r7 = r26
            r8 = r27
            r9 = r30
            r11 = 0
            r12 = r28
            r13 = r33
            r7.<init>(r8, r11, r12, r13)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.contacts = r0
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r7.selectedContacts = r0
            r14 = 0
            r7.spansEnterProgress = r14
            org.telegram.ui.Components.InviteMembersBottomSheet$1 r0 = new org.telegram.ui.Components.InviteMembersBottomSheet$1
            r0.<init>()
            r7.spanClickListener = r0
            r15 = r29
            r7.ignoreUsers = r15
            r7.needSnapToTop = r11
            r6 = r32
            r7.parentFragment = r6
            r7.chatId = r9
            r26.fixNavigationBar()
            org.telegram.ui.Components.UsersAlertBase$SearchField r0 = r7.searchView
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.searchEditText
            java.lang.String r1 = "SearchForChats"
            r2 = 2131628106(0x7f0e104a, float:1.8883495E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setHint(r1)
            android.view.ViewConfiguration r16 = android.view.ViewConfiguration.get(r27)
            int r0 = r16.getScaledTouchSlop()
            float r0 = (float) r0
            r7.touchSlop = r0
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r0 = new org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter
            r0.<init>()
            r7.searchAdapter = r0
            r7.searchListViewAdapter = r0
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            org.telegram.ui.Components.InviteMembersBottomSheet$ListAdapter r1 = new org.telegram.ui.Components.InviteMembersBottomSheet$ListAdapter
            r2 = 0
            r1.<init>()
            r7.listViewAdapter = r1
            r0.setAdapter(r1)
            org.telegram.messenger.ContactsController r0 = org.telegram.messenger.ContactsController.getInstance(r28)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact> r5 = r0.contacts
            r0 = 0
        L_0x006d:
            int r1 = r5.size()
            if (r0 >= r1) goto L_0x009c
            int r1 = r7.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Object r2 = r5.get(r0)
            org.telegram.tgnet.TLRPC$TL_contact r2 = (org.telegram.tgnet.TLRPC.TL_contact) r2
            long r2 = r2.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 == 0) goto L_0x0099
            boolean r2 = r1.self
            if (r2 != 0) goto L_0x0099
            boolean r2 = r1.deleted
            if (r2 == 0) goto L_0x0094
            goto L_0x0099
        L_0x0094:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r2 = r7.contacts
            r2.add(r1)
        L_0x0099:
            int r0 = r0 + 1
            goto L_0x006d
        L_0x009c:
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r4 = new org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer
            r4.<init>(r8)
            r7.spansContainer = r4
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda8 r3 = new org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda8
            r0 = r3
            r1 = r26
            r14 = r2
            r11 = r3
            r2 = r30
            r12 = r4
            r4 = r32
            r18 = r5
            r5 = r29
            r6 = r27
            r0.<init>(r1, r2, r4, r5, r6)
            r14.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r11)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            org.telegram.ui.Components.InviteMembersBottomSheet$ItemAnimator r1 = new org.telegram.ui.Components.InviteMembersBottomSheet$ItemAnimator
            r1.<init>()
            r0.setItemAnimator(r1)
            r26.updateRows()
            org.telegram.ui.Components.InviteMembersBottomSheet$2 r0 = new org.telegram.ui.Components.InviteMembersBottomSheet$2
            r0.<init>(r8)
            r7.spansScrollView = r0
            r1 = 8
            r0.setVisibility(r1)
            r1 = 0
            r0.setClipChildren(r1)
            r0.addView(r12)
            android.view.ViewGroup r1 = r7.containerView
            r1.addView(r0)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r8)
            r7.floatingButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            r1 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r3 = "chats_actionBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "chats_actionPressedBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r3, r4)
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r3 >= r4) goto L_0x0137
            android.content.res.Resources r3 = r27.getResources()
            r5 = 2131165414(0x7var_e6, float:1.7945044E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r5)
            android.graphics.drawable.Drawable r3 = r3.mutate()
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r6, r11)
            r3.setColorFilter(r5)
            org.telegram.ui.Components.CombinedDrawable r5 = new org.telegram.ui.Components.CombinedDrawable
            r6 = 0
            r5.<init>(r3, r2, r6, r6)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r5.setIconSize(r6, r1)
            r2 = r5
        L_0x0137:
            r0.setBackgroundDrawable(r2)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "chats_actionIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r5)
            r0.setColorFilter(r1)
            r1 = 2131165412(0x7var_e4, float:1.794504E38)
            r0.setImageResource(r1)
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 1082130432(0x40800000, float:4.0)
            if (r1 < r4) goto L_0x01b6
            android.animation.StateListAnimator r1 = new android.animation.StateListAnimator
            r1.<init>()
            r5 = 1
            int[] r6 = new int[r5]
            r11 = 16842919(0x10100a7, float:2.3694026E-38)
            r12 = 0
            r6[r12] = r11
            r11 = 2
            float[] r14 = new float[r11]
            r17 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            r14[r12] = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            r14[r5] = r4
            java.lang.String r4 = "translationZ"
            android.animation.ObjectAnimator r14 = android.animation.ObjectAnimator.ofFloat(r0, r4, r14)
            r19 = r4
            r3 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r14 = r14.setDuration(r3)
            r1.addState(r6, r14)
            int[] r6 = new int[r12]
            float[] r11 = new float[r11]
            r14 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r3 = (float) r3
            r11[r12] = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            r11[r5] = r3
            r3 = r19
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r3, r11)
            r4 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r3 = r3.setDuration(r4)
            r1.addState(r6, r3)
            r0.setStateListAnimator(r1)
            org.telegram.ui.Components.InviteMembersBottomSheet$3 r3 = new org.telegram.ui.Components.InviteMembersBottomSheet$3
            r3.<init>()
            r0.setOutlineProvider(r3)
        L_0x01b6:
            org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda2
            r1.<init>(r7, r8, r9)
            r0.setOnClickListener(r1)
            r1 = 4
            r0.setVisibility(r1)
            r1 = 0
            r0.setScaleX(r1)
            r0.setScaleY(r1)
            r0.setAlpha(r1)
            r1 = 2131626801(0x7f0e0b31, float:1.8880848E38)
            java.lang.String r3 = "Next"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            android.view.ViewGroup r1 = r7.containerView
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 56
            r5 = 60
            r6 = 21
            if (r3 < r6) goto L_0x01e7
            r19 = 56
            goto L_0x01e9
        L_0x01e7:
            r19 = 60
        L_0x01e9:
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r6) goto L_0x01ee
            goto L_0x01f0
        L_0x01ee:
            r4 = 60
        L_0x01f0:
            float r3 = (float) r4
            r21 = 85
            r22 = 1096810496(0x41600000, float:14.0)
            r23 = 1096810496(0x41600000, float:14.0)
            r24 = 1096810496(0x41600000, float:14.0)
            r25 = 1096810496(0x41600000, float:14.0)
            r20 = r3
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r1.addView(r0, r3)
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1101004800(0x41a00000, float:20.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.topMargin = r1
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r1 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.leftMargin = r3
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.rightMargin = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.<init>(android.content.Context, int, androidx.collection.LongSparseArray, long, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$new$0$org-telegram-ui-Components-InviteMembersBottomSheet  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1059lambda$new$0$orgtelegramuiComponentsInviteMembersBottomSheet(long r9, org.telegram.ui.ActionBar.BaseFragment r11, androidx.collection.LongSparseArray r12, android.content.Context r13, android.view.View r14, int r15) {
        /*
            r8 = this;
            r0 = 0
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r1 = r1.getAdapter()
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r2 = r8.searchAdapter
            r3 = 1
            if (r1 != r2) goto L_0x0087
            java.util.ArrayList r1 = r2.searchResult
            int r1 = r1.size()
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r2 = r8.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r2 = r2.searchAdapterHelper
            java.util.ArrayList r2 = r2.getGlobalSearch()
            int r2 = r2.size()
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r4 = r8.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r4 = r4.searchAdapterHelper
            java.util.ArrayList r4 = r4.getLocalServerSearch()
            int r4 = r4.size()
            int r15 = r15 + -1
            if (r15 < 0) goto L_0x0044
            if (r15 >= r1) goto L_0x0044
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r5 = r8.searchAdapter
            java.util.ArrayList r5 = r5.searchResult
            java.lang.Object r5 = r5.get(r15)
            r0 = r5
            org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
            goto L_0x007c
        L_0x0044:
            if (r15 < r1) goto L_0x005e
            int r5 = r4 + r1
            if (r15 >= r5) goto L_0x005e
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r5 = r8.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r5.searchAdapterHelper
            java.util.ArrayList r5 = r5.getLocalServerSearch()
            int r6 = r15 - r1
            java.lang.Object r5 = r5.get(r6)
            r0 = r5
            org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
            goto L_0x007c
        L_0x005e:
            int r5 = r1 + r4
            if (r15 <= r5) goto L_0x007c
            int r5 = r2 + r1
            int r5 = r5 + r4
            if (r15 > r5) goto L_0x007c
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r5 = r8.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r5.searchAdapterHelper
            java.util.ArrayList r5 = r5.getGlobalSearch()
            int r6 = r15 - r1
            int r6 = r6 - r4
            int r6 = r6 - r3
            java.lang.Object r5 = r5.get(r6)
            r0 = r5
            org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
        L_0x007c:
            org.telegram.ui.Components.InviteMembersBottomSheet$InviteMembersBottomSheetDelegate r5 = r8.dialogsDelegate
            if (r5 == 0) goto L_0x0085
            org.telegram.ui.Components.UsersAlertBase$SearchField r5 = r8.searchView
            r5.closeSearch()
        L_0x0085:
            goto L_0x0103
        L_0x0087:
            int r1 = r8.copyLinkRow
            if (r15 != r1) goto L_0x00f1
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = r8.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r2.getChatFull(r9)
            r4 = 0
            if (r1 == 0) goto L_0x00c2
            java.lang.String r5 = r1.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x00c2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "https://t.me/"
            r5.append(r6)
            java.lang.String r6 = r1.username
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            goto L_0x00d0
        L_0x00c2:
            if (r2 == 0) goto L_0x00cd
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r5 = r2.exported_invite
            if (r5 == 0) goto L_0x00cd
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r5 = r2.exported_invite
            java.lang.String r4 = r5.link
            goto L_0x00d0
        L_0x00cd:
            r8.generateLink()
        L_0x00d0:
            if (r4 != 0) goto L_0x00d3
            return
        L_0x00d3:
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r6 = "clipboard"
            java.lang.Object r5 = r5.getSystemService(r6)
            android.content.ClipboardManager r5 = (android.content.ClipboardManager) r5
            java.lang.String r6 = "label"
            android.content.ClipData r6 = android.content.ClipData.newPlainText(r6, r4)
            r5.setPrimaryClip(r6)
            r8.dismiss()
            org.telegram.ui.Components.Bulletin r7 = org.telegram.ui.Components.BulletinFactory.createCopyLinkBulletin((org.telegram.ui.ActionBar.BaseFragment) r11)
            r7.show()
            goto L_0x0102
        L_0x00f1:
            int r1 = r8.contactsStartRow
            if (r15 < r1) goto L_0x0102
            int r1 = r8.contactsEndRow
            if (r15 >= r1) goto L_0x0102
            androidx.recyclerview.widget.RecyclerView$Adapter r1 = r8.listViewAdapter
            org.telegram.ui.Components.InviteMembersBottomSheet$ListAdapter r1 = (org.telegram.ui.Components.InviteMembersBottomSheet.ListAdapter) r1
            org.telegram.tgnet.TLObject r0 = r1.getObject(r15)
            goto L_0x0103
        L_0x0102:
        L_0x0103:
            if (r0 == 0) goto L_0x0162
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x010f
            r1 = r0
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            long r1 = r1.id
            goto L_0x011c
        L_0x010f:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r1 == 0) goto L_0x011a
            r1 = r0
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            long r1 = r1.id
            long r1 = -r1
            goto L_0x011c
        L_0x011a:
            r1 = 0
        L_0x011c:
            if (r12 == 0) goto L_0x0125
            int r4 = r12.indexOfKey(r1)
            if (r4 < 0) goto L_0x0125
            return
        L_0x0125:
            r4 = 0
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x015a
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r4 = r8.selectedContacts
            int r4 = r4.indexOfKey(r1)
            if (r4 < 0) goto L_0x0146
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r4 = r8.selectedContacts
            java.lang.Object r4 = r4.get(r1)
            org.telegram.ui.Components.GroupCreateSpan r4 = (org.telegram.ui.Components.GroupCreateSpan) r4
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r5 = r8.selectedContacts
            r5.remove(r1)
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r5 = r8.spansContainer
            r5.removeSpan(r4)
            goto L_0x015a
        L_0x0146:
            org.telegram.ui.Components.GroupCreateSpan r4 = new org.telegram.ui.Components.GroupCreateSpan
            r4.<init>((android.content.Context) r13, (java.lang.Object) r0)
            android.view.View$OnClickListener r5 = r8.spanClickListener
            r4.setOnClickListener(r5)
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r5 = r8.selectedContacts
            r5.put(r1, r4)
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r5 = r8.spansContainer
            r5.addSpan(r4, r3)
        L_0x015a:
            r8.spansCountChanged(r3)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.messenger.AndroidUtilities.updateVisibleRows(r3)
        L_0x0162:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.m1059lambda$new$0$orgtelegramuiComponentsInviteMembersBottomSheet(long, org.telegram.ui.ActionBar.BaseFragment, androidx.collection.LongSparseArray, android.content.Context, android.view.View, int):void");
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-InviteMembersBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1061lambda$new$2$orgtelegramuiComponentsInviteMembersBottomSheet(Context context, long chatId2, View v) {
        Activity activity;
        if ((this.dialogsDelegate != null || this.selectedContacts.size() != 0) && (activity = AndroidUtilities.findActivity(context)) != null) {
            if (this.dialogsDelegate != null) {
                ArrayList<Long> dialogs = new ArrayList<>();
                for (int a = 0; a < this.selectedContacts.size(); a++) {
                    dialogs.add(Long.valueOf(this.selectedContacts.keyAt(a)));
                }
                this.dialogsDelegate.didSelectDialogs(dialogs);
                dismiss();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
            if (this.selectedContacts.size() == 1) {
                builder.setTitle(LocaleController.getString("AddOneMemberAlertTitle", NUM));
            } else {
                builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", NUM, LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0])));
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int a2 = 0; a2 < this.selectedContacts.size(); a2++) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.selectedContacts.keyAt(a2)));
                if (user != null) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append("**");
                    stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                    stringBuilder.append("**");
                }
            }
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatId2));
            if (this.selectedContacts.size() > 5) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0]), chat.title)));
                String countString = String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())});
                int index = TextUtils.indexOf(spannableStringBuilder, countString);
                if (index >= 0) {
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), index, countString.length() + index, 33);
                }
                builder.setMessage(spannableStringBuilder);
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, stringBuilder, chat.title)));
            }
            builder.setPositiveButton(LocaleController.getString("Add", NUM), new InviteMembersBottomSheet$$ExternalSyntheticLambda1(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.create();
            builder.show();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-InviteMembersBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1060lambda$new$1$orgtelegramuiComponentsInviteMembersBottomSheet(DialogInterface dialogInterface, int i) {
        onAddToGroupDone(0);
    }

    private void onAddToGroupDone(int i) {
        ArrayList<TLRPC.User> result = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            result.add(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.selectedContacts.keyAt(a))));
        }
        GroupCreateActivity.ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(result, i);
        }
        dismiss();
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    public void setSelectedContacts(ArrayList<Long> dialogs) {
        int i;
        int width;
        int newAdditionalH;
        TLObject object;
        int a = 0;
        int N = dialogs.size();
        while (true) {
            i = 0;
            if (a >= N) {
                break;
            }
            long dialogId = dialogs.get(a).longValue();
            if (DialogObject.isChatDialog(dialogId)) {
                object = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialogId));
            } else {
                object = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId));
            }
            GroupCreateSpan span = new GroupCreateSpan(this.spansContainer.getContext(), (Object) object);
            this.spansContainer.addSpan(span, false);
            span.setOnClickListener(this.spanClickListener);
            a++;
        }
        spansCountChanged(false);
        int count = this.spansContainer.getChildCount();
        boolean isPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
        if (AndroidUtilities.isTablet() || isPortrait) {
            this.maxSize = AndroidUtilities.dp(144.0f);
        } else {
            this.maxSize = AndroidUtilities.dp(56.0f);
        }
        if (AndroidUtilities.isTablet()) {
            width = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.8f);
        } else {
            width = AndroidUtilities.displaySize.x;
            if (!isPortrait) {
                width = (int) Math.max(((float) width) * 0.8f, (float) Math.min(AndroidUtilities.dp(480.0f), AndroidUtilities.displaySize.x));
            }
        }
        int maxWidth = width - AndroidUtilities.dp(26.0f);
        int currentLineWidth = 0;
        int y2 = AndroidUtilities.dp(10.0f);
        for (int a2 = 0; a2 < count; a2++) {
            View child = this.spansContainer.getChildAt(a2);
            if (child instanceof GroupCreateSpan) {
                child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                if (child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                    y2 += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                    currentLineWidth = 0;
                }
                currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
            }
        }
        int animateToH = AndroidUtilities.dp(42.0f) + y2;
        if (this.dialogsDelegate != null) {
            newAdditionalH = this.spanEnter ? Math.min(this.maxSize, animateToH) : 0;
        } else {
            newAdditionalH = Math.max(0, Math.min(this.maxSize, animateToH) - AndroidUtilities.dp(52.0f));
        }
        int oldSearchAdditionalH = this.searchAdditionalHeight;
        if (this.selectedContacts.size() > 0) {
            i = AndroidUtilities.dp(56.0f);
        }
        this.searchAdditionalHeight = i;
        if (newAdditionalH != this.additionalHeight || oldSearchAdditionalH != i) {
            this.additionalHeight = newAdditionalH;
        }
    }

    /* access modifiers changed from: private */
    public void spansCountChanged(boolean animated) {
        final boolean enter = this.selectedContacts.size() > 0;
        if (this.spanEnter != enter) {
            ValueAnimator valueAnimator = this.spansEnterAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.spansEnterAnimator.cancel();
            }
            this.spanEnter = enter;
            if (enter) {
                this.spansScrollView.setVisibility(0);
            }
            if (animated) {
                float[] fArr = new float[2];
                fArr[0] = this.spansEnterProgress;
                fArr[1] = enter ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.spansEnterAnimator = ofFloat;
                ofFloat.addUpdateListener(new InviteMembersBottomSheet$$ExternalSyntheticLambda0(this));
                this.spansEnterAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        float unused = InviteMembersBottomSheet.this.spansEnterProgress = enter ? 1.0f : 0.0f;
                        InviteMembersBottomSheet.this.containerView.invalidate();
                        if (!enter) {
                            InviteMembersBottomSheet.this.spansScrollView.setVisibility(8);
                        }
                    }
                });
                this.spansEnterAnimator.setDuration(150);
                this.spansEnterAnimator.start();
                if (this.spanEnter || this.dialogsDelegate != null) {
                    AnimatorSet animatorSet = this.currentDoneButtonAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    this.currentDoneButtonAnimation = new AnimatorSet();
                    this.floatingButton.setVisibility(0);
                    this.currentDoneButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, new float[]{1.0f})});
                    this.currentDoneButtonAnimation.setDuration(180);
                    this.currentDoneButtonAnimation.start();
                    return;
                }
                AnimatorSet animatorSet2 = this.currentDoneButtonAnimation;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.currentDoneButtonAnimation = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, new float[]{0.0f})});
                this.currentDoneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        InviteMembersBottomSheet.this.floatingButton.setVisibility(4);
                    }
                });
                this.currentDoneButtonAnimation.setDuration(180);
                this.currentDoneButtonAnimation.start();
                return;
            }
            this.spansEnterProgress = enter ? 1.0f : 0.0f;
            this.containerView.invalidate();
            if (!enter) {
                this.spansScrollView.setVisibility(8);
            }
            AnimatorSet animatorSet4 = this.currentDoneButtonAnimation;
            if (animatorSet4 != null) {
                animatorSet4.cancel();
            }
            if (this.spanEnter || this.dialogsDelegate != null) {
                this.floatingButton.setScaleY(1.0f);
                this.floatingButton.setScaleX(1.0f);
                this.floatingButton.setAlpha(1.0f);
                this.floatingButton.setVisibility(0);
                return;
            }
            this.floatingButton.setScaleY(0.0f);
            this.floatingButton.setScaleX(0.0f);
            this.floatingButton.setAlpha(0.0f);
            this.floatingButton.setVisibility(4);
        }
    }

    /* renamed from: lambda$spansCountChanged$3$org-telegram-ui-Components-InviteMembersBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1063x2ee01541(ValueAnimator valueAnimator1) {
        this.spansEnterProgress = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.containerView.invalidate();
    }

    private void updateRows() {
        this.contactsStartRow = -1;
        this.contactsEndRow = -1;
        this.noContactsStubRow = -1;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.emptyRow = 0;
        if (this.dialogsDelegate == null) {
            this.rowCount = i + 1;
            this.copyLinkRow = i;
            if (this.contacts.size() != 0) {
                int i2 = this.rowCount;
                this.contactsStartRow = i2;
                int size = i2 + this.contacts.size();
                this.rowCount = size;
                this.contactsEndRow = size;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.noContactsStubRow = i3;
            }
        } else {
            this.copyLinkRow = -1;
            if (this.dialogsServerOnly.size() != 0) {
                int i4 = this.rowCount;
                this.contactsStartRow = i4;
                int size2 = i4 + this.dialogsServerOnly.size();
                this.rowCount = size2;
                this.contactsEndRow = size2;
            } else {
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.noContactsStubRow = i5;
            }
        }
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.lastRow = i6;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload && this.dialogsDelegate != null && this.dialogsServerOnly.isEmpty()) {
            this.dialogsServerOnly = new ArrayList<>(MessagesController.getInstance(this.currentAccount).dialogsServerOnly);
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private ListAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 2:
                    view = new View(context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + InviteMembersBottomSheet.this.additionalHeight, NUM));
                        }
                    };
                    break;
                case 3:
                    view = new GroupCreateUserCell(context, 1, 0, InviteMembersBottomSheet.this.dialogsDelegate != null);
                    break;
                case 4:
                    view = new View(context);
                    break;
                case 5:
                    AnonymousClass2 r1 = new StickerEmptyView(context, (View) null, 0) {
                        /* access modifiers changed from: protected */
                        public void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            this.stickerView.getImageReceiver().startAnimation();
                        }
                    };
                    r1.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    r1.subtitle.setVisibility(8);
                    if (InviteMembersBottomSheet.this.dialogsDelegate != null) {
                        r1.title.setText(LocaleController.getString("FilterNoChats", NUM));
                    } else {
                        r1.title.setText(LocaleController.getString("NoContacts", NUM));
                    }
                    r1.setAnimateLayoutChange(true);
                    view = r1;
                    break;
                default:
                    ManageChatTextCell manageChatTextCell = new ManageChatTextCell(context);
                    manageChatTextCell.setText(LocaleController.getString("VoipGroupCopyInviteLink", NUM), (String) null, NUM, 7, true);
                    manageChatTextCell.setColors("dialogTextBlue2", "dialogTextBlue2");
                    view = manageChatTextCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public TLObject getObject(int position) {
            if (InviteMembersBottomSheet.this.dialogsDelegate == null) {
                return (TLObject) InviteMembersBottomSheet.this.contacts.get(position - InviteMembersBottomSheet.this.contactsStartRow);
            }
            TLRPC.Dialog dialog = (TLRPC.Dialog) InviteMembersBottomSheet.this.dialogsServerOnly.get(position - InviteMembersBottomSheet.this.contactsStartRow);
            if (DialogObject.isUserDialog(dialog.id)) {
                return MessagesController.getInstance(InviteMembersBottomSheet.this.currentAccount).getUser(Long.valueOf(dialog.id));
            }
            return MessagesController.getInstance(InviteMembersBottomSheet.this.currentAccount).getChat(Long.valueOf(-dialog.id));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long oldId;
            long id;
            switch (holder.getItemViewType()) {
                case 2:
                    holder.itemView.requestLayout();
                    return;
                case 3:
                    GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                    TLObject object = getObject(position);
                    Object oldObject = cell.getObject();
                    if (oldObject instanceof TLRPC.User) {
                        oldId = ((TLRPC.User) oldObject).id;
                    } else if (oldObject instanceof TLRPC.Chat) {
                        oldId = -((TLRPC.Chat) oldObject).id;
                    } else {
                        oldId = 0;
                    }
                    boolean z = false;
                    cell.setObject(object, (CharSequence) null, (CharSequence) null, position != InviteMembersBottomSheet.this.contactsEndRow);
                    if (object instanceof TLRPC.User) {
                        id = ((TLRPC.User) object).id;
                    } else if (object instanceof TLRPC.Chat) {
                        id = -((TLRPC.Chat) object).id;
                    } else {
                        id = 0;
                    }
                    if (id == 0) {
                        return;
                    }
                    if (InviteMembersBottomSheet.this.ignoreUsers == null || InviteMembersBottomSheet.this.ignoreUsers.indexOfKey(id) < 0) {
                        boolean z2 = InviteMembersBottomSheet.this.selectedContacts.indexOfKey(id) >= 0;
                        if (oldId == id) {
                            z = true;
                        }
                        cell.setChecked(z2, z);
                        cell.setCheckBoxEnabled(true);
                        return;
                    }
                    cell.setChecked(true, false);
                    cell.setCheckBoxEnabled(false);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == InviteMembersBottomSheet.this.copyLinkRow) {
                return 1;
            }
            if (position == InviteMembersBottomSheet.this.emptyRow) {
                return 2;
            }
            if (position >= InviteMembersBottomSheet.this.contactsStartRow && position < InviteMembersBottomSheet.this.contactsEndRow) {
                return 3;
            }
            if (position == InviteMembersBottomSheet.this.lastRow) {
                return 4;
            }
            if (position == InviteMembersBottomSheet.this.noContactsStubRow) {
                return 5;
            }
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 3 || holder.getItemViewType() == 1) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return InviteMembersBottomSheet.this.rowCount;
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int currentItemsCount;
        /* access modifiers changed from: private */
        public final SearchAdapterHelper searchAdapterHelper;
        /* access modifiers changed from: private */
        public ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter() {
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda4(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1064xef2d3d33(int searchId) {
            InviteMembersBottomSheet.this.showItemsAnimated(this.currentItemsCount - 1);
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() <= 2) {
                InviteMembersBottomSheet.this.emptyView.showProgress(false, true);
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 1) {
                return true;
            }
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 1:
                    view = new GroupCreateUserCell(context, 1, 0, false);
                    break;
                case 2:
                    view = new View(context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + InviteMembersBottomSheet.this.additionalHeight + InviteMembersBottomSheet.this.searchAdditionalHeight, NUM));
                        }
                    };
                    break;
                case 4:
                    view = new View(context);
                    break;
                default:
                    view = new GroupCreateSectionCell(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v0, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v11, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v24, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x0125  */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x012b  */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x013f  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x0145  */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x0158  */
        /* JADX WARNING: Removed duplicated region for block: B:90:0x019e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                r18 = this;
                r1 = r18
                r2 = r19
                int r0 = r19.getItemViewType()
                switch(r0) {
                    case 0: goto L_0x01a3;
                    case 1: goto L_0x0014;
                    case 2: goto L_0x000d;
                    default: goto L_0x000b;
                }
            L_0x000b:
                goto L_0x01b4
            L_0x000d:
                android.view.View r0 = r2.itemView
                r0.requestLayout()
                goto L_0x01b4
            L_0x0014:
                android.view.View r0 = r2.itemView
                r3 = r0
                org.telegram.ui.Cells.GroupCreateUserCell r3 = (org.telegram.ui.Cells.GroupCreateUserCell) r3
                r4 = 0
                r5 = 0
                java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
                int r6 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r7 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r8 = r0.size()
                int r9 = r20 + -1
                r10 = 1
                if (r9 < 0) goto L_0x0046
                if (r9 >= r6) goto L_0x0046
                java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
                java.lang.Object r0 = r0.get(r9)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                r11 = r0
                goto L_0x0079
            L_0x0046:
                if (r9 < r6) goto L_0x005c
                int r0 = r8 + r6
                if (r9 >= r0) goto L_0x005c
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r11 = r9 - r6
                java.lang.Object r0 = r0.get(r11)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                r11 = r0
                goto L_0x0079
            L_0x005c:
                int r0 = r6 + r8
                if (r9 <= r0) goto L_0x0077
                int r0 = r7 + r6
                int r0 = r0 + r8
                if (r9 > r0) goto L_0x0077
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r11 = r9 - r6
                int r11 = r11 - r8
                int r11 = r11 - r10
                java.lang.Object r0 = r0.get(r11)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                r11 = r0
                goto L_0x0079
            L_0x0077:
                r0 = 0
                r11 = r0
            L_0x0079:
                if (r11 == 0) goto L_0x0119
                boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC.User
                if (r0 == 0) goto L_0x0086
                r0 = r11
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
                java.lang.String r0 = r0.username
                r12 = r0
                goto L_0x008c
            L_0x0086:
                r0 = r11
                org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
                java.lang.String r0 = r0.username
                r12 = r0
            L_0x008c:
                java.lang.String r0 = "@"
                if (r9 >= r6) goto L_0x00be
                java.util.ArrayList<java.lang.CharSequence> r13 = r1.searchResultNames
                java.lang.Object r13 = r13.get(r9)
                r5 = r13
                java.lang.CharSequence r5 = (java.lang.CharSequence) r5
                if (r5 == 0) goto L_0x011d
                boolean r13 = android.text.TextUtils.isEmpty(r12)
                if (r13 != 0) goto L_0x011d
                java.lang.String r13 = r5.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r0)
                r14.append(r12)
                java.lang.String r0 = r14.toString()
                boolean r0 = r13.startsWith(r0)
                if (r0 == 0) goto L_0x011d
                r4 = r5
                r5 = 0
                goto L_0x011d
            L_0x00be:
                if (r9 <= r6) goto L_0x0116
                boolean r13 = android.text.TextUtils.isEmpty(r12)
                if (r13 != 0) goto L_0x0116
                org.telegram.ui.Adapters.SearchAdapterHelper r13 = r1.searchAdapterHelper
                java.lang.String r13 = r13.getLastFoundUsername()
                boolean r14 = r13.startsWith(r0)
                if (r14 == 0) goto L_0x00d6
                java.lang.String r13 = r13.substring(r10)
            L_0x00d6:
                android.text.SpannableStringBuilder r14 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0111 }
                r14.<init>()     // Catch:{ Exception -> 0x0111 }
                r14.append(r0)     // Catch:{ Exception -> 0x0111 }
                r14.append(r12)     // Catch:{ Exception -> 0x0111 }
                int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r12, r13)     // Catch:{ Exception -> 0x0111 }
                r15 = r0
                r10 = -1
                if (r0 == r10) goto L_0x010d
                int r0 = r13.length()     // Catch:{ Exception -> 0x0111 }
                if (r15 != 0) goto L_0x00f2
                int r0 = r0 + 1
                goto L_0x00f4
            L_0x00f2:
                int r15 = r15 + 1
            L_0x00f4:
                android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0111 }
                java.lang.String r16 = "windowBackgroundWhiteBlueText4"
                r17 = r4
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r16)     // Catch:{ Exception -> 0x010b }
                r10.<init>(r4)     // Catch:{ Exception -> 0x010b }
                int r4 = r15 + r0
                r16 = r0
                r0 = 33
                r14.setSpan(r10, r15, r4, r0)     // Catch:{ Exception -> 0x010b }
                goto L_0x010f
            L_0x010b:
                r0 = move-exception
                goto L_0x0114
            L_0x010d:
                r17 = r4
            L_0x010f:
                r4 = r14
                goto L_0x011d
            L_0x0111:
                r0 = move-exception
                r17 = r4
            L_0x0114:
                r4 = r12
                goto L_0x011d
            L_0x0116:
                r17 = r4
                goto L_0x011b
            L_0x0119:
                r17 = r4
            L_0x011b:
                r4 = r17
            L_0x011d:
                java.lang.Object r0 = r3.getObject()
                boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC.User
                if (r10 == 0) goto L_0x012b
                r10 = r0
                org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC.User) r10
                long r12 = r10.id
                goto L_0x0138
            L_0x012b:
                boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC.Chat
                if (r10 == 0) goto L_0x0136
                r10 = r0
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC.Chat) r10
                long r12 = r10.id
                long r12 = -r12
                goto L_0x0138
            L_0x0136:
                r12 = 0
            L_0x0138:
                r3.setObject(r11, r5, r4)
                boolean r10 = r11 instanceof org.telegram.tgnet.TLRPC.User
                if (r10 == 0) goto L_0x0145
                r10 = r11
                org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC.User) r10
                long r14 = r10.id
                goto L_0x0152
            L_0x0145:
                boolean r10 = r11 instanceof org.telegram.tgnet.TLRPC.Chat
                if (r10 == 0) goto L_0x0150
                r10 = r11
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC.Chat) r10
                long r14 = r10.id
                long r14 = -r14
                goto L_0x0152
            L_0x0150:
                r14 = 0
            L_0x0152:
                r16 = 0
                int r10 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
                if (r10 == 0) goto L_0x019e
                org.telegram.ui.Components.InviteMembersBottomSheet r10 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                androidx.collection.LongSparseArray r10 = r10.ignoreUsers
                r16 = r0
                r0 = 0
                if (r10 == 0) goto L_0x0180
                org.telegram.ui.Components.InviteMembersBottomSheet r10 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                androidx.collection.LongSparseArray r10 = r10.ignoreUsers
                int r10 = r10.indexOfKey(r14)
                if (r10 < 0) goto L_0x0180
                int r10 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r10 != 0) goto L_0x0175
                r10 = 1
                goto L_0x0176
            L_0x0175:
                r10 = 0
            L_0x0176:
                r17 = r4
                r4 = 1
                r3.setChecked(r4, r10)
                r3.setCheckBoxEnabled(r0)
                goto L_0x01b6
            L_0x0180:
                r17 = r4
                org.telegram.ui.Components.InviteMembersBottomSheet r4 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                androidx.collection.LongSparseArray r4 = r4.selectedContacts
                int r4 = r4.indexOfKey(r14)
                if (r4 < 0) goto L_0x0190
                r4 = 1
                goto L_0x0191
            L_0x0190:
                r4 = 0
            L_0x0191:
                int r10 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r10 != 0) goto L_0x0196
                r0 = 1
            L_0x0196:
                r3.setChecked(r4, r0)
                r4 = 1
                r3.setCheckBoxEnabled(r4)
                goto L_0x01b6
            L_0x019e:
                r16 = r0
                r17 = r4
                goto L_0x01b6
            L_0x01a3:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.GroupCreateSectionCell r0 = (org.telegram.ui.Cells.GroupCreateSectionCell) r0
                r3 = 2131626079(0x7f0e085f, float:1.8879384E38)
                java.lang.String r4 = "GlobalSearch"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setText(r3)
            L_0x01b4:
                r9 = r20
            L_0x01b6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 2;
            }
            if (position == this.currentItemsCount - 1) {
                return 4;
            }
            if (position - 1 == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()) {
                return 0;
            }
            return 1;
        }

        public int getItemCount() {
            int count = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int count2 = count + localServerCount;
            if (globalCount != 0) {
                count2 += globalCount + 1;
            }
            int count3 = count2 + 2;
            this.currentItemsCount = count3;
            return count3;
        }

        private void updateSearchResults(ArrayList<Object> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda3(this, users, names));
        }

        /* renamed from: lambda$updateSearchResults$1$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1068x989c1db9(ArrayList users, ArrayList names) {
            this.searchRunnable = null;
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            InviteMembersBottomSheet.this.showItemsAnimated(this.currentItemsCount - 1);
            notifyDataSetChanged();
            if (!this.searchAdapterHelper.isSearchInProgress() && getItemCount() <= 2) {
                InviteMembersBottomSheet.this.emptyView.showProgress(false, true);
            }
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, false, false, false, 0, false, 0, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(query)) {
                if (InviteMembersBottomSheet.this.listView.getAdapter() != InviteMembersBottomSheet.this.searchListViewAdapter) {
                    InviteMembersBottomSheet.this.listView.setAdapter(InviteMembersBottomSheet.this.searchListViewAdapter);
                }
                InviteMembersBottomSheet.this.emptyView.showProgress(true, false);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2 inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2 = new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2(this, query);
                this.searchRunnable = inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2;
                dispatchQueue.postRunnable(inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2, 300);
            } else if (InviteMembersBottomSheet.this.listView.getAdapter() != InviteMembersBottomSheet.this.listViewAdapter) {
                InviteMembersBottomSheet.this.listView.setAdapter(InviteMembersBottomSheet.this.listViewAdapter);
            }
        }

        /* renamed from: lambda$searchDialogs$4$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1067x84772ba(String query) {
            AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda1(this, query));
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1066x2CLASSNAMEf6f9(String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, InviteMembersBottomSheet.this.dialogsDelegate != null, true, InviteMembersBottomSheet.this.dialogsDelegate != null, false, 0, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0 inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0 = new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0(this, query);
            this.searchRunnable = inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0;
            dispatchQueue.postRunnable(inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d4, code lost:
            if (r12.contains(" " + r3) != false) goto L_0x00e4;
         */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0137 A[LOOP:1: B:27:0x0096->B:52:0x0137, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00e8 A[SYNTHETIC] */
        /* renamed from: lambda$searchDialogs$2$org-telegram-ui-Components-InviteMembersBottomSheet$SearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m1065x50CLASSNAMEb38(java.lang.String r19) {
            /*
                r18 = this;
                r0 = r18
                java.lang.String r1 = r19.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r0.updateSearchResults(r2, r3)
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                if (r3 != 0) goto L_0x0032
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r3 = 0
                r4 = 1
                if (r2 == 0) goto L_0x0039
                r5 = 1
                goto L_0x003a
            L_0x0039:
                r5 = 0
            L_0x003a:
                int r5 = r5 + r4
                java.lang.String[] r5 = new java.lang.String[r5]
                r5[r3] = r1
                if (r2 == 0) goto L_0x0043
                r5[r4] = r2
            L_0x0043:
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r8 = 0
            L_0x004e:
                org.telegram.ui.Components.InviteMembersBottomSheet r9 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                java.util.ArrayList r9 = r9.contacts
                int r9 = r9.size()
                if (r8 >= r9) goto L_0x0149
                org.telegram.ui.Components.InviteMembersBottomSheet r9 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                java.util.ArrayList r9 = r9.contacts
                java.lang.Object r9 = r9.get(r8)
                org.telegram.tgnet.TLObject r9 = (org.telegram.tgnet.TLObject) r9
                boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC.User
                if (r10 == 0) goto L_0x007c
                r10 = r9
                org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC.User) r10
                java.lang.String r11 = r10.first_name
                java.lang.String r12 = r10.last_name
                java.lang.String r11 = org.telegram.messenger.ContactsController.formatName(r11, r12)
                java.lang.String r11 = r11.toLowerCase()
                java.lang.String r10 = r10.username
                goto L_0x0084
            L_0x007c:
                r10 = r9
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC.Chat) r10
                java.lang.String r11 = r10.title
                java.lang.String r12 = r10.username
                r10 = r12
            L_0x0084:
                org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r12 = r12.getTranslitString(r11)
                boolean r13 = r11.equals(r12)
                if (r13 == 0) goto L_0x0093
                r12 = 0
            L_0x0093:
                r13 = 0
                int r14 = r5.length
                r15 = 0
            L_0x0096:
                if (r15 >= r14) goto L_0x013f
                r3 = r5[r15]
                boolean r16 = r11.startsWith(r3)
                if (r16 != 0) goto L_0x00e2
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r17 = r1
                java.lang.String r1 = " "
                r4.append(r1)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r11.contains(r4)
                if (r4 != 0) goto L_0x00e4
                if (r12 == 0) goto L_0x00d7
                boolean r4 = r12.startsWith(r3)
                if (r4 != 0) goto L_0x00e4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r1)
                r4.append(r3)
                java.lang.String r1 = r4.toString()
                boolean r1 = r12.contains(r1)
                if (r1 == 0) goto L_0x00d7
                goto L_0x00e4
            L_0x00d7:
                if (r10 == 0) goto L_0x00e6
                boolean r1 = r10.startsWith(r3)
                if (r1 == 0) goto L_0x00e6
                r1 = 2
                r13 = r1
                goto L_0x00e6
            L_0x00e2:
                r17 = r1
            L_0x00e4:
                r1 = 1
                r13 = r1
            L_0x00e6:
                if (r13 == 0) goto L_0x0137
                r1 = 0
                r4 = 1
                if (r13 != r4) goto L_0x010c
                boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC.User
                if (r14 == 0) goto L_0x00ff
                r1 = r9
                org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
                java.lang.String r14 = r1.first_name
                java.lang.String r15 = r1.last_name
                java.lang.CharSequence r14 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r15, r3)
                r7.add(r14)
                goto L_0x0133
            L_0x00ff:
                r14 = r9
                org.telegram.tgnet.TLRPC$Chat r14 = (org.telegram.tgnet.TLRPC.Chat) r14
                java.lang.String r15 = r14.title
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r15, r1, r3)
                r7.add(r1)
                goto L_0x0133
            L_0x010c:
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                java.lang.String r15 = "@"
                r14.append(r15)
                r14.append(r10)
                java.lang.String r14 = r14.toString()
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r15)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r1, r4)
                r7.add(r1)
            L_0x0133:
                r6.add(r9)
                goto L_0x0141
            L_0x0137:
                int r15 = r15 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x0096
            L_0x013f:
                r17 = r1
            L_0x0141:
                int r8 = r8 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x004e
            L_0x0149:
                r0.updateSearchResults(r6, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.SearchAdapter.m1065x50CLASSNAMEb38(java.lang.String):void");
        }
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent ev, EditTextBoldCursor searchEditText) {
        if (ev.getAction() == 0) {
            this.y = (float) this.scrollOffsetY;
        } else if (ev.getAction() == 1 && Math.abs(((float) this.scrollOffsetY) - this.y) < this.touchSlop && !this.enterEventSent) {
            Activity activity = AndroidUtilities.findActivity(getContext());
            BaseFragment fragment = null;
            if (activity instanceof LaunchActivity) {
                fragment = ((LaunchActivity) activity).getActionBarLayout().fragmentsStack.get(((LaunchActivity) activity).getActionBarLayout().fragmentsStack.size() - 1);
            }
            if (fragment instanceof ChatActivity) {
                boolean keyboardVisible = ((ChatActivity) fragment).needEnterText();
                this.enterEventSent = true;
                AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda6(this, searchEditText), keyboardVisible ? 200 : 0);
                return;
            }
            this.enterEventSent = true;
            setFocusable(true);
            searchEditText.requestFocus();
            AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda4(searchEditText));
        }
    }

    /* renamed from: lambda$onSearchViewTouched$5$org-telegram-ui-Components-InviteMembersBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1062xvar_b33a7(EditTextBoldCursor searchEditText) {
        setFocusable(true);
        searchEditText.requestFocus();
        AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda3(searchEditText));
    }

    private class SpansContainer extends ViewGroup {
        boolean addAnimation;
        private int animationIndex = -1;
        /* access modifiers changed from: private */
        public boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList<>();
        /* access modifiers changed from: private */
        public View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int newAdditionalH;
            RecyclerView.ViewHolder holder;
            int count = getChildCount();
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(26.0f);
            int currentLineWidth = 0;
            int y = AndroidUtilities.dp(10.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.dp(10.0f);
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.dp(13.0f) + currentLineWidth;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (child == view) {
                            child.setTranslationX((float) (AndroidUtilities.dp(13.0f) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (view != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_X, new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, new float[]{(float) y}));
                            }
                        } else {
                            child.setTranslationX((float) x);
                            child.setTranslationY((float) y);
                        }
                    }
                    if (child != this.removingSpan) {
                        currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
            }
            int h = AndroidUtilities.dp(42.0f) + allY;
            int animateToH = AndroidUtilities.dp(42.0f) + y;
            if (InviteMembersBottomSheet.this.dialogsDelegate != null) {
                newAdditionalH = InviteMembersBottomSheet.this.spanEnter ? Math.min(InviteMembersBottomSheet.this.maxSize, animateToH) : 0;
            } else {
                newAdditionalH = Math.max(0, Math.min(InviteMembersBottomSheet.this.maxSize, animateToH) - AndroidUtilities.dp(52.0f));
            }
            int oldSearchAdditionalH = InviteMembersBottomSheet.this.searchAdditionalHeight;
            InviteMembersBottomSheet inviteMembersBottomSheet = InviteMembersBottomSheet.this;
            int unused = inviteMembersBottomSheet.searchAdditionalHeight = (inviteMembersBottomSheet.dialogsDelegate != null || InviteMembersBottomSheet.this.selectedContacts.size() <= 0) ? 0 : AndroidUtilities.dp(56.0f);
            if (!(newAdditionalH == InviteMembersBottomSheet.this.additionalHeight && oldSearchAdditionalH == InviteMembersBottomSheet.this.searchAdditionalHeight)) {
                int unused2 = InviteMembersBottomSheet.this.additionalHeight = newAdditionalH;
                if (!(InviteMembersBottomSheet.this.listView.getAdapter() == null || InviteMembersBottomSheet.this.listView.getAdapter().getItemCount() <= 0 || (holder = InviteMembersBottomSheet.this.listView.findViewHolderForAdapterPosition(0)) == null)) {
                    InviteMembersBottomSheet.this.listView.getAdapter().notifyItemChanged(0);
                    InviteMembersBottomSheet.this.layoutManager.scrollToPositionWithOffset(0, holder.itemView.getTop() - InviteMembersBottomSheet.this.listView.getPaddingTop());
                }
            }
            int newSize = Math.min(InviteMembersBottomSheet.this.maxSize, animateToH);
            if (InviteMembersBottomSheet.this.scrollViewH != newSize) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(new int[]{InviteMembersBottomSheet.this.scrollViewH, newSize});
                valueAnimator.addUpdateListener(new InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda0(this));
                this.animators.add(valueAnimator);
            }
            if (this.addAnimation && animateToH > InviteMembersBottomSheet.this.maxSize) {
                AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda1(this, animateToH));
            } else if (!this.addAnimation && InviteMembersBottomSheet.this.spansScrollView.getScrollY() + InviteMembersBottomSheet.this.spansScrollView.getMeasuredHeight() > animateToH) {
                AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda2(this, animateToH));
            }
            if (!this.animationStarted && InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.playTogether(this.animators);
                InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = InviteMembersBottomSheet.this.currentAnimation = null;
                        SpansContainer.this.requestLayout();
                    }
                });
                InviteMembersBottomSheet.this.currentAnimation.start();
                this.animationStarted = true;
            }
            if (InviteMembersBottomSheet.this.currentAnimation == null) {
                int unused3 = InviteMembersBottomSheet.this.scrollViewH = newSize;
                InviteMembersBottomSheet.this.containerView.invalidate();
            }
            setMeasuredDimension(width, Math.max(animateToH, h));
            InviteMembersBottomSheet.this.listView.setTranslationY(0.0f);
        }

        /* renamed from: lambda$onMeasure$0$org-telegram-ui-Components-InviteMembersBottomSheet$SpansContainer  reason: not valid java name */
        public /* synthetic */ void m1069x155bedad(ValueAnimator valueAnimator1) {
            int unused = InviteMembersBottomSheet.this.scrollViewH = ((Integer) valueAnimator1.getAnimatedValue()).intValue();
            InviteMembersBottomSheet.this.containerView.invalidate();
        }

        /* renamed from: lambda$onMeasure$1$org-telegram-ui-Components-InviteMembersBottomSheet$SpansContainer  reason: not valid java name */
        public /* synthetic */ void m1070xb1c9ea0c(int animateToH) {
            InviteMembersBottomSheet.this.spansScrollView.smoothScrollTo(0, animateToH - InviteMembersBottomSheet.this.maxSize);
        }

        /* renamed from: lambda$onMeasure$2$org-telegram-ui-Components-InviteMembersBottomSheet$SpansContainer  reason: not valid java name */
        public /* synthetic */ void m1071x4e37e66b(int animateToH) {
            InviteMembersBottomSheet.this.spansScrollView.smoothScrollTo(0, animateToH - InviteMembersBottomSheet.this.maxSize);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span, boolean animated) {
            this.addAnimation = true;
            InviteMembersBottomSheet.this.selectedContacts.put(span.getUid(), span);
            if (InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.setupEndValues();
                InviteMembersBottomSheet.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            if (animated) {
                AnimatorSet unused = InviteMembersBottomSheet.this.currentAnimation = new AnimatorSet();
                InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = InviteMembersBottomSheet.this.currentAnimation = null;
                        boolean unused2 = SpansContainer.this.animationStarted = false;
                    }
                });
                InviteMembersBottomSheet.this.currentAnimation.setDuration(150);
                InviteMembersBottomSheet.this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animators.clear();
                this.animators.add(ObjectAnimator.ofFloat(span, View.SCALE_X, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(span, View.SCALE_Y, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(span, View.ALPHA, new float[]{0.0f, 1.0f}));
            }
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            this.addAnimation = false;
            InviteMembersBottomSheet.this.selectedContacts.remove(span.getUid());
            span.setOnClickListener((View.OnClickListener) null);
            if (InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.setupEndValues();
                InviteMembersBottomSheet.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet unused = InviteMembersBottomSheet.this.currentAnimation = new AnimatorSet();
            InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    View unused = SpansContainer.this.removingSpan = null;
                    AnimatorSet unused2 = InviteMembersBottomSheet.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                }
            });
            InviteMembersBottomSheet.this.currentAnimation.setDuration(150);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public UsersAlertBase.ContainerView createContainerView(Context context) {
        return new UsersAlertBase.ContainerView(context) {
            float animateToEmptyViewOffset;
            float deltaOffset;
            float emptyViewOffset;
            Paint paint = new Paint();
            private VerticalPositionAutoAnimator verticalPositionAutoAnimator;

            public void onViewAdded(View child) {
                if (child == InviteMembersBottomSheet.this.floatingButton && this.verticalPositionAutoAnimator == null) {
                    this.verticalPositionAutoAnimator = VerticalPositionAutoAnimator.attach(child);
                }
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                VerticalPositionAutoAnimator verticalPositionAutoAnimator2 = this.verticalPositionAutoAnimator;
                if (verticalPositionAutoAnimator2 != null) {
                    verticalPositionAutoAnimator2.ignoreNextLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                InviteMembersBottomSheet.this.spansScrollView.setTranslationY((float) (AndroidUtilities.dp(64.0f) + (InviteMembersBottomSheet.this.scrollOffsetY - InviteMembersBottomSheet.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f)));
                float newEmptyViewOffset = (float) (InviteMembersBottomSheet.this.additionalHeight + InviteMembersBottomSheet.this.searchAdditionalHeight);
                if (InviteMembersBottomSheet.this.emptyView.getVisibility() != 0) {
                    this.emptyViewOffset = newEmptyViewOffset;
                    this.animateToEmptyViewOffset = newEmptyViewOffset;
                } else if (this.animateToEmptyViewOffset != newEmptyViewOffset) {
                    this.animateToEmptyViewOffset = newEmptyViewOffset;
                    this.deltaOffset = (newEmptyViewOffset - this.emptyViewOffset) * 0.10666667f;
                }
                float f = this.emptyViewOffset;
                float f2 = this.animateToEmptyViewOffset;
                if (f != f2) {
                    float f3 = this.deltaOffset;
                    float f4 = f + f3;
                    this.emptyViewOffset = f4;
                    if (f3 > 0.0f && f4 > f2) {
                        this.emptyViewOffset = f2;
                    } else if (f3 >= 0.0f || f4 >= f2) {
                        invalidate();
                    } else {
                        this.emptyViewOffset = f2;
                    }
                }
                InviteMembersBottomSheet.this.emptyView.setTranslationY(((float) InviteMembersBottomSheet.this.scrollOffsetY) + this.emptyViewOffset);
                super.dispatchDraw(canvas);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != InviteMembersBottomSheet.this.spansScrollView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                canvas.save();
                canvas.clipRect(0.0f, child.getY() - ((float) AndroidUtilities.dp(4.0f)), (float) getMeasuredWidth(), child.getY() + ((float) InviteMembersBottomSheet.this.scrollViewH) + 1.0f);
                canvas.drawColor(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), (int) (InviteMembersBottomSheet.this.spansEnterProgress * 255.0f)));
                this.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("divider"), (int) (InviteMembersBottomSheet.this.spansEnterProgress * 255.0f)));
                canvas.drawRect(0.0f, child.getY() + ((float) InviteMembersBottomSheet.this.scrollViewH), (float) getMeasuredWidth(), child.getY() + ((float) InviteMembersBottomSheet.this.scrollViewH) + 1.0f, this.paint);
                boolean rez = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return rez;
            }
        };
    }

    /* access modifiers changed from: protected */
    public void search(String text) {
        this.searchAdapter.searchDialogs(text);
    }

    public void setDelegate(GroupCreateActivity.ContactsAddActivityDelegate contactsAddActivityDelegate) {
        this.delegate = contactsAddActivityDelegate;
    }

    public void setDelegate(InviteMembersBottomSheetDelegate inviteMembersBottomSheetDelegate, ArrayList<Long> arrayList) {
        this.dialogsDelegate = inviteMembersBottomSheetDelegate;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        this.dialogsServerOnly = new ArrayList<>(MessagesController.getInstance(this.currentAccount).dialogsServerOnly);
        updateRows();
    }

    private class ItemAnimator extends DefaultItemAnimator {
        public ItemAnimator() {
            this.translationInterpolator = CubicBezierInterpolator.DEFAULT;
            setMoveDuration(150);
            setAddDuration(150);
            setRemoveDuration(150);
            InviteMembersBottomSheet.this.setShowWithoutAnimation(false);
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        if (this.enterEventSent) {
            Activity activity = AndroidUtilities.findActivity(getContext());
            if (activity instanceof LaunchActivity) {
                BaseFragment fragment = ((LaunchActivity) activity).getActionBarLayout().fragmentsStack.get(((LaunchActivity) activity).getActionBarLayout().fragmentsStack.size() - 1);
                if (fragment instanceof ChatActivity) {
                    ((ChatActivity) fragment).onEditTextDialogClose(true, true);
                }
            }
        }
    }

    private void generateLink() {
        if (!this.linkGenerating) {
            this.linkGenerating = true;
            TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
            req.legacy_revoke_permanent = true;
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new InviteMembersBottomSheet$$ExternalSyntheticLambda7(this));
        }
    }

    /* renamed from: lambda$generateLink$8$org-telegram-ui-Components-InviteMembersBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1058xedvar_(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda5(this, error, response));
    }

    /* renamed from: lambda$generateLink$7$org-telegram-ui-Components-InviteMembersBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1057x60b551c0(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) response;
            TLRPC.ChatFull chatInfo = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatInfo != null) {
                chatInfo.exported_invite = this.invite;
            }
            if (this.invite.link != null) {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                BulletinFactory.createCopyLinkBulletin(this.parentFragment).show();
                dismiss();
            } else {
                return;
            }
        }
        this.linkGenerating = false;
    }
}
