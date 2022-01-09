package com.main.service;

import com.main.model.Group;
import com.main.model.User;
import com.main.repository.db.GroupDbRepository;

public class GroupService {
    private final GroupDbRepository groupRepo;

    public GroupService(GroupDbRepository groupRepo) {
        this.groupRepo = groupRepo;
    }

    public Group add(Group entity) {
        return this.groupRepo.save(entity);
    }
    public void addMemberToGroup(Group group, User user){
        this.groupRepo.addMember(group.getId(), user.getId());
        group.addMemberId(user.getId());
    }
    public Group findGroupByName(String name, User user) {
        return this.groupRepo.findOneByName(name, user.getId());
    }
    public Group findGroupById(Long groupId) {
        return this.groupRepo.findOneById(groupId);
    }
    public Iterable<Group> findAll(Long userId) {
        return this.groupRepo.findAll(userId);
    }
}
