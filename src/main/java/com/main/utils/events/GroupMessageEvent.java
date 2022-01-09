package com.main.utils.events;

import com.main.model.GroupMessage;
import com.main.utils.observer.OperationType;

public class GroupMessageEvent implements Event {
    GroupMessage message;
    OperationType operationType;

    public GroupMessageEvent(GroupMessage message, OperationType operationType) {
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
