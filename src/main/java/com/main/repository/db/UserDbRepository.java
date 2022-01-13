package com.main.repository.db;

import  com.main.model.User;
import  com.main.model.validators.Validator;

import com.main.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDbRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOneById(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        String sqlSelect = "select * from users where id=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement psSelect = connection.prepareStatement(sqlSelect)){
            psSelect.setLong(1,id);
            ResultSet resultSet = psSelect.executeQuery();
            if(resultSet.next()) {
                String userName = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userPassword = resultSet.getString("user_password");
                return new User(id, userName,firstName, lastName, userPassword);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public User findOneByUsername(String name) {
        if(username == null)
            throw new IllegalArgumentException("entity must not be null");
        String sqlSelect = "select * from users where username=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect)){
            psSelect.setString(1,name);
            ResultSet resultSet = psSelect.executeQuery();
            if(resultSet.next()) {
                Long id = resultSet.getLong("id");
                String userName = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userPassword = resultSet.getString("user_password");
                User user = new User(userName,firstName, lastName, userPassword);
                user.setId(id);
                return user;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String userName = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userPassword = resultSet.getString("user_password");
                User utilizator = new User(id, userName, firstName, lastName, userPassword);
                users.add(utilizator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Iterable<User> findAll(Long id) {
        return null;
    }

    @Override
    public User save(User entity) {
        this.validator.validate(entity);
        String sql = "insert into users (username, first_name, last_name, user_password) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setString(4, entity.getUserPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        User found = this.findOneById(id);
        if(found == null)
            return null;
        String sqlDelete = "delete from users where (id=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement psDelete = connection.prepareStatement(sqlDelete)){
            psDelete.setLong(1, id);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    @Override
    public Integer size() {
        String sqlCount = "select count(*) from users";
        try(Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement psCount = connection.prepareStatement(sqlCount)){
            ResultSet resultSet = psCount.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public User update(User entity) {
        this.validator.validate(entity);
        User oldState = this.findOneById(entity.getId());
        if(oldState == null)
            return null;
        String sqlUpdate = "update users set first_name=?, last_name=? where id=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)){
            psUpdate.setString(1,entity.getFirstName());
            psUpdate.setString(2,entity.getLastName());
            psUpdate.setLong(3,entity.getId());
            psUpdate.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return oldState;
    }

    public String getSalt(User user) {
        return "";
    }
}