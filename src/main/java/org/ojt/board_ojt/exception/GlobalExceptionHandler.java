package org.ojt.board_ojt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptionHandler { //spring AOP 의 기능, 컨트롤러 마다 필요한 예외 핸들러를 한번에 처리할 수 있도록 한다

    @ExceptionHandler(DuplicateEmailException.class) //해당 클래스의 자손들만 처리하겠다는 뜻
    // response entity에 상태를 저장하는 것 대신 response status를 쓸 수도 있음
    public ResponseEntity<String> handleDuplicateEmailException(DuplicateEmailException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) //409 상태코드
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) //409 상태코드
                .body(e.getMessage());
    }

    //IllegalArgumentException 은 runtime error 를 상속받는 예외이다

}
