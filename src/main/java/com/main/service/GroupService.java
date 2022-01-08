package com.main.service;

import com.main.model.Group;
import com.main.repository.db.GroupDbRepository;

public class GroupService {
    private final GroupDbRepository groupRepo;

    public GroupService(GroupDbRepository groupRepo) {
        this.groupRepo = groupRepo;
    }

    public Group add(Group entity) {
        return this.groupRepo.save(entity);
    }
    public Group findGroupByName(String name, Long userId) {
        return this.groupRepo.findOneByName(name, userId);
    }
    public Group findGroupById(Long groupId) {
        return this.groupRepo.findOneById(groupId);
    }
    public Iterable<Group> findAll(Long userId) {
        return this.groupRepo.findAll(userId);
    }
}
