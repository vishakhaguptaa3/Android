package com.tnc.adapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.tnc.R;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CategoryBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.KeyIntValuePairBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.Name;
import com.tnc.database.DBQuery;
import com.tnc.imageloader.ImageLoadTask;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.RoundedImageViewCircular;
import com.tnc.widgets.CustomSpinner;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TncUserNotifyRegistrationAdapter extends BaseAdapter
{
    private Context mContext;
    public ArrayList<BBContactsBean> listContacts;
    private ArrayList<BBContactsBean> listContactsFiltered;
    private ArrayList<BBContactsBean> listFirstName = null;
    private ArrayList<BBContactsBean> listLastName = null;
    private ArrayList<BBContactsBean> listContainsName = null;
    private int selectedPosition;
    private boolean isColorSet;
    private ImageLoadTask imageLoader=null;
    private Bitmap _bmp=null;
    private SharedPreference pref=null;
    private int BBID=0;
    private Bitmap displayImage=null;
    private String displayName="";
    private String phoneNumber="";
    private ArrayList<ContactTilesBean> listContactTiles=null;
    private ArrayList<BBContactsBean> listBBContacts=null;
    private INotifyGalleryDialog iNotifyUncheckBox;
    private boolean isAllSelected;
    public ArrayList<BBContactsBean> listTncContactsAdded=new ArrayList<BBContactsBean>();

    private ArrayList<CategoryBean> mListCategoryFromDB = new ArrayList<CategoryBean>();

    public ArrayList<String> mListCategory = new ArrayList<String>();

    private ArrayList<TextView> mListTextViewCategory = new ArrayList<TextView>();

    private ArrayList<ImageView> mListImageViewCategory = new ArrayList<ImageView>();

    private ArrayList<CheckBox> mListImageViewCheckBoxes = new ArrayList<CheckBox>();

    private boolean isFirstTime = true;

//    private int mListButtonClickedPosition = -1;

    private ListPopupWindow mPopupWindow = null;

    private Typeface mTypeFace;

    private int mPositionCategorySelected = -1;

    private boolean isNotifyMode = false;

    public TncUserNotifyRegistrationAdapter(Context mContext,
                                            ArrayList<BBContactsBean> listContacts,
                                            INotifyGalleryDialog iNotifyUncheckBox,
                                            boolean isNotifyMode)
    {
        this.mContext=mContext;
        this.listContacts         = new ArrayList<BBContactsBean>();
        this.listContacts         = listContacts;
        this.listContactsFiltered = new ArrayList<BBContactsBean>();
        this.listContactsFiltered.addAll(listContacts);
        this.iNotifyUncheckBox    = iNotifyUncheckBox;
        this.isNotifyMode         = isNotifyMode;
        _bmp                      = ((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.no_image))).getBitmap();
        pref                      = new SharedPreference();

        // Fetch Categories from the Database and set into arraylist to display it into drop - down item
        mListCategoryFromDB       = new ArrayList<CategoryBean>();
        mListCategoryFromDB       = DBQuery.getCategoriesForTiles(mContext);

        mListCategory             = new ArrayList<String>();

        for(CategoryBean mCategoryBean : mListCategoryFromDB){
            if(mCategoryBean.getCategoryName()!=null && !mCategoryBean.getCategoryName().trim().equalsIgnoreCase("") &&
                    !mCategoryBean.getCategoryID().equalsIgnoreCase("0") &&
                    !mCategoryBean.getCategoryID().equalsIgnoreCase("1"))  // 0 - For Recent Calls, 1 - For All
                mListCategory.add(mCategoryBean.getCategoryName());
        }

        mListTextViewCategory    = new ArrayList<TextView>();
        mListImageViewCategory   = new ArrayList<ImageView>();
        mListImageViewCheckBoxes = new ArrayList<CheckBox>();
    }

    /**
     *
     * @param position
     * @param isColorSet
     * set selected List item row color and content color
     */
    public void setRowColor(int position, boolean isColorSet) {
        selectedPosition = position;
        this.isColorSet = isColorSet;
        this.notifyDataSetInvalidated();
    }

    /**
     *
     * @param isAllSelected
     * set selectedstate of checkbox
     */
    public void setAllSelected(boolean isAllSelected)
    {
        this.isAllSelected=isAllSelected;
        this.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return listContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return listContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {

        FrameLayout flImageHolder;

        LinearLayout llContactNameHolder;

        TextView tvContactName, tvCategory;

        ImageView imViewContactImage;

        CheckBox chkBoxSelection;

        Button mBtnCategoryMenu;
    }

    ViewHolder holder;

    @SuppressWarnings("deprecation")
    @SuppressLint({ "InflateParams", "NewApi" })
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = null;
        BBID=0;
        displayImage=null;
        boolean isTncUser = false;

        // reuse views
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tncusersnotifyregistrationadapter, null);
            // configure view holder
            holder = new ViewHolder();
            holder.tvContactName = (TextView) convertView
                    .findViewById(R.id.tvContactName);
            holder.tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
            holder.imViewContactImage=(ImageView)convertView.findViewById(R.id.imViewContactImage);
            holder.flImageHolder = (FrameLayout) convertView.findViewById(R.id.flImageHolder);
            holder.llContactNameHolder=(LinearLayout) convertView.findViewById(R.id.llContactNameHolder);
            holder.chkBoxSelection=(CheckBox) convertView.findViewById(R.id.chkBoxSelection);
            holder.mBtnCategoryMenu = (Button) convertView.findViewById(R.id.btnCategoryMenu);
            //holder.spinnerCategory = (CustomSpinner) convertView.findViewById(R.id.spinnerCategory);
            convertView.setTag(holder);
        } else {
            // fill data
            holder = (ViewHolder) convertView.getTag();
        }

        holder.chkBoxSelection.setVisibility(View.VISIBLE);
        holder.llContactNameHolder.setVisibility(View.GONE);

        holder.chkBoxSelection.setTag(position);
        holder.mBtnCategoryMenu.setTag(position);
        holder.tvContactName.setTag(position);

        mListTextViewCategory.add(holder.tvCategory);
        mListImageViewCategory.add(holder.imViewContactImage);
        mListImageViewCheckBoxes.add(holder.chkBoxSelection);

        if(isNotifyMode){
            // in notifying mode, hide the category menu display button and selected category name label
            holder.mBtnCategoryMenu.setVisibility(View.GONE);
            holder.tvCategory.setVisibility(View.GONE);
        }else{
            holder.mBtnCategoryMenu.setVisibility(View.VISIBLE);
            holder.tvCategory.setVisibility(View.VISIBLE);
        }

        if(isFirstTime){

            if(position == listContacts.size()-1){
                isFirstTime =false;
            }

            if(mListCategory!=null && mListCategory.size()>0) {
                holder.tvCategory.setText(mListCategory.get(0));
            }
        }

        holder.tvContactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    int mPosition = Integer.parseInt(String.valueOf(view.getTag()));
                    if(mPosition > -1){
                        isAllSelected = false;
                        mListImageViewCheckBoxes.get(mPosition).performClick();
                    }
                }catch(Exception e){
                    e.getMessage();
                }
            }
        });

        holder.chkBoxSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewCheckBox) {

                CheckBox cb = (CheckBox)viewCheckBox;
                int mPosition = Integer.parseInt(String.valueOf(cb.getTag()));

                isAllSelected = false;

                if(cb.isChecked()){
                    if(listTncContactsAdded!=null && !listTncContactsAdded.contains(listContacts.get(mPosition))){
                        //Set Checked State
                        listContacts.get(mPosition).setContactChecked(true);

                        // SetCategory ID
                        listContacts.get(mPosition).setCategory(
                                GlobalConfig_Methods.getCategoryIdFromString(mContext, mListTextViewCategory.get(mPosition).getText().toString()));

                        //listContacts.get(position).setCategory(GlobalConfig_Methods.getCategoryIdFromString(mContext, mListTextViewCategory.get(position).getText().toString()));

                        // Set Image as ByteArray in the List To use it for Creating the Tiles on the HomeScreen
                        ImageView mImageView = mListImageViewCategory.get(mPosition);

                        Bitmap mBitmapImage = ((BitmapDrawable)((ImageView)mImageView).getDrawable()).getBitmap();

                        if(mBitmapImage!=null && mBitmapImage.getByteCount()>0){
                            try{
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                listContacts.get(mPosition).setmImageArray(byteArray);
                            }catch(Exception e){
                                e.getMessage();
                            }
                        }
                        listTncContactsAdded.add(listContacts.get(mPosition));
                    }
                }else{

                    //Set Checked State
                    listContacts.get(mPosition).setContactChecked(false);

                    // remove Item object from the list
                    BBContactsBean objContactDetails = new BBContactsBean();
                    objContactDetails=listContacts.get((int)cb.getTag());
                    if(listTncContactsAdded.contains(objContactDetails)){
                        listTncContactsAdded.remove(objContactDetails);
                    }

                }
                if(iNotifyUncheckBox!=null){
                    iNotifyUncheckBox.yes();
                }
            }
        });

        // Display category available
        if(!isNotifyMode)
            holder.mBtnCategoryMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View buttonView) {
                    mPositionCategorySelected = Integer.parseInt(String.valueOf(buttonView.getTag()));
                    if(mPositionCategorySelected > -1){

                        openCategoriesPopup(((Button) buttonView));

//                    listContacts.get(mListButtonClickedPosition).setCategory("");
                    }
                }
            });

        listBBContacts=new ArrayList<BBContactsBean>();
        try {
            if (isColorSet && position == selectedPosition) {
                holder.tvContactName.setTextColor(mContext.getResources().getColor(
                        R.color.white));
                ((LinearLayout) convertView).setBackgroundColor(mContext
                        .getResources().getColor(R.color.stripDarkBlueColor));
            } else {
                holder.tvContactName.setTextColor(mContext.getResources().getColor(
                        R.color.textGreyColorChooseConatct));
                ((LinearLayout) convertView).setBackgroundColor(mContext
                        .getResources().getColor(R.color.white));
            }
            CustomFonts.setFontOfTextView(mContext, holder.tvContactName,"fonts/Roboto-Regular_1.ttf");
            holder.imViewContactImage.setVisibility(View.VISIBLE);
            /*holder.llContactNameHolder.setVisibility(View.VISIBLE);*/

            /*holder.llContactNameHolder.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notificationimageborder));*/
            holder.tvContactName.setText(listContacts.get(position).getName());
            phoneNumber=listContacts.get(position).getPhoneNumber();
            listContactTiles= DBQuery.getTileFromPhoneNumber(mContext, GlobalConfig_Methods.trimSpecialCharactersFromString(phoneNumber));
            displayName="";
        } catch (Exception e) {
            e.getMessage();
        }
        if(listContactTiles.size()>0)  // In case of Tile of the number exists
        {
            if(listContactTiles.get(0).isIsTncUser()){
                // Enable is Tnc User to true
                isTncUser = true;
            }

            displayName=listContactTiles.get(0).getName();
            if(listContactTiles.get(0).getImage()!=null && listContactTiles.get(0).getImage().length>0)
            {
                byte arrayImage[]=listContactTiles.get(0).getImage();
                if(arrayImage!=null && arrayImage.length>0)
                {
                    holder.imViewContactImage.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
                }
            }
            else {
                listBBContacts=DBQuery.checkBBContactExistence(mContext,listContacts.get(position).getBBID());
                if(listBBContacts.size()>0)
                {
                    BBID=listBBContacts.get(0).getBBID();
                }
                if(BBID!=0)
                {
                    // Enable is Tnc User to true
                    isTncUser = true;
                    if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
                    {
                        imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewContactImage,320);
                        imageLoader.execute();
                    }
                    else{
                        displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                        if(displayImage!=null)
                        {
                            holder.imViewContactImage.setImageBitmap(displayImage);
                        }
                        else{
                            holder.imViewContactImage.setImageBitmap(_bmp);
                        }
                    }
                }
                else{
                    displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                    if(displayImage!=null)
                    {
                        holder.imViewContactImage.setImageBitmap(displayImage);
                    }
                    else{
                        holder.imViewContactImage.setImageBitmap(_bmp);
                    }
                }
            }
        }
        else {
            // In case of tile of that number doesn't exists
            listBBContacts=DBQuery.checkBBContactExistence(mContext,listContacts.get(position).getBBID());
            if(listBBContacts.size()>0)
            {
                BBID=listBBContacts.get(0).getBBID();
            }
            if(BBID!=0)
            {
                // Enable is Tnc User to true
                isTncUser = true;

                displayName=listBBContacts.get(0).getName();
                if(listBBContacts.get(0).getImage()!=null && !listBBContacts.get(0).getImage().trim().equals("") && !listBBContacts.get(0).getImage().equalsIgnoreCase("NULL"))
                {
                    imageLoader=new ImageLoadTask(mContext,listBBContacts.get(0).getImage(),holder.imViewContactImage,320);
                    imageLoader.execute();
                }
                else{
                    displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                    if(displayImage!=null)
                    {
                        holder.imViewContactImage.setImageBitmap(displayImage);
                    }
                    else{
                        holder.imViewContactImage.setImageBitmap(_bmp);
                    }
                }
            }
            else{
                if(!phoneNumber.equals(""))
                {
                    displayName=GlobalConfig_Methods.getContactName(mContext, phoneNumber);
                    displayImage=GlobalConfig_Methods.getContactBitmap(mContext, phoneNumber);
                }
                if(displayImage!=null)
                {
                    holder.imViewContactImage.setImageBitmap(displayImage);
                }
                else{
                    holder.imViewContactImage.setImageBitmap(_bmp);
                }
            }
        }
        if(!displayName.trim().equalsIgnoreCase(""))
            holder.tvContactName.setText(displayName);
        else if(listContacts.get(position).getName().equalsIgnoreCase("No Contact Found") || listContacts.get(position).getName().equalsIgnoreCase("No matching record found"))
        {
            holder.tvContactName.setText(listContacts.get(position).getName());
            holder.chkBoxSelection.setVisibility(View.GONE);
        }
        if(listContacts.get(position).getName().equalsIgnoreCase("No Contact Found") || listContacts.get(position).getName().equalsIgnoreCase("No matching record found"))
        {
            holder.imViewContactImage.setVisibility(View.GONE);
            holder.flImageHolder.setVisibility(View.GONE);
            holder.llContactNameHolder.setVisibility(View.GONE);

            /*holder.llContactNameHolder.setVisibility(View.GONE);*/
        }

        // In case checkbox for Select All is checked
        if(isAllSelected){
            holder.chkBoxSelection.setChecked(true);

            // Set Category ID
            listContacts.get(position).setCategory(GlobalConfig_Methods.getCategoryIdFromString(mContext, mListTextViewCategory.get(position).getText().toString()));

            // Set Image as ByteArray in the List To use it for Creating the Tiles on the HomeScreen
            ImageView mImageView = mListImageViewCategory.get(position);

            Bitmap mBitmapImage = ((BitmapDrawable)((ImageView)mImageView).getDrawable()).getBitmap();

            if(mBitmapImage!=null && mBitmapImage.getByteCount()>0){
                try{
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    listContacts.get(position).setmImageArray(byteArray);
                }catch(Exception e){
                    e.getMessage();
                }
            }

            // add item only if it down not exist in the list of added contacts
            if(!listTncContactsAdded.contains(listContacts.get(position)))
                listTncContactsAdded.add(listContacts.get(position));

        }else{

            BBContactsBean mObjectBBContactsBean = listContacts.get(position);

            // even if all is not selected then also check whether that list item's checked state is true
            if(mObjectBBContactsBean.isContactChecked()){
                holder.chkBoxSelection.setChecked(true);
                // add item only if it down not exist in the list of added contacts
                if(!listTncContactsAdded.contains(mObjectBBContactsBean))
                    listTncContactsAdded.add(mObjectBBContactsBean);

            }else if(!mObjectBBContactsBean.isContactChecked()){
                holder.chkBoxSelection.setChecked(false);
                if(listTncContactsAdded.contains(mObjectBBContactsBean))
                    listTncContactsAdded.remove(mObjectBBContactsBean);
            }

           /* if(listTncContactsAdded!=null)
                listTncContactsAdded.clear();*/
        }

        /*if(isTncUser){
            holder.llContactNameHolder.setVisibility(View.VISIBLE);
        }*/

        if(position%2==0)
        {
            convertView.setBackgroundColor(Color.parseColor("#EFEDED"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        /*if(position == listContacts.size()-1){
            Log.i("count :-" + count +"  -- SIZE -- :- ", "" + listTncContactsAdded.size());
        }
*/
        holder.llContactNameHolder.setVisibility(View.VISIBLE);

        return convertView;
    }

    /**
     * @param :instance of text to be filtered in list
     *filters the contents of the list as per input given in the searchview
     */
    public void filterData(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        if(listContacts!=null && !listContacts.isEmpty())
        {
            listContacts.clear();
            if (charText.trim().length() == 0)
            {
                listContacts.addAll(listContactsFiltered);
            }
            else
            {
                listFirstName = new ArrayList<BBContactsBean>();
                listLastName = new ArrayList<BBContactsBean>();
                listContainsName = new ArrayList<BBContactsBean>();
                Name name;
                for (BBContactsBean contactDetailBean : listContactsFiltered) {
                    name = checkName(
                            contactDetailBean.getName().toLowerCase(
                                    Locale.getDefault()), charText);
                    if (name != null) {
                        if (name.isFirst) {
                            listFirstName.add(contactDetailBean);
                        } else if (name.isLast) {
                            listLastName.add(contactDetailBean);
                        } else if (name.isContainer) {
                            listContainsName.add(contactDetailBean);
                        }
                    }
                }
                showList();
            }
        }
    }

    /*
     * Check Existence of Name
     */
    private Name checkName(String contactName, String searchString) {
        String nameArray[] = contactName.split(" ");
        if (nameArray != null && nameArray.length > 0) {
            Name name;
            int nameLength = nameArray.length;
            if (nameArray[0].startsWith(searchString)) {
                name = new Name();
                name.isFirst = true;
                return name;
            } else if (nameArray[nameLength - 1].startsWith(searchString)) {
                name = new Name();
                name.isLast = true;
                return name;
            } else if (contactName.contains(searchString)) {
                name = new Name();
                name.isContainer = true;
                return name;
            }
        }
        return null;
    }

    //display filtered contact List
    private void showList() {
        listContacts.clear();

        if (listFirstName != null && listFirstName.size() > 0) {
            listContacts.addAll(listFirstName);
        }

        if (listLastName != null && listLastName.size() > 0) {
            listContacts.addAll(listLastName);
        }

        if (listContainsName != null && listContainsName.size() > 0) {
            listContacts.addAll(listContainsName);
        }
        this.notifyDataSetInvalidated();
    }

    /**
     * Method to display popup-menu of categories
     */
    private void openCategoriesPopup(Button mButtonCategory){

        setCustomFont(GlobalCommonValues.FONT_Helvetica_Bold);

        // initialize a pop up window type
        if(mPopupWindow == null)
            mPopupWindow = new ListPopupWindow(mContext);

        mPopupWindow.setAnchorView(mButtonCategory);
        mPopupWindow.setModal(true);

        if(mListCategory != null){
            MyFontAdapter adapter = new MyFontAdapter(
                    mContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    mListCategory, mTypeFace);
            mPopupWindow.setAdapter(adapter);
        }

        mPopupWindow.setWidth((int)mContext.getResources().getDimension(R.dimen.tileImageDimensionheight));
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /**
                 * Interface to handle click action of category dropdown
                 */

                String category = mListCategory.get(position);

                String mCategoryID = GlobalConfig_Methods.getCategoryIdFromString(mContext, category);

//                Toast.makeText(mContext, mCategoryID + " : " + category, Toast.LENGTH_LONG).show();

                if(position > -1){

                    if(mPositionCategorySelected > -1){
                        // update object value for the category in listview
                        listContacts.get(mPositionCategorySelected).setCategory(mCategoryID);
                        mListTextViewCategory.get(mPositionCategorySelected).setText(category);
                    }

                    BBContactsBean objContactDetails = new BBContactsBean();
                    if(mPositionCategorySelected > -1)
                        objContactDetails=listContacts.get(mPositionCategorySelected);
                    // to check if item whose category is updated, exists in the list of selected items then updat it's category alsoadd lit of items
                    if(listTncContactsAdded.contains(objContactDetails)){

                        int mObjectPosition = listTncContactsAdded.indexOf(objContactDetails);
                        //update Object in a list
                        listTncContactsAdded.get(mObjectPosition).setCategory(mCategoryID);
                    }
                }

                //mSelectedPosition   =   position;
                //CustomSpinner.this.setText(mListItems.get(mSelectedPosition));
//                if(mINotifyAction!=null){
//                    mINotifyAction.setAction(mListItems.get(mSelectedPosition));
//                }
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.show();
    }

    /**
     *  Set your font here
     */
    public void setCustomFont(String fontPath) {
        mTypeFace = Typeface.createFromAsset(mContext.getAssets(), fontPath);
        holder.mBtnCategoryMenu.setTypeface(mTypeFace);
    }

    /**
     *  -- Spinner adapter with custom font
     */
    private static class MyFontAdapter extends ArrayAdapter<String> {
        Typeface mTypeFace;

        private MyFontAdapter(Context context, int resource, List<String> items, Typeface typeface) {
            super(context, resource, items);
            mTypeFace = typeface;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(mTypeFace);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(mTypeFace);
            return view;
        }
    }

//    INotifyAction mINotifyCategorySelected = new INotifyAction() {
//        @Override
//        public void setAction(String category) {
//            /**
//             * Interface to handle click action of category dropdown
//             */
//
//            String mCategoryID = GlobalConfig_Methods.getCategoryIdFromString(category);
//            Toast.makeText(mContext, mCategoryID + " : " + category, Toast.LENGTH_LONG).show();
//
//            if(mSpinnerItemPosition > -1)
//                listContacts.get(mSpinnerItemPosition).setCategory(mCategoryID);
//
//        }
//    };
}

