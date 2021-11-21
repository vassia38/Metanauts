package com.main.service;

import com.main.model.Message;
import com.main.repository.db.MessageDbRepository;

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
    public Integer size(){
        return messageRepo.size();
    }
}
