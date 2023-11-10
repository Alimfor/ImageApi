package alim.com.imageApi.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class ResponseEntityUtil {
    public static <T> ResponseEntity<T> sendResponse(final HttpStatus httpStatus,
                                                     final MediaType mediaType,
                                                     final T responseData) {
        return ResponseEntity.status(httpStatus)
                .contentType(mediaType)
                .body(responseData);
    }
}
