package org.telegram.ui.Components;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ProfileActivity;

public class BackButtonMenu {
    private static HashMap<Integer, ArrayList<PulledDialog>> pulledDialogs;

    public static class PulledDialog<T> {
        Class<T> activity;
        TLRPC$Chat chat;
        long dialogId;
        int filterId;
        int folderId;
        int stackIndex;
        TLRPC$User user;
    }

    public static ActionBarPopupWindow show(BaseFragment baseFragment, View view, long j) {
        long j2;
        ArrayList<BaseFragment> arrayList;
        Activity activity;
        String str;
        BaseFragment baseFragment2 = baseFragment;
        if (baseFragment2 == null) {
            return null;
        }
        ActionBarLayout parentLayout = baseFragment.getParentLayout();
        int currentAccount = baseFragment.getCurrentAccount();
        if (parentLayout == null) {
            j2 = j;
            arrayList = null;
        } else {
            arrayList = parentLayout.fragmentsStack;
            j2 = j;
        }
        ArrayList<PulledDialog> stackedHistoryDialogs = getStackedHistoryDialogs(currentAccount, arrayList, j2);
        if (stackedHistoryDialogs.size() <= 0) {
            return null;
        }
        Activity parentActivity = baseFragment.getParentActivity();
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(parentActivity);
        Rect rect = new Rect();
        baseFragment.getParentActivity().getResources().getDrawable(NUM).mutate().getPadding(rect);
        actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
        AtomicReference atomicReference = new AtomicReference();
        int i = 0;
        while (i < stackedHistoryDialogs.size()) {
            PulledDialog pulledDialog = stackedHistoryDialogs.get(i);
            TLRPC$Chat tLRPC$Chat = pulledDialog.chat;
            TLRPC$User tLRPC$User = pulledDialog.user;
            FrameLayout frameLayout = new FrameLayout(parentActivity);
            frameLayout.setMinimumWidth(AndroidUtilities.dp(200.0f));
            BackupImageView backupImageView = new BackupImageView(parentActivity);
            backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            frameLayout.addView(backupImageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 13.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(parentActivity);
            textView.setLines(1);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            frameLayout.addView(textView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 59.0f, 0.0f, 12.0f, 0.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setSmallSize(true);
            if (tLRPC$Chat != null) {
                avatarDrawable.setInfo(tLRPC$Chat);
                backupImageView.setImage(ImageLocation.getForChat(tLRPC$Chat, 1), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$Chat);
                textView.setText(tLRPC$Chat.title);
            } else if (tLRPC$User != null) {
                activity = parentActivity;
                if (pulledDialog.activity == ChatActivity.class && UserObject.isUserSelf(tLRPC$User)) {
                    str = LocaleController.getString("SavedMessages", NUM);
                    avatarDrawable.setAvatarType(1);
                    backupImageView.setImageDrawable(avatarDrawable);
                } else if (UserObject.isReplyUser(tLRPC$User)) {
                    str = LocaleController.getString("RepliesTitle", NUM);
                    avatarDrawable.setAvatarType(12);
                    backupImageView.setImageDrawable(avatarDrawable);
                } else if (UserObject.isDeleted(tLRPC$User)) {
                    str = LocaleController.getString("HiddenName", NUM);
                    avatarDrawable.setInfo(tLRPC$User);
                    backupImageView.setImage(ImageLocation.getForUser(tLRPC$User, 1), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$User);
                } else {
                    str = UserObject.getUserName(tLRPC$User);
                    avatarDrawable.setInfo(tLRPC$User);
                    backupImageView.setImage(ImageLocation.getForUser(tLRPC$User, 1), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$User);
                }
                textView.setText(str);
                frameLayout.setBackground(Theme.getSelectorDrawable(Theme.getColor("listSelectorSDK21"), false));
                frameLayout.setOnClickListener(new BackButtonMenu$$ExternalSyntheticLambda0(atomicReference, pulledDialog, parentLayout, baseFragment2));
                actionBarPopupWindowLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 48));
                i++;
                parentActivity = activity;
            }
            activity = parentActivity;
            frameLayout.setBackground(Theme.getSelectorDrawable(Theme.getColor("listSelectorSDK21"), false));
            frameLayout.setOnClickListener(new BackButtonMenu$$ExternalSyntheticLambda0(atomicReference, pulledDialog, parentLayout, baseFragment2));
            actionBarPopupWindowLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 48));
            i++;
            parentActivity = activity;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
        atomicReference.set(actionBarPopupWindow);
        actionBarPopupWindow.setPauseNotifications(true);
        actionBarPopupWindow.setDismissAnimationDuration(220);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setAnimationStyle(NUM);
        actionBarPopupWindow.setFocusable(true);
        actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.setSoftInputMode(0);
        actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
        actionBarPopupWindowLayout.setFitItems(true);
        View fragmentView = baseFragment.getFragmentView();
        if (fragmentView != null) {
            int dp = AndroidUtilities.dp(8.0f) - rect.left;
            if (AndroidUtilities.isTablet()) {
                int[] iArr = new int[2];
                fragmentView.getLocationInWindow(iArr);
                dp += iArr[0];
            }
            actionBarPopupWindow.showAtLocation(fragmentView, 51, dp, (view.getBottom() - rect.top) - AndroidUtilities.dp(8.0f));
            try {
                fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
        }
        return actionBarPopupWindow;
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

    public static ArrayList<PulledDialog> getStackedHistoryDialogs(int i, ArrayList<BaseFragment> arrayList, long j) {
        ArrayList arrayList2;
        boolean z;
        int i2;
        long j2;
        TLRPC$User tLRPC$User;
        TLRPC$Chat tLRPC$Chat;
        Class cls;
        int i3;
        boolean z2;
        ArrayList<BaseFragment> arrayList3 = arrayList;
        ArrayList<PulledDialog> arrayList4 = new ArrayList<>();
        if (arrayList3 != null) {
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                BaseFragment baseFragment = arrayList3.get(i4);
                TLRPC$User tLRPC$User2 = null;
                if (baseFragment instanceof ChatActivity) {
                    cls = ChatActivity.class;
                    ChatActivity chatActivity = (ChatActivity) baseFragment;
                    if (chatActivity.getChatMode() == 0 && !chatActivity.isReport()) {
                        tLRPC$Chat = chatActivity.getCurrentChat();
                        tLRPC$User = chatActivity.getCurrentUser();
                        j2 = chatActivity.getDialogId();
                        i2 = chatActivity.getDialogFolderId();
                        i3 = chatActivity.getDialogFilterId();
                    }
                } else if (baseFragment instanceof ProfileActivity) {
                    Class cls2 = ProfileActivity.class;
                    ProfileActivity profileActivity = (ProfileActivity) baseFragment;
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
                if (j2 != j && (j != 0 || !UserObject.isUserSelf(tLRPC$User))) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= arrayList4.size()) {
                            z2 = false;
                            break;
                        } else if (arrayList4.get(i5).dialogId == j2) {
                            z2 = true;
                            break;
                        } else {
                            i5++;
                        }
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
                            arrayList4.add(pulledDialog);
                        }
                    }
                }
            }
        }
        HashMap<Integer, ArrayList<PulledDialog>> hashMap = pulledDialogs;
        if (!(hashMap == null || (arrayList2 = hashMap.get(Integer.valueOf(i))) == null)) {
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                PulledDialog pulledDialog2 = (PulledDialog) it.next();
                if (pulledDialog2.dialogId != j) {
                    int i6 = 0;
                    while (true) {
                        if (i6 >= arrayList4.size()) {
                            z = false;
                            break;
                        } else if (arrayList4.get(i6).dialogId == pulledDialog2.dialogId) {
                            z = true;
                            break;
                        } else {
                            i6++;
                        }
                    }
                    if (!z) {
                        arrayList4.add(pulledDialog2);
                    }
                }
            }
        }
        Collections.sort(arrayList4, BackButtonMenu$$ExternalSyntheticLambda1.INSTANCE);
        return arrayList4;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getStackedHistoryDialogs$1(PulledDialog pulledDialog, PulledDialog pulledDialog2) {
        return pulledDialog2.stackIndex - pulledDialog.stackIndex;
    }

    public static void addToPulledDialogs(int i, int i2, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, long j, int i3, int i4) {
        if (tLRPC$Chat != null || tLRPC$User != null) {
            if (pulledDialogs == null) {
                pulledDialogs = new HashMap<>();
            }
            ArrayList arrayList = null;
            if (pulledDialogs.containsKey(Integer.valueOf(i))) {
                arrayList = pulledDialogs.get(Integer.valueOf(i));
            }
            if (arrayList == null) {
                HashMap<Integer, ArrayList<PulledDialog>> hashMap = pulledDialogs;
                Integer valueOf = Integer.valueOf(i);
                ArrayList arrayList2 = new ArrayList();
                hashMap.put(valueOf, arrayList2);
                arrayList = arrayList2;
            }
            boolean z = false;
            Iterator it = arrayList.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (((PulledDialog) it.next()).dialogId == j) {
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
                pulledDialog.stackIndex = i2;
                pulledDialog.dialogId = j;
                pulledDialog.filterId = i4;
                pulledDialog.folderId = i3;
                pulledDialog.chat = tLRPC$Chat;
                pulledDialog.user = tLRPC$User;
                arrayList.add(pulledDialog);
            }
        }
    }

    public static void clearPulledDialogs(int i, int i2) {
        ArrayList arrayList;
        HashMap<Integer, ArrayList<PulledDialog>> hashMap = pulledDialogs;
        if (hashMap != null && hashMap.containsKey(Integer.valueOf(i)) && (arrayList = pulledDialogs.get(Integer.valueOf(i))) != null) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                if (((PulledDialog) arrayList.get(i3)).stackIndex > i2) {
                    arrayList.remove(i3);
                    i3--;
                }
                i3++;
            }
        }
    }
}
