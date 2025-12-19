package bca.bankX.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Given
        String message = "Test error message";

        // When
        BusinessException exception = new BusinessException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        // Given
        BusinessException exception = new BusinessException("Test");

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowBusinessException() {
        // Given
        String message = "Business logic error";

        // When & Then
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            throw new BusinessException(message);
        });

        assertEquals(message, thrown.getMessage());
    }

    @Test
    void testNullMessage() {
        // When
        BusinessException exception = new BusinessException(null);

        // Then
        assertNull(exception.getMessage());
    }
}
