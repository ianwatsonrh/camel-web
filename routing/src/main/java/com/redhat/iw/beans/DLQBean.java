package com.redhat.iw.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Headers;

public class DLQBean {

	public DLQEntity error(@Body String body, @Headers Map headers) {
        DLQEntity map = new DLQEntity();
        map.setErrorCode(String.valueOf((Integer)headers.get("FailureCode")));
        map.setErrorMessage((String)headers.get("FailureMessage"));
        map.setMessage(body);
        map.setStatus("ERROR");
        return map;
    }
	
}
