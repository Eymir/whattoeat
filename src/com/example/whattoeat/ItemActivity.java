package com.example.whattoeat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivity extends Activity {

	Button backbtn, newphotobtn;
	RatingBar rbar;
	ImageView view1;
	TextView itemTitle;
	EditText et;
	
	SQLiteDatabase db;
	DBHelper helper = new DBHelper(ItemActivity.this);
	String photo = null;
	int item,rating=0;
	String rest,title = "TEMP";
	String comment = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		
		Bundle bundle = getIntent().getExtras();
		
		title = bundle.getString("food");
		rest = bundle.getString("restaurant");
		
		db = helper.getReadableDatabase();
		final String sql = "select * from history where `restaurant` = '"+rest+"' and `menu` = '"+title+"' order by `date` desc";
		Cursor cursor = db.rawQuery(sql, null);
		
		int rowsNum = cursor.getCount();
		if (rowsNum != 0) {
			cursor.moveToFirst();
			item = cursor.getInt(0);
			photo = cursor.getString(4);
			rating = cursor.getInt(5);
			comment = cursor.getString(6);
			Log.d("emo","rate "+rating);
			//Log.d("emo", photo);
		}
		cursor.close();
		db.close();
		
		view1 = (ImageView) findViewById(R.id.view1);
		itemTitle = (TextView) findViewById(R.id.itemTitle);
		rbar = (RatingBar) findViewById(R.id.ratingBar1);
		rbar.setRating(rating);
		rbar.setStepSize(1);
		et = (EditText) findViewById(R.id.editText1);
		if (photo == null || photo.equals("")) {
			
		} else {
			File imgFile = new File(Environment.getExternalStorageDirectory(),photo);
			if(imgFile.exists()) {
				Log.d("emo","file exist");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Config.RGB_565;
				options.inSampleSize = 2;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
			    view1.setImageBitmap(myBitmap);
			    
			} else {
				
			}
		}
		//view1.setBackgroundResource(img);
		itemTitle.setText(title);
		
		et.setText(comment);
		et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String text = s.toString();
				db = helper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("comment", text);
				int r = db.update("history", values, "_id= "+item, null);
				Log.d("emo","affect row "+r);
				db.close();
			}
		});
		
		newphotobtn = (Button) findViewById(R.id.newPhotobtn);
		newphotobtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File tmpFile = new File(Environment.getExternalStorageDirectory(),"img"+item+"_"+rating+".jpg");
				Uri outputFileUri = Uri.fromFile(tmpFile);
				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); 
				startActivityForResult(intent, 0); 
			}
		});
		
		backbtn = (Button) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				Intent intent = new Intent();
				intent.setClass(ItemActivity.this, MainActivity.class);
				startActivity(intent); 
				ItemActivity.this.finish(); 
			}
		});
		
		rbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				// TODO Auto-generated method stub
				db = helper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("rating", (int)rating);
				int r = db.update("history", values, "_id= "+item, null);
				Log.d("emo","affect row "+r);
				db.close();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK ) {
			photo = "img"+item+"_"+rating+".jpg";
			db = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("photo", photo);
			int r = db.update("history", values, "_id= "+item, null);
			Log.d("emo","affect row "+r);
			db.close();

			File imgFile = new File(Environment.getExternalStorageDirectory(), photo);
			if(imgFile.exists()) {
				Log.d("emo","file exist");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Config.RGB_565;
				options.inSampleSize = 2;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
			    view1.setImageBitmap(myBitmap);
			    
			} else {
				Log.d("emo","can't find photo");
				Log.d("emo",photo);
			}
			
		} else if (resultCode == Activity.RESULT_CANCELED ) {
			Toast.makeText(getApplicationContext(), "Failed to take photo", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item, menu);
		return true;
	}
	
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(ItemActivity.this, MainActivity.class);
		startActivity(intent); 
		ItemActivity.this.finish(); 
	}
}
