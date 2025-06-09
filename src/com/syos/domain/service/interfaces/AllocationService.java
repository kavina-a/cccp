package com.syos.domain.service.interfaces;

import com.syos.application.dto.AllocatedRestockDTO;
import com.syos.application.dto.request.AllocationRequest;
import java.util.List;

public interface AllocationService {
    List<AllocatedRestockDTO> allocateToWeb(AllocationRequest request);
    List<AllocatedRestockDTO> allocateToShelf(AllocationRequest request);
}
