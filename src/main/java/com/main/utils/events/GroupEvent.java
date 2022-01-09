package com.main.utils.events;

import com.main.model.Group;
import com.main.utils.observer.OperationType;

public class GroupEvent implements Event{
    Group group;
    OperationType operationType;

    public GroupEvent(Group group, OperationType operationType) {
        this.group = group;
        this.operationType = operationType;
    }

    @Override
    public Object getObject() {
        return group;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }
}
