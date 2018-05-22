package com.tnc.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import com.tnc.R;
import com.tnc.adapter.ChatRecyclerAdapter;
import com.tnc.chat.ChatContract;
import com.tnc.chat.ChatPresenter;
import com.tnc.events.PushNotificationEvent;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.model.Chat;
import com.tnc.model.ChatInfo;
import com.tnc.utility.Constants;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import wseemann.media.FFmpegMediaMetadataRetriever;
import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment implements ChatContract.View, TextView.OnEditorActionListener, View.OnClickListener {

    public String TAG = ChatFragment.class.getSimpleName();
    DatabaseReference databaseReference;
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chatstasy-f0999.appspot.com");
    private ImageView mAttachment ;
    private INotifyGalleryDialog iNotifyRefreshSelectedTab;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int FILE_SELECT_CODE = 2;

    String room_type_2;
    public static ChatFragment newInstance(String email,
                                           String receiverUid,
                                           String firebaseToken) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_EMAIL, email);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_UID, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public static ChatFragment newInstanceForGroup(String groupName,
                                                   String firebaseToken) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_GROUPS_NAME, groupName);
        args.putString(Constants.ARG_FIREBASE_UID, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);

        Toast.makeText(getContext(), "Text.......", Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        setChatType();
        bindViews(fragmentView);

        return fragmentView;
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) view.findViewById(R.id.edit_text_message);
        mAttachment = (ImageView)view.findViewById(R.id.attachment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);
        mAttachment.setOnClickListener(this);

        mChatPresenter = new ChatPresenter(this);

        if (getArguments().getString(Constants.ARG_RECEIVER_UID)!=null) {

            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    getArguments().getString(Constants.ARG_RECEIVER_UID));

        }else {

            mChatPresenter.getMessageForGroup(getArguments().getString(Constants.ARG_GROUPS_NAME));
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Log.e("2", "2");
        try {
            Log.e("intent","intent"+intent.getType());
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
        }

       /* VideoView videoView= null;

        videoView.requestFocus();
        videoView.start();
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    progressBarLandScape.setVisibility(View.GONE);
                    return true;
                }
                else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    progressBarLandScape.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });*/

    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public void retriveThumbnailFromLibrary(String videoPath){

        Bitmap bitmap ;
        FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
        try {
            fmmr.setDataSource(videoPath);

            Bitmap b = fmmr.getFrameAtTime();

            if (b != null) {
                Bitmap b2 = fmmr.getFrameAtTime(4000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                if (b2 != null) {
                    b = b2;
                }

            }

            if (b != null) {
                Log.i("Thumbnail", "Extracted frame");
                bitmap = b;
            } else {
                Log.e("Thumbnail", "Failed to extract frame");

            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } finally {
            fmmr.release();
        }
    }

   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Bitmap retriveThumbnailfromThumbnailsUtills(){
        Bitmap thumb = null;
        Uri videoUri = data.getData();
        String selectedPathVideo="";
        selectedPathVideo = ImageFilePath.getPath( getActivity(), videoUri);
        Log.i("Image File Path", ""+selectedPathVideo);

        try {
           thumb  = ThumbnailUtils.createVideoThumbnail(selectedPathVideo, MediaStore.Video.Thumbnails.MICRO_KIND);


           // imgFarmerVideo.setImageBitmap(thumb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thumb;
    }*/



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Log.d("onActivityResult", "......");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            UploadPostTask uploadPostTask = new UploadPostTask();
            uploadPostTask.execute(imageBitmap);


        }if (resultCode == RESULT_OK) {
            if (requestCode == FILE_SELECT_CODE) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {



                    /*Intent intent = new Intent(HomeActivity.this,
                            VideoplayAvtivity.class);
                    intent.putExtra("path", selectedImagePath);
                    startActivity(intent);*/
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public ChatFragment newInstance(INotifyGalleryDialog iNotifyRefreshSelectedTab) {
        ChatFragment frag = new ChatFragment();
        this.iNotifyRefreshSelectedTab = iNotifyRefreshSelectedTab;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    private class UploadPostTask
            extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            storageRef.child(UUID.randomUUID().toString() + "jpg").putBytes(
                    byteArrayOutputStream.toByteArray()).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getDownloadUrl() != null) {
                                final String imageUrl = taskSnapshot.getDownloadUrl().toString();
                                String storageImageUrl = taskSnapshot.getStorage().toString()!=null?taskSnapshot.getStorage().toString():null;
                                Log.e("imageURL", imageUrl);
                                String message = "";
                                String receiver = getArguments().getString(Constants.ARG_EMAIL);
                                String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                String Firebaseuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_UID);
                                String imageA = imageUrl;
                                String imageI = storageImageUrl;
                                ChatInfo chatInfo = new ChatInfo("Solo");
                                Chat chat = new Chat(email,
                                        email,
                                        Firebaseuid,
                                        receiverUid,
                                        message,
                                        String.valueOf(System.currentTimeMillis()),imageA, imageI);

                                mChatPresenter.sendMessageWithImage(getActivity().getApplicationContext(),
                                        chat,chatInfo,
                                        receiverFirebaseToken);
                                /*Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                String chkdate = simpleDateFormat.format(timestamp);
                                newMessage.convertdate = chkdate;
                                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);

                                databaseReference.push().setValue(message);*/


                            }else {
                                Log.d("sdsd", "sfd");
                            }
                        }
                    });

            return null;
        }
    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (getArguments().getString(Constants.ARG_GROUPS_NAME)==null) {
                sendMessage();
            }else {
                sendMessageforGroup();
            }
            return true;
        }
        return false;
    }

    private void sendMessageforGroup() {
        String message = mETxtMessage.getText().toString();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_UID);

        Chat chat = new Chat(senderUid,message);
        mChatPresenter.sendMessageforGroup(getActivity().getApplicationContext(),chat,receiverFirebaseToken,getArguments().getString(Constants.ARG_GROUPS_NAME));
    }

    private void sendMessage() {
        String displayName = FirebaseAuth.getInstance().getCurrentUser().getEmail().
                substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().lastIndexOf("@"));
        String message = mETxtMessage.getText().toString();
        String receiver = getArguments().getString(Constants.ARG_EMAIL);
        String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
        String email = receiver;
        String Firebaseuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_UID);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        String fuid = databaseReference.child(Constants.ARG_CHAT_ROOMS).child(Constants.ARG_SOLO_MESSAGES).push().getKey();
        String createdOn = String.valueOf(System.currentTimeMillis());

        Log.e(TAG, "dataSnapshot   timestamp 11....... " + "\n"+
                "displayName......" + displayName +"\n"+
                "message......." + message  +"\n" +
                "email..........." + email +"\n"+
                "receiver.........." + receiver +"\n"+
                "receiverUid........" + receiverUid +"\n"+
                "Firebaseuid........." + Firebaseuid +"\n"+
                "receiverFirebaseToken..........." + receiverFirebaseToken +"\n"+
                "deviceToken.........." + deviceToken +"\n"+
                "uid.........." + fuid
        );

        ChatInfo chatInfo = new ChatInfo("Solo");
        Chat chat = new Chat(displayName,
                email,
                Firebaseuid,
                receiverUid,
                message,
                String.valueOf(System.currentTimeMillis()),
                deviceToken,
                fuid
        );

        mChatPresenter.sendMessage(getActivity().getApplicationContext(),
                chat,chatInfo,
                receiverFirebaseToken);
    }
    /* private void sendMessageWithImage() {
         String message = "";
         String receiver = getArguments().getString(Constants.ARG_RECEIVER);
         String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
         String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
         String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
         Chat chat = new Chat(sender,
                 receiver,
                 senderUid,
                 receiverUid,
                 message,
                 System.currentTimeMillis());
         mChatPresenter.sendMessageWithImage(getActivity().getApplicationContext(),
                 chat,
                 receiverFirebaseToken);
     }

    */
    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateMessage(Chat chat) {
        mChatRecyclerAdapter.update(chat);
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(getActivity(),new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        try{
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.attachment:
                //dispatchTakePictureIntent();
                showFileChooser();
                break;
        }
    }

    void setChatType(){

        final String room_type_1 = FirebaseAuth.getInstance().getCurrentUser().getUid();// + "_" + getArguments().getString(Constants.ARG_RECEIVER_UID);
        try {
             room_type_2 = getArguments().getString(Constants.ARG_RECEIVER_UID);// + "_" + FirebaseAuth.getInstance().getCurrentUser().getUid();

        }catch (NullPointerException e){
            e.printStackTrace();
        }
        final Map<String,Object> map = new HashMap<>();
        map.put("chatType","one-to-one");


        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {

//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);

                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).updateChildren(map);
                    //                   Log.e(TAG, "databaseReference 11....... " + databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).push().getKey());

                } else if (dataSnapshot.hasChild(room_type_2)) {

//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).updateChildren(map);
                    Log.e(TAG, "databaseReference 11f....... " + databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).updateChildren(map));


                } else {
//                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);

                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).updateChildren(map);
                    //                   Log.e(TAG, "databaseReference 11f.dd...... " +  databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).updateChildren(map));


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("canceled failure", databaseError.getMessage());
            }
        });

    }
}