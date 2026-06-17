package com.bencebanyai.kanban.kanbantaskmanager.web.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
