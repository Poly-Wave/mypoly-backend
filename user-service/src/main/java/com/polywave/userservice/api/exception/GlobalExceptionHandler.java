package com.polywave.userservice.api.exception;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.exception.ErrorCode;
import com.polywave.userservice.common.exception.DuplicateNicknameException;
import com.polywave.userservice.common.exception.ForbiddenNicknameException;
import com.polywave.userservice.common.exception.TermsNotFoundException;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.common.exception.InvalidSocialTokenException;
import com.polywave.userservice.common.exception.UnsupportedSocialLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnsupportedSocialLoginException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedSocialLoginException(UnsupportedSocialLoginException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCode.BAD_REQUEST.name()));
    }

    @ExceptionHandler(InvalidSocialTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSocialTokenException(InvalidSocialTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ErrorCode.UNAUTHORIZED.name()));
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateNicknameException(DuplicateNicknameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("DUPLICATE_NICKNAME"));
    }

    @ExceptionHandler(ForbiddenNicknameException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenNicknameException(ForbiddenNicknameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCode.BAD_REQUEST.name()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("USER_NOT_FOUND"));
    }

    @ExceptionHandler(TermsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTermsNotFoundException(TermsNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("TERMS_NOT_FOUND"));
    }

}
