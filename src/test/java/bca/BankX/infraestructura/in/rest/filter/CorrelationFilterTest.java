package bca.bankX.infraestructura.in.rest.filter;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import bca.bankX.infrastructure.adapter.in.rest.filters.CorrelationFilter;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CorrelationFilterTest {

    @Mock
    private WebFilterChain chain;

    private final CorrelationFilter filter = new CorrelationFilter();

    @Test
    void testFilter_WithExistingCorrelationId() {
        // Given
        String correlationId = "test-correlation-id";
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header("X-Correlation-Id", correlationId)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mockito.when(chain.filter(exchange)).thenReturn(Mono.empty());

        // When
        filter.filter(exchange, chain).block();

        // Then
        Mockito.verify(chain).filter(exchange);
        // UUID should not be called
        try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
            mockedUuid.verifyNoInteractions();
        }
    }

    @Test
    void testFilter_WithoutCorrelationId() {
        // Given
        String generatedUuid = "generated-uuid";
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mockito.when(chain.filter(exchange)).thenReturn(Mono.empty());

        try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
            UUID mockUuid = Mockito.mock(UUID.class);
            Mockito.when(mockUuid.toString()).thenReturn(generatedUuid);
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);

            // When
            filter.filter(exchange, chain).block();

            // Then
            Mockito.verify(chain).filter(exchange);
            mockedUuid.verify(UUID::randomUUID);
        }
    }
}
