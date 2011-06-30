package marco.android.udpDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UDPDemoMain extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button b = (Button) findViewById(R.id.main_client_button);
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UDPDemoMain.this, ClientMain.class);
				startActivity(i);
			}
		});
        
        b = (Button) findViewById(R.id.main_server_button);
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UDPDemoMain.this, ServerMain.class);
				startActivity(i);
			}
		});
    }
}