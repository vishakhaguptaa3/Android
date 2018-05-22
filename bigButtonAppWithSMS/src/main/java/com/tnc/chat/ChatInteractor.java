package com.tnc.chat;

import android.content.Context;
import android.util.Log;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tnc.fcm.FcmNotificationBuilder;
import com.tnc.model.Chat;
import com.tnc.model.ChatInfo;
import com.tnc.utility.Constants;
import com.tnc.utility.SharedPrefUtil;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";
    //StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://crazyfirebasechat.appspot.com");

    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final Chat chat, final ChatInfo chatInfo, final String receiverFirebaseToken) {
        final String room_type_1 = chat.senderId + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderId;


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    /*databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).push().setValue(chatInfo);*/
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(Constants.ARG_SOLO_MESSAGES).push().setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                    /*databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).push().setValue(chatInfo);*/
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(Constants.ARG_SOLO_MESSAGES).push().setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    /*databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).push().setValue(chatInfo);*/
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(Constants.ARG_SOLO_MESSAGES).push().setValue(chat);
                    getMessageFromFirebaseUser(chat.senderId, chat.receiverUid);
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver(chat.displayName,chat.phoneNumber,
                        chat.text,
                        chat.senderId,
                        new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_UID),
                        receiverFirebaseToken);
                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void sendMessageToFirebaseGroup(final Context context, final Chat chat, final String receiverFirebaseToken, final String groupName) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_GROUPS).child(groupName).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    databaseReference.child(Constants.ARG_GROUPS).child(groupName).child(Constants.ARG_GROUPS_MESSAGES).setValue(chat);
                    //getMessageFromFirebaseUser(chat.senderUid, chat.receiverUid);

              /*  // send push notification to the receiver
                sendPushNotificationToReceiver(chat.sender,
                        chat.message,
                        chat.senderUid,
                        new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                        receiverFirebaseToken);
                mOnSendMessageListener.onSendMessageSuccess();*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void sendMessageToFirebaseUserWithImage(final Context context, final Chat chat, final ChatInfo chatInfo, final String receiverFirebaseToken) {
        final String room_type_1 = chat.senderId + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderId;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    /*databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).setValue(chatInfo);*/
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(Constants.ARG_SOLO_MESSAGES).push().setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                    /*databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).setValue(chatInfo);*/
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(Constants.ARG_SOLO_MESSAGES).push().setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(Constants.ARG_SOLO_MESSAGES).push().setValue(chat);
                    getMessageFromFirebaseUser(chat.senderId, chat.receiverUid);
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver(chat.displayName,chat.phoneNumber,
                        chat.text,
                        chat.senderId,
                        new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_UID),
                        receiverFirebaseToken);

                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }

    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String phoneNumber,
                                                String Firebaseuid,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .phoneNumber(phoneNumber)
                .uid(Firebaseuid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }

    @Override
    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).child(Constants.ARG_SOLO_MESSAGES).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onUpdateChat(chat);
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).child(Constants.ARG_SOLO_MESSAGES).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onUpdateChat(chat);
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });

    }

    @Override
    public void getMessageFromFirebaseGroup(String groupName) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(""/*room_type_1*/).addChildEventListener(new ChildEventListener() {// shivangi
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });

    }
}
