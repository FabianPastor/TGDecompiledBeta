package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ProfileActivity;

public class BackButtonMenu {

    public static class PulledDialog<T> {
        Class<T> activity;
        TLRPC.Chat chat;
        long dialogId;
        int filterId;
        int folderId;
        int stackIndex;
        TLRPC.User user;
    }

    public static ActionBarPopupWindow show(BaseFragment thisFragment, View backButton, long currentDialogId, Theme.ResourcesProvider resourcesProvider) {
        View fragmentView;
        Rect backgroundPaddings;
        View fragmentView2;
        Drawable thumb;
        String name;
        Drawable thumb2;
        BaseFragment baseFragment = thisFragment;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (baseFragment == null) {
            return null;
        }
        ActionBarLayout parentLayout = thisFragment.getParentLayout();
        Context context = thisFragment.getParentActivity();
        View fragmentView3 = thisFragment.getFragmentView();
        if (parentLayout == null || context == null) {
            View view = fragmentView3;
        } else if (fragmentView3 == null) {
            Activity activity = context;
            View view2 = fragmentView3;
        } else {
            ArrayList<PulledDialog> dialogs = getStackedHistoryDialogs(baseFragment, currentDialogId);
            if (dialogs.size() <= 0) {
                return null;
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, resourcesProvider2);
            Rect backgroundPaddings2 = new Rect();
            Drawable shadowDrawable = thisFragment.getParentActivity().getResources().getDrawable(NUM).mutate();
            shadowDrawable.getPadding(backgroundPaddings2);
            layout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider2));
            AtomicReference<ActionBarPopupWindow> scrimPopupWindowRef = new AtomicReference<>();
            int i = 0;
            while (i < dialogs.size()) {
                PulledDialog pDialog = dialogs.get(i);
                TLRPC.Chat chat = pDialog.chat;
                TLRPC.User user = pDialog.user;
                FrameLayout cell = new FrameLayout(context);
                cell.setMinimumWidth(AndroidUtilities.dp(200.0f));
                BackupImageView imageView = new BackupImageView(context);
                ArrayList<PulledDialog> dialogs2 = dialogs;
                imageView.setRoundRadius(AndroidUtilities.dp(32.0f));
                cell.addView(imageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 13.0f, 0.0f, 0.0f, 0.0f));
                TextView titleView = new TextView(context);
                Context context2 = context;
                titleView.setLines(1);
                Drawable shadowDrawable2 = shadowDrawable;
                titleView.setTextSize(1, 16.0f);
                titleView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem", resourcesProvider2));
                titleView.setEllipsize(TextUtils.TruncateAt.END);
                cell.addView(titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 59.0f, 0.0f, 12.0f, 0.0f));
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setSmallSize(true);
                AvatarDrawable avatarDrawable2 = avatarDrawable;
                if (chat != null) {
                    avatarDrawable.setInfo(chat);
                    fragmentView2 = fragmentView3;
                    if (chat.photo == null || chat.photo.strippedBitmap == null) {
                        thumb2 = avatarDrawable2;
                    } else {
                        thumb2 = chat.photo.strippedBitmap;
                    }
                    backgroundPaddings = backgroundPaddings2;
                    imageView.setImage(ImageLocation.getForChat(chat, 1), "50_50", thumb2, (Object) chat);
                    titleView.setText(chat.title);
                    Drawable drawable = thumb2;
                    TLRPC.Chat chat2 = chat;
                } else {
                    fragmentView2 = fragmentView3;
                    backgroundPaddings = backgroundPaddings2;
                    if (user != null) {
                        if (user.photo == null || user.photo.strippedBitmap == null) {
                            thumb = avatarDrawable2;
                        } else {
                            thumb = user.photo.strippedBitmap;
                        }
                        TLRPC.Chat chat3 = chat;
                        if (pDialog.activity == ChatActivity.class && UserObject.isUserSelf(user)) {
                            name = LocaleController.getString("SavedMessages", NUM);
                            avatarDrawable.setAvatarType(1);
                            imageView.setImageDrawable(avatarDrawable);
                        } else if (UserObject.isReplyUser(user)) {
                            name = LocaleController.getString("RepliesTitle", NUM);
                            avatarDrawable.setAvatarType(12);
                            imageView.setImageDrawable(avatarDrawable);
                        } else if (UserObject.isDeleted(user)) {
                            name = LocaleController.getString("HiddenName", NUM);
                            avatarDrawable.setInfo(user);
                            imageView.setImage(ImageLocation.getForUser(user, 1), "50_50", (Drawable) avatarDrawable, (Object) user);
                        } else {
                            name = UserObject.getUserName(user);
                            avatarDrawable.setInfo(user);
                            imageView.setImage(ImageLocation.getForUser(user, 1), "50_50", thumb, (Object) user);
                        }
                        titleView.setText(name);
                        Drawable drawable2 = thumb;
                    } else {
                        AvatarDrawable avatarDrawable3 = avatarDrawable2;
                    }
                }
                cell.setBackground(Theme.getSelectorDrawable(Theme.getColor("listSelectorSDK21", resourcesProvider2), false));
                cell.setOnClickListener(new BackButtonMenu$$ExternalSyntheticLambda0(scrimPopupWindowRef, pDialog, parentLayout, baseFragment));
                layout.addView(cell, LayoutHelper.createLinear(-1, 48));
                i++;
                long j = currentDialogId;
                context = context2;
                dialogs = dialogs2;
                shadowDrawable = shadowDrawable2;
                fragmentView3 = fragmentView2;
                backgroundPaddings2 = backgroundPaddings;
            }
            View fragmentView4 = fragmentView3;
            ArrayList<PulledDialog> arrayList = dialogs;
            Drawable drawable3 = shadowDrawable;
            ActionBarPopupWindow scrimPopupWindow = new ActionBarPopupWindow(layout, -2, -2);
            scrimPopupWindowRef.set(scrimPopupWindow);
            scrimPopupWindow.setPauseNotifications(true);
            scrimPopupWindow.setDismissAnimationDuration(220);
            scrimPopupWindow.setOutsideTouchable(true);
            scrimPopupWindow.setClippingEnabled(true);
            scrimPopupWindow.setAnimationStyle(NUM);
            scrimPopupWindow.setFocusable(true);
            layout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            scrimPopupWindow.setInputMethodMode(2);
            scrimPopupWindow.setSoftInputMode(0);
            scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
            layout.setFitItems(true);
            Rect backgroundPaddings3 = backgroundPaddings2;
            int popupX = AndroidUtilities.dp(8.0f) - backgroundPaddings3.left;
            if (AndroidUtilities.isTablet()) {
                int[] location = new int[2];
                fragmentView = fragmentView4;
                fragmentView.getLocationInWindow(location);
                popupX += location[0];
            } else {
                fragmentView = fragmentView4;
            }
            scrimPopupWindow.showAtLocation(fragmentView, 51, popupX, (backButton.getBottom() - backgroundPaddings3.top) - AndroidUtilities.dp(8.0f));
            return scrimPopupWindow;
        }
        return null;
    }

    static /* synthetic */ void lambda$show$0(AtomicReference scrimPopupWindowRef, PulledDialog pDialog, ActionBarLayout parentLayout, BaseFragment thisFragment, View e2) {
        if (scrimPopupWindowRef.get() != null) {
            ((ActionBarPopupWindow) scrimPopupWindowRef.getAndSet((Object) null)).dismiss();
        }
        if (pDialog.stackIndex >= 0) {
            Long nextFragmentDialogId = null;
            if (parentLayout == null || parentLayout.fragmentsStack == null || pDialog.stackIndex >= parentLayout.fragmentsStack.size()) {
                nextFragmentDialogId = null;
            } else {
                BaseFragment nextFragment = parentLayout.fragmentsStack.get(pDialog.stackIndex);
                if (nextFragment instanceof ChatActivity) {
                    nextFragmentDialogId = Long.valueOf(((ChatActivity) nextFragment).getDialogId());
                } else if (nextFragment instanceof ProfileActivity) {
                    nextFragmentDialogId = Long.valueOf(((ProfileActivity) nextFragment).getDialogId());
                }
            }
            if (nextFragmentDialogId != null && nextFragmentDialogId.longValue() != pDialog.dialogId) {
                for (int j = parentLayout.fragmentsStack.size() - 2; j > pDialog.stackIndex; j--) {
                    parentLayout.removeFragmentFromStack(j);
                }
            } else if (!(parentLayout == null || parentLayout.fragmentsStack == null)) {
                for (int j2 = parentLayout.fragmentsStack.size() - 2; j2 > pDialog.stackIndex; j2--) {
                    if (j2 >= 0 && j2 < parentLayout.fragmentsStack.size()) {
                        parentLayout.removeFragmentFromStack(j2);
                    }
                }
                if (pDialog.stackIndex < parentLayout.fragmentsStack.size()) {
                    parentLayout.showFragment(pDialog.stackIndex);
                    parentLayout.closeLastFragment(true);
                    return;
                }
            }
        }
        goToPulledDialog(thisFragment, pDialog);
    }

    public static void goToPulledDialog(BaseFragment fragment, PulledDialog dialog) {
        if (dialog != null) {
            if (dialog.activity == ChatActivity.class) {
                Bundle bundle = new Bundle();
                if (dialog.chat != null) {
                    bundle.putLong("chat_id", dialog.chat.id);
                } else if (dialog.user != null) {
                    bundle.putLong("user_id", dialog.user.id);
                }
                bundle.putInt("dialog_folder_id", dialog.folderId);
                bundle.putInt("dialog_filter_id", dialog.filterId);
                fragment.presentFragment(new ChatActivity(bundle), true);
            } else if (dialog.activity == ProfileActivity.class) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("dialog_id", dialog.dialogId);
                fragment.presentFragment(new ProfileActivity(bundle2), true);
            }
        }
    }

    public static ArrayList<PulledDialog> getStackedHistoryDialogs(BaseFragment thisFragment, long ignoreDialogId) {
        ActionBarLayout parentLayout;
        ArrayList<BaseFragment> fragmentsStack;
        ActionBarLayout parentLayout2;
        int folderId;
        long dialogId;
        TLRPC.Chat chat;
        int filterId;
        Class activity;
        ArrayList<PulledDialog> dialogs = new ArrayList<>();
        if (thisFragment == null || (parentLayout = thisFragment.getParentLayout()) == null) {
            return dialogs;
        }
        ArrayList<BaseFragment> fragmentsStack2 = parentLayout.fragmentsStack;
        ArrayList<PulledDialog> pulledDialogs = parentLayout.pulledDialogs;
        if (fragmentsStack2 != null) {
            int count = fragmentsStack2.size();
            int i = 0;
            while (i < count) {
                BaseFragment fragment = fragmentsStack2.get(i);
                TLRPC.User user = null;
                if (fragment instanceof ChatActivity) {
                    activity = ChatActivity.class;
                    ChatActivity chatActivity = (ChatActivity) fragment;
                    if (chatActivity.getChatMode() != 0) {
                        parentLayout2 = parentLayout;
                        fragmentsStack = fragmentsStack2;
                    } else if (chatActivity.isReport()) {
                        parentLayout2 = parentLayout;
                        fragmentsStack = fragmentsStack2;
                    } else {
                        chat = chatActivity.getCurrentChat();
                        user = chatActivity.getCurrentUser();
                        dialogId = chatActivity.getDialogId();
                        folderId = chatActivity.getDialogFolderId();
                        filterId = chatActivity.getDialogFilterId();
                    }
                    i++;
                    parentLayout = parentLayout2;
                    fragmentsStack2 = fragmentsStack;
                } else if (fragment instanceof ProfileActivity) {
                    Class activity2 = ProfileActivity.class;
                    ProfileActivity profileActivity = (ProfileActivity) fragment;
                    TLRPC.Chat chat2 = profileActivity.getCurrentChat();
                    try {
                        user = profileActivity.getUserInfo().user;
                    } catch (Exception e) {
                    }
                    long dialogId2 = profileActivity.getDialogId();
                    chat = chat2;
                    dialogId = dialogId2;
                    folderId = 0;
                    activity = activity2;
                    filterId = 0;
                } else {
                    parentLayout2 = parentLayout;
                    fragmentsStack = fragmentsStack2;
                    i++;
                    parentLayout = parentLayout2;
                    fragmentsStack2 = fragmentsStack;
                }
                if (dialogId == ignoreDialogId) {
                    parentLayout2 = parentLayout;
                    fragmentsStack = fragmentsStack2;
                } else if (ignoreDialogId != 0 || !UserObject.isUserSelf(user)) {
                    boolean alreadyAddedDialog = false;
                    int d = 0;
                    while (true) {
                        parentLayout2 = parentLayout;
                        if (d >= dialogs.size()) {
                            fragmentsStack = fragmentsStack2;
                            break;
                        }
                        fragmentsStack = fragmentsStack2;
                        if (dialogs.get(d).dialogId == dialogId) {
                            alreadyAddedDialog = true;
                            break;
                        }
                        d++;
                        parentLayout = parentLayout2;
                        fragmentsStack2 = fragmentsStack;
                    }
                    if (!alreadyAddedDialog) {
                        PulledDialog pDialog = new PulledDialog();
                        pDialog.activity = activity;
                        pDialog.stackIndex = i;
                        pDialog.chat = chat;
                        pDialog.user = user;
                        pDialog.dialogId = dialogId;
                        pDialog.folderId = folderId;
                        pDialog.filterId = filterId;
                        if (pDialog.chat != null || pDialog.user != null) {
                            dialogs.add(pDialog);
                        }
                    }
                } else {
                    parentLayout2 = parentLayout;
                    fragmentsStack = fragmentsStack2;
                }
                i++;
                parentLayout = parentLayout2;
                fragmentsStack2 = fragmentsStack;
            }
            ArrayList<BaseFragment> arrayList = fragmentsStack2;
        } else {
            ArrayList<BaseFragment> arrayList2 = fragmentsStack2;
        }
        if (pulledDialogs != null) {
            for (int i2 = pulledDialogs.size() - 1; i2 >= 0; i2--) {
                PulledDialog pulledDialog = pulledDialogs.get(i2);
                if (pulledDialog.dialogId != ignoreDialogId) {
                    boolean alreadyAddedDialog2 = false;
                    int d2 = 0;
                    while (true) {
                        if (d2 >= dialogs.size()) {
                            break;
                        } else if (dialogs.get(d2).dialogId == pulledDialog.dialogId) {
                            alreadyAddedDialog2 = true;
                            break;
                        } else {
                            d2++;
                        }
                    }
                    if (!alreadyAddedDialog2) {
                        dialogs.add(pulledDialog);
                    }
                }
            }
        }
        Collections.sort(dialogs, BackButtonMenu$$ExternalSyntheticLambda1.INSTANCE);
        return dialogs;
    }

    static /* synthetic */ int lambda$getStackedHistoryDialogs$1(PulledDialog d1, PulledDialog d2) {
        return d2.stackIndex - d1.stackIndex;
    }

    public static void addToPulledDialogs(BaseFragment thisFragment, int stackIndex, TLRPC.Chat chat, TLRPC.User user, long dialogId, int folderId, int filterId) {
        ActionBarLayout parentLayout;
        if ((chat != null || user != null) && thisFragment != null && (parentLayout = thisFragment.getParentLayout()) != null) {
            if (parentLayout.pulledDialogs == null) {
                parentLayout.pulledDialogs = new ArrayList<>();
            }
            boolean alreadyAdded = false;
            Iterator<PulledDialog> it = parentLayout.pulledDialogs.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().dialogId == dialogId) {
                        alreadyAdded = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!alreadyAdded) {
                PulledDialog d = new PulledDialog();
                d.activity = ChatActivity.class;
                d.stackIndex = stackIndex;
                d.dialogId = dialogId;
                d.filterId = filterId;
                d.folderId = folderId;
                d.chat = chat;
                d.user = user;
                parentLayout.pulledDialogs.add(d);
            }
        }
    }

    public static void clearPulledDialogs(BaseFragment thisFragment, int fromIndex) {
        ActionBarLayout parentLayout;
        if (thisFragment != null && (parentLayout = thisFragment.getParentLayout()) != null && parentLayout.pulledDialogs != null) {
            int i = 0;
            while (i < parentLayout.pulledDialogs.size()) {
                if (parentLayout.pulledDialogs.get(i).stackIndex > fromIndex) {
                    parentLayout.pulledDialogs.remove(i);
                    i--;
                }
                i++;
            }
        }
    }
}
