package com.iisovaii.client_bff.dto.common;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last,
        boolean first,
        boolean empty
) {}
