package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hepp.cov2words.common.constant.ErrorCodes;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Default response object, that is generic. If the error code is 0  and the error message is empty, the request was
 * successful.
 *
 * @param <T> specifies which instance of WrappedResponse
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "Default",
        description = "The default service response object uses error code and message to indicate" +
                " errors. These errors are handled by the client.")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WrappedResponseDTO<T> implements Serializable {

    @JsonProperty("error_code")
    @ApiModelProperty(value = "Contains the error of the request. If the error code is 0, everything is fine.")
    private int errorCode;

    @JsonProperty("error_message")
    @ApiModelProperty(value = "Contains the error message, that possibly occurred. If it is empty, everything is fine.",
            allowEmptyValue = true)
    private String errorMessage;

    @JsonProperty("error_id")
    @ApiModelProperty(value = "Contains the ID of the error, that is logged with the exception on error.",
            allowEmptyValue = true)
    private String errorId;

    @JsonProperty("data")
    @ApiModelProperty(value = "Generic response object which contains the response data, e.g. timestamp information.")
    private T responseBody;

    /**
     * Creates a erroneous response with no data.
     *
     * @param errorCode    Error code to provide
     * @param errorMessage Error message to provide
     * @return Response with provided error code and error message
     */
    public static WrappedResponseDTO fromError(int errorCode, String errorMessage, String errorId) {
        return new WrappedResponseDTO<>(errorCode, errorMessage, errorId, null);
    }

    /**
     * Creates a successful response with no data.
     *
     * @return Response with status code 0 and no error message or content.
     */
    public static WrappedResponseDTO empty() {
        return new WrappedResponseDTO<>(ErrorCodes.NO_ERROR, null, null, null);
    }

    /**
     * Creates a successful response with the given data.
     *
     * @param data Data to set
     * @return Response with status code 0, no error message and provided content.
     */
    public static <E> WrappedResponseDTO<E> fromData(E data) {
        return new WrappedResponseDTO<>(ErrorCodes.NO_ERROR, null, null, data);
    }
}
