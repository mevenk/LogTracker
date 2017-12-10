/**
 * 
 */
package mevenk.logging.tracker.runner;

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

		buffer = new CircularBuffer<>(5);

		final int port = 4445;
		SocketLogServer socketLogServer = new SocketLogServer(port);

		socketLogServer.start();

		while (true) {
			//System.out.println(buffer.size());
			for (Log currentLog : buffer) {
				System.out.println(currentLog);
			}
			Thread.sleep(1000);

		}

		// socketLogServer.stop();

	}

}
