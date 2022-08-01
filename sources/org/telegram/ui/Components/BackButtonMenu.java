package org.telegram.ui.Components;

import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ProfileActivity;

public class BackButtonMenu {

    public static class PulledDialog<T> {
        Class<T> activity;
        TLRPC$Chat chat;
        long dialogId;
        int filterId;
        int folderId;
        int stackIndex;
        TLRPC$User user;
    }

    /* JADX WARNING: type inference failed for: r14v4, types: [android.graphics.drawable.BitmapDrawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.ActionBar.ActionBarPopupWindow show(org.telegram.ui.ActionBar.BaseFragment r25, android.view.View r26, long r27, org.telegram.ui.ActionBar.Theme.ResourcesProvider r29) {
        /*
            r0 = r25
            r1 = r29
            r2 = 0
            if (r0 != 0) goto L_0x0008
            return r2
        L_0x0008:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r25.getParentLayout()
            android.app.Activity r4 = r25.getParentActivity()
            android.view.View r5 = r25.getFragmentView()
            if (r3 == 0) goto L_0x0217
            if (r4 == 0) goto L_0x0217
            if (r5 != 0) goto L_0x001c
            goto L_0x0217
        L_0x001c:
            r6 = r27
            java.util.ArrayList r6 = getStackedHistoryDialogs(r0, r6)
            int r7 = r6.size()
            if (r7 > 0) goto L_0x0029
            return r2
        L_0x0029:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r2 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout
            r2.<init>(r4, r1)
            android.graphics.Rect r7 = new android.graphics.Rect
            r7.<init>()
            android.app.Activity r8 = r25.getParentActivity()
            android.content.res.Resources r8 = r8.getResources()
            r9 = 2131166090(0x7var_a, float:1.7946416E38)
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r9)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r8.getPadding(r7)
            java.lang.String r8 = "actionBarDefaultSubmenuBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r2.setBackgroundColor(r8)
            java.util.concurrent.atomic.AtomicReference r8 = new java.util.concurrent.atomic.AtomicReference
            r8.<init>()
            r10 = 0
        L_0x0058:
            int r11 = r6.size()
            if (r10 >= r11) goto L_0x0197
            java.lang.Object r11 = r6.get(r10)
            org.telegram.ui.Components.BackButtonMenu$PulledDialog r11 = (org.telegram.ui.Components.BackButtonMenu.PulledDialog) r11
            org.telegram.tgnet.TLRPC$Chat r13 = r11.chat
            org.telegram.tgnet.TLRPC$User r14 = r11.user
            android.widget.FrameLayout r15 = new android.widget.FrameLayout
            r15.<init>(r4)
            r16 = 1128792064(0x43480000, float:200.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r15.setMinimumWidth(r9)
            org.telegram.ui.Components.BackupImageView r9 = new org.telegram.ui.Components.BackupImageView
            r9.<init>(r4)
            r16 = 1107296256(0x42000000, float:32.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r9.setRoundRadius(r12)
            r17 = 1107296256(0x42000000, float:32.0)
            r18 = 8388627(0x800013, float:1.175497E-38)
            r19 = 1095761920(0x41500000, float:13.0)
            r20 = 0
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r16, r17, r18, r19, r20, r21, r22)
            r15.addView(r9, r12)
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r4)
            r16 = r4
            r4 = 1
            r12.setLines(r4)
            r17 = r6
            r6 = 1098907648(0x41800000, float:16.0)
            r12.setTextSize(r4, r6)
            java.lang.String r4 = "actionBarDefaultSubmenuItem"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r12.setTextColor(r4)
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r12.setEllipsize(r4)
            r18 = -1082130432(0xffffffffbvar_, float:-1.0)
            r19 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r20 = 8388627(0x800013, float:1.175497E-38)
            r21 = 1114374144(0x426CLASSNAME, float:59.0)
            r23 = 1094713344(0x41400000, float:12.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r18, r19, r20, r21, r22, r23, r24)
            r15.addView(r12, r4)
            org.telegram.ui.Components.AvatarDrawable r4 = new org.telegram.ui.Components.AvatarDrawable
            r4.<init>()
            r6 = 1
            r4.setSmallSize(r6)
            java.lang.String r6 = "50_50"
            if (r13 == 0) goto L_0x00f4
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r13)
            org.telegram.tgnet.TLRPC$ChatPhoto r14 = r13.photo
            if (r14 == 0) goto L_0x00e5
            android.graphics.drawable.BitmapDrawable r14 = r14.strippedBitmap
            if (r14 == 0) goto L_0x00e5
            r4 = r14
        L_0x00e5:
            r14 = 1
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForChat(r13, r14)
            r9.setImage((org.telegram.messenger.ImageLocation) r14, (java.lang.String) r6, (android.graphics.drawable.Drawable) r4, (java.lang.Object) r13)
            java.lang.String r4 = r13.title
            r12.setText(r4)
            goto L_0x0167
        L_0x00f4:
            if (r14 == 0) goto L_0x0167
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r14.photo
            if (r13 == 0) goto L_0x00ff
            android.graphics.drawable.BitmapDrawable r13 = r13.strippedBitmap
            if (r13 == 0) goto L_0x00ff
            goto L_0x0100
        L_0x00ff:
            r13 = r4
        L_0x0100:
            r18 = r5
            java.lang.Class<T> r5 = r11.activity
            r19 = r7
            java.lang.Class<org.telegram.ui.ChatActivity> r7 = org.telegram.ui.ChatActivity.class
            if (r5 != r7) goto L_0x0121
            boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r14)
            if (r5 == 0) goto L_0x0121
            r5 = 2131628153(0x7f0e1079, float:1.888359E38)
            java.lang.String r6 = "SavedMessages"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 1
            r4.setAvatarType(r6)
            r9.setImageDrawable(r4)
            goto L_0x0163
        L_0x0121:
            boolean r5 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r14)
            if (r5 == 0) goto L_0x0139
            r5 = 2131627995(0x7f0e0fdb, float:1.888327E38)
            java.lang.String r6 = "RepliesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 12
            r4.setAvatarType(r6)
            r9.setImageDrawable(r4)
            goto L_0x0163
        L_0x0139:
            boolean r5 = org.telegram.messenger.UserObject.isDeleted(r14)
            if (r5 == 0) goto L_0x0154
            r5 = 2131626191(0x7f0e08cf, float:1.8879611E38)
            java.lang.String r7 = "HiddenName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r4.setInfo((org.telegram.tgnet.TLRPC$User) r14)
            r7 = 1
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForUser(r14, r7)
            r9.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r6, (android.graphics.drawable.Drawable) r4, (java.lang.Object) r14)
            goto L_0x0163
        L_0x0154:
            r7 = 1
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r14)
            r4.setInfo((org.telegram.tgnet.TLRPC$User) r14)
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUser(r14, r7)
            r9.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r6, (android.graphics.drawable.Drawable) r13, (java.lang.Object) r14)
        L_0x0163:
            r12.setText(r5)
            goto L_0x016b
        L_0x0167:
            r18 = r5
            r19 = r7
        L_0x016b:
            java.lang.String r4 = "listSelectorSDK21"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r5 = 0
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r4, (boolean) r5)
            r15.setBackground(r4)
            org.telegram.ui.Components.BackButtonMenu$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Components.BackButtonMenu$$ExternalSyntheticLambda0
            r4.<init>(r8, r11, r3, r0)
            r15.setOnClickListener(r4)
            r4 = -1
            r5 = 48
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5)
            r2.addView(r15, r4)
            int r10 = r10 + 1
            r4 = r16
            r6 = r17
            r5 = r18
            r7 = r19
            goto L_0x0058
        L_0x0197:
            r18 = r5
            r19 = r7
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = new org.telegram.ui.ActionBar.ActionBarPopupWindow
            r1 = -2
            r0.<init>(r2, r1, r1)
            r8.set(r0)
            r1 = 1
            r0.setPauseNotifications(r1)
            r3 = 220(0xdc, float:3.08E-43)
            r0.setDismissAnimationDuration(r3)
            r0.setOutsideTouchable(r1)
            r0.setClippingEnabled(r1)
            r3 = 2131689481(0x7f0var_, float:1.9007979E38)
            r0.setAnimationStyle(r3)
            r0.setFocusable(r1)
            r1 = 1148846080(0x447a0000, float:1000.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r4)
            r2.measure(r3, r1)
            r1 = 2
            r0.setInputMethodMode(r1)
            r3 = 0
            r0.setSoftInputMode(r3)
            android.view.View r3 = r0.getContentView()
            r4 = 1
            r3.setFocusableInTouchMode(r4)
            r2.setFitItems(r4)
            r2 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = r19
            int r5 = r4.left
            int r3 = r3 - r5
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0203
            int[] r1 = new int[r1]
            r5 = r18
            r5.getLocationInWindow(r1)
            r6 = 0
            r1 = r1[r6]
            int r3 = r3 + r1
            goto L_0x0205
        L_0x0203:
            r5 = r18
        L_0x0205:
            int r1 = r26.getBottom()
            int r4 = r4.top
            int r1 = r1 - r4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r2
            r2 = 51
            r0.showAtLocation(r5, r2, r3, r1)
            return r0
        L_0x0217:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackButtonMenu.show(org.telegram.ui.ActionBar.BaseFragment, android.view.View, long, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.ActionBar.ActionBarPopupWindow");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$show$0(AtomicReference atomicReference, PulledDialog pulledDialog, ActionBarLayout actionBarLayout, BaseFragment baseFragment, View view) {
        ArrayList<BaseFragment> arrayList;
        int i;
        ArrayList<BaseFragment> arrayList2;
        Long l = null;
        if (atomicReference.get() != null) {
            ((ActionBarPopupWindow) atomicReference.getAndSet((Object) null)).dismiss();
        }
        int i2 = pulledDialog.stackIndex;
        if (i2 >= 0) {
            if (!(actionBarLayout == null || (arrayList2 = actionBarLayout.fragmentsStack) == null || i2 >= arrayList2.size())) {
                BaseFragment baseFragment2 = actionBarLayout.fragmentsStack.get(pulledDialog.stackIndex);
                if (baseFragment2 instanceof ChatActivity) {
                    l = Long.valueOf(((ChatActivity) baseFragment2).getDialogId());
                } else if (baseFragment2 instanceof ProfileActivity) {
                    l = Long.valueOf(((ProfileActivity) baseFragment2).getDialogId());
                }
            }
            if (l != null && l.longValue() != pulledDialog.dialogId) {
                for (int size = actionBarLayout.fragmentsStack.size() - 2; size > pulledDialog.stackIndex; size--) {
                    actionBarLayout.removeFragmentFromStack(size);
                }
            } else if (!(actionBarLayout == null || (arrayList = actionBarLayout.fragmentsStack) == null)) {
                int size2 = arrayList.size() - 2;
                while (true) {
                    i = pulledDialog.stackIndex;
                    if (size2 <= i) {
                        break;
                    }
                    if (size2 >= 0 && size2 < actionBarLayout.fragmentsStack.size()) {
                        actionBarLayout.removeFragmentFromStack(size2);
                    }
                    size2--;
                }
                if (i < actionBarLayout.fragmentsStack.size()) {
                    actionBarLayout.showFragment(pulledDialog.stackIndex);
                    actionBarLayout.closeLastFragment(true);
                    return;
                }
            }
        }
        goToPulledDialog(baseFragment, pulledDialog);
    }

    public static void goToPulledDialog(BaseFragment baseFragment, PulledDialog pulledDialog) {
        if (pulledDialog != null) {
            Class<T> cls = pulledDialog.activity;
            if (cls == ChatActivity.class) {
                Bundle bundle = new Bundle();
                TLRPC$Chat tLRPC$Chat = pulledDialog.chat;
                if (tLRPC$Chat != null) {
                    bundle.putLong("chat_id", tLRPC$Chat.id);
                } else {
                    TLRPC$User tLRPC$User = pulledDialog.user;
                    if (tLRPC$User != null) {
                        bundle.putLong("user_id", tLRPC$User.id);
                    }
                }
                bundle.putInt("dialog_folder_id", pulledDialog.folderId);
                bundle.putInt("dialog_filter_id", pulledDialog.filterId);
                baseFragment.presentFragment(new ChatActivity(bundle), true);
            } else if (cls == ProfileActivity.class) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("dialog_id", pulledDialog.dialogId);
                baseFragment.presentFragment(new ProfileActivity(bundle2), true);
            }
        }
    }

    public static ArrayList<PulledDialog> getStackedHistoryDialogs(BaseFragment baseFragment, long j) {
        ActionBarLayout parentLayout;
        boolean z;
        int i;
        int i2;
        long j2;
        TLRPC$User tLRPC$User;
        TLRPC$Chat tLRPC$Chat;
        Class cls;
        int i3;
        boolean z2;
        ArrayList<PulledDialog> arrayList = new ArrayList<>();
        if (baseFragment == null || (parentLayout = baseFragment.getParentLayout()) == null) {
            return arrayList;
        }
        ArrayList<BaseFragment> arrayList2 = parentLayout.fragmentsStack;
        ArrayList<PulledDialog> arrayList3 = parentLayout.pulledDialogs;
        if (arrayList2 != null) {
            int size = arrayList2.size();
            int i4 = 0;
            while (i4 < size) {
                BaseFragment baseFragment2 = arrayList2.get(i4);
                TLRPC$User tLRPC$User2 = null;
                if (baseFragment2 instanceof ChatActivity) {
                    cls = ChatActivity.class;
                    ChatActivity chatActivity = (ChatActivity) baseFragment2;
                    if (chatActivity.getChatMode() == 0 && !chatActivity.isReport()) {
                        tLRPC$Chat = chatActivity.getCurrentChat();
                        tLRPC$User = chatActivity.getCurrentUser();
                        j2 = chatActivity.getDialogId();
                        i2 = chatActivity.getDialogFolderId();
                        i3 = chatActivity.getDialogFilterId();
                    }
                    i = size;
                    i4++;
                    size = i;
                } else {
                    if (baseFragment2 instanceof ProfileActivity) {
                        Class cls2 = ProfileActivity.class;
                        ProfileActivity profileActivity = (ProfileActivity) baseFragment2;
                        TLRPC$Chat currentChat = profileActivity.getCurrentChat();
                        try {
                            tLRPC$User2 = profileActivity.getUserInfo().user;
                        } catch (Exception unused) {
                        }
                        j2 = profileActivity.getDialogId();
                        i3 = 0;
                        i2 = 0;
                        TLRPC$Chat tLRPC$Chat2 = currentChat;
                        tLRPC$User = tLRPC$User2;
                        cls = cls2;
                        tLRPC$Chat = tLRPC$Chat2;
                    }
                    i = size;
                    i4++;
                    size = i;
                }
                if (j2 != j && (j != 0 || !UserObject.isUserSelf(tLRPC$User))) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= arrayList.size()) {
                            i = size;
                            z2 = false;
                            break;
                        }
                        i = size;
                        if (arrayList.get(i5).dialogId == j2) {
                            z2 = true;
                            break;
                        }
                        i5++;
                        size = i;
                    }
                    if (!z2) {
                        PulledDialog pulledDialog = new PulledDialog();
                        pulledDialog.activity = cls;
                        pulledDialog.stackIndex = i4;
                        pulledDialog.chat = tLRPC$Chat;
                        pulledDialog.user = tLRPC$User;
                        pulledDialog.dialogId = j2;
                        pulledDialog.folderId = i2;
                        pulledDialog.filterId = i3;
                        if (tLRPC$Chat != null || tLRPC$User != null) {
                            arrayList.add(pulledDialog);
                        }
                    }
                    i4++;
                    size = i;
                }
                i = size;
                i4++;
                size = i;
            }
        }
        if (arrayList3 != null) {
            for (int size2 = arrayList3.size() - 1; size2 >= 0; size2--) {
                PulledDialog pulledDialog2 = arrayList3.get(size2);
                if (pulledDialog2.dialogId != j) {
                    int i6 = 0;
                    while (true) {
                        if (i6 >= arrayList.size()) {
                            z = false;
                            break;
                        } else if (arrayList.get(i6).dialogId == pulledDialog2.dialogId) {
                            z = true;
                            break;
                        } else {
                            i6++;
                        }
                    }
                    if (!z) {
                        arrayList.add(pulledDialog2);
                    }
                }
            }
        }
        Collections.sort(arrayList, BackButtonMenu$$ExternalSyntheticLambda1.INSTANCE);
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getStackedHistoryDialogs$1(PulledDialog pulledDialog, PulledDialog pulledDialog2) {
        return pulledDialog2.stackIndex - pulledDialog.stackIndex;
    }

    public static void addToPulledDialogs(BaseFragment baseFragment, int i, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, long j, int i2, int i3) {
        ActionBarLayout parentLayout;
        if ((tLRPC$Chat != null || tLRPC$User != null) && baseFragment != null && (parentLayout = baseFragment.getParentLayout()) != null) {
            if (parentLayout.pulledDialogs == null) {
                parentLayout.pulledDialogs = new ArrayList<>();
            }
            boolean z = false;
            Iterator<PulledDialog> it = parentLayout.pulledDialogs.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().dialogId == j) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!z) {
                PulledDialog pulledDialog = new PulledDialog();
                pulledDialog.activity = ChatActivity.class;
                pulledDialog.stackIndex = i;
                pulledDialog.dialogId = j;
                pulledDialog.filterId = i3;
                pulledDialog.folderId = i2;
                pulledDialog.chat = tLRPC$Chat;
                pulledDialog.user = tLRPC$User;
                parentLayout.pulledDialogs.add(pulledDialog);
            }
        }
    }

    public static void clearPulledDialogs(BaseFragment baseFragment, int i) {
        ActionBarLayout parentLayout;
        if (baseFragment != null && (parentLayout = baseFragment.getParentLayout()) != null && parentLayout.pulledDialogs != null) {
            int i2 = 0;
            while (i2 < parentLayout.pulledDialogs.size()) {
                if (parentLayout.pulledDialogs.get(i2).stackIndex > i) {
                    parentLayout.pulledDialogs.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
    }
}
