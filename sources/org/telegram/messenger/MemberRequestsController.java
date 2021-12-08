package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class MemberRequestsController extends BaseController {
    private static final MemberRequestsController[] instances = new MemberRequestsController[3];
    private final LongSparseArray<TLRPC.TL_messages_chatInviteImporters> firstImportersCache = new LongSparseArray<>();

    public static MemberRequestsController getInstance(int accountNum) {
        MemberRequestsController[] memberRequestsControllerArr = instances;
        MemberRequestsController local = memberRequestsControllerArr[accountNum];
        if (local == null) {
            synchronized (MemberRequestsController.class) {
                local = memberRequestsControllerArr[accountNum];
                if (local == null) {
                    local = new MemberRequestsController(accountNum);
                    memberRequestsControllerArr[accountNum] = local;
                }
            }
        }
        return local;
    }

    public MemberRequestsController(int accountNum) {
        super(accountNum);
    }

    public TLRPC.TL_messages_chatInviteImporters getCachedImporters(long chatId) {
        return this.firstImportersCache.get(chatId);
    }

    public int getImporters(long chatId, String query, TLRPC.TL_chatInviteImporter lastImporter, LongSparseArray<TLRPC.User> users, RequestDelegate onComplete) {
        boolean isEmptyQuery = TextUtils.isEmpty(query);
        TLRPC.TL_messages_getChatInviteImporters req = new TLRPC.TL_messages_getChatInviteImporters();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-chatId);
        req.requested = true;
        req.limit = 30;
        if (!isEmptyQuery) {
            req.q = query;
            req.flags |= 4;
        }
        if (lastImporter == null) {
            req.offset_user = new TLRPC.TL_inputUserEmpty();
        } else {
            req.offset_user = getMessagesController().getInputUser(users.get(lastImporter.user_id));
            req.offset_date = lastImporter.date;
        }
        return getConnectionsManager().sendRequest(req, new MemberRequestsController$$ExternalSyntheticLambda1(this, chatId, onComplete));
    }

    /* renamed from: lambda$getImporters$1$org-telegram-messenger-MemberRequestsController  reason: not valid java name */
    public /* synthetic */ void m909x928219c8(long chatId, RequestDelegate onComplete, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MemberRequestsController$$ExternalSyntheticLambda0(this, error, response, chatId, onComplete));
    }

    /* renamed from: lambda$getImporters$0$org-telegram-messenger-MemberRequestsController  reason: not valid java name */
    public /* synthetic */ void m908x92var_fc7(TLRPC.TL_error error, TLObject response, long chatId, RequestDelegate onComplete) {
        if (error == null) {
            this.firstImportersCache.put(chatId, (TLRPC.TL_messages_chatInviteImporters) response);
        }
        onComplete.run(response, error);
    }

    public void onPendingRequestsUpdated(TLRPC.TL_updatePendingJoinRequests update) {
        long peerId = MessageObject.getPeerId(update.peer);
        this.firstImportersCache.put(-peerId, (Object) null);
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(-peerId);
        if (chatFull != null) {
            chatFull.requests_pending = update.requests_pending;
            chatFull.recent_requesters = update.recent_requesters;
            chatFull.flags |= 131072;
            getMessagesStorage().updateChatInfo(chatFull, false);
            getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, 0, false, false);
        }
    }
}
