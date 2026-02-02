package it.ticketflow.ticketflow.dto.graphql;

import it.ticketflow.ticketflow.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public record OrderPageDTO(
        List<Order> content,
        PageInfoDTO pageInfo
) {
    public static OrderPageDTO from(Page<Order> page) {
        return new OrderPageDTO(
                page.getContent(),
                PageInfoDTO.from(page)
        );
    }
}