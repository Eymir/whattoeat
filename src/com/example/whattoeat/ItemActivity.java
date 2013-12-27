package com.example.whattoeat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.whattoeat.MainActivity.LoadFromServer;

import android.media.MediaFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemActivity extends Activity {

	private static String server_url_load = "http://192.168.1.101/wteRestName.php";
	JSONParser jsonParser = new JSONParser();
	Button backbtn, newphotobtn;
	View view1;
	TextView itemTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		
		Bundle bundle = getIntent().getExtras();
		//String title = bundle.getString("title");
		String title = "TEMP";
		String photo = bundle.getString("photo");
		int restaurant = bundle.getInt("restaurant");
		
		view1 = findViewById(R.id.view1);
		itemTitle = (TextView) findViewById(R.id.itemTitle);
		if (photo.equals("")) {
			
		} else {
			File imgFile = new File(photo);
			if(imgFile.exists()) {
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    ((ImageView) view1).setImageBitmap(myBitmap);
			    
			} else {
				
			}
		}
		//view1.setBackgroundResource(img);
		//itemTitle.setText(title);
		LoadFromServer query = new LoadFromServer();
		query.setParams(restaurant);;
		query.execute();
		
		newphotobtn = (Button) findViewById(R.id.newPhotobtn);
		newphotobtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File tmpFile = new File(Environment.getExternalStorageDirectory(),"image.jpg");
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
				//Intent intent = new Intent();
				//intent.setClass(ItemActivity.this, MainActivity.class);
				//startActivity(intent); 
				ItemActivity.this.finish(); 
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item, menu);
		return true;
	}
	
	class LoadFromServer extends AsyncTask<String, String, String> {
		private int restaurant;
		private String name;
		public void setParams(int r) {
			restaurant = r;
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("restaurant", Integer.toString(restaurant)));
			JSONObject json = jsonParser.makeHttpRequest(server_url_load,
                    "POST", params);
			
			try {
				 int success = json.getInt("success");
				 if (success == 1) {
					 //histmenu = json.getJSONArray("restaurant");
					 name = json.getString("restaurant");
					 itemTitle.setText(name);
				 }
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		
	}

}
