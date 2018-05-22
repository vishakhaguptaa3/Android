package com.tnc.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.dialog.MessageSaveConfirmationDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

/**
 * Created by a3logics on 2/1/17.
 */

public class AddEditChatButtonCategory extends BaseFragmentTabs implements View.OnClickListener{

    private Activity             mActivity;
    private FrameLayout          flBackArrow,flInformationButton;

    private TextView             tvTitle,tvHeading;
    private Button               btnBack, btnSaveCategory, btnDelete;

    private EditText             etComposeCategory;

    private String               message = "", messagePrevious = "";
    int id;
    private INotifyGalleryDialog iNotifyGalleryDialog;

    private boolean isEditModeEnabled = false;

    public AddEditChatButtonCategory newInstance(Activity mActivity, int id,String message,
                                                 INotifyGalleryDialog iNotifyGalleryDialog)
    {
        AddEditChatButtonCategory frag = new AddEditChatButtonCategory();
        this.mActivity                 = mActivity;
        Bundle args                    = new Bundle();
        frag.setArguments(args);
        this.id                        = id;
        this.message                   = message;
        messagePrevious                = message;
        this.iNotifyGalleryDialog      = iNotifyGalleryDialog;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.add_edit_chat_button_category, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        flInformationButton=(FrameLayout) view.findViewById(R.id.flInformationButton);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvHeading=(TextView) view.findViewById(R.id.tvHeading);

        etComposeCategory=(EditText) view.findViewById(R.id.etComposeMessage);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        btnSaveCategory=(Button) view.findViewById(R.id.btnSaveMessage);

        btnDelete=(Button) view.findViewById(R.id.btnDelete);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");

        CustomFonts.setFontOfTextView(getActivity(),tvHeading, "fonts/Roboto-Bold_1.ttf");

        flBackArrow.setVisibility(View.VISIBLE);
        flInformationButton.setVisibility(View.GONE);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        if(message!=null && !message.trim().equals("")){
            isEditModeEnabled = true;
            etComposeCategory.setText(message);
        }

        if(isEditModeEnabled){
            btnDelete.setVisibility(View.VISIBLE);
            tvHeading.setText(getResources().getString(R.string.txtEditCategory));
        }else{
            tvHeading.setText(getResources().getString(R.string.txtAddCategory));
        }

        btnBack.setOnClickListener(this);
        btnSaveCategory.setOnClickListener(this);

        btnDelete.setOnClickListener(this);
        btnSaveCategory.setVisibility(View.GONE);

        etComposeCategory.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().equals(""))
                {
                    btnSaveCategory.setVisibility(View.VISIBLE);

                    // display delete button in case of empty text string in a textbox
                    if(isEditModeEnabled){
                        btnDelete.setVisibility(View.VISIBLE);
                    }
                }
                else if(s.toString().trim().equals("")){
                    btnSaveCategory.setVisibility(View.GONE);
                    // hide delete button in case of empty text string in a textbox
                    if(btnDelete.getVisibility() == View.VISIBLE){
                        btnDelete.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    /**
     *  interface to delete chat button categories messages
     */
    INotifyGalleryDialog iNotifyDeletecategory = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            int i=DBQuery.deleteChatButtonCategory(mActivity,String.valueOf(id));
            if(i>=1){
                GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etComposeCategory);
                ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();

                if(iNotifyGalleryDialog!=null)
                    iNotifyGalleryDialog.yes();
            }
            else{

            }
        }
        @Override
        public void no() {
        }
    };

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnBack)
        {
            GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etComposeCategory);
            ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
        }
        else if(view.getId()==R.id.btnSaveMessage)
        {
            if(etComposeCategory.getText().toString().trim().equals("")){
                ImageRequestDialog dialog=new ImageRequestDialog();
                dialog.newInstance("",mActivityTabs,"Please Enter Category","",null);
                dialog.show(getChildFragmentManager(), "test");
            }
            else if(messagePrevious!=null && !messagePrevious.trim().equalsIgnoreCase("") &&
                    messagePrevious.trim().equalsIgnoreCase(etComposeCategory.getText().toString().trim())){
                ImageRequestDialog dialog=new ImageRequestDialog();
                dialog.newInstance("", mActivityTabs,"Category already exists","",null,null);
                dialog.show(getChildFragmentManager(),"test");
            }else{

                int maxId=-1;
                int saveId=-1;

                MessageSaveConfirmationDialog objDialog=new MessageSaveConfirmationDialog();

                if(isEditModeEnabled){
                    // update database in case of edit mode
                    saveId = DBQuery.insertUpdateCategories(mActivityTabs, id,
                            etComposeCategory.getText().toString(),true,false);

                    if(saveId>0)  //In Case Of Update
                    {
                        objDialog.newInstance("",((HomeScreenActivity)mActivityTabs),"Category Updated Successfully",
                                iNotifyGalleryDialog,0);
                    }
                    objDialog.show(getChildFragmentManager(),"test");

                }else{
                    // add into database in case of edit mode
                    maxId  = DBQuery.getCategoriesMaxCount(getActivity());
                    saveId = DBQuery.insertUpdateCategories(mActivityTabs, maxId+1,
                            etComposeCategory.getText().toString(),false,false);

                    if(saveId > 0)  //In Case Of New Category Insertion
                    {
                        objDialog.newInstance("",((HomeScreenActivity)mActivityTabs),"Category Saved Successfully",
                                iNotifyGalleryDialog,0);
                    }
                    objDialog.show(getChildFragmentManager(),"test");
                }

                //check type     type -0 For Init     type -1 For resp
                /*if(isLocked)
                    saveInitResponseMessage(type,1);
                else if(!isLocked)
                    saveInitResponseMessage(type,0);
                saveState.setChanged(mActivity, true);*/
            }
        }
        else if(view.getId()==R.id.btnCancel)
        {
            GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etComposeCategory);
            ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
        }
        else if(view.getId()==R.id.btnDelete)
        {
            // check for existence of tiles of a particular, if tiles of this category exists then give the warning that category can't be deleted.

            int mCategoryID = DBQuery.checkCategoryExistenceInTiles(getActivity(), id);

            if(mCategoryID > 0){
                ImageRequestDialog mDialog = new ImageRequestDialog();
                mDialog.setCancelable(false);
                mDialog.newInstance("", getActivity(), getResources().getString(R.string.txtCategoryDeleteWarningMessage), "", null);
                mDialog.show(getChildFragmentManager(), "test");
            }else{
                MessageDeleteConfirmation dialogConfirmation=new MessageDeleteConfirmation();
                dialogConfirmation.newInstance("",mActivityTabs,"Do you want to delete this category?","", iNotifyDeletecategory);
                dialogConfirmation.show(getChildFragmentManager(), "test");
            }
        }
    }
}
