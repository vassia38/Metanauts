package com.main.service;

import com.main.model.Message;
import com.main.repository.db.MessageDbRepository;

import java.util.Set;

public class MessageService {
    public final MessageDbRepository messageRepo;
    public MessageService(MessageDbRepository messageRepo){
        this.messageRepo = messageRepo;
    }

    public Message add(Message entity){
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
    public Iterable<Message> findAllMessagesBySource(Long sourceId){
        return messageRepo.findAllMessagesBySource(sourceId);
    }
    public Set<Message> findConversation(Long id1, Long id2){
        return messageRepo.findConversation(id1,id2);
    }
    public Integer size(){
        return messageRepo.size();
    }
}
