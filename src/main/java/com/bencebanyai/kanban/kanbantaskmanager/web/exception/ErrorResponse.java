package com.bencebanyai.kanban.kanbantaskmanager.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String errorCode,
    String message,
    Instant timestamp,
    Map<String, String> validationErrors) {}
