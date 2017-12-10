package mevenk.logging.tracker.converter;

import mevenk.logging.tracker.bean.Log;

public interface Converter {

	Log convert(Object obj);
}
