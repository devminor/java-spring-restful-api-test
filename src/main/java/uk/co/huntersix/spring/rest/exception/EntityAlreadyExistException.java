package uk.co.huntersix.spring.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Entity already exists")
public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException() {
        super("Entity already exists");
    }
}
