package com.escola.dto;

import java.util.Objects;

/**
 * A generic wrapper for API responses.
 * This class standardizes the structure of responses sent from the API,
 * including a success flag, an optional message, and the data payload.
 * This class is declared as final as it is a simple data carrier.
 *
 * @param <T> The type of the data payload.
 * @version 1.0
 * @author SeuNomeAqui
 */
public final class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data; // Generic data payload

    /**
     * Constructs an ApiResponse.
     *
     * @param success Indicates if the operation was successful.
     * @param message An optional message (e.g., error details or success confirmation).
     * @param data    The actual data payload of the response.
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Static factory methods for convenience

    /**
     * Creates a successful API response with data and a default success message.
     * @param data The data payload.
     * @param <T> The type of the data.
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operação realizada com sucesso.", data);
    }

    /**
     * Creates a successful API response with data and a custom success message.
     * @param data The data payload.
     * @param message The custom success message.
     * @param <T> The type of the data.
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates a failed API response with an error message and no data.
     * @param message The error message.
     * @param <T> The type of the data (will be null).
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Creates a failed API response with an error message and optional error details/data.
     * @param message The error message.
     * @param errorData Optional data associated with the error.
     * @param <T> The type of the error data.
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> error(String message, T errorData) {
        return new ApiResponse<>(false, message, errorData);
    }


    // Getters

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse<?> that = (ApiResponse<?>) o;
        return success == that.success &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, data);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}