package com.main.utils.events;

import com.main.utils.observer.OperationType;

public interface Event {
    Object getObject();
    OperationType getOperationType();
}
