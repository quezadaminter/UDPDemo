package marco.android.udpDemo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ClientMain extends Activity
{
	private boolean mConnected = false;
	private TextView mServerSays = null;
	private EditText mEntry = null;
	private String mIP = null;
	Handler mHandler = new Handler();
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_main_layout);
        
        mEntry = (EditText) findViewById(R.id.client_main_server_ip_editText_view);
        
        mServerSays = (TextView) findViewById(R.id.client_main_server_says_text_view);
        
        Button b = (Button) findViewById(R.id.client_main_connect_button);
        b.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIP = mEntry.getText().toString();
				if(mIP != null)
				{
				   new ConnectToServer().execute(mIP);
				}
			}
		});
    }
	
	private class ConnectToServer extends AsyncTask<String, String, Void>
	{
		private String ip = null;
		private Integer port = null;
		
		protected void onPreExecute()
		{
			if(!mIP.equals(""))
			{
				int sep = mIP.indexOf(':');
				if(sep > 0)
				{
					ip = mIP.substring(0, sep);
				   port = Integer.valueOf(mIP.substring(sep + 1, mIP.length()));
				}
			}
		}

		protected Void doInBackground(String... urls)
		{
			if(port != null && ip != null)
			{
				// Connect to port
				byte[] message = new byte[1500];
				DatagramSocket s = null;
				try
				{
					DatagramPacket p = null;
					// bind to any available local port
					s = new DatagramSocket();
					InetAddress addr = InetAddress.getByName(ip);
					
					byte[] call = new String("client:KnockKNock").getBytes();
					p = new DatagramPacket(call, call.length, addr, port);
					
					publishProgress("Calling: " +  ip + String.valueOf(port));
					s.send(p);
					
					// Now we wait to see if we are welcome in by the server
					p = new DatagramPacket(message, message.length);
					s.receive(p);
					String received = new String(p.getData(), 0, p.getLength());
					
					if(received.equals("server:Welcome"))
					{
					   mConnected = true;
					   publishProgress("Connected!");
					}
				}
				catch(UnknownHostException e)
				{
					e.printStackTrace();
				}
				catch (SocketException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch(IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while(mConnected && s != null)
				{
				   try
				   {
					   DatagramPacket p = new DatagramPacket(message, message.length);
					   s.receive(p);
					   String received = new String(p.getData(), 0, p.getLength());
					   publishProgress(received);
					   Log.d("Udp tutorial","message:" + received);
				   }
				   catch(IOException e)
				   {
					  // TODO Auto-generated catch block
					  e.printStackTrace();
					  Log.d("Udp tutorial", e.getMessage());
					  mConnected = false;
				   }
				}
				s.close();
				//return loadImageFromNetwork(urls[0]);
			}
			else
			{
				publishProgress("Bad format for IP. Use: IP:port");
			}
			return null;
		}
		
		protected void onProgressUpdate(String... values)
		{
			super.onProgressUpdate(values);
			mServerSays.setText(values[0]);
		}
		
		protected void onPostExecute(Void... voids)
		{
			super.onPostExecute(null);
			mServerSays.setText("Session ended.");
		}
	}
}
