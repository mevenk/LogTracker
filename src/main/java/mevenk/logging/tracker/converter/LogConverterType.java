package mevenk.logging.tracker.converter;

import mevenk.logging.tracker.bean.Log;

public enum LogConverterType {

	LOG4J_1(null), LOG4J_2(new Log4j2Converter()), LOGBACK(null);

	private final Converter converter;

	private LogConverterType(Converter converter) {
		this.converter = converter;
	}

	public Log convert(Object obj) {
		return converter.convert(obj);
	}

	public static LogConverterType valueOf(Object value) {
		if (value instanceof org.apache.log4j.spi.LoggingEvent) {
			return LOG4J_1;
		} else if (value instanceof org.apache.logging.log4j.core.LogEvent) {
			return LOG4J_2;
		} else if (value instanceof ch.qos.logback.classic.spi.ILoggingEvent) {
			return LOGBACK;
		} else {
			throw new IllegalArgumentException("No converter for such a value: " + value);
		}
	}
}