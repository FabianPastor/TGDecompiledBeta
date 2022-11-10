package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
/* loaded from: classes3.dex */
public class ContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private boolean allowBots;
    private boolean allowSelf;
    private boolean allowUsernameSearch;
    private int animationIndex;
    private boolean askAboutContacts;
    private AnimatorSet bounceIconAnimator;
    private long channelId;
    private long chatId;
    private boolean checkPermission;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private boolean disableSections;
    private StickerEmptyView emptyView;
    private RLottieImageView floatingButton;
    private FrameLayout floatingButtonContainer;
    private boolean floatingHidden;
    private AccelerateDecelerateInterpolator floatingInterpolator;
    private boolean hasGps;
    private LongSparseArray<TLRPC$User> ignoreUsers;
    private String initialSearchString;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ContactsAdapter listViewAdapter;
    private boolean needFinishFragment;
    private boolean needForwardCount;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    private long permissionRequestTime;
    private int prevPosition;
    private int prevTop;
    private boolean resetDelegate;
    private boolean returnAsResult;
    private boolean scrollUpdated;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;
    private boolean sortByName;
    private ActionBarMenuItem sortItem;

    /* loaded from: classes3.dex */
    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity);
    }

    public ContactsActivity(Bundle bundle) {
        super(bundle);
        this.floatingInterpolator = new AccelerateDecelerateInterpolator();
        this.allowSelf = true;
        this.allowBots = true;
        this.needForwardCount = true;
        this.needFinishFragment = true;
        this.resetDelegate = true;
        this.selectAlertString = null;
        this.allowUsernameSearch = true;
        this.askAboutContacts = true;
        this.checkPermission = true;
        this.animationIndex = -1;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        Bundle bundle = this.arguments;
        if (bundle != null) {
            this.onlyUsers = bundle.getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.allowSelf = this.arguments.getBoolean("allowSelf", true);
            this.channelId = this.arguments.getLong("channelId", 0L);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chatId = this.arguments.getLong("chat_id", 0L);
            this.disableSections = this.arguments.getBoolean("disableSections", false);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", false);
        } else {
            this.needPhonebook = true;
        }
        if (!this.createSecretChat && !this.returnAsResult) {
            this.sortByName = SharedConfig.sortContactsByName;
        }
        getContactsController().checkInviteText();
        getContactsController().reloadContactsStatusesMaybe();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        getNotificationCenter().onAnimationFinish(this.animationIndex);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationProgress(boolean z, float f) {
        super.onTransitionAnimationProgress(z, f);
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(17:1|(2:3|(1:5)(2:86|(1:88)(1:89)))(1:90)|6|(3:10|(1:12)(1:14)|13)|15|(1:17)(2:77|(2:79|(1:84)(1:83))(11:85|19|20|21|(2:23|(1:25)(1:73))(1:74)|26|(20:30|(1:32)(1:66)|33|(1:35)(1:65)|36|(1:38)|39|(1:41)(1:64)|42|(1:44)(1:63)|45|(1:47)|48|(1:50)(1:62)|51|(1:53)|54|(1:56)(1:61)|(1:58)(1:60)|59)|67|(1:69)|70|71))|18|19|20|21|(0)(0)|26|(21:28|30|(0)(0)|33|(0)(0)|36|(0)|39|(0)(0)|42|(0)(0)|45|(0)|48|(0)(0)|51|(0)|54|(0)(0)|(0)(0)|59)|67|(0)|70|71) */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x011a, code lost:
        r23.hasGps = false;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0135  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x013d  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0200  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0203  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0209  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x020c  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0216  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x021e  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0221  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0227  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x022a  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0265  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x02b5  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x02b8  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x02ce  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0331  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0334  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0338  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x033b  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0355  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View createView(android.content.Context r24) {
        /*
            Method dump skipped, instructions count: 864
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactsActivity.createView(android.content.Context):android.view.View");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(int i, View view, int i2) {
        Activity parentActivity;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        boolean z = false;
        boolean z2 = true;
        if (adapter == searchAdapter) {
            Object item = searchAdapter.getItem(i2);
            if (item instanceof TLRPC$User) {
                TLRPC$User tLRPC$User = (TLRPC$User) item;
                if (this.searchListViewAdapter.isGlobalSearch(i2)) {
                    ArrayList<TLRPC$User> arrayList = new ArrayList<>();
                    arrayList.add(tLRPC$User);
                    getMessagesController().putUsers(arrayList, false);
                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, false, true);
                }
                if (this.returnAsResult) {
                    LongSparseArray<TLRPC$User> longSparseArray = this.ignoreUsers;
                    if (longSparseArray != null && longSparseArray.indexOfKey(tLRPC$User.id) >= 0) {
                        return;
                    }
                    didSelectResult(tLRPC$User, true, null);
                    return;
                } else if (this.createSecretChat) {
                    if (tLRPC$User.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        return;
                    }
                    this.creatingChat = true;
                    SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), tLRPC$User);
                    return;
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", tLRPC$User.id);
                    if (!getMessagesController().checkCanOpenChat(bundle, this)) {
                        return;
                    }
                    presentFragment(new ChatActivity(bundle), true);
                    return;
                }
            } else if (!(item instanceof String)) {
                return;
            } else {
                String str = (String) item;
                if (str.equals("section")) {
                    return;
                }
                NewContactActivity newContactActivity = new NewContactActivity();
                newContactActivity.setInitialPhoneNumber(str, true);
                presentFragment(newContactActivity);
                return;
            }
        }
        int sectionForPosition = this.listViewAdapter.getSectionForPosition(i2);
        int positionInSectionForPosition = this.listViewAdapter.getPositionInSectionForPosition(i2);
        if (positionInSectionForPosition < 0 || sectionForPosition < 0) {
            return;
        }
        if ((!this.onlyUsers || i != 0) && sectionForPosition == 0) {
            if (this.needPhonebook) {
                if (positionInSectionForPosition == 0) {
                    presentFragment(new InviteContactsActivity());
                    return;
                } else if (positionInSectionForPosition != 1 || !this.hasGps) {
                    return;
                } else {
                    int i3 = Build.VERSION.SDK_INT;
                    if (i3 >= 23 && (parentActivity = getParentActivity()) != null && parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                        presentFragment(new ActionIntroActivity(1));
                        return;
                    }
                    if (i3 >= 28) {
                        z2 = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
                    } else if (i3 >= 19) {
                        try {
                            if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) != 0) {
                                z = true;
                            }
                            z2 = z;
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    }
                    if (!z2) {
                        presentFragment(new ActionIntroActivity(4));
                        return;
                    } else {
                        presentFragment(new PeopleNearbyActivity());
                        return;
                    }
                }
            } else if (i != 0) {
                if (positionInSectionForPosition != 0) {
                    return;
                }
                long j = this.chatId;
                if (j == 0) {
                    j = this.channelId;
                }
                presentFragment(new GroupInviteActivity(j));
                return;
            } else if (positionInSectionForPosition == 0) {
                presentFragment(new GroupCreateActivity(new Bundle()), false);
                return;
            } else if (positionInSectionForPosition == 1) {
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("onlyUsers", true);
                bundle2.putBoolean("destroyAfterSelect", true);
                bundle2.putBoolean("createSecretChat", true);
                bundle2.putBoolean("allowBots", false);
                bundle2.putBoolean("allowSelf", false);
                presentFragment(new ContactsActivity(bundle2), false);
                return;
            } else if (positionInSectionForPosition != 2) {
                return;
            } else {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (!BuildVars.DEBUG_VERSION && globalMainSettings.getBoolean("channel_intro", false)) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(bundle3));
                    return;
                }
                presentFragment(new ActionIntroActivity(0));
                globalMainSettings.edit().putBoolean("channel_intro", true).commit();
                return;
            }
        }
        Object mo1793getItem = this.listViewAdapter.mo1793getItem(sectionForPosition, positionInSectionForPosition);
        if (mo1793getItem instanceof TLRPC$User) {
            TLRPC$User tLRPC$User2 = (TLRPC$User) mo1793getItem;
            if (this.returnAsResult) {
                LongSparseArray<TLRPC$User> longSparseArray2 = this.ignoreUsers;
                if (longSparseArray2 != null && longSparseArray2.indexOfKey(tLRPC$User2.id) >= 0) {
                    return;
                }
                didSelectResult(tLRPC$User2, true, null);
            } else if (this.createSecretChat) {
                this.creatingChat = true;
                SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), tLRPC$User2);
            } else {
                Bundle bundle4 = new Bundle();
                bundle4.putLong("user_id", tLRPC$User2.id);
                if (!getMessagesController().checkCanOpenChat(bundle4, this)) {
                    return;
                }
                presentFragment(new ChatActivity(bundle4), true);
            }
        } else if (!(mo1793getItem instanceof ContactsController.Contact)) {
        } else {
            ContactsController.Contact contact = (ContactsController.Contact) mo1793getItem;
            final String str2 = !contact.phones.isEmpty() ? contact.phones.get(0) : null;
            if (str2 == null || getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("InviteUser", R.string.InviteUser));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    ContactsActivity.this.lambda$createView$0(str2, dialogInterface, i4);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(String str, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", str, null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(final TLRPC$User tLRPC$User, boolean z, final String str) {
        final EditTextBoldCursor editTextBoldCursor;
        if (z && this.selectAlertString != null) {
            if (getParentActivity() == null) {
                return;
            }
            if (tLRPC$User.bot) {
                if (tLRPC$User.bot_nochats) {
                    try {
                        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups)).show();
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                } else if (this.channelId != 0) {
                    TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", R.string.AddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", R.string.MakeAdmin), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda3
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ContactsActivity.this.lambda$didSelectResult$3(tLRPC$User, str, dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", R.string.CantAddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            String formatStringSimple = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(tLRPC$User));
            if (tLRPC$User.bot || !this.needForwardCount) {
                editTextBoldCursor = null;
            } else {
                formatStringSimple = String.format("%s\n\n%s", formatStringSimple, LocaleController.getString("AddToTheGroupForwardCount", R.string.AddToTheGroupForwardCount));
                editTextBoldCursor = new EditTextBoldCursor(getParentActivity());
                editTextBoldCursor.setTextSize(1, 18.0f);
                editTextBoldCursor.setText("50");
                editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
                editTextBoldCursor.setGravity(17);
                editTextBoldCursor.setInputType(2);
                editTextBoldCursor.setImeOptions(6);
                editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                editTextBoldCursor.addTextChangedListener(new TextWatcher(this) { // from class: org.telegram.ui.ContactsActivity.9
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable editable) {
                        try {
                            String obj = editable.toString();
                            if (obj.length() != 0) {
                                int intValue = Utilities.parseInt((CharSequence) obj).intValue();
                                if (intValue < 0) {
                                    editTextBoldCursor.setText("0");
                                    EditText editText = editTextBoldCursor;
                                    editText.setSelection(editText.length());
                                } else if (intValue > 300) {
                                    editTextBoldCursor.setText("300");
                                    EditText editText2 = editTextBoldCursor;
                                    editText2.setSelection(editText2.length());
                                } else {
                                    if (!obj.equals("" + intValue)) {
                                        EditText editText3 = editTextBoldCursor;
                                        editText3.setText("" + intValue);
                                        EditText editText4 = editTextBoldCursor;
                                        editText4.setSelection(editText4.length());
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                });
                builder2.setView(editTextBoldCursor);
            }
            builder2.setMessage(formatStringSimple);
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ContactsActivity.this.lambda$didSelectResult$4(tLRPC$User, editTextBoldCursor, dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder2.create());
            if (editTextBoldCursor == null) {
                return;
            }
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) editTextBoldCursor.getLayoutParams();
            if (marginLayoutParams != null) {
                if (marginLayoutParams instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) marginLayoutParams).gravity = 1;
                }
                int dp = AndroidUtilities.dp(24.0f);
                marginLayoutParams.leftMargin = dp;
                marginLayoutParams.rightMargin = dp;
                marginLayoutParams.height = AndroidUtilities.dp(36.0f);
                editTextBoldCursor.setLayoutParams(marginLayoutParams);
            }
            editTextBoldCursor.setSelection(editTextBoldCursor.getText().length());
            return;
        }
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(tLRPC$User, str, this);
            if (this.resetDelegate) {
                this.delegate = null;
            }
        }
        if (!this.needFinishFragment) {
            return;
        }
        finishFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$3(TLRPC$User tLRPC$User, String str, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(tLRPC$User, str, this);
            this.delegate = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$4(TLRPC$User tLRPC$User, EditText editText, DialogInterface dialogInterface, int i) {
        didSelectResult(tLRPC$User, false, editText != null ? editText.getText().toString() : "0");
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        Activity parentActivity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
        if (!this.checkPermission || Build.VERSION.SDK_INT < 23 || (parentActivity = getParentActivity()) == null) {
            return;
        }
        this.checkPermission = false;
        if (parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            return;
        }
        if (parentActivity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
            AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda7
                @Override // org.telegram.messenger.MessagesStorage.IntCallback
                public final void run(int i) {
                    ContactsActivity.this.lambda$onResume$5(i);
                }
            }).create();
            this.permissionDialog = create;
            showDialog(create);
            return;
        }
        askForPermissons(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$5(int i) {
        this.askAboutContacts = i != 0;
        if (i == 0) {
            return;
        }
        askForPermissons(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: org.telegram.ui.ContactsActivity.10
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    ContactsActivity.this.floatingButtonContainer.setTranslationY(ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0);
                    ContactsActivity.this.floatingButtonContainer.setClickable(!ContactsActivity.this.floatingHidden);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog == null || dialog != alertDialog || getParentActivity() == null || !this.askAboutContacts) {
            return;
        }
        askForPermissons(false);
    }

    @TargetApi(23)
    private void askForPermissons(boolean z) {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null || !UserConfig.getInstance(this.currentAccount).syncContacts || parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            return;
        }
        if (z && this.askAboutContacts) {
            showDialog(AlertsCreator.createContactsPermissionDialog(parentActivity, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda6
                @Override // org.telegram.messenger.MessagesStorage.IntCallback
                public final void run(int i) {
                    ContactsActivity.this.lambda$askForPermissons$6(i);
                }
            }).create());
            return;
        }
        this.permissionRequestTime = SystemClock.elapsedRealtime();
        ArrayList arrayList = new ArrayList();
        arrayList.add("android.permission.READ_CONTACTS");
        arrayList.add("android.permission.WRITE_CONTACTS");
        arrayList.add("android.permission.GET_ACCOUNTS");
        try {
            parentActivity.requestPermissions((String[]) arrayList.toArray(new String[0]), 1);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$askForPermissons$6(int i) {
        this.askAboutContacts = i != 0;
        if (i == 0) {
            return;
        }
        askForPermissons(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (iArr.length > i2 && "android.permission.READ_CONTACTS".equals(strArr[i2])) {
                    if (iArr[i2] == 0) {
                        ContactsController.getInstance(this.currentAccount).forceImportContacts();
                        return;
                    }
                    SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                    this.askAboutContacts = false;
                    edit.putBoolean("askAboutContacts", false).commit();
                    if (SystemClock.elapsedRealtime() - this.permissionRequestTime >= 200) {
                        return;
                    }
                    try {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", ApplicationLoader.applicationContext.getPackageName(), null));
                        getParentActivity().startActivity(intent);
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.closeSearchField();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ContactsAdapter contactsAdapter;
        if (i == NotificationCenter.contactsDidLoad) {
            ContactsAdapter contactsAdapter2 = this.listViewAdapter;
            if (contactsAdapter2 == null) {
                return;
            }
            if (!this.sortByName) {
                contactsAdapter2.setSortType(2, true);
            }
            this.listViewAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & intValue) != 0 || (MessagesController.UPDATE_MASK_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_STATUS & intValue) != 0) {
                updateVisibleRows(intValue);
            }
            if ((intValue & MessagesController.UPDATE_MASK_STATUS) == 0 || this.sortByName || (contactsAdapter = this.listViewAdapter) == null) {
                return;
            }
            contactsAdapter.sortOnlineContacts();
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (!this.createSecretChat || !this.creatingChat) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("enc_id", ((TLRPC$EncryptedChat) objArr[0]).id);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
        } else if (i != NotificationCenter.closeChats || this.creatingChat) {
        } else {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int i) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden == z) {
            return;
        }
        this.floatingHidden = z;
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        FrameLayout frameLayout = this.floatingButtonContainer;
        Property property = View.TRANSLATION_Y;
        float[] fArr = new float[1];
        fArr[0] = this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(300L);
        animatorSet.setInterpolator(this.floatingInterpolator);
        this.floatingButtonContainer.setClickable(!z);
        animatorSet.start();
    }

    public void setDelegate(ContactsActivityDelegate contactsActivityDelegate) {
        this.delegate = contactsActivityDelegate;
    }

    public void setInitialSearchString(String str) {
        this.initialSearchString = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showItemsAnimated() {
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        final int findLastVisibleItemPosition = linearLayoutManager == null ? 0 : linearLayoutManager.findLastVisibleItemPosition();
        this.listView.invalidate();
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ContactsActivity.11
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                ContactsActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = ContactsActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ContactsActivity.this.listView.getChildAt(i);
                    if (ContactsActivity.this.listView.getChildAdapterPosition(childAt) > findLastVisibleItemPosition) {
                        childAt.setAlpha(0.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay((int) ((Math.min(ContactsActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop())) / ContactsActivity.this.listView.getMeasuredHeight()) * 100.0f));
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public AnimatorSet onCustomTransitionAnimation(final boolean z, final Runnable runnable) {
        final ValueAnimator ofFloat;
        float[] fArr = {0.0f, 1.0f};
        if (z) {
            // fill-array-data instruction
            fArr[0] = 1.0f;
            fArr[1] = 0.0f;
            ofFloat = ValueAnimator.ofFloat(fArr);
        } else {
            ofFloat = ValueAnimator.ofFloat(fArr);
        }
        final ViewGroup viewGroup = (ViewGroup) this.fragmentView.getParent();
        BaseFragment baseFragment = this.parentLayout.getFragmentStack().size() > 1 ? this.parentLayout.getFragmentStack().get(this.parentLayout.getFragmentStack().size() - 2) : null;
        DialogsActivity dialogsActivity = baseFragment instanceof DialogsActivity ? (DialogsActivity) baseFragment : null;
        if (dialogsActivity == null) {
            return null;
        }
        final RLottieImageView floatingButton = dialogsActivity.getFloatingButton();
        final View view = floatingButton.getParent() != null ? (View) floatingButton.getParent() : null;
        if (this.floatingButtonContainer == null || view == null || floatingButton.getVisibility() != 0 || Math.abs(view.getTranslationY()) > AndroidUtilities.dp(4.0f) || Math.abs(this.floatingButtonContainer.getTranslationY()) > AndroidUtilities.dp(4.0f)) {
            return null;
        }
        view.setVisibility(8);
        if (z) {
            viewGroup.setAlpha(0.0f);
        }
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ContactsActivity.lambda$onCustomTransitionAnimation$7(ofFloat, viewGroup, valueAnimator);
            }
        });
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            ((ViewGroup) this.fragmentView).removeView(frameLayout);
            this.parentLayout.getOverlayContainerView().addView(this.floatingButtonContainer);
        }
        ofFloat.setDuration(150L);
        ofFloat.setInterpolator(new DecelerateInterpolator(1.5f));
        final AnimatorSet animatorSet = new AnimatorSet();
        final View view2 = view;
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ContactsActivity.12
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    if (ContactsActivity.this.floatingButtonContainer.getParent() instanceof ViewGroup) {
                        ((ViewGroup) ContactsActivity.this.floatingButtonContainer.getParent()).removeView(ContactsActivity.this.floatingButtonContainer);
                    }
                    ((ViewGroup) ((BaseFragment) ContactsActivity.this).fragmentView).addView(ContactsActivity.this.floatingButtonContainer);
                    view2.setVisibility(0);
                    if (!z) {
                        floatingButton.setAnimation(R.raw.write_contacts_fab_icon_reverse, 52, 52);
                        floatingButton.getAnimatedDrawable().setCurrentFrame(ContactsActivity.this.floatingButton.getAnimatedDrawable().getCurrentFrame());
                        floatingButton.playAnimation();
                    }
                }
                runnable.run();
            }
        });
        animatorSet.playTogether(ofFloat);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ContactsActivity.this.lambda$onCustomTransitionAnimation$8(animatorSet, z, view);
            }
        }, 50L);
        return animatorSet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCustomTransitionAnimation$7(ValueAnimator valueAnimator, ViewGroup viewGroup, ValueAnimator valueAnimator2) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        viewGroup.setTranslationX(AndroidUtilities.dp(48.0f) * floatValue);
        viewGroup.setAlpha(1.0f - floatValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$8(AnimatorSet animatorSet, boolean z, final View view) {
        this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, new int[]{NotificationCenter.diceStickersDidLoad}, false);
        animatorSet.start();
        if (z) {
            this.floatingButton.setAnimation(R.raw.write_contacts_fab_icon, 52, 52);
            this.floatingButton.playAnimation();
        } else {
            this.floatingButton.setAnimation(R.raw.write_contacts_fab_icon_reverse, 52, 52);
            this.floatingButton.playAnimation();
        }
        AnimatorSet animatorSet2 = this.bounceIconAnimator;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.bounceIconAnimator = new AnimatorSet();
        float duration = (float) this.floatingButton.getAnimatedDrawable().getDuration();
        long j = 0;
        int i = 4;
        if (z) {
            for (int i2 = 0; i2 < 6; i2++) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                if (i2 == 0) {
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.0f, 0.9f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.0f, 0.9f), ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 0.9f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 0.9f));
                    animatorSet3.setDuration(0.12765957f * duration);
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                } else if (i2 == 1) {
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.9f, 1.06f), ObjectAnimator.ofFloat(view, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.9f, 1.06f));
                    animatorSet3.setDuration(0.3617021f * duration);
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 2) {
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.06f, 0.9f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.06f, 0.9f), ObjectAnimator.ofFloat(view, View.SCALE_X, 1.06f, 0.9f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.06f, 0.9f));
                    animatorSet3.setDuration(0.21276596f * duration);
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 3) {
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.9f, 1.03f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.9f, 1.03f), ObjectAnimator.ofFloat(view, View.SCALE_X, 0.9f, 1.03f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.9f, 1.03f));
                    animatorSet3.setDuration(duration * 0.10638298f);
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 4) {
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.03f, 0.98f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.03f, 0.98f), ObjectAnimator.ofFloat(view, View.SCALE_X, 1.03f, 0.98f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.03f, 0.98f));
                    animatorSet3.setDuration(duration * 0.10638298f);
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else {
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.98f, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.98f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_X, 0.98f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.98f, 1.0f));
                    animatorSet3.setDuration(0.08510638f * duration);
                    animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_IN);
                }
                animatorSet3.setStartDelay(j);
                j += animatorSet3.getDuration();
                this.bounceIconAnimator.playTogether(animatorSet3);
            }
        } else {
            for (int i3 = 0; i3 < 5; i3++) {
                AnimatorSet animatorSet4 = new AnimatorSet();
                if (i3 == 0) {
                    Animator[] animatorArr = new Animator[i];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.0f, 0.9f);
                    animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.0f, 0.9f);
                    animatorArr[2] = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 0.9f);
                    animatorArr[3] = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 0.9f);
                    animatorSet4.playTogether(animatorArr);
                    animatorSet4.setDuration(0.19444445f * duration);
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                } else if (i3 == 1) {
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.9f, 1.06f), ObjectAnimator.ofFloat(view, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.9f, 1.06f));
                    animatorSet4.setDuration(0.22222222f * duration);
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i3 == 2) {
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.06f, 0.92f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.06f, 0.92f), ObjectAnimator.ofFloat(view, View.SCALE_X, 1.06f, 0.92f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.06f, 0.92f));
                    animatorSet4.setDuration(0.19444445f * duration);
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i3 == 3) {
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.92f, 1.02f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.92f, 1.02f), ObjectAnimator.ofFloat(view, View.SCALE_X, 0.92f, 1.02f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.92f, 1.02f));
                    animatorSet4.setDuration(0.25f * duration);
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else {
                    i = 4;
                    animatorSet4.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.02f, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.02f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_X, 1.02f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.02f, 1.0f));
                    animatorSet4.setDuration(duration * 0.10638298f);
                    animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_IN);
                    animatorSet4.setStartDelay(j);
                    j += animatorSet4.getDuration();
                    this.bounceIconAnimator.playTogether(animatorSet4);
                }
                i = 4;
                animatorSet4.setStartDelay(j);
                j += animatorSet4.getDuration();
                this.bounceIconAnimator.playTogether(animatorSet4);
            }
        }
        this.bounceIconAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ContactsActivity.13
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ContactsActivity.this.floatingButton.setScaleX(1.0f);
                ContactsActivity.this.floatingButton.setScaleY(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                ContactsActivity.this.bounceIconAnimator = null;
                ContactsActivity.this.getNotificationCenter().onAnimationFinish(ContactsActivity.this.animationIndex);
            }
        });
        this.bounceIconAnimator.start();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ContactsActivity.this.lambda$getThemeDescriptions$9();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, "chats_verifiedCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, "chats_verifiedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, "windowBackgroundWhiteBlueText3"));
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$9() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                }
            }
        }
    }
}
