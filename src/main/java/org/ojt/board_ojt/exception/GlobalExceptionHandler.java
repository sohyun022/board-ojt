package org.ojt.board_ojt.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler { //spring AOP 의 기능, 컨트롤러 마다 필요한 예외 핸들러를 한번에 처리할 수 있도록 한다

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) //409 상태코드
                .body(e.getMessage());
    }

    //IllegalArgumentException 은 runtime error 를 상속받는 예외이다

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<?> handleCustomException(CustomException ex) {

        CustomErrorInfo customErrorInfo = ex.getCustomErrorInfo();
        ErrorResponseBody errorResponseBody = new ErrorResponseBody(customErrorInfo.getDetailStatusCode(), customErrorInfo.getMessage());

        log.error("Exception : {}", ex.getMessage());

        return ResponseEntity
                .status(customErrorInfo.getStatusCode())
                .body(errorResponseBody);
    }

}
