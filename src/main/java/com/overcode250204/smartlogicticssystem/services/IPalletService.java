package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.PalletCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletItemCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletItemResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;

import java.util.List;

public interface IPalletService {

    PalletResponseDTO create(PalletCreateRequest request, int roleId, int userId);

    PalletResponseDTO getById(Long id, int roleId, int userId);

    List<PalletResponseDTO> getAll(int roleId, int userId);

    List<PalletResponseDTO> getStaffTasks(int roleId, int userId);

    PalletResponseDTO getStaffTaskById(Long id, int roleId, int userId);


    void delete(Long id, int roleId, int userId);

   PalletItemResponseDTO addPalletItem(
            PalletItemCreateRequest request,
            Long palletId,
            int roleId,
            int userId
    );

   PalletItemResponseDTO scanPalletItem( Long palletId, String orderCode, int roleId, int userId);

    void removePalletItem(
            Long palletId,
            String orderCode,
            int roleId,
            int userId
    );

    PalletResponseDTO makeSealed(Long palletId, int roleId, int userId);

    PalletResponseDTO confirmPalletArrival(String palletCode, Double latitude, Double longitude, int roleId, int userId);

    void confirmOrderArrival(String orderCode, Double latitude, Double longitude, int roleId, int userId);

    PalletResponseDTO updateStatusToCanSeal(Long palletId, int roleId, int userId);
}
