package com.tnc.adapter;
//package com.bigbutton.adapter;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.apache.commons.io.FileUtils;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.BitmapFactory;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnLongClickListener;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bigbutton.R;
//import com.bigbutton.bean.BBContactsBean;
//import com.bigbutton.bean.ContactTilesBean;
//import com.bigbutton.common.CustomFonts;
//import com.bigbutton.common.GlobalCommonValues;
//import com.bigbutton.common.GlobalConfig_Methods;
//import com.bigbutton.database.DBQuery;
//import com.bigbutton.pageddragdropgrid.PagedDragDropGrid;
//import com.bigbutton.pageddragdropgrid.PagedDragDropGridAdapter;
//
//@SuppressLint("InflateParams")
//public class TilesAdapterDraggable implements PagedDragDropGridAdapter
//{
//	ArrayList<ContactTilesBean> listTiles=null;
//	ArrayList<ContactTilesBean> listTilesShuffling=null;
//	ArrayList<BBContactsBean> listBBContacts = null;
//	int view_item_width=0;
//	Context mContext;
//	PagedDragDropGrid gridview;
//	int pagesDivided=0,pagesRemainder=0;
//	int totalPages=0;
//	List<Page> pages = new ArrayList<Page>();
//	int itemCount=6;
//
//	public TilesAdapterDraggable(Context mContext,ArrayList<ContactTilesBean> listTiles, PagedDragDropGrid gridview,int view_item_width)
//	{
//		this.mContext=mContext;
//		this.listTiles=new ArrayList<ContactTilesBean>();
//		this.listBBContacts = new ArrayList<BBContactsBean>();
//		this.gridview=gridview;
//		this.listBBContacts = new ArrayList<BBContactsBean>();
//
//		if(GlobalCommonValues.listBBContacts!=null && GlobalCommonValues.listBBContacts.isEmpty())
//		{
//			listBBContacts = DBQuery.getAllBBContacts(mContext);
//			GlobalCommonValues.listBBContacts = listBBContacts;
//		}
//		else if(GlobalCommonValues.listBBContacts!=null){
//			this.listBBContacts=GlobalCommonValues.listBBContacts;
//		}
//
//		//		this.listBBContacts = DBQuery.getAllBBContacts(mContext);	
//		//		GlobalCommonValues.listBBContacts=listBBContacts;
//		this.listTiles=listTiles;
//		this.view_item_width=view_item_width;
//
//		if(this.listTiles.size()<=6)
//		{
//			totalPages=1;
//		}
//		else
//		{
//			pagesDivided=this.listTiles.size()/6;
//			pagesRemainder=this.listTiles.size()%6;
//			if(pagesRemainder==0)
//			{
//				totalPages=pagesDivided;
//			}
//			else
//			{
//				totalPages=pagesDivided+1;
//			}
//		}
//		if(totalPages==1)
//		{
//			Page page1 = new Page();
//			List<Item> items = new ArrayList<Item>();
//			for(int i=0;i<listTiles.size();i++)
//			{
//				items.add(new Item(this.listTiles.get(i).getName(),this.listTiles.get(i).getImage(),i));
//			}
//			page1.setItems(items);
//			pages.add(page1);
//		}
//		else if(totalPages>1)
//		{
//			for(int i=0;i<totalPages;i++)
//			{
//				List<Item> items = new ArrayList<Item>();
//				Page page1 = new Page();
//
//				for(int pageItem = i*6; pageItem < (i+1)*6; pageItem++){
//					if(pageItem< listTiles.size()){
//						Log.e("Print the name ", this.listTiles.get(pageItem).getName());
//						items.add(new Item(this.listTiles.get(pageItem).getName(),this.listTiles.get(pageItem).getImage(),pageItem));	
//					}
//				}
//				page1.setItems(items);
//				Log.e("pagesRemainder ", ""+pagesDivided);
//				pages.add(page1);
//			}
//		}
//	}
//
//	@Override
//	public int pageCount() 
//	{
//		return pages.size();
//	}
//
//	private List<Item> itemsInPage(int page)
//	{
//		if (pages.size() > page)
//		{
//			return pages.get(page).getItems();
//		}	
//		return Collections.emptyList();
//	}
//
//	@Override
//	public View view(int page, int index) 
//	{
//		/*LinearLayout layout = new LinearLayout(mContext);
//		layout.setOrientation(LinearLayout.VERTICAL);
//		ImageView icon = new ImageView(mContext);
//		Item item = getItem(page, index);
//		icon.setImageBitmap(BitmapFactory.decodeByteArray(item.getDrawable(),0,item.getDrawable().length));
//		icon.setPadding(15, 15, 15, 15);
//		layout.addView(icon);
//		TextView label = new TextView(mContext);
//		label.setTag("text");
//		label.setText(item.getName());	
//		label.setTextColor(Color.BLACK);
//		label.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//
//		label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
//
//		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//
//		// only set selector on every other page for demo purposes
//		// if you do not wish to use the selector functionality, simply disregard this code
//		if(page % 2 == 0) 
//		{
//			setViewBackground(layout);
//			layout.setClickable(true);
//			layout.setOnLongClickListener(new OnLongClickListener() 
//			{
//				@Override
//				public boolean onLongClick(View v) 
//				{
//					return gridview.onLongClick(v);
//				}
//			});
//		}
//		layout.addView(label);*/
//
//		LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View convertView = inflater.inflate(R.layout.tileviewadapter, null);
//		// configure view holder
//		LinearLayout llParent=(LinearLayout) convertView.findViewById(R.id.llParent);
//		FrameLayout  llImageHolder=(FrameLayout)convertView.findViewById(R.id.llImageHolder);
//		TextView  tvContactName=(TextView)convertView.findViewById(R.id.tvContactName);
//		ImageView  imViewUserImage=(ImageView)convertView.findViewById(R.id.imViewUserImage);
//		ImageView imViewTag=(ImageView) convertView.findViewById(R.id.imViewTag);
//		tvContactName.setSelected(true);
//		CustomFonts.setFontOfTextView(mContext,tvContactName, "fonts/Roboto-Regular_1.ttf");
//		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//		params.height=view_item_width-20;
//		params.width=view_item_width-25;
//		llImageHolder.setLayoutParams(params);
//		imViewTag.setVisibility(View.GONE);
//		Item item = getItem(page, index);
//		tvContactName.setText(item.getName());
//		llParent.setTag(R.id.llParent,item.getPos());
//		imViewUserImage.setImageBitmap(BitmapFactory.decodeByteArray(item.getDrawable(),0,item.getDrawable().length));
//		if(page % 2 == 0) 
//		{
//			convertView.setClickable(true);
//			convertView.setOnLongClickListener(new OnLongClickListener() 
//			{
//				@Override
//				public boolean onLongClick(View v) 
//				{
//					return gridview.onLongClick(v);
//				}
//			});
//		}
//		//To Display tag for the registered users
//		if (listBBContacts!=null && !listBBContacts.isEmpty()) 
//		{
//			tagloop:for (int k = 0; k < listBBContacts.size(); k++) 
//			{
//				String strPhone = listTiles.get(item.getPos()).getPhoneNumber();
//				String contactNumber = "";
//				GlobalConfig_Methods.trimSpecialCharactersFromString(strPhone);
//				contactNumber = strPhone;
//				try 
//				{
//					if (contactNumber.equals(listBBContacts.get(k).getPhoneNumber())) 
//					{
//						imViewTag.setVisibility(View.VISIBLE);
//						break tagloop;
//					}
//					else 
//					{
//						imViewTag.setVisibility(View.GONE);
//					}
//				}
//				catch (Exception e) 
//				{
//					e.getMessage();
//				}	
//			}
//		/*tagloop:for (int k = 0; k < listBBContacts.size(); k++) 
//			{
//				String strPhone = listTiles.get(index).getPhoneNumber();
//				String contactNumber = "";
//				GlobalConfig_Methods.trimSpecialCharactersFromString(strPhone);
//				contactNumber = strPhone;
//				try 
//				{
//					if (contactNumber.equals(listBBContacts.get(k).getPhoneNumber())) 
//					{
//						imViewTag.setVisibility(View.VISIBLE);
//						break tagloop;
//					}
//					else 
//					{
//						imViewTag.setVisibility(View.GONE);
//					}
//				}
//				catch (Exception e) 
//				{
//					e.getMessage();
//				}
//			}*/
//		}
//		return convertView;
//	}
//
//	@SuppressLint("NewApi")
//	private void setViewBackground(LinearLayout layout) 
//	{
//		layout.setBackground(mContext.getResources().getDrawable(R.drawable.list_selector_holo_light));
//	}
//
//	private Item getItem(int page, int index) 
//	{
//		List<Item> items = itemsInPage(page);
//		return items.get(index);
//	}
//
//	@Override
//	public int rowCount() 
//	{
//		return AUTOMATIC;
//	}
//
//	@Override
//	public int columnCount() {
//		return AUTOMATIC;
//	}
//
//	@Override
//	public int itemCountInPage(int page) {
//		return itemsInPage(page).size();
//	}
//
//	public void printLayout() {
//		int i=0;
//		for (Page page : pages) {
//			Log.d("Page", Integer.toString(i++));
//			for (@SuppressWarnings("unused")
//			Item item : page.getItems()) {
//				//				Log.d("Item", Long.toString(item.getId()));
//			}
//		}
//	}
//
//	private Page getPage(int pageIndex) {
//		return pages.get(pageIndex);
//	}
//
//	@Override
//	public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
//		getPage(pageIndex).swapItems(itemIndexA, itemIndexB);//itemIndexA-Dropped Final Position
//		//itemIndexB-Item Being Shuffled Position
//		Log.e("----->>>",itemIndexA+"--------->"+itemIndexB);
//		listTilesShuffling=new ArrayList<ContactTilesBean>();
//		for(int i=0;i<listTiles.size();i++)
//		{
//			if(i==listTiles.size() || i==listTiles.size()-1)
//				//system.out.println("Success");
//			if(i==itemIndexA)
//			{
//				listTilesShuffling.add(listTiles.get(itemIndexB));
//			}
//			/*else if(i==itemIndexB)
//			{
//				listTilesShuffling.add(listTiles.get(itemIndexA));
//			}*/
//			else
//			{
//				listTilesShuffling.add(listTiles.get(i));				
//			}
//		}
//		DBQuery.deleteTable("Tiles","",null,mContext);
//		DBQuery.insertTile(mContext,listTilesShuffling,false);
//		testCopy();
//		//		refreshAdapter(listTilesShuffling);
//	}
//
//	@SuppressLint("SdCardPath")
//	private void testCopy()
//	{
//		File f1=new File("/data/data/com.bigbutton/databases/"+"big_button_db.sqlite");
//		String path = android.os.Environment.getExternalStorageDirectory()
//				+ File.separator
//				+ "BigButtonDatabaseTest";
//		File fileBackupDir = new File(path);
//		if (!fileBackupDir.exists()) {
//			fileBackupDir.mkdirs();
//		}
//		try 
//		{
//			if (f1.exists()) {
//				File fileBackup = new File(fileBackupDir,"big_button_test_db.sqlite");
//				try {
//					fileBackup.createNewFile();
//					FileUtils.copyFile(f1, fileBackup);
//				}
//				catch (Exception exception) 
//				{
//					exception.getMessage();
//				}
//			}
//		}
//		catch (Exception e) 
//		{
//			e.getMessage();
//		}	
//	}
//
//	@Override
//	public void moveItemToPreviousPage(int pageIndex, int itemIndex) {
//		int leftPageIndex = pageIndex-1;
//		if (leftPageIndex >= 0) {
//			Page startpage = getPage(pageIndex);
//			Page landingPage = getPage(leftPageIndex);
//
//			Item item = startpage.removeItem(itemIndex);
//			landingPage.addItem(item);	
//		}	
//	}
//
//	@Override
//	public void moveItemToNextPage(int pageIndex, int itemIndex) {
//		int rightPageIndex = pageIndex+1;
//		if (rightPageIndex < pageCount()) {
//			Page startpage = getPage(pageIndex);
//			Page landingPage = getPage(rightPageIndex);
//
//			Item item = startpage.removeItem(itemIndex);
//			landingPage.addItem(item);			
//		}	
//	}
//
//	@Override
//	public void deleteItem(int pageIndex, int itemIndex) {
//		getPage(pageIndex).deleteItem(itemIndex);
//	}
//
//	@Override
//	public int deleteDropZoneLocation() {        
//		return BOTTOM;
//	}
//
//	@Override
//	public boolean showRemoveDropZone() {
//		return false;
//	}
//
//	@Override
//	public int getPageWidth(int page) {
//		return 0;
//	}
//
//	@Override
//	public Object getItemAt(int page, int index) {
//		return getPage(page).getItems().get(index);
//	}
//
//	@Override
//	public boolean disableZoomAnimationsOnChangePage() {
//		return true;
//	}
//
//}
