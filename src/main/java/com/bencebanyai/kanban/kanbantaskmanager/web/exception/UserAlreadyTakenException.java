package com.bencebanyai.kanban.kanbantaskmanager.web.exception;

public class UserAlreadyTakenException extends RuntimeException {
  public UserAlreadyTakenException(String message) {
    super(message);
  }
}
