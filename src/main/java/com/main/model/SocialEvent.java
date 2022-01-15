package com.main.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SocialEvent extends Entity<Long> {
    private String name;
    private LocalDateTime date;
    private final List<Long> idsParticipants;
    private String coverphoto;

    public SocialEvent(String name) {
        this.name = name;
        this.date = LocalDateTime.now();
        this.idsParticipants = new ArrayList<>();
        this.coverphoto = "";
    }

    public SocialEvent(String name, LocalDateTime date) {
        this.name = name;
        this.date = date;
        this.idsParticipants = new ArrayList<>();
        this.coverphoto = "";
    }

    public SocialEvent(String name, LocalDateTime date, List<Long> idsParticipants) {
        this.name = name;
        this.date = date;
        this.idsParticipants = idsParticipants;
        this.coverphoto = "";
    }

    public SocialEvent(Long id, String name, LocalDateTime date, List<Long> idsParticipants) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.idsParticipants = idsParticipants;
        this.coverphoto = "";
    }
    public SocialEvent(Long id, String name, LocalDateTime date, List<Long> idsParticipants, String coverphoto) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.idsParticipants = idsParticipants;
        this.coverphoto = coverphoto;
    }

    public SocialEvent(Long id, String name, LocalDateTime date, String coverphoto) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.idsParticipants = new ArrayList<>();
        this.coverphoto = coverphoto;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCoverphoto() {
        return coverphoto;
    }

    public void setCoverphoto(String coverphoto) {
        this.coverphoto = coverphoto;
    }

    public List<Long> getIdsParticipants() {
        return idsParticipants;
    }
    public void setIdsParticipants(List<Long> idsParticipants) {
        this.idsParticipants.clear();
        this.idsParticipants.addAll(idsParticipants);
    }
    public void addIdParticipant(Long id) {
        this.idsParticipants.add(id);
    }
    public Long removeIdParticipant(Long id) {
        this.idsParticipants.remove(id);
        return id;
    }
    public int size() {
        return this.idsParticipants.size();
    }

    @Override
    public String toString() {
        return this.getId() + " " + this.getName() + " " + this.getIdsParticipants();
    }
}
