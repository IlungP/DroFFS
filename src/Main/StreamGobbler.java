/**######################################################################################################################
 * @author Michael C. Docanta
 * The following code is copied from http://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html?page=2
 * 
 * This code empties output and error streams for AdbCommand when calling runtime.exec(). It passes the streams in a 
 * separate Thread. 
 * 
 * ######################################################################################################################
 */

package Main;
import java.io.*;

public class StreamGobbler extends Thread{
	InputStream is;
    String type;
    
    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                System.out.println(type + ">" + line);    
            } catch (IOException ioe)
              {
                ioe.printStackTrace();  
              }
    }
}
