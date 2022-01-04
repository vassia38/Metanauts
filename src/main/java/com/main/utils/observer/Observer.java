package com.main.utils.observer;

public interface Observer{

    void updateFriends(OperationType operationType);
    void updateRequests(OperationType operationType);
    void updateSolvedRequests(OperationType operationType);
    void updateUsers(OperationType operationType);
    void updateMessages(OperationType operationType);
}
