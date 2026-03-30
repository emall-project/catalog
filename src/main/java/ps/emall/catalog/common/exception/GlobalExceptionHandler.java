package ps.emall.catalog.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.message.MessageService;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.response.ErrorCode;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private final MessageService messageService;

    @ExceptionHandler(EMallsException.class)
    public ResponseEntity<EMallsResponseEntity<Void>> handleEMallsException(EMallsException ex, Locale locale) {
        log.error("Business exception occurred: {}", ex.getMessage());

        String translatedMessage =
                messageService.getMessage(ex.getMessage(), locale);

        List<ErrorCode> translatedErrors = null;
        if (ex.getErrorCode() != null) {
            translatedErrors = ex.getErrorCode().stream()
                    .map(ec -> new ErrorCode(
                            ec.getField(),
                            messageService.getMessage(ec.getMessage(), locale)
                    ))
                    .toList();
        }

        log.warn(
                "Business exception [{}]: {}",
                ex.getHttpStatus(),
                translatedMessage
        );

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(
                        EMallsResponseEntity.<Void>builder()
                                .status(ex.getHttpStatus())
                                .message(translatedMessage)
                                .errorCodes(translatedErrors)
                                .data(null)
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EMallsResponseEntity<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            Locale locale
    ) {

        log.warn("Validation failed for request. Locale: {}", locale);

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            log.warn(
                    "Validation error - field: '{}', rejected value: '{}', message key: '{}'",
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage()
            );
        });

        List<ErrorCode> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorCode(
                        error.getField(),
                        messageService.getMessage(
                                error.getDefaultMessage(),
                                locale
                        )
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        EMallsResponseEntity.<Void>builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message(
                                        messageService.getMessage(
                                                MessageKey.HTTP_BAD_REQUEST.getKey(),
                                                locale
                                        )
                                )
                                .errorCodes(errors)
                                .data(null)
                                .build()
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<EMallsResponseEntity<Void>> handleInvalidFormatException(
            HttpMessageNotReadableException ex,
            Locale locale
    ) {
        log.warn("Malformed JSON request", ex);

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();
            String fieldName = ife.getPath().get(0).getPropertyName();

            if (targetType == UUID.class) {

                List<ErrorCode> errors = List.of(
                        new ErrorCode(
                                fieldName,
                                messageService.getMessage("invalid.uuid", locale)
                        )
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(
                                EMallsResponseEntity.<Void>builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .message(
                                                messageService.getMessage(
                                                        MessageKey.HTTP_BAD_REQUEST.getKey(),
                                                        locale
                                                )
                                        )
                                        .errorCodes(errors)
                                        .data(null)
                                        .build()
                        );
            }
            else if(targetType.isEnum()) {
                Object[] enumValues = targetType.getEnumConstants();

                String allowedValues = Arrays.toString(enumValues);

                List<ErrorCode> errors = List.of(
                        new ErrorCode(
                                fieldName,
                                messageService.getMessage("invalid.enum", locale)
                                        + " Allowed values: " + allowedValues
                        )
                );

                return ResponseEntity.badRequest().body(
                        EMallsResponseEntity.<Void>builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message(messageService.getMessage(
                                        MessageKey.HTTP_BAD_REQUEST.getKey(), locale))
                                .errorCodes(errors)
                                .data(null)
                                .build()
                );
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        EMallsResponseEntity.<Void>builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message(
                                        messageService.getMessage(
                                                MessageKey.HTTP_BAD_REQUEST.getKey(),
                                                locale
                                        )
                                )
                                .data(null)
                                .build()
                );
    }
}
