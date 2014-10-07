package ac.uk.abdn.t3.bboxsim;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TresholdActivity extends Activity {

EditText y_low_tresh;
EditText y_medium_tresh;
EditText y_high_tresh;
EditText x_low_tresh;
EditText x_medium_tresh;
EditText x_high_tresh;

Button submit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_treshold);
		
	
		y_low_tresh=(EditText)findViewById(R.id.y_low_tresh);
		y_medium_tresh=(EditText)findViewById(R.id.y_medium_tresh);
		y_high_tresh=(EditText)findViewById(R.id.y_high_tresh);
		x_low_tresh=(EditText)findViewById(R.id.x_low_tresh);
		x_medium_tresh=(EditText)findViewById(R.id.x_medium_tresh);
		x_high_tresh=(EditText)findViewById(R.id.x_high_tresh);
		
		

 y_low_tresh.setText(Memory.Y_LOW_TRESHOLD+"");
y_medium_tresh.setText(Memory.Y_MEDIUM_TRESHOLD+"");
 y_high_tresh.setText(Memory.Y_HIGH_TRESHOLD+"");
 x_low_tresh.setText(Memory.X_LOW_TRESHOLD+"");
 x_medium_tresh.setText(Memory.X_MEDIUM_TRESHOLD+"");
 x_high_tresh.setText(Memory.X_HIGH_TRESHOLD+"");
		
		submit=(Button)findViewById(R.id.submit_button);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
			Memory.Y_LOW_TRESHOLD=Double.parseDouble(y_low_tresh.getText().toString());
			Memory.Y_MEDIUM_TRESHOLD=Double.parseDouble(y_medium_tresh.getText().toString());
			Memory.Y_HIGH_TRESHOLD=Double.parseDouble(y_high_tresh.getText().toString());
			Memory.X_LOW_TRESHOLD=Double.parseDouble(x_low_tresh.getText().toString());
			Memory.X_MEDIUM_TRESHOLD=Double.parseDouble(x_medium_tresh.getText().toString());
			Memory.X_HIGH_TRESHOLD=Double.parseDouble(x_high_tresh.getText().toString());
			Intent i= new Intent(TresholdActivity.this,MainActivity.class);
			startActivity(i);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.treshold, menu);
		return true;
	}

}
