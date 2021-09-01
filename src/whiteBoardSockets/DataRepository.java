package whiteBoardSockets;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

class DataRepository 
{
	ArrayList<String> list; 

	public DataRepository() 
	{
		list = new ArrayList<String>();
	}

	public synchronized void append(String message) 
	{
		list.add(message); 
	}

	public synchronized void remove(String message) 
	{
		list.remove(message);
	}
	public synchronized void update(BufferedWriter out) throws IOException
	{
		System.out.println("Sending previous work to newly joined memeber");
		for (int i = 0; i < list.size(); ++i) 
		{
			String message = list.get(i);
			out.write(message);
			out.newLine();
			out.flush();
		}
			
	}
}
