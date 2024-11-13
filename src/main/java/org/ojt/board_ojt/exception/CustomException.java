package org.ojt.board_ojt.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final CustomErrorInfo customErrorInfo;

    public CustomException(CustomErrorInfo customErrorInfo) {
        super(customErrorInfo.getMessage());
        this.customErrorInfo = customErrorInfo;
    }

}
