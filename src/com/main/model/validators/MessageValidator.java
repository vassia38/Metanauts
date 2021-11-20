package com.main.model.validators;

import com.main.model.Message;

public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message entity) throws ValidationException {
        UserValidator userVal = new UserValidator();
        String msg = "";
        try {
            userVal.validate(entity.getSource());
        }catch(ValidationException e){
            msg += e.getMessage();
        }
        if(entity.getMessageText().equals("")){
            msg += "Empty message!\n";
        }
        if(entity.getDate() == null){
            msg += "Empty date!\n";
        }
        if(msg.length() > 0)
            throw new ValidationException(msg);
    }
}
