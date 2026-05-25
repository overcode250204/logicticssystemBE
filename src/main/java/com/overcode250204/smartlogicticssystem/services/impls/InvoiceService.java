package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.DashboardResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InvoiceResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Invoice;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.exception.InvoiceErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UserErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InvoiceMapper;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.InvoiceRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService extends BaseServiceImpl implements IInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final InventoryBatchRepository inventoryBatchRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceResponseDTO getById(Long id, int roleId, int userId) {
        Invoice invoice = findByIdOrThrow(invoiceRepository, id, InvoiceErrorCode.INVOICE_NOT_FOUND);
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponseDTO create(InvoiceCreateRequest request, int roleId, int userId) {
        User creator = findByIdOrThrow(userRepository, request.getCreatedById(), UserErrorCode.USER_NOT_FOUND);

        Invoice invoice = invoiceMapper.toEntity(request);
        invoice.setCreatedBy(creator);

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public InvoiceResponseDTO update(Long id, InvoiceUpdateRequest request, int roleId, int userId) {
        Invoice invoice = findByIdOrThrow(invoiceRepository, id, InvoiceErrorCode.INVOICE_NOT_FOUND);
        User creator = findByIdOrThrow(userRepository, request.getCreatedById(), UserErrorCode.USER_NOT_FOUND);

        invoiceMapper.updateEntity(request, invoice);
        invoice.setCreatedBy(creator);

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        Invoice invoice = findByIdOrThrow(invoiceRepository, id, InvoiceErrorCode.INVOICE_NOT_FOUND);
        invoiceRepository.delete(invoice);
    }

    @Override
    public DashboardResponseDTO getDashboardStats() {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        BigDecimal revenue = invoiceRepository.sumTotalAmountByMonth(month, year);
        long delivered = orderRepository.countByOrderStatus("Delivered");
        long total = inventoryBatchRepository.count();
        long damaged = inventoryBatchRepository.countByStatus("Damaged");
        double ratio = total == 0 ? 0.0 : (damaged * 100.0 / total);

        return DashboardResponseDTO.builder()
                .totalRevenueThisMonth(revenue)
                .totalDeliveredOrders(delivered)
                .damagedInventoryRatio(ratio)
                .build();
    }
}
