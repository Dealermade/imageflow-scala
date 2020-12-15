package com.dealermade.imageflow.entities.libimageflow;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

/**
 * A Java class that represent a struct in C There is an issue with JNR it parse
 * all chars from the buffer which makes it more difficult to get the specific
 * structure that's why I used just two fields and the data field will contain
 * the whole data of the buffer
 */
// We can't convert this to Scala because JNR throw an exception can't access to
// private final
public class ImageFlowResponseStruct extends Struct {
    // Left success because there is an issue with the library JNR
    // it shutdown the JVM
    public final Boolean success = new Boolean();

    public final String data = new UTF8StringRef();

    public ImageFlowResponseStruct() {
        super(Runtime.getSystemRuntime());
    }

    public ImageFlowResponseStruct(Runtime runtime) {
        super(runtime);
    }

}