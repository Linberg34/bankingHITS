package com.gautama.bankhitsaccount.mapper;

import com.gautama.bankhitsaccount.dto.CreateOperationRequest;
import com.gautama.bankhitsaccount.dto.OperationDTO;
import com.gautama.bankhitsaccount.model.Account;
import com.gautama.bankhitsaccount.model.Operation;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {AccountMapper.class})
public interface OperationMapper {

    // Базовое маппинг Operation -> OperationDTO
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "balanceBefore", source = "balanceBefore")
    @Mapping(target = "balanceAfter", source = "balanceAfter")
    @Mapping(target = "createdAt", source = "createdAt")
    OperationDTO toDTO(Operation operation);

    // Маппинг списка
    List<OperationDTO> toDTOList(List<Operation> operations);

    // Маппинг CreateOperationRequest + Account -> Operation
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "operationType", source = "request.operationType")
    @Mapping(target = "amount", source = "request.amount")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "balanceBefore", ignore = true)
    @Mapping(target = "balanceAfter", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    Operation toEntity(CreateOperationRequest request, @Context Account account);

    // Маппинг для обновления существующей операции
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateOperation(@MappingTarget Operation operation, OperationDTO operationDTO);

    // Кастомный метод для установки балансов после завершения операции
//    @AfterMapping
//    default void setBalances(@MappingTarget Operation operation) {
//        if (operation.getAccount() != null) {
//            operation.setCurrency(operation.getAccount().getCurrency());
//        }
//    }

    // Кастомный метод для создания операции с балансами
    default Operation createOperationWithBalances(CreateOperationRequest request,
                                                  Account account,
                                                  java.math.BigDecimal balanceBefore,
                                                  java.math.BigDecimal balanceAfter,
                                                  String status) {
        Operation operation = toEntity(request, account);
        operation.setBalanceBefore(balanceBefore);
        operation.setBalanceAfter(balanceAfter);
        operation.setStatus(status);
        return operation;
    }
}