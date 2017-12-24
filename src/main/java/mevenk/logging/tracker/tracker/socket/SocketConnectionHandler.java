package mevenk.logging.tracker.tracker.socket;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import mevenk.logging.tracker.bean.Log;
import mevenk.logging.tracker.converter.LogConverterType;

public class SocketConnectionHandler implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Socket socket;

	private final EventBus eventBus;

	public SocketConnectionHandler(Socket socket, EventBus eventBus) {
		Preconditions.checkNotNull(socket, "Socket is null");
		Preconditions.checkNotNull(eventBus, "Event bus is null");
		this.socket = socket;
		this.eventBus = eventBus;
	}

	@Override
	public void run() {
		try (BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
				ObjectInputStream objectStream = new ObjectInputStream(inputStream)) {

			LogConverterType prevConverterType = null;

			while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
				Object obj = objectStream.readObject();
				LogConverterType converterType = LogConverterType.valueOf(obj);

				if (prevConverterType != converterType) {
					prevConverterType = converterType;
					logger.info("Log converter type detected: {} for log's object: {}", converterType.name(),
							obj.getClass().getName());
				}
				Log log = converterType.convert(obj);
				eventBus.post(log);
			}
		} catch (IllegalArgumentException illegalArgumentException) {
			String messageException = illegalArgumentException.getMessage();
			if (messageException.startsWith("Unknown level constant [")) {
				String level = messageException.substring(messageException.indexOf('[') + 1,
						messageException.indexOf(']'));
				System.out.println("LEVEL Received: " + level);
			} else {
				throw illegalArgumentException;
			}
		} catch (EOFException e) {
			// When client closes connection, the stream will run out of data,
			// and the ObjectInputStream.readObject method will throw the exception
			logger.warn("Reached EOF while reading from socket");
		} catch (Exception e) {
			logger.error("Failed to handle input stream", e);
		} finally {
			stop();
		}
	}

	private void stop() {
		logger.info("Stopping connection handler");
		try {
			socket.close();
			logger.info("Connection handler stopped");
		} catch (IOException e) {
			logger.error("Failed to stop connection handler", e);
		}
	}
}
