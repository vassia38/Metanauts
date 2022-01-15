package com.main.utils.events;

import com.main.model.SocialEvent;
import com.main.utils.observer.OperationType;

public class SocialEventEvent implements Event {
    SocialEvent socialEvent;
    OperationType operationType;

    public SocialEventEvent(SocialEvent socialEvent, OperationType operationType) {
        this.socialEvent = socialEvent;
        this.operationType = operationType;
    }

    @Override
    public Object getObject() {
        return socialEvent;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }
}
