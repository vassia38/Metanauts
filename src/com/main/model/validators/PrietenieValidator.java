package com.main.model.validators;

import com.main.model.Friendship;

import java.time.LocalDateTime;
import java.util.Objects;

public class PrietenieValidator  implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String msg="";
        if(entity.getId() == null ||
        entity.getId().getLeft() == null || entity.getId().getRight() == null ||
                Objects.equals(entity.getId().getLeft(), entity.getId().getRight())) {
            msg += "Invalid friendship!\n";
        }
        LocalDateTime start = LocalDateTime.parse("2021-10-01T00:00");
        if(entity.getDate().isAfter(LocalDateTime.now()) ||
        entity.getDate().isBefore(start)) {
            msg += "Invalid date!\n";
        }
        if(msg.length() > 0)
            throw new ValidationException(msg);
    }
}
