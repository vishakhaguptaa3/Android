package com.tnc.chat;

import android.content.Context;

import com.tnc.model.Chat;
import com.tnc.model.ChatInfo;


/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface ChatContract {
    interface View {
        void onSendMessageSuccess();
        void onUpdateMessage(Chat chat);

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, Chat chat, ChatInfo chatInfo, String receiverFirebaseToken);

        void sendMessageforGroup(Context context, Chat chat, String receiverFirebaseToken, String groupName);

        void sendMessageWithImage(Context context, Chat chat, ChatInfo chatInfo, String receiverFirebaseToken);

        void getMessage(String senderUid, String receiverUid);

        void getMessageForGroup(String groupName);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, Chat chat, ChatInfo chatInfo, String receiverFirebaseToken);

        void sendMessageToFirebaseGroup(Context context, Chat chat, String receiverFirebaseToken, String groupName);

        void sendMessageToFirebaseUserWithImage(Context context, Chat chat, ChatInfo chatInfo, String receiverFirebaseToken);

        void getMessageFromFirebaseUser(String senderUid, String receiverUid);

        void getMessageFromFirebaseGroup(String groupName);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(Chat chat);
        void onUpdateChat(Chat chat);

        void onGetMessagesFailure(String message);
    }
}
