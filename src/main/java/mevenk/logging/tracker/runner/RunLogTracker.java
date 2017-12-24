/**
 * 
 */
package mevenk.logging.tracker.runner;

import java.util.Iterator;

import mevenk.logging.tracker.bean.CircularBuffer;
import mevenk.logging.tracker.bean.Log;
import mevenk.logging.tracker.tracker.socket.SocketLogServer;

/**
 * @author Venkatesh
 *
 */
public class RunLogTracker {

	private static CircularBuffer<Log> buffer;

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		buffer = new CircularBuffer<>(50000);

		final int port = 4445;
		SocketLogServer socketLogServer = new SocketLogServer(port);
		socketLogServer.addLogEventListener(buffer);

		socketLogServer.start();

		Thread.sleep(30000);

		socketLogServer.stop();

		Log log = null;
		Iterator<Log> iteratorLogs = buffer.iterator();
		System.out.println("Logs Received: -----------------------------");
		while (iteratorLogs.hasNext()) {
			log = iteratorLogs.next();
			System.out.println(log);
		}

	}

}
