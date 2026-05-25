package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.DashboardResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InvoiceResponseDTO;

import java.util.List;

public interface IInvoiceService {
    InvoiceResponseDTO create(InvoiceCreateRequest request, int roleId, int userId);

    InvoiceResponseDTO update(Long id, InvoiceUpdateRequest request, int roleId, int userId);

    InvoiceResponseDTO getById(Long id, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    List<InvoiceResponseDTO> getAllInvoices();

    DashboardResponseDTO getDashboardStats();
}
