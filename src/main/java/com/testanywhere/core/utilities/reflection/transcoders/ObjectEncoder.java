package com.testanywhere.core.utilities.reflection.transcoders;


/**
 * Special handler which allows an extra encoder to be placed into the transcoder pipeline which
 * will be called when objects are being encoded using whichever transcoder this is handed to,
 * the implementation of this must be threadsafe
 */
public interface ObjectEncoder {

    /**
     * This will ensure that no objects that are known to be impossible to serialize properly will
     * cause problems with the transcoders by allowing them to go into loops
     * 
     * @param object
     * @return a null if the current object is not special, an empty string to indicate the 
     * object should be skipped over with no output, and any string value to indicate the
     * return value to use instead of attempting to encode the object
     */
    public String encodeObject(Object object);
}
