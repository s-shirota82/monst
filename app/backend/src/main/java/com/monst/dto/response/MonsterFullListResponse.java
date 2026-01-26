package com.monst.dto.response;

import java.util.List;

public record MonsterFullListResponse(
        List<MonsterFullResponse> items,
        int page,
        int size,
        long total) {
}
