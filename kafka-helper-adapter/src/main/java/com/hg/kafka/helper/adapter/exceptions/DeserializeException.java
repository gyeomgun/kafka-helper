package com.hg.kafka.helper.adapter.exceptions;

import java.io.Serializable;

public class DeserializeException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public DeserializeException() {
        super();
    }

    public DeserializeException(String msg) {
        super(msg);
    }

    public DeserializeException(Throwable cause) {
        super(cause);
    }

    public DeserializeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
