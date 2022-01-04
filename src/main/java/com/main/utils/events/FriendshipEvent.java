package com.main.utils.events;

import com.main.model.Friendship;
import com.main.utils.observer.OperationType;

public class FriendshipEvent implements Event{
    Friendship friendship;
    OperationType operationType;

    public FriendshipEvent(Friendship friendship, OperationType operationType) {
        this.friendship = friendship;
        this.operationType = operationType;
    }

    @Override
    public Object getObject() {
        return friendship;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }
}
