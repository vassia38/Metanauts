package com.main.utils.events;

import com.main.model.User;
import com.main.utils.observer.OperationType;

public class UserEvent implements Event{
    User user;
    OperationType operationType;

    public UserEvent(User user, OperationType operationType) {
        this.user = user;
        this.operationType = operationType;
    }

    @Override
    public Object getObject() {
        return user;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }
}
