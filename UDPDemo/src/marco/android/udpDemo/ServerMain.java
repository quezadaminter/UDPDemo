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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ServerMain extends Activity
{
	EditText mPortEntry = null;
	TextView mClientSays = null;
	Button mServerSendButton = null;
	EditText mMessageText = null;
	Integer mPort = null;
	boolean mConnected = false;
	DatagramSocket mClient = null;
	DatagramPacket mClientIP = null;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_main_layout);
        
        mPortEntry = (EditText) findViewById(R.id.server_main_port_number_entry);
        
        mClientSays = (TextView) findViewById(R.id.server_main_client_says_text);
        
        Button b = (Button) findViewById(R.id.server_main_start_button);
        b.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mPortEntry.getText().toString().equals(""))
				{
				   mPort = Integer.valueOf(mPortEntry.getText().toString());
				   if(mPort != null && mPort > 0)
				   {
				      new StartServer().execute(mPort);
			   	   }
				}
			}
		});
        
        mMessageText = (EditText)findViewById(R.id.server_main_message_entry);
        
        mServerSendButton = (Button) findViewById(R.id.server_main_send_button);
        mServerSendButton.setEnabled(false);
        mServerSendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mMessageText.getText().length() > 0)
				{
					sendToClient(mMessageText.getText().toString());
				}
			}
		});
    }
	
	private void sendToClient(String msg)
	{
		if(mClient != null)
		{
			try
			{
			   byte[] buf = msg.getBytes();
			   DatagramPacket p = new DatagramPacket(buf, buf.length, mClientIP.getAddress(), mClientIP.getPort());
			   mClient.send(p);
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class StartServer extends AsyncTask<Integer, String, DatagramSocket>
	{
		protected void onPreExecute()
		{
		}

		protected DatagramSocket doInBackground(Integer... ports)
		{
			int port = ports[0];
			// Connect to port
			byte[] message = new byte[1500];
			DatagramSocket s = null;
			InetAddress clientIP = null;
			int clientPort = -1;
			try
			{
				// bind to local port
				s = new DatagramSocket(port);
				// Wait for connection requests
				publishProgress("Waiting for clients.");
				while(mConnected == false)
				{
					DatagramPacket p = new DatagramPacket(message, message.length);
					s.receive(p);

					String received = new String(p.getData(), 0, p.getLength());
					publishProgress("Received message: " + received);
					if(received.equals("client:KnockKnock"))
					{
						mConnected = true;
						clientIP = p.getAddress();
						clientPort = p.getPort();
						// Store client data for later retrieval
						mClientIP = p;
						// Tell the client we hear them.
						byte[] buf = new String("server:Welcome").getBytes();
						p = new DatagramPacket(buf, buf.length, clientIP, clientPort);
						s.send(p);
						publishProgress("Valid client @" + clientIP.getHostAddress() + ":" + clientPort);
					}
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
			//return loadImageFromNetwork(urls[0]);
			return s;
		}
		
		protected void onProgressUpdate(String... values)
		{
			super.onProgressUpdate(values);
			mClientSays.setText(values[0]);
		}
		
		protected void onPostExecute(DatagramSocket s)
		{
			mClient = s;
			if(mConnected == true)
			{
			   mClientSays.setText("Connected to client.");
			   mServerSendButton.setEnabled(true);
			}
			else
			{
			   mClientSays.setText("Session ended.");
			}
		}
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		if(mClient != null)
		{
			mClient.close();
		}
	}
}
