package whiteBoardSockets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

class OutputList 
{
	ArrayList<BufferedWriter> list; 

	public OutputList() 
	{
		list = new ArrayList<BufferedWriter>();
	}

	public synchronized void append(BufferedWriter writer) 
	{
		list.add(writer); 
	}

	public synchronized void remove(BufferedWriter writer) 
	{
		list.remove(writer);
	}

	public synchronized void update(String dataBuffer) throws IOException
	{
		System.out.println("Sending...");
		for (int i = 0; i < list.size(); ++i) {
			BufferedWriter out = (BufferedWriter) list.get(i);
			// out.flush();
			out.write(dataBuffer);
			out.newLine();
			out.flush();
	}
		System.out.println("Finish sending!");
	}
}