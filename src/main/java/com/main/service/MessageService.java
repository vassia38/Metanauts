package com.main.service;

import com.main.model.GroupMessage;
import com.main.model.Message;
import com.main.repository.db.MessageDbRepository;
import com.main.repository.paging.Page;
import com.main.repository.paging.Pageable;
import com.main.repository.paging.PageableImplementation;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MessageService {
    private final MessageDbRepository messageRepo;
    public MessageService(MessageDbRepository messageRepo){
        this.messageRepo = messageRepo;
    }

    public Message add(Message entity){
        return messageRepo.save(entity);
    }
    public GroupMessage add(GroupMessage entity){
        return messageRepo.save(entity);
    }
    public Message delete(Message entity){
        return messageRepo.delete(entity.getId());
    }
    public Message update(Message entity){
        return messageRepo.update(entity);
    }
    public Message findMessageById(Long id){
        return messageRepo.findMessageById(id);
    }
    public GroupMessage findGroupMessageById(Long id) {
        return messageRepo.findGroupMessageById(id);
    }
    public Iterable<Message> findAllMessagesBySource(Long sourceId){
        return messageRepo.findAllMessagesBySource(sourceId);
    }
    public Set<Message> findConversation(Long id1, Long id2){
        return messageRepo.findConversation(id1,id2);
    }
    public Set<GroupMessage> findGroupConversation( Long groupId) {
        return messageRepo.findGroupConversation(groupId);
    }
    public Integer size(){
        return messageRepo.size();
    }

    private int page = -1;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

    public void setPage(int page) {
        this.page = -1;
    }

    public Set<Message> getNextMessages(Long sourceId) {
        this.page++;
        return getMessagesOnPage(this.page, sourceId);
    }

    public Set<Message> getMessagesOnPage(int page, Long sourceId) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Message> messagePage = messageRepo.findAll(pageable, sourceId);
        Set<Message> messages = new TreeSet<>();
        messagePage.getContent().forEach(messages::add);
        return messages;
    }
}
