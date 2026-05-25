package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoicePaymentCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoicePaymentResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoiceResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.entities.SupplierInvoice;
import com.overcode250204.smartlogicticssystem.entities.SupplierInvoicePayment;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.enums.InvoiceStatus;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import com.overcode250204.smartlogicticssystem.exception.SupplierInvoiceErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UserErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.SupplierInvoiceMapper;
import com.overcode250204.smartlogicticssystem.mapper.SupplierInvoicePaymentMapper;
import com.overcode250204.smartlogicticssystem.repositories.SupplierInvoicePaymentRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierInvoiceRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.ISupplierInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierInvoiceService extends BaseServiceImpl implements ISupplierInvoiceService {
    private final SupplierInvoiceRepository invoiceRepository;
    private final SupplierInvoicePaymentRepository paymentRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final SupplierInvoiceMapper invoiceMapper;
    private final SupplierInvoicePaymentMapper paymentMapper;

    @Override
    public Page<SupplierInvoiceResponseDTO> search(String keyword, Integer supplierId, InvoiceStatus status,
                                                   LocalDate fromDate, LocalDate toDate, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "invoiceDate")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt")));
        return invoiceRepository.search(keyword, supplierId, status, fromDate, toDate, pageable)
                .map(invoiceMapper::toResponse);
    }

    @Override
    @Transactional
    public SupplierInvoiceResponseDTO create(SupplierInvoiceCreateRequest request, int roleId, int userId) {
        if (invoiceRepository.existsByInvoiceCode(request.getInvoiceCode())) {
            throw new AppException(SupplierInvoiceErrorCode.SUPPLIER_INVOICE_CODE_EXISTS);
        }
        validateDueDate(request.getInvoiceDate(), request.getDueDate());

        Supplier supplier = findByIdOrThrow(supplierRepository, request.getSupplierId(),
                SupplierErrorCode.SUPPLIER_NOT_FOUND);

        SupplierInvoice invoice = invoiceMapper.toEntity(request);
        invoice.setSupplier(supplier);
        invoice.setPaidAmount(BigDecimal.ZERO);
        recalculate(invoice);

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public SupplierInvoiceResponseDTO update(Long id, SupplierInvoiceUpdateRequest request, int roleId, int userId) {
        SupplierInvoice invoice = getActiveInvoice(id);
        validateDueDate(request.getInvoiceDate(), request.getDueDate());

        if (invoiceRepository.existsByInvoiceCodeAndIdNot(request.getInvoiceCode(), id)) {
            throw new AppException(SupplierInvoiceErrorCode.SUPPLIER_INVOICE_CODE_EXISTS);
        }
        if (request.getTotalAmount().compareTo(invoice.getPaidAmount()) < 0) {
            throw new AppException(SupplierInvoiceErrorCode.TOTAL_AMOUNT_LESS_THAN_PAID_AMOUNT);
        }

        Supplier supplier = findByIdOrThrow(supplierRepository, request.getSupplierId(),
                SupplierErrorCode.SUPPLIER_NOT_FOUND);

        invoiceMapper.updateEntity(request, invoice);
        invoice.setSupplier(supplier);
        recalculate(invoice);

        return invoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    public SupplierInvoiceResponseDTO getById(Long id, int roleId, int userId) {
        return invoiceMapper.toResponse(getActiveInvoice(id));
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        SupplierInvoice invoice = getActiveInvoice(id);
        invoice.setIsActive(false);
        invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public SupplierInvoicePaymentResponseDTO addPayment(Long invoiceId, SupplierInvoicePaymentCreateRequest request,
                                                        int roleId, int userId) {
        SupplierInvoice invoice = getActiveInvoice(invoiceId);
        recalculate(invoice);

        if (request.getAmount().compareTo(invoice.getRemainingAmount()) > 0) {
            throw new AppException(SupplierInvoiceErrorCode.PAYMENT_EXCEEDS_REMAINING_AMOUNT);
        }

        User createdBy = findByIdOrThrow(userRepository, Long.valueOf(userId), UserErrorCode.USER_NOT_FOUND);
        SupplierInvoicePayment payment = paymentMapper.toEntity(request);
        payment.setInvoice(invoice);
        payment.setCreatedBy(createdBy);

        invoice.setPaidAmount(invoice.getPaidAmount().add(request.getAmount()));
        recalculate(invoice);
        invoiceRepository.save(invoice);

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    public List<SupplierInvoicePaymentResponseDTO> getPayments(Long invoiceId) {
        getActiveInvoice(invoiceId);
        return paymentRepository.findByInvoice_IdOrderByPaymentDateDescCreatedAtDesc(invoiceId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    private SupplierInvoice getActiveInvoice(Long id) {
        return invoiceRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(SupplierInvoiceErrorCode.SUPPLIER_INVOICE_NOT_FOUND));
    }

    private void validateDueDate(LocalDate invoiceDate, LocalDate dueDate) {
        if (invoiceDate != null && dueDate != null && dueDate.isBefore(invoiceDate)) {
            throw new AppException(SupplierInvoiceErrorCode.DUE_DATE_BEFORE_INVOICE_DATE);
        }
    }

    private void recalculate(SupplierInvoice invoice) {
        BigDecimal paidAmount = invoice.getPaidAmount() == null ? BigDecimal.ZERO : invoice.getPaidAmount();
        BigDecimal totalAmount = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();
        BigDecimal remainingAmount = totalAmount.subtract(paidAmount);

        invoice.setPaidAmount(paidAmount);
        invoice.setRemainingAmount(remainingAmount.max(BigDecimal.ZERO));

        if (paidAmount.compareTo(totalAmount) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(LocalDate.now())) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        } else if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.UNPAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIAL_PAID);
        }

        if (invoice.getIsActive() == null) {
            invoice.setIsActive(true);
        }
    }
}
