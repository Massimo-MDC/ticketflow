package it.ticketflow.ticketflow.dto.graphql;

import it.ticketflow.ticketflow.entity.Event;
import org.springframework.data.domain.Page;

import java.util.List;

public record EventPageDTO(
        List<Event> content,
        PageInfoDTO pageInfo
) {
    public static EventPageDTO from(Page<Event> page) {
        return new EventPageDTO(
                page.getContent(),
                PageInfoDTO.from(page)
        );
    }
}