package com.main.utils.events;

import com.main.model.Request;
import com.main.utils.observer.OperationType;

public class RequestEvent implements Event{
    Request request;
    OperationType operationType;

    public RequestEvent(Request request, OperationType operationType) {
        this.request = request;
        this.operationType = operationType;
    }

    @Override
    public Object getObject() {
        return request;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }
}
