package com.example.whattoeat;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ItemActivity extends Activity {

	Button backbtn;
	View view1;
	TextView itemTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		
		Bundle bundle = getIntent().getExtras();
		String title = bundle.getString("title");
		int img = bundle.getInt("id");
		
		view1 = findViewById(R.id.view1);
		itemTitle = (TextView) findViewById(R.id.itemTitle);
		
		view1.setBackgroundResource(img);
		itemTitle.setText(title);
		
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item, menu);
		return true;
	}

}
