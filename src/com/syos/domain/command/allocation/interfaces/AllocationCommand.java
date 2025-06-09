package com.syos.domain.command.allocation.interfaces;

import com.syos.application.dto.AllocatedRestockDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AllocationCommand {
    List<AllocatedRestockDTO> execute(Connection conn) throws SQLException;

    default void undo(Connection conn) {
        throw new UnsupportedOperationException("Undo not implemented for this command");
    }
}