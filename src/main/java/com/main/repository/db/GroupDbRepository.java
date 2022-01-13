package com.main.repository.db;

import com.main.model.Group;
import com.main.repository.Repository;

import java.sql.*;
import java.util.*;

public class GroupDbRepository implements Repository<Long, Group> {
    private final String url;
    private final String username;
    private final String password;

    public GroupDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Group findOneById(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must not be null");
        String sqlGroupSelect = "select * from groups where id_group=?";
        String sqlMembersSelect = "select * from group_members where id_group=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psGroupSelect = connection.prepareStatement(sqlGroupSelect);
            PreparedStatement psMembersSelect = connection.prepareStatement(sqlMembersSelect)){
            psGroupSelect.setLong(1, id);
            psMembersSelect.setLong(1, id);
            ResultSet resultSet = psGroupSelect.executeQuery();
            if(resultSet.next()) {
                String name_group = resultSet.getString("name_group");
                List<Long> idsMembers = new ArrayList<>();
                resultSet = psMembersSelect.executeQuery();
                while(resultSet.next()) {
                    Long idMember = resultSet.getLong("id_user");
                    idsMembers.add(idMember);
                }
                System.out.println("[findOneById] " + id + " " +name_group);
                return new Group(id, name_group, idsMembers);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Group> findAll() {
        return null;
    }

    public Group findOneByName(String name, Long userId) {
        if(userId == null)
            throw new IllegalArgumentException("User id must not be null");
        if(name == null)
            throw new IllegalArgumentException("Name must not be null");
        String sqlGroupSelect = "select G.* from groups G, group_members GM " +
                "where G.name_group = ? and GM.id_user = ? and G.id_group = GM.id_group";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psGroupSelect = connection.prepareStatement(sqlGroupSelect)) {
            psGroupSelect.setString(1, name);
            psGroupSelect.setLong(2, userId);
            ResultSet resultSet = psGroupSelect.executeQuery();
            if(resultSet.next()) {
                Long groupId = resultSet.getLong("id_group");
                return this.findOneById(groupId);
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Group> findAll(Long userId) {
        if (userId==null)
            throw new IllegalArgumentException("User id must not be null");
        Set<Group> groups = new HashSet<>();
        String sqlGroupsIdsSelect = "select * from group_members where id_user=?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psGroupsIdsSelect = connection.prepareStatement(sqlGroupsIdsSelect)) {
            psGroupsIdsSelect.setLong(1, userId);
            ResultSet resultSet = psGroupsIdsSelect.executeQuery();
            while(resultSet.next()) {
                Long idGroup = resultSet.getLong("id_group");
                Group group = this.findOneById(idGroup);
                if(group != null)
                    groups.add(group);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public Group save(Group entity) {
        if(entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        String sqlGroupInsert = "insert into groups (name_group) values (?)";
        String sqlGroupMemberInsert = "insert into group_members (id_group, id_user) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psGroupInsert = connection.prepareStatement(sqlGroupInsert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psGroupMemberInsert = connection.prepareStatement(sqlGroupMemberInsert)) {
            psGroupInsert.setString(1, entity.getName());
            psGroupInsert.executeUpdate();
            ResultSet rs = psGroupInsert.getGeneratedKeys();
            if(entity.getIdsMembers().size() > 0 && rs.next()) {
                long idGroup = rs.getLong(1);
                System.out.println(idGroup);
                psGroupMemberInsert.setLong(1, idGroup);
                for(Long idUser : entity.getIdsMembers()) {
                    psGroupMemberInsert.setLong(2, idUser);
                    psGroupMemberInsert.executeUpdate();
                }
                return new Group(idGroup, entity.getName(), entity.getIdsMembers());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addMember(Long id, Long userId) {
        if(id == null)
            throw new IllegalArgumentException("id must not be null");
        if (userId==null)
            throw new IllegalArgumentException("User id must not be null");
        String sqlInsert = "insert into group_members(id_group, id_user) values(?,?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement psInsert = connection.prepareStatement(sqlInsert)) {
            psInsert.setLong(1, id);
            psInsert.setLong(2, userId);
            psInsert.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Group delete(Long id) {
        // TODO
        return null;
    }

    @Override
    public Integer size() {
        return null;
    }

    @Override
    public Group update(Group entity) {
        // TODO
        return null;
    }
}
