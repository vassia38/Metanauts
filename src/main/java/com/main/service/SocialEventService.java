package com.main.service;

import com.main.model.SocialEvent;
import com.main.repository.db.SocialEventDbRepository;

public class SocialEventService {
    final SocialEventDbRepository repo;

    public SocialEventService (SocialEventDbRepository repo) {
        this.repo = repo;
    }

    public SocialEvent findOneById(Long id) {
        return this.repo.findOneById(id);
    }
    public Iterable<SocialEvent> findAll() {
        return this.repo.findAll();
    }
    public Iterable<SocialEvent> findAll(Long idUser) {
        return this.repo.findAll(idUser);
    }
    public Iterable<SocialEvent> findAll(String name) {
        return this.repo.findAll(name);
    }
    public SocialEvent save(SocialEvent event) {
        return this.repo.save(event);
    }
    public void addParticipant(Long idEvent, Long idUser, Integer notification) {
        this.repo.addParticipant(idEvent, idUser, notification);
    }
    public SocialEvent delete(Long id) {
        return this.repo.delete(id);
    }
    public Long removeParticipant(Long idEvent, Long idUser) {
        return this.repo.removeParticipant(idEvent, idUser);
    }
    public void addNotification(Long idEvent, Long idUser) {
        this.repo.updateNotification(idEvent, idUser, 1);
    }
    public void removeNotification(Long idEvent, Long idUser) {
        this.repo.updateNotification(idEvent, idUser, 0);
    }
    public boolean findParticipantInEvent(Long idEvent, Long idUser) {
        return this.repo.findParticipantInEvent(idEvent, idUser);
    }
}
