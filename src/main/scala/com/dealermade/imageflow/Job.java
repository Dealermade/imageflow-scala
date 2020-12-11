package com.dealermade.imageflow;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class Job extends Struct {
    public final Pointer struct_imageflow_context = new Pointer();

    protected Job(Runtime runtime) {
        super(runtime);
    }
}
