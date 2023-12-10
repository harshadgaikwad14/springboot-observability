package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ApplicationException extends RuntimeException {

  HttpStatusCode httpStatusCode;
  String message;

  public ApplicationException(HttpStatusCode httpStatusCode,String message) {
    super(message);
    this.httpStatusCode=httpStatusCode;
    this.message=message;
  }

  public HttpStatusCode getHttpStatusCode() {
    return httpStatusCode;
  }

  public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
