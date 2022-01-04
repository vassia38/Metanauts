package com.main.utils.events;

import com.main.model.Message;
import com.main.utils.observer.OperationType;

public class MessageEvent implements Event {
    Message message;
    OperationType operationType;

    public MessageEvent(Message message, OperationType operationType) {
        this.message = message;
        this.operationType =operationType;
    }

    @Override
    public Object getObject() {
        return message;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }
}
