package dm.core.chunkWorker;

import android.os.Environment;
import android.util.Log;

import dm.Utils.helper.FileUtils;
import dm.database.elements.Chunk;
import dm.database.elements.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by Majid Golshadi on 4/14/2014.
 */
public class AsyncWorker extends Thread{

    private final int BUFFER_SIZE = 1024;

    private final Task task;
    private final Chunk chunk;
    private final Moderator observer;
    private byte[] buffer;
    private ConnectionWatchDog watchDog;

    public boolean stop = false;


    public AsyncWorker(Task task, Chunk chunk, Moderator moderator){
        buffer = new byte[BUFFER_SIZE];

        this.task = task;
        this.chunk = chunk;
        this.observer = moderator;
    }


    @Override
	public void run() {
        try {

            URL url = new URL(task.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            if (chunk.end != 0) // support unresumable links
               connection.setRequestProperty("Range", "bytes=" + chunk.begin + "-" + chunk.end);

            connection.connect();

            File cf = new File(FileUtils.address(task.save_address, String.valueOf(chunk.id)));
            InputStream remoteFileIn = connection.getInputStream();
            FileOutputStream chunkFile = new FileOutputStream(cf, true);

            //InputStream remoteFileIn = new BufferedInputStream(url.openStream());
            //OutputStream chunkFile = new FileOutputStream(cf, true);


            int len = 0;
            // set watchDoger to stop thread after 1sec if no connection lost
           watchDog = new ConnectionWatchDog(5000, this);
           watchDog.start();

            while (!this.isInterrupted() &&
                    (len = remoteFileIn.read(buffer)) > 0) {
                //Log.d("EXE", "LOOP");
                watchDog.reset();
                chunkFile.write(buffer, 0, len);
                process(len);
            }

            Log.d("EXE", ""+this.isInterrupted());
            Log.d("EXE", ""+remoteFileIn.read(buffer));

            Log.d("EXE", "EXECUTED");

            chunkFile.flush();
            chunkFile.close();
            watchDog.interrupt();
            connection.disconnect();

            if (!this.isInterrupted()) {
                observer.rebuild(chunk);
            }
        }catch (SocketTimeoutException e) {
        	e.printStackTrace();
        	observer.connectionLost(task.id);
        	puaseRelatedTask();
        	
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e){
            observer.connectionLost(task.id);
            puaseRelatedTask();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    private void process(int read) {
        observer.process(chunk.task_id, read);
    }
    
    private void puaseRelatedTask()	{
    	observer.pause(task.id);
    }
    
    private boolean flag = true;
    public void connectionTimeOut(){
    	if (flag) {
    		watchDog.interrupt();
    		flag = false;
    		observer.connectionLost(task.id);
        	puaseRelatedTask();
		}
    	
    }

}
