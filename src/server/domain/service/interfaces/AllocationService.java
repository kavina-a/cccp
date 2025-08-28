package server.domain.service.interfaces;

import server.application.dto.AllocatedRestockDTO;
import server.application.dto.request.AllocationRequest;
import java.util.List;

public interface AllocationService {
    List<AllocatedRestockDTO> allocateToWeb(AllocationRequest request);
    List<AllocatedRestockDTO> allocateToShelf(AllocationRequest request);
}
