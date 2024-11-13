package org.ojt.board_ojt.exception;

public record ErrorResponseBody(
        int detailStatusCode,
        String message
) {
}
