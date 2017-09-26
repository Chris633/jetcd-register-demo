package etcdtest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch.Watcher;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.options.PutOption;

public class App 
{
	private static ByteSequence key = ByteSequence.fromString("testServer");
	private static ByteSequence value = ByteSequence.fromString("testNode1");
	private static Client client = Client.builder().endpoints("http://localhost:2379").build();
	private static KV kvClient = client.getKVClient();
	private static Lease leaseClient = client.getLeaseClient();
	private static Long leaseId;
	
	private static void myWatch() {
		Watcher watcher=client.getWatchClient().watch(key, WatchOption.newBuilder().withNoPut(true).withPrevKV(true).build());
    	try {
    		watcher.listen();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void myRefresh() {
		Thread myRefreshThreaad = new Thread(new MyRefresh());
		myRefreshThreaad.setDaemon(true);
		myRefreshThreaad.start();
	}
	
	private static void myServer() throws InterruptedException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (true) {
			Thread.sleep(1000L);
			System.out.println("Hello! I'm Node1. Now is "+simpleDateFormat.format(new Date(System.currentTimeMillis())));
		}
	}
	
	static class MyRefresh implements Runnable {
    	public void run() {
    		try {
    			while (true) {
    				Thread.sleep(1500L);
    				leaseClient.keepAliveOnce(leaseId);
    			}
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    }
	
	public static void main( String[] args ) throws InterruptedException, ExecutionException, ParseException{
		do {
			if (kvClient.get(key).get().getCount() != 0  ) {
	    		System.out.println("I'm Node1. Begin watching!");
	    		myWatch();
	    	}
			System.out.println("I'm Node1. I'm going to work！");
			if (kvClient.get(key).get().getCount() != 0 ) continue;
	    	leaseId =leaseClient.grant(3L).get().getID();
			kvClient.put(key, value, PutOption.newBuilder().withLeaseId(leaseId).build());
			Thread.sleep(20L);
		} while (kvClient.get(key).get().getCount() == 0 || !kvClient.get(key).get().getKvs().get(0).getValue().equals(value));
		myRefresh();
		myServer();
    }
    
}

