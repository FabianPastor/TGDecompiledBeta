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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$User;
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
    public ArrayList<TLRPC$Dialog> dialogsServerOnly;
    /* access modifiers changed from: private */
    public int emptyRow;
    boolean enterEventSent;
    /* access modifiers changed from: private */
    public final ImageView floatingButton;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> ignoreUsers;
    TLRPC$TL_chatInviteExported invite;
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
        public void onClick(View view) {
            GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
            if (groupCreateSpan.isDeleting()) {
                GroupCreateSpan unused = InviteMembersBottomSheet.this.currentDeletingSpan = null;
                InviteMembersBottomSheet.this.selectedContacts.remove(groupCreateSpan.getUid());
                InviteMembersBottomSheet.this.spansContainer.removeSpan(groupCreateSpan);
                InviteMembersBottomSheet.this.spansCountChanged(true);
                AndroidUtilities.updateVisibleRows(InviteMembersBottomSheet.this.listView);
                return;
            }
            if (InviteMembersBottomSheet.this.currentDeletingSpan != null) {
                InviteMembersBottomSheet.this.currentDeletingSpan.cancelDeleteAnimation();
            }
            GroupCreateSpan unused2 = InviteMembersBottomSheet.this.currentDeletingSpan = groupCreateSpan;
            groupCreateSpan.startDeleteAnimation();
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
    public InviteMembersBottomSheet(android.content.Context r17, int r18, androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r19, long r20, org.telegram.ui.ActionBar.BaseFragment r22, org.telegram.ui.ActionBar.Theme.ResourcesProvider r23) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r20
            r11 = 0
            r0 = r18
            r1 = r23
            r7.<init>(r8, r11, r0, r1)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.contacts = r1
            androidx.collection.LongSparseArray r1 = new androidx.collection.LongSparseArray
            r1.<init>()
            r7.selectedContacts = r1
            r12 = 0
            r7.spansEnterProgress = r12
            org.telegram.ui.Components.InviteMembersBottomSheet$1 r1 = new org.telegram.ui.Components.InviteMembersBottomSheet$1
            r1.<init>()
            r7.spanClickListener = r1
            r5 = r19
            r7.ignoreUsers = r5
            r7.needSnapToTop = r11
            r4 = r22
            r7.parentFragment = r4
            r7.chatId = r9
            org.telegram.ui.Components.UsersAlertBase$SearchField r1 = r7.searchView
            org.telegram.ui.Components.EditTextBoldCursor r1 = r1.searchEditText
            java.lang.String r2 = "SearchForChats"
            r3 = 2131627884(0x7f0e0f6c, float:1.8883045E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setHint(r2)
            android.view.ViewConfiguration r1 = android.view.ViewConfiguration.get(r17)
            int r1 = r1.getScaledTouchSlop()
            float r1 = (float) r1
            r7.touchSlop = r1
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r1 = new org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter
            r1.<init>()
            r7.searchAdapter = r1
            r7.searchListViewAdapter = r1
            org.telegram.ui.Components.RecyclerListView r1 = r7.listView
            org.telegram.ui.Components.InviteMembersBottomSheet$ListAdapter r2 = new org.telegram.ui.Components.InviteMembersBottomSheet$ListAdapter
            r3 = 0
            r2.<init>()
            r7.listViewAdapter = r2
            r1.setAdapter(r2)
            org.telegram.messenger.ContactsController r0 = org.telegram.messenger.ContactsController.getInstance(r18)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_contact> r0 = r0.contacts
            r1 = 0
        L_0x006a:
            int r2 = r0.size()
            if (r1 >= r2) goto L_0x0099
            int r2 = r7.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Object r3 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_contact r3 = (org.telegram.tgnet.TLRPC$TL_contact) r3
            long r13 = r3.user_id
            java.lang.Long r3 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            if (r2 == 0) goto L_0x0096
            boolean r3 = r2.self
            if (r3 != 0) goto L_0x0096
            boolean r3 = r2.deleted
            if (r3 == 0) goto L_0x0091
            goto L_0x0096
        L_0x0091:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r7.contacts
            r3.add(r2)
        L_0x0096:
            int r1 = r1 + 1
            goto L_0x006a
        L_0x0099:
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r13 = new org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer
            r13.<init>(r8)
            r7.spansContainer = r13
            org.telegram.ui.Components.RecyclerListView r14 = r7.listView
            org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda8 r15 = new org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda8
            r0 = r15
            r1 = r16
            r2 = r20
            r4 = r22
            r5 = r19
            r6 = r17
            r0.<init>(r1, r2, r4, r5, r6)
            r14.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r15)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            org.telegram.ui.Components.InviteMembersBottomSheet$ItemAnimator r1 = new org.telegram.ui.Components.InviteMembersBottomSheet$ItemAnimator
            r1.<init>(r7)
            r0.setItemAnimator(r1)
            r16.updateRows()
            org.telegram.ui.Components.InviteMembersBottomSheet$2 r0 = new org.telegram.ui.Components.InviteMembersBottomSheet$2
            r0.<init>(r8)
            r7.spansScrollView = r0
            r1 = 8
            r0.setVisibility(r1)
            r0.setClipChildren(r11)
            r0.addView(r13)
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
            if (r3 >= r4) goto L_0x012d
            android.content.res.Resources r5 = r17.getResources()
            r6 = 2131165445(0x7var_, float:1.7945107E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            r13 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r13, r14)
            r5.setColorFilter(r6)
            org.telegram.ui.Components.CombinedDrawable r6 = new org.telegram.ui.Components.CombinedDrawable
            r6.<init>(r5, r2, r11, r11)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r6.setIconSize(r2, r1)
            r2 = r6
        L_0x012d:
            r0.setBackgroundDrawable(r2)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "chats_actionIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r5)
            r0.setColorFilter(r1)
            r1 = 2131165442(0x7var_, float:1.7945101E38)
            r0.setImageResource(r1)
            r1 = 1082130432(0x40800000, float:4.0)
            if (r3 < r4) goto L_0x01a4
            android.animation.StateListAnimator r2 = new android.animation.StateListAnimator
            r2.<init>()
            r5 = 1
            int[] r6 = new int[r5]
            r13 = 16842919(0x10100a7, float:2.3694026E-38)
            r6[r11] = r13
            r13 = 2
            float[] r14 = new float[r13]
            r15 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r4 = (float) r4
            r14[r11] = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r4
            r14[r5] = r4
            java.lang.String r4 = "translationZ"
            android.animation.ObjectAnimator r14 = android.animation.ObjectAnimator.ofFloat(r0, r4, r14)
            r12 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r14 = r14.setDuration(r12)
            r2.addState(r6, r14)
            int[] r6 = new int[r11]
            r14 = 2
            float[] r14 = new float[r14]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r12 = (float) r12
            r14[r11] = r12
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r11 = (float) r11
            r14[r5] = r11
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r0, r4, r14)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r4.setDuration(r11)
            r2.addState(r6, r4)
            r0.setStateListAnimator(r2)
            org.telegram.ui.Components.InviteMembersBottomSheet$3 r2 = new org.telegram.ui.Components.InviteMembersBottomSheet$3
            r2.<init>(r7)
            r0.setOutlineProvider(r2)
        L_0x01a4:
            org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.InviteMembersBottomSheet$$ExternalSyntheticLambda2
            r2.<init>(r7, r8, r9)
            r0.setOnClickListener(r2)
            r2 = 4
            r0.setVisibility(r2)
            r2 = 0
            r0.setScaleX(r2)
            r0.setScaleY(r2)
            r0.setAlpha(r2)
            r2 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r4 = "Next"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setContentDescription(r2)
            android.view.ViewGroup r2 = r7.containerView
            r4 = 56
            r5 = 60
            r6 = 21
            if (r3 < r6) goto L_0x01d3
            r8 = 56
            goto L_0x01d5
        L_0x01d3:
            r8 = 60
        L_0x01d5:
            if (r3 < r6) goto L_0x01d8
            goto L_0x01da
        L_0x01d8:
            r4 = 60
        L_0x01da:
            float r3 = (float) r4
            r4 = 85
            r5 = 1096810496(0x41600000, float:14.0)
            r6 = 1096810496(0x41600000, float:14.0)
            r9 = 1096810496(0x41600000, float:14.0)
            r10 = 1096810496(0x41600000, float:14.0)
            r17 = r8
            r18 = r3
            r19 = r4
            r20 = r5
            r21 = r6
            r22 = r9
            r23 = r10
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r0, r3)
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.topMargin = r2
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.leftMargin = r2
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.rightMargin = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.<init>(android.content.Context, int, androidx.collection.LongSparseArray, long, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v37, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: org.telegram.tgnet.TLObject} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$0(long r4, org.telegram.ui.ActionBar.BaseFragment r6, androidx.collection.LongSparseArray r7, android.content.Context r8, android.view.View r9, int r10) {
        /*
            r3 = this;
            org.telegram.ui.Components.RecyclerListView r9 = r3.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r9 = r9.getAdapter()
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r0 = r3.searchAdapter
            r1 = 0
            r2 = 1
            if (r9 != r0) goto L_0x0084
            java.util.ArrayList r4 = r0.searchResult
            int r4 = r4.size()
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r5 = r3.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r5.searchAdapterHelper
            java.util.ArrayList r5 = r5.getGlobalSearch()
            int r5 = r5.size()
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r6 = r3.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r6 = r6.searchAdapterHelper
            java.util.ArrayList r6 = r6.getLocalServerSearch()
            int r6 = r6.size()
            int r10 = r10 + -1
            if (r10 < 0) goto L_0x0044
            if (r10 >= r4) goto L_0x0044
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r4 = r3.searchAdapter
            java.util.ArrayList r4 = r4.searchResult
            java.lang.Object r4 = r4.get(r10)
            r1 = r4
            org.telegram.tgnet.TLObject r1 = (org.telegram.tgnet.TLObject) r1
            goto L_0x0079
        L_0x0044:
            if (r10 < r4) goto L_0x005d
            int r9 = r6 + r4
            if (r10 >= r9) goto L_0x005d
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r5 = r3.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r5.searchAdapterHelper
            java.util.ArrayList r5 = r5.getLocalServerSearch()
            int r10 = r10 - r4
            java.lang.Object r4 = r5.get(r10)
            r1 = r4
            org.telegram.tgnet.TLObject r1 = (org.telegram.tgnet.TLObject) r1
            goto L_0x0079
        L_0x005d:
            int r9 = r4 + r6
            if (r10 <= r9) goto L_0x0079
            int r5 = r5 + r4
            int r5 = r5 + r6
            if (r10 > r5) goto L_0x0079
            org.telegram.ui.Components.InviteMembersBottomSheet$SearchAdapter r5 = r3.searchAdapter
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r5.searchAdapterHelper
            java.util.ArrayList r5 = r5.getGlobalSearch()
            int r10 = r10 - r4
            int r10 = r10 - r6
            int r10 = r10 - r2
            java.lang.Object r4 = r5.get(r10)
            r1 = r4
            org.telegram.tgnet.TLObject r1 = (org.telegram.tgnet.TLObject) r1
        L_0x0079:
            org.telegram.ui.Components.InviteMembersBottomSheet$InviteMembersBottomSheetDelegate r4 = r3.dialogsDelegate
            if (r4 == 0) goto L_0x00fc
            org.telegram.ui.Components.UsersAlertBase$SearchField r4 = r3.searchView
            r4.closeSearch()
            goto L_0x00fc
        L_0x0084:
            int r9 = r3.copyLinkRow
            if (r10 != r9) goto L_0x00ec
            int r9 = r3.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            java.lang.Long r10 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            int r10 = r3.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.getChatFull(r4)
            if (r9 == 0) goto L_0x00be
            java.lang.String r5 = r9.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x00be
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "https://t.me/"
            r4.append(r5)
            java.lang.String r5 = r9.username
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            goto L_0x00cb
        L_0x00be:
            if (r4 == 0) goto L_0x00c7
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r4 = r4.exported_invite
            if (r4 == 0) goto L_0x00c7
            java.lang.String r4 = r4.link
            goto L_0x00cb
        L_0x00c7:
            r3.generateLink()
            r4 = r1
        L_0x00cb:
            if (r4 != 0) goto L_0x00ce
            return
        L_0x00ce:
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r9 = "clipboard"
            java.lang.Object r5 = r5.getSystemService(r9)
            android.content.ClipboardManager r5 = (android.content.ClipboardManager) r5
            java.lang.String r9 = "label"
            android.content.ClipData r4 = android.content.ClipData.newPlainText(r9, r4)
            r5.setPrimaryClip(r4)
            r3.dismiss()
            org.telegram.ui.Components.Bulletin r4 = org.telegram.ui.Components.BulletinFactory.createCopyLinkBulletin((org.telegram.ui.ActionBar.BaseFragment) r6)
            r4.show()
            goto L_0x00fc
        L_0x00ec:
            int r4 = r3.contactsStartRow
            if (r10 < r4) goto L_0x00fc
            int r4 = r3.contactsEndRow
            if (r10 >= r4) goto L_0x00fc
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r3.listViewAdapter
            org.telegram.ui.Components.InviteMembersBottomSheet$ListAdapter r4 = (org.telegram.ui.Components.InviteMembersBottomSheet.ListAdapter) r4
            org.telegram.tgnet.TLObject r1 = r4.getObject(r10)
        L_0x00fc:
            if (r1 == 0) goto L_0x015a
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$User
            r5 = 0
            if (r4 == 0) goto L_0x010a
            r4 = r1
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            long r9 = r4.id
            goto L_0x0116
        L_0x010a:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r4 == 0) goto L_0x0115
            r4 = r1
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC$Chat) r4
            long r9 = r4.id
            long r9 = -r9
            goto L_0x0116
        L_0x0115:
            r9 = r5
        L_0x0116:
            if (r7 == 0) goto L_0x011f
            int r4 = r7.indexOfKey(r9)
            if (r4 < 0) goto L_0x011f
            return
        L_0x011f:
            int r4 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r4 == 0) goto L_0x0152
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r4 = r3.selectedContacts
            int r4 = r4.indexOfKey(r9)
            if (r4 < 0) goto L_0x013e
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r4 = r3.selectedContacts
            java.lang.Object r4 = r4.get(r9)
            org.telegram.ui.Components.GroupCreateSpan r4 = (org.telegram.ui.Components.GroupCreateSpan) r4
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r5 = r3.selectedContacts
            r5.remove(r9)
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r5 = r3.spansContainer
            r5.removeSpan(r4)
            goto L_0x0152
        L_0x013e:
            org.telegram.ui.Components.GroupCreateSpan r4 = new org.telegram.ui.Components.GroupCreateSpan
            r4.<init>((android.content.Context) r8, (java.lang.Object) r1)
            android.view.View$OnClickListener r5 = r3.spanClickListener
            r4.setOnClickListener(r5)
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r5 = r3.selectedContacts
            r5.put(r9, r4)
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r5 = r3.spansContainer
            r5.addSpan(r4, r2)
        L_0x0152:
            r3.spansCountChanged(r2)
            org.telegram.ui.Components.RecyclerListView r4 = r3.listView
            org.telegram.messenger.AndroidUtilities.updateVisibleRows(r4)
        L_0x015a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.lambda$new$0(long, org.telegram.ui.ActionBar.BaseFragment, androidx.collection.LongSparseArray, android.content.Context, android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(Context context, long j, View view) {
        Activity findActivity;
        if ((this.dialogsDelegate != null || this.selectedContacts.size() != 0) && (findActivity = AndroidUtilities.findActivity(context)) != null) {
            if (this.dialogsDelegate != null) {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < this.selectedContacts.size(); i++) {
                    arrayList.add(Long.valueOf(this.selectedContacts.keyAt(i)));
                }
                this.dialogsDelegate.didSelectDialogs(arrayList);
                dismiss();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) findActivity);
            if (this.selectedContacts.size() == 1) {
                builder.setTitle(LocaleController.getString("AddOneMemberAlertTitle", NUM));
            } else {
                builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", NUM, LocaleController.formatPluralString("Members", this.selectedContacts.size())));
            }
            StringBuilder sb = new StringBuilder();
            for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.selectedContacts.keyAt(i2)));
                if (user != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("**");
                    sb.append(ContactsController.formatName(user.first_name, user.last_name));
                    sb.append("**");
                }
            }
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
            if (this.selectedContacts.size() > 5) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, LocaleController.formatPluralString("Members", this.selectedContacts.size()), chat.title)));
                String format = String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())});
                int indexOf = TextUtils.indexOf(spannableStringBuilder, format);
                if (indexOf >= 0) {
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), indexOf, format.length() + indexOf, 33);
                }
                builder.setMessage(spannableStringBuilder);
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, sb, chat.title)));
            }
            builder.setPositiveButton(LocaleController.getString("Add", NUM), new InviteMembersBottomSheet$$ExternalSyntheticLambda1(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.create();
            builder.show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DialogInterface dialogInterface, int i) {
        onAddToGroupDone(0);
    }

    private void onAddToGroupDone(int i) {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
            arrayList.add(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.selectedContacts.keyAt(i2))));
        }
        GroupCreateActivity.ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(arrayList, i);
        }
        dismiss();
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c8  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x013e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSelectedContacts(java.util.ArrayList<java.lang.Long> r12) {
        /*
            r11 = this;
            int r0 = r12.size()
            r1 = 0
            r2 = 0
        L_0x0006:
            if (r2 >= r0) goto L_0x004e
            java.lang.Object r3 = r12.get(r2)
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            boolean r5 = org.telegram.messenger.DialogObject.isChatDialog(r3)
            if (r5 == 0) goto L_0x0028
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r5.getChat(r3)
            goto L_0x0036
        L_0x0028:
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
        L_0x0036:
            org.telegram.ui.Components.GroupCreateSpan r4 = new org.telegram.ui.Components.GroupCreateSpan
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r5 = r11.spansContainer
            android.content.Context r5 = r5.getContext()
            r4.<init>((android.content.Context) r5, (java.lang.Object) r3)
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r3 = r11.spansContainer
            r3.addSpan(r4, r1)
            android.view.View$OnClickListener r3 = r11.spanClickListener
            r4.setOnClickListener(r3)
            int r2 = r2 + 1
            goto L_0x0006
        L_0x004e:
            r11.spansCountChanged(r1)
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r12 = r11.spansContainer
            int r12 = r12.getChildCount()
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r0.x
            int r0 = r0.y
            if (r2 >= r0) goto L_0x0061
            r0 = 1
            goto L_0x0062
        L_0x0061:
            r0 = 0
        L_0x0062:
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            r3 = 1113587712(0x42600000, float:56.0)
            if (r2 != 0) goto L_0x0074
            if (r0 == 0) goto L_0x006d
            goto L_0x0074
        L_0x006d:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r11.maxSize = r2
            goto L_0x007c
        L_0x0074:
            r2 = 1125122048(0x43100000, float:144.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r11.maxSize = r2
        L_0x007c:
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            r4 = 1061997773(0x3f4ccccd, float:0.8)
            if (r2 == 0) goto L_0x0094
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r0.x
            int r0 = r0.y
            int r0 = java.lang.Math.min(r2, r0)
            float r0 = (float) r0
            float r0 = r0 * r4
        L_0x0092:
            int r0 = (int) r0
            goto L_0x00b6
        L_0x0094:
            if (r0 == 0) goto L_0x009b
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            goto L_0x00b6
        L_0x009b:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            float r0 = (float) r0
            float r0 = r0 * r4
            r2 = 1139802112(0x43var_, float:480.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            int r2 = java.lang.Math.min(r2, r4)
            float r2 = (float) r2
            float r0 = java.lang.Math.max(r0, r2)
            goto L_0x0092
        L_0x00b6:
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r0 - r2
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 0
            r6 = 0
        L_0x00c6:
            if (r5 >= r12) goto L_0x010b
            org.telegram.ui.Components.InviteMembersBottomSheet$SpansContainer r7 = r11.spansContainer
            android.view.View r7 = r7.getChildAt(r5)
            boolean r8 = r7 instanceof org.telegram.ui.Components.GroupCreateSpan
            if (r8 != 0) goto L_0x00d3
            goto L_0x0108
        L_0x00d3:
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r8)
            r9 = 1107296256(0x42000000, float:32.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 1073741824(0x40000000, float:2.0)
            int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r10)
            r7.measure(r8, r9)
            int r8 = r7.getMeasuredWidth()
            int r8 = r8 + r6
            if (r8 <= r2) goto L_0x00fc
            int r6 = r7.getMeasuredHeight()
            r8 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 + r8
            int r4 = r4 + r6
            r6 = 0
        L_0x00fc:
            int r7 = r7.getMeasuredWidth()
            r8 = 1091567616(0x41100000, float:9.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 + r8
            int r6 = r6 + r7
        L_0x0108:
            int r5 = r5 + 1
            goto L_0x00c6
        L_0x010b:
            r12 = 1109917696(0x42280000, float:42.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r4 = r4 + r12
            org.telegram.ui.Components.InviteMembersBottomSheet$InviteMembersBottomSheetDelegate r12 = r11.dialogsDelegate
            if (r12 == 0) goto L_0x0123
            boolean r12 = r11.spanEnter
            if (r12 == 0) goto L_0x0121
            int r12 = r11.maxSize
            int r12 = java.lang.Math.min(r12, r4)
            goto L_0x0134
        L_0x0121:
            r12 = 0
            goto L_0x0134
        L_0x0123:
            int r12 = r11.maxSize
            int r12 = java.lang.Math.min(r12, r4)
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r12 = r12 - r0
            int r12 = java.lang.Math.max(r1, r12)
        L_0x0134:
            int r0 = r11.searchAdditionalHeight
            androidx.collection.LongSparseArray<org.telegram.ui.Components.GroupCreateSpan> r2 = r11.selectedContacts
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x0142
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x0142:
            r11.searchAdditionalHeight = r1
            int r2 = r11.additionalHeight
            if (r12 != r2) goto L_0x014a
            if (r0 == r1) goto L_0x014c
        L_0x014a:
            r11.additionalHeight = r12
        L_0x014c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.setSelectedContacts(java.util.ArrayList):void");
    }

    /* access modifiers changed from: private */
    public void spansCountChanged(boolean z) {
        final boolean z2 = this.selectedContacts.size() > 0;
        if (this.spanEnter != z2) {
            ValueAnimator valueAnimator = this.spansEnterAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.spansEnterAnimator.cancel();
            }
            this.spanEnter = z2;
            if (z2) {
                this.spansScrollView.setVisibility(0);
            }
            if (z) {
                float[] fArr = new float[2];
                fArr[0] = this.spansEnterProgress;
                fArr[1] = z2 ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.spansEnterAnimator = ofFloat;
                ofFloat.addUpdateListener(new InviteMembersBottomSheet$$ExternalSyntheticLambda0(this));
                this.spansEnterAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        float unused = InviteMembersBottomSheet.this.spansEnterProgress = z2 ? 1.0f : 0.0f;
                        InviteMembersBottomSheet.this.containerView.invalidate();
                        if (!z2) {
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
                    public void onAnimationEnd(Animator animator) {
                        InviteMembersBottomSheet.this.floatingButton.setVisibility(4);
                    }
                });
                this.currentDoneButtonAnimation.setDuration(180);
                this.currentDoneButtonAnimation.start();
                return;
            }
            this.spansEnterProgress = z2 ? 1.0f : 0.0f;
            this.containerView.invalidate();
            if (!z2) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$spansCountChanged$3(ValueAnimator valueAnimator) {
        this.spansEnterProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload && this.dialogsDelegate != null && this.dialogsServerOnly.isEmpty()) {
            this.dialogsServerOnly = new ArrayList<>(MessagesController.getInstance(this.currentAccount).dialogsServerOnly);
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private ListAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatTextCell manageChatTextCell;
            Context context = viewGroup.getContext();
            if (i == 2) {
                manageChatTextCell = new View(context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + InviteMembersBottomSheet.this.additionalHeight, NUM));
                    }
                };
            } else if (i == 3) {
                manageChatTextCell = new GroupCreateUserCell(context, 1, 0, InviteMembersBottomSheet.this.dialogsDelegate != null);
            } else if (i == 4) {
                manageChatTextCell = new View(context);
            } else if (i != 5) {
                ManageChatTextCell manageChatTextCell2 = new ManageChatTextCell(context);
                manageChatTextCell2.setText(LocaleController.getString("VoipGroupCopyInviteLink", NUM), (String) null, NUM, 7, true);
                manageChatTextCell2.setColors("dialogTextBlue2", "dialogTextBlue2");
                manageChatTextCell = manageChatTextCell2;
            } else {
                AnonymousClass2 r11 = new StickerEmptyView(this, context, (View) null, 0) {
                    /* access modifiers changed from: protected */
                    public void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        this.stickerView.getImageReceiver().startAnimation();
                    }
                };
                r11.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                r11.subtitle.setVisibility(8);
                if (InviteMembersBottomSheet.this.dialogsDelegate != null) {
                    r11.title.setText(LocaleController.getString("FilterNoChats", NUM));
                } else {
                    r11.title.setText(LocaleController.getString("NoContacts", NUM));
                }
                r11.setAnimateLayoutChange(true);
                manageChatTextCell = r11;
            }
            return new RecyclerListView.Holder(manageChatTextCell);
        }

        public TLObject getObject(int i) {
            if (InviteMembersBottomSheet.this.dialogsDelegate == null) {
                return (TLObject) InviteMembersBottomSheet.this.contacts.get(i - InviteMembersBottomSheet.this.contactsStartRow);
            }
            TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) InviteMembersBottomSheet.this.dialogsServerOnly.get(i - InviteMembersBottomSheet.this.contactsStartRow);
            if (DialogObject.isUserDialog(tLRPC$Dialog.id)) {
                return MessagesController.getInstance(InviteMembersBottomSheet.this.currentAccount).getUser(Long.valueOf(tLRPC$Dialog.id));
            }
            return MessagesController.getInstance(InviteMembersBottomSheet.this.currentAccount).getChat(Long.valueOf(-tLRPC$Dialog.id));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            long j2;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 2) {
                viewHolder.itemView.requestLayout();
            } else if (itemViewType == 3) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                TLObject object = getObject(i);
                Object object2 = groupCreateUserCell.getObject();
                if (object2 instanceof TLRPC$User) {
                    j = ((TLRPC$User) object2).id;
                } else {
                    j = object2 instanceof TLRPC$Chat ? -((TLRPC$Chat) object2).id : 0;
                }
                boolean z = false;
                groupCreateUserCell.setObject(object, (CharSequence) null, (CharSequence) null, i != InviteMembersBottomSheet.this.contactsEndRow);
                if (object instanceof TLRPC$User) {
                    j2 = ((TLRPC$User) object).id;
                } else {
                    j2 = object instanceof TLRPC$Chat ? -((TLRPC$Chat) object).id : 0;
                }
                if (j2 == 0) {
                    return;
                }
                if (InviteMembersBottomSheet.this.ignoreUsers == null || InviteMembersBottomSheet.this.ignoreUsers.indexOfKey(j2) < 0) {
                    boolean z2 = InviteMembersBottomSheet.this.selectedContacts.indexOfKey(j2) >= 0;
                    if (j == j2) {
                        z = true;
                    }
                    groupCreateUserCell.setChecked(z2, z);
                    groupCreateUserCell.setCheckBoxEnabled(true);
                    return;
                }
                groupCreateUserCell.setChecked(true, false);
                groupCreateUserCell.setCheckBoxEnabled(false);
            }
        }

        public int getItemViewType(int i) {
            if (i == InviteMembersBottomSheet.this.copyLinkRow) {
                return 1;
            }
            if (i == InviteMembersBottomSheet.this.emptyRow) {
                return 2;
            }
            if (i >= InviteMembersBottomSheet.this.contactsStartRow && i < InviteMembersBottomSheet.this.contactsEndRow) {
                return 3;
            }
            if (i == InviteMembersBottomSheet.this.lastRow) {
                return 4;
            }
            return i == InviteMembersBottomSheet.this.noContactsStubRow ? 5 : 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 1;
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            InviteMembersBottomSheet.this.showItemsAnimated(this.currentItemsCount - 1);
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() <= 2) {
                InviteMembersBottomSheet.this.emptyView.showProgress(false, true);
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            Context context = viewGroup.getContext();
            if (i == 1) {
                view = new GroupCreateUserCell(context, 1, 0, false);
            } else if (i != 2) {
                view = i != 4 ? new GroupCreateSectionCell(context) : new View(context);
            } else {
                view = new View(context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + InviteMembersBottomSheet.this.additionalHeight + InviteMembersBottomSheet.this.searchAdditionalHeight, NUM));
                    }
                };
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00ae, code lost:
            if (r13.toString().startsWith("@" + r3) != false) goto L_0x00ff;
         */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0109  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x010e  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x0120  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x0125  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x0134  */
        /* JADX WARNING: Removed duplicated region for block: B:89:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r12, int r13) {
            /*
                r11 = this;
                int r0 = r12.getItemViewType()
                if (r0 == 0) goto L_0x0172
                r1 = 1
                if (r0 == r1) goto L_0x0015
                r13 = 2
                if (r0 == r13) goto L_0x000e
                goto L_0x0182
            L_0x000e:
                android.view.View r12 = r12.itemView
                r12.requestLayout()
                goto L_0x0182
            L_0x0015:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.GroupCreateUserCell r12 = (org.telegram.ui.Cells.GroupCreateUserCell) r12
                java.util.ArrayList<java.lang.Object> r0 = r11.searchResult
                int r0 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r2 = r11.searchAdapterHelper
                java.util.ArrayList r2 = r2.getGlobalSearch()
                int r2 = r2.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r3 = r11.searchAdapterHelper
                java.util.ArrayList r3 = r3.getLocalServerSearch()
                int r3 = r3.size()
                r4 = -1
                int r13 = r13 + r4
                r5 = 0
                if (r13 < 0) goto L_0x0043
                if (r13 >= r0) goto L_0x0043
                java.util.ArrayList<java.lang.Object> r2 = r11.searchResult
                java.lang.Object r2 = r2.get(r13)
                org.telegram.tgnet.TLObject r2 = (org.telegram.tgnet.TLObject) r2
                goto L_0x0072
            L_0x0043:
                if (r13 < r0) goto L_0x0058
                int r6 = r3 + r0
                if (r13 >= r6) goto L_0x0058
                org.telegram.ui.Adapters.SearchAdapterHelper r2 = r11.searchAdapterHelper
                java.util.ArrayList r2 = r2.getLocalServerSearch()
                int r3 = r13 - r0
                java.lang.Object r2 = r2.get(r3)
                org.telegram.tgnet.TLObject r2 = (org.telegram.tgnet.TLObject) r2
                goto L_0x0072
            L_0x0058:
                int r6 = r0 + r3
                if (r13 <= r6) goto L_0x0071
                int r2 = r2 + r0
                int r2 = r2 + r3
                if (r13 > r2) goto L_0x0071
                org.telegram.ui.Adapters.SearchAdapterHelper r2 = r11.searchAdapterHelper
                java.util.ArrayList r2 = r2.getGlobalSearch()
                int r6 = r13 - r0
                int r6 = r6 - r3
                int r6 = r6 - r1
                java.lang.Object r2 = r2.get(r6)
                org.telegram.tgnet.TLObject r2 = (org.telegram.tgnet.TLObject) r2
                goto L_0x0072
            L_0x0071:
                r2 = r5
            L_0x0072:
                if (r2 == 0) goto L_0x00fe
                boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$User
                if (r3 == 0) goto L_0x007e
                r3 = r2
                org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
                java.lang.String r3 = r3.username
                goto L_0x0083
            L_0x007e:
                r3 = r2
                org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC$Chat) r3
                java.lang.String r3 = r3.username
            L_0x0083:
                java.lang.String r6 = "@"
                if (r13 >= r0) goto L_0x00b5
                java.util.ArrayList<java.lang.CharSequence> r0 = r11.searchResultNames
                java.lang.Object r13 = r0.get(r13)
                java.lang.CharSequence r13 = (java.lang.CharSequence) r13
                if (r13 == 0) goto L_0x00b1
                boolean r0 = android.text.TextUtils.isEmpty(r3)
                if (r0 != 0) goto L_0x00b1
                java.lang.String r0 = r13.toString()
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r6)
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                boolean r0 = r0.startsWith(r3)
                if (r0 == 0) goto L_0x00b1
                goto L_0x00ff
            L_0x00b1:
                r10 = r5
                r5 = r13
                r13 = r10
                goto L_0x00ff
            L_0x00b5:
                if (r13 <= r0) goto L_0x00fe
                boolean r13 = android.text.TextUtils.isEmpty(r3)
                if (r13 != 0) goto L_0x00fe
                org.telegram.ui.Adapters.SearchAdapterHelper r13 = r11.searchAdapterHelper
                java.lang.String r13 = r13.getLastFoundUsername()
                boolean r0 = r13.startsWith(r6)
                if (r0 == 0) goto L_0x00cd
                java.lang.String r13 = r13.substring(r1)
            L_0x00cd:
                android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x00fc }
                r0.<init>()     // Catch:{ Exception -> 0x00fc }
                r0.append(r6)     // Catch:{ Exception -> 0x00fc }
                r0.append(r3)     // Catch:{ Exception -> 0x00fc }
                int r6 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r3, r13)     // Catch:{ Exception -> 0x00fc }
                if (r6 == r4) goto L_0x00fa
                int r13 = r13.length()     // Catch:{ Exception -> 0x00fc }
                if (r6 != 0) goto L_0x00e7
                int r13 = r13 + 1
                goto L_0x00e9
            L_0x00e7:
                int r6 = r6 + 1
            L_0x00e9:
                android.text.style.ForegroundColorSpan r4 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x00fc }
                java.lang.String r7 = "windowBackgroundWhiteBlueText4"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)     // Catch:{ Exception -> 0x00fc }
                r4.<init>(r7)     // Catch:{ Exception -> 0x00fc }
                int r13 = r13 + r6
                r7 = 33
                r0.setSpan(r4, r6, r13, r7)     // Catch:{ Exception -> 0x00fc }
            L_0x00fa:
                r13 = r0
                goto L_0x00ff
            L_0x00fc:
                r13 = r3
                goto L_0x00ff
            L_0x00fe:
                r13 = r5
            L_0x00ff:
                java.lang.Object r0 = r12.getObject()
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$User
                r6 = 0
                if (r3 == 0) goto L_0x010e
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
                long r3 = r0.id
                goto L_0x0119
            L_0x010e:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
                if (r3 == 0) goto L_0x0118
                org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
                long r3 = r0.id
                long r3 = -r3
                goto L_0x0119
            L_0x0118:
                r3 = r6
            L_0x0119:
                r12.setObject(r2, r5, r13)
                boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$User
                if (r13 == 0) goto L_0x0125
                org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
                long r8 = r2.id
                goto L_0x0130
            L_0x0125:
                boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$Chat
                if (r13 == 0) goto L_0x012f
                org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
                long r8 = r2.id
                long r8 = -r8
                goto L_0x0130
            L_0x012f:
                r8 = r6
            L_0x0130:
                int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r13 == 0) goto L_0x0182
                org.telegram.ui.Components.InviteMembersBottomSheet r13 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                androidx.collection.LongSparseArray r13 = r13.ignoreUsers
                r0 = 0
                if (r13 == 0) goto L_0x0157
                org.telegram.ui.Components.InviteMembersBottomSheet r13 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                androidx.collection.LongSparseArray r13 = r13.ignoreUsers
                int r13 = r13.indexOfKey(r8)
                if (r13 < 0) goto L_0x0157
                int r13 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r13 != 0) goto L_0x014f
                r13 = 1
                goto L_0x0150
            L_0x014f:
                r13 = 0
            L_0x0150:
                r12.setChecked(r1, r13)
                r12.setCheckBoxEnabled(r0)
                goto L_0x0182
            L_0x0157:
                org.telegram.ui.Components.InviteMembersBottomSheet r13 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                androidx.collection.LongSparseArray r13 = r13.selectedContacts
                int r13 = r13.indexOfKey(r8)
                if (r13 < 0) goto L_0x0165
                r13 = 1
                goto L_0x0166
            L_0x0165:
                r13 = 0
            L_0x0166:
                int r2 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r2 != 0) goto L_0x016b
                r0 = 1
            L_0x016b:
                r12.setChecked(r13, r0)
                r12.setCheckBoxEnabled(r1)
                goto L_0x0182
            L_0x0172:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.GroupCreateSectionCell r12 = (org.telegram.ui.Cells.GroupCreateSectionCell) r12
                r13 = 2131625965(0x7f0e07ed, float:1.8879153E38)
                java.lang.String r0 = "GlobalSearch"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
            L_0x0182:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 2;
            }
            if (i == this.currentItemsCount - 1) {
                return 4;
            }
            if (i - 1 == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()) {
                return 0;
            }
            return 1;
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            int i = size + size2;
            if (size3 != 0) {
                i += size3 + 1;
            }
            int i2 = i + 2;
            this.currentItemsCount = i2;
            return i2;
        }

        private void updateSearchResults(ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda3(this, arrayList, arrayList2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$1(ArrayList arrayList, ArrayList arrayList2) {
            this.searchRunnable = null;
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(arrayList);
            InviteMembersBottomSheet.this.showItemsAnimated(this.currentItemsCount - 1);
            notifyDataSetChanged();
            if (!this.searchAdapterHelper.isSearchInProgress() && getItemCount() <= 2) {
                InviteMembersBottomSheet.this.emptyView.showProgress(false, true);
            }
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, false, false, false, 0, false, 0, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(str)) {
                RecyclerView.Adapter adapter = InviteMembersBottomSheet.this.listView.getAdapter();
                InviteMembersBottomSheet inviteMembersBottomSheet = InviteMembersBottomSheet.this;
                RecyclerView.Adapter adapter2 = inviteMembersBottomSheet.searchListViewAdapter;
                if (adapter != adapter2) {
                    inviteMembersBottomSheet.listView.setAdapter(adapter2);
                }
                InviteMembersBottomSheet.this.emptyView.showProgress(true, false);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2 inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2 = new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2(this, str);
                this.searchRunnable = inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2;
                dispatchQueue.postRunnable(inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda2, 300);
                return;
            }
            RecyclerView.Adapter adapter3 = InviteMembersBottomSheet.this.listView.getAdapter();
            InviteMembersBottomSheet inviteMembersBottomSheet2 = InviteMembersBottomSheet.this;
            RecyclerView.Adapter adapter4 = inviteMembersBottomSheet2.listViewAdapter;
            if (adapter3 != adapter4) {
                inviteMembersBottomSheet2.listView.setAdapter(adapter4);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$4(String str) {
            AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda1(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$3(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, InviteMembersBottomSheet.this.dialogsDelegate != null, true, InviteMembersBottomSheet.this.dialogsDelegate != null, false, 0, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0 inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0 = new InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0(this, str);
            this.searchRunnable = inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0;
            dispatchQueue.postRunnable(inviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda0);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d0, code lost:
            if (r13.contains(" " + r3) != false) goto L_0x00dd;
         */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x0130 A[LOOP:1: B:27:0x0094->B:51:0x0130, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00e0 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchDialogs$2(java.lang.String r18) {
            /*
                r17 = this;
                r0 = r17
                java.lang.String r1 = r18.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r0.updateSearchResults(r1, r2)
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
                r5 = 1
                if (r2 == 0) goto L_0x0039
                r6 = 1
                goto L_0x003a
            L_0x0039:
                r6 = 0
            L_0x003a:
                int r6 = r6 + r5
                java.lang.String[] r7 = new java.lang.String[r6]
                r7[r3] = r1
                if (r2 == 0) goto L_0x0043
                r7[r5] = r2
            L_0x0043:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r8 = 0
            L_0x004e:
                org.telegram.ui.Components.InviteMembersBottomSheet r9 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                java.util.ArrayList r9 = r9.contacts
                int r9 = r9.size()
                if (r8 >= r9) goto L_0x0140
                org.telegram.ui.Components.InviteMembersBottomSheet r9 = org.telegram.ui.Components.InviteMembersBottomSheet.this
                java.util.ArrayList r9 = r9.contacts
                java.lang.Object r9 = r9.get(r8)
                org.telegram.tgnet.TLObject r9 = (org.telegram.tgnet.TLObject) r9
                boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$User
                if (r10 == 0) goto L_0x007c
                r11 = r9
                org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
                java.lang.String r12 = r11.first_name
                java.lang.String r13 = r11.last_name
                java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r12, r13)
                java.lang.String r12 = r12.toLowerCase()
                java.lang.String r11 = r11.username
                goto L_0x0083
            L_0x007c:
                r11 = r9
                org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC$Chat) r11
                java.lang.String r12 = r11.title
                java.lang.String r11 = r11.username
            L_0x0083:
                org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r13 = r13.getTranslitString(r12)
                boolean r14 = r12.equals(r13)
                if (r14 == 0) goto L_0x0092
                r13 = 0
            L_0x0092:
                r14 = 0
                r15 = 0
            L_0x0094:
                if (r14 >= r6) goto L_0x0138
                r3 = r7[r14]
                boolean r16 = r12.startsWith(r3)
                if (r16 != 0) goto L_0x00dd
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = " "
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r12.contains(r4)
                if (r4 != 0) goto L_0x00dd
                if (r13 == 0) goto L_0x00d3
                boolean r4 = r13.startsWith(r3)
                if (r4 != 0) goto L_0x00dd
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r13.contains(r4)
                if (r4 == 0) goto L_0x00d3
                goto L_0x00dd
            L_0x00d3:
                if (r11 == 0) goto L_0x00de
                boolean r4 = r11.startsWith(r3)
                if (r4 == 0) goto L_0x00de
                r15 = 2
                goto L_0x00de
            L_0x00dd:
                r15 = 1
            L_0x00de:
                if (r15 == 0) goto L_0x0130
                r4 = 1
                if (r15 != r4) goto L_0x0103
                if (r10 == 0) goto L_0x00f4
                r5 = r9
                org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
                java.lang.String r10 = r5.first_name
                java.lang.String r5 = r5.last_name
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r10, r5, r3)
                r2.add(r3)
                goto L_0x0101
            L_0x00f4:
                r5 = r9
                org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
                java.lang.String r5 = r5.title
                r10 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r3)
                r2.add(r3)
            L_0x0101:
                r10 = 0
                goto L_0x012b
            L_0x0103:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r10 = "@"
                r5.append(r10)
                r5.append(r11)
                java.lang.String r5 = r5.toString()
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r10)
                r11.append(r3)
                java.lang.String r3 = r11.toString()
                r10 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r3)
                r2.add(r3)
            L_0x012b:
                r1.add(r9)
                r3 = r10
                goto L_0x013a
            L_0x0130:
                r3 = 0
                r4 = 1
                int r14 = r14 + 1
                r3 = 0
                r5 = 1
                goto L_0x0094
            L_0x0138:
                r3 = 0
                r4 = 1
            L_0x013a:
                int r8 = r8 + 1
                r3 = 0
                r5 = 1
                goto L_0x004e
            L_0x0140:
                r0.updateSearchResults(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteMembersBottomSheet.SearchAdapter.lambda$searchDialogs$2(java.lang.String):void");
        }
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
        if (motionEvent.getAction() == 0) {
            this.y = (float) this.scrollOffsetY;
        } else if (motionEvent.getAction() == 1 && Math.abs(((float) this.scrollOffsetY) - this.y) < this.touchSlop && !this.enterEventSent) {
            Activity findActivity = AndroidUtilities.findActivity(getContext());
            BaseFragment baseFragment = null;
            if (findActivity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) findActivity;
                baseFragment = launchActivity.getActionBarLayout().fragmentsStack.get(launchActivity.getActionBarLayout().fragmentsStack.size() - 1);
            }
            if (baseFragment instanceof ChatActivity) {
                boolean needEnterText = ((ChatActivity) baseFragment).needEnterText();
                this.enterEventSent = true;
                AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda6(this, editTextBoldCursor), needEnterText ? 200 : 0);
                return;
            }
            this.enterEventSent = true;
            setFocusable(true);
            editTextBoldCursor.requestFocus();
            AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda4(editTextBoldCursor));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSearchViewTouched$5(EditTextBoldCursor editTextBoldCursor) {
        setFocusable(true);
        editTextBoldCursor.requestFocus();
        AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda3(editTextBoldCursor));
    }

    private class SpansContainer extends ViewGroup {
        boolean addAnimation;
        /* access modifiers changed from: private */
        public boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList<>();
        /* access modifiers changed from: private */
        public View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition;
            int childCount = getChildCount();
            int size = View.MeasureSpec.getSize(i);
            int dp = size - AndroidUtilities.dp(26.0f);
            int dp2 = AndroidUtilities.dp(10.0f);
            int dp3 = AndroidUtilities.dp(10.0f);
            int i4 = 0;
            int i5 = 0;
            for (int i6 = 0; i6 < childCount; i6++) {
                View childAt = getChildAt(i6);
                if (childAt instanceof GroupCreateSpan) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (childAt != this.removingSpan && childAt.getMeasuredWidth() + i4 > dp) {
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        i4 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i5 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        i5 = 0;
                    }
                    int dp4 = AndroidUtilities.dp(13.0f) + i4;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (childAt == view) {
                            childAt.setTranslationX((float) (AndroidUtilities.dp(13.0f) + i5));
                            childAt.setTranslationY((float) dp3);
                        } else if (view != null) {
                            float f = (float) dp4;
                            if (childAt.getTranslationX() != f) {
                                this.animators.add(ObjectAnimator.ofFloat(childAt, View.TRANSLATION_X, new float[]{f}));
                            }
                            float f2 = (float) dp2;
                            if (childAt.getTranslationY() != f2) {
                                this.animators.add(ObjectAnimator.ofFloat(childAt, View.TRANSLATION_Y, new float[]{f2}));
                            }
                        } else {
                            childAt.setTranslationX((float) dp4);
                            childAt.setTranslationY((float) dp2);
                        }
                    }
                    if (childAt != this.removingSpan) {
                        i4 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
            }
            int dp5 = dp3 + AndroidUtilities.dp(42.0f);
            int dp6 = dp2 + AndroidUtilities.dp(42.0f);
            if (InviteMembersBottomSheet.this.dialogsDelegate != null) {
                i3 = InviteMembersBottomSheet.this.spanEnter ? Math.min(InviteMembersBottomSheet.this.maxSize, dp6) : 0;
            } else {
                i3 = Math.max(0, Math.min(InviteMembersBottomSheet.this.maxSize, dp6) - AndroidUtilities.dp(52.0f));
            }
            int access$2400 = InviteMembersBottomSheet.this.searchAdditionalHeight;
            InviteMembersBottomSheet inviteMembersBottomSheet = InviteMembersBottomSheet.this;
            int unused = inviteMembersBottomSheet.searchAdditionalHeight = (inviteMembersBottomSheet.dialogsDelegate != null || InviteMembersBottomSheet.this.selectedContacts.size() <= 0) ? 0 : AndroidUtilities.dp(56.0f);
            if (!(i3 == InviteMembersBottomSheet.this.additionalHeight && access$2400 == InviteMembersBottomSheet.this.searchAdditionalHeight)) {
                int unused2 = InviteMembersBottomSheet.this.additionalHeight = i3;
                if (!(InviteMembersBottomSheet.this.listView.getAdapter() == null || InviteMembersBottomSheet.this.listView.getAdapter().getItemCount() <= 0 || (findViewHolderForAdapterPosition = InviteMembersBottomSheet.this.listView.findViewHolderForAdapterPosition(0)) == null)) {
                    InviteMembersBottomSheet.this.listView.getAdapter().notifyItemChanged(0);
                    InviteMembersBottomSheet.this.layoutManager.scrollToPositionWithOffset(0, findViewHolderForAdapterPosition.itemView.getTop() - InviteMembersBottomSheet.this.listView.getPaddingTop());
                }
            }
            int min = Math.min(InviteMembersBottomSheet.this.maxSize, dp6);
            if (InviteMembersBottomSheet.this.scrollViewH != min) {
                ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{InviteMembersBottomSheet.this.scrollViewH, min});
                ofInt.addUpdateListener(new InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda0(this));
                this.animators.add(ofInt);
            }
            if (this.addAnimation && dp6 > InviteMembersBottomSheet.this.maxSize) {
                AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda1(this, dp6));
            } else if (!this.addAnimation && InviteMembersBottomSheet.this.spansScrollView.getScrollY() + InviteMembersBottomSheet.this.spansScrollView.getMeasuredHeight() > dp6) {
                AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$SpansContainer$$ExternalSyntheticLambda2(this, dp6));
            }
            if (!this.animationStarted && InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.playTogether(this.animators);
                InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = InviteMembersBottomSheet.this.currentAnimation = null;
                        SpansContainer.this.requestLayout();
                    }
                });
                InviteMembersBottomSheet.this.currentAnimation.start();
                this.animationStarted = true;
            }
            if (InviteMembersBottomSheet.this.currentAnimation == null) {
                int unused3 = InviteMembersBottomSheet.this.scrollViewH = min;
                InviteMembersBottomSheet.this.containerView.invalidate();
            }
            setMeasuredDimension(size, Math.max(dp6, dp5));
            InviteMembersBottomSheet.this.listView.setTranslationY(0.0f);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onMeasure$0(ValueAnimator valueAnimator) {
            int unused = InviteMembersBottomSheet.this.scrollViewH = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            InviteMembersBottomSheet.this.containerView.invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onMeasure$1(int i) {
            InviteMembersBottomSheet.this.spansScrollView.smoothScrollTo(0, i - InviteMembersBottomSheet.this.maxSize);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onMeasure$2(int i) {
            InviteMembersBottomSheet.this.spansScrollView.smoothScrollTo(0, i - InviteMembersBottomSheet.this.maxSize);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan, boolean z) {
            this.addAnimation = true;
            InviteMembersBottomSheet.this.selectedContacts.put(groupCreateSpan.getUid(), groupCreateSpan);
            if (InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.setupEndValues();
                InviteMembersBottomSheet.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            if (z) {
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
                this.animators.add(ObjectAnimator.ofFloat(groupCreateSpan, View.SCALE_X, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(groupCreateSpan, View.SCALE_Y, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(groupCreateSpan, View.ALPHA, new float[]{0.0f, 1.0f}));
            }
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            this.addAnimation = false;
            InviteMembersBottomSheet.this.selectedContacts.remove(groupCreateSpan.getUid());
            groupCreateSpan.setOnClickListener((View.OnClickListener) null);
            if (InviteMembersBottomSheet.this.currentAnimation != null) {
                InviteMembersBottomSheet.this.currentAnimation.setupEndValues();
                InviteMembersBottomSheet.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet unused = InviteMembersBottomSheet.this.currentAnimation = new AnimatorSet();
            InviteMembersBottomSheet.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    View unused = SpansContainer.this.removingSpan = null;
                    AnimatorSet unused2 = InviteMembersBottomSheet.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                }
            });
            InviteMembersBottomSheet.this.currentAnimation.setDuration(150);
            this.removingSpan = groupCreateSpan;
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

            public void onViewAdded(View view) {
                if (view == InviteMembersBottomSheet.this.floatingButton && this.verticalPositionAutoAnimator == null) {
                    this.verticalPositionAutoAnimator = VerticalPositionAutoAnimator.attach(view);
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
                InviteMembersBottomSheet inviteMembersBottomSheet = InviteMembersBottomSheet.this;
                InviteMembersBottomSheet.this.spansScrollView.setTranslationY((float) ((inviteMembersBottomSheet.scrollOffsetY - inviteMembersBottomSheet.backgroundPaddingTop) + AndroidUtilities.dp(6.0f) + AndroidUtilities.dp(64.0f)));
                float access$1000 = (float) (InviteMembersBottomSheet.this.additionalHeight + InviteMembersBottomSheet.this.searchAdditionalHeight);
                if (InviteMembersBottomSheet.this.emptyView.getVisibility() != 0) {
                    this.emptyViewOffset = access$1000;
                    this.animateToEmptyViewOffset = access$1000;
                } else if (this.animateToEmptyViewOffset != access$1000) {
                    this.animateToEmptyViewOffset = access$1000;
                    this.deltaOffset = (access$1000 - this.emptyViewOffset) * 0.10666667f;
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
                InviteMembersBottomSheet inviteMembersBottomSheet2 = InviteMembersBottomSheet.this;
                inviteMembersBottomSheet2.emptyView.setTranslationY(((float) inviteMembersBottomSheet2.scrollOffsetY) + this.emptyViewOffset);
                super.dispatchDraw(canvas);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view != InviteMembersBottomSheet.this.spansScrollView) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                canvas.clipRect(0.0f, view.getY() - ((float) AndroidUtilities.dp(4.0f)), (float) getMeasuredWidth(), view.getY() + ((float) InviteMembersBottomSheet.this.scrollViewH) + 1.0f);
                canvas.drawColor(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), (int) (InviteMembersBottomSheet.this.spansEnterProgress * 255.0f)));
                this.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("divider"), (int) (InviteMembersBottomSheet.this.spansEnterProgress * 255.0f)));
                canvas.drawRect(0.0f, view.getY() + ((float) InviteMembersBottomSheet.this.scrollViewH), (float) getMeasuredWidth(), view.getY() + ((float) InviteMembersBottomSheet.this.scrollViewH) + 1.0f, this.paint);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }
        };
    }

    /* access modifiers changed from: protected */
    public void search(String str) {
        this.searchAdapter.searchDialogs(str);
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
        public ItemAnimator(InviteMembersBottomSheet inviteMembersBottomSheet) {
            this.translationInterpolator = CubicBezierInterpolator.DEFAULT;
            setMoveDuration(150);
            setAddDuration(150);
            setRemoveDuration(150);
            inviteMembersBottomSheet.setShowWithoutAnimation(false);
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        if (this.enterEventSent) {
            Activity findActivity = AndroidUtilities.findActivity(getContext());
            if (findActivity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) findActivity;
                BaseFragment baseFragment = launchActivity.getActionBarLayout().fragmentsStack.get(launchActivity.getActionBarLayout().fragmentsStack.size() - 1);
                if (baseFragment instanceof ChatActivity) {
                    ((ChatActivity) baseFragment).onEditTextDialogClose(true);
                }
            }
        }
    }

    private void generateLink() {
        if (!this.linkGenerating) {
            this.linkGenerating = true;
            TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
            tLRPC$TL_messages_exportChatInvite.legacy_revoke_permanent = true;
            tLRPC$TL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_exportChatInvite, new InviteMembersBottomSheet$$ExternalSyntheticLambda7(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$generateLink$8(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new InviteMembersBottomSheet$$ExternalSyntheticLambda5(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$generateLink$7(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.invite = (TLRPC$TL_chatInviteExported) tLObject;
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatFull != null) {
                chatFull.exported_invite = this.invite;
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
