package ps.emall.catalog.event;

import java.time.Instant;

public record CatalogEvent(
        String user,
        Long entityId,
        InteractionType interactionType,
        Instant occurredAt,
        String source,
        String correlationId,
        String metadata
) {
}