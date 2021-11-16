package com.main.repository.file;

import com.main.model.User;
import com.main.model.validators.Validator;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User> {
    public UserFile(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1),attributes.get(2),attributes.get(3));
        Long userID = Long.parseLong(attributes.get(0));
        user.setId(userID);
        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }

    @Override
    public User delete(Long id){
        return super.delete(id);
    }
}
