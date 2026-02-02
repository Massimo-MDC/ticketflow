package it.ticketflow.ticketflow.dto.graphql;

import it.ticketflow.ticketflow.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserPageDTO(
        List<User> content,
        PageInfoDTO pageInfo
) {
    public static UserPageDTO from(Page<User> page) {
        return new UserPageDTO(
                page.getContent(),
                PageInfoDTO.from(page)
        );
    }
}