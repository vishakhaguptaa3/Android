package com.tnc.chat;

import android.content.Context;

import com.tnc.model.Chat;
import com.tnc.model.ChatInfo;


/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class ChatPresenter implements ChatContract.Presenter, ChatContract.OnSendMessageListener,
        ChatContract.OnGetMessagesListener {
    private ChatContract.View mView;
    private ChatInteractor mChatInteractor;

    public ChatPresenter(ChatContract.View view) {
        this.mView = view;
        mChatInteractor = new ChatInteractor(this, this);
    }

    @Override
    public void sendMessage(Context context, Chat chat, ChatInfo chatInfo, String receiverFirebaseToken) {
        mChatInteractor.sendMessageToFirebaseUser(context, chat,chatInfo, receiverFirebaseToken);
    }

    @Override
    public void sendMessageforGroup(Context context, Chat chat, String receiverFirebaseToken, String groupName) {
        mChatInteractor.sendMessageToFirebaseGroup(context, chat, receiverFirebaseToken,groupName);
    }

    @Override
    public void sendMessageWithImage(Context context, Chat chat, ChatInfo chatInfo, String receiverFirebaseToken) {
        mChatInteractor.sendMessageToFirebaseUserWithImage(context,chat,chatInfo,receiverFirebaseToken);
    }

    @Override
    public void getMessage(String senderUid, String receiverUid) {
        mChatInteractor.getMessageFromFirebaseUser(senderUid, receiverUid);
    }

    @Override
    public void getMessageForGroup(String groupName) {

    }

    @Override
    public void onSendMessageSuccess() {
        mView.onSendMessageSuccess();
    }

    @Override
    public void onSendMessageFailure(String message) {
        mView.onSendMessageFailure(message);
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        mView.onGetMessagesSuccess(chat);
    }

    @Override
    public void onUpdateChat(Chat chat) {
        mView.onUpdateMessage(chat);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }
}
