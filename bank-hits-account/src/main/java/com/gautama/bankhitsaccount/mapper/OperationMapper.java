package com.gautama.bankhitsaccount.mapper;

import com.gautama.bankhitsaccount.dto.CreateOperationRequest;
import com.gautama.bankhitsaccount.dto.OperationDTO;
import com.gautama.bankhitsaccount.model.Account;
import com.gautama.bankhitsaccount.model.Operation;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {AccountMapper.class})
public interface OperationMapper {

    // Маппинг Operation -> OperationDTO
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "balanceBefore", source = "balanceBefore")
    @Mapping(target = "balanceAfter", source = "balanceAfter")
    @Mapping(target = "createdAt", source = "createdAt")
    OperationDTO toDTO(Operation operation);

    // Маппинг списка
    List<OperationDTO> toDTOList(List<Operation> operations);

    // Маппинг CreateOperationRequest + Account -> Operation
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "operationType", source = "request.operationType")
    @Mapping(target = "amount", source = "request.amount")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "balanceBefore", ignore = true)
    @Mapping(target = "balanceAfter", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Operation toEntity(CreateOperationRequest request, String accountNumber);

//    // Метод для создания успешной операции с балансами
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "account", source = "account")
//    @Mapping(target = "operationType", source = "request.operationType")
//    @Mapping(target = "amount", source = "request.amount")
//    @Mapping(target = "description", source = "request.description")
//    @Mapping(target = "balanceBefore", source = "balanceBefore")
//    @Mapping(target = "balanceAfter", source = "balanceAfter")
//    @Mapping(target = "status", constant = "SUCCESS")
//    Operation createSuccessfulOperation(CreateOperationRequest request,
//                                        Account account,
//                                        BigDecimal balanceBefore,
//                                        BigDecimal balanceAfter);
//
//    // Метод для создания неуспешной операции
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "account", source = "account")
//    @Mapping(target = "operationType", source = "request.operationType")
//    @Mapping(target = "amount", source = "request.amount")
//    @Mapping(target = "description", source = "request.description")
//    @Mapping(target = "balanceBefore", source = "balanceBefore")
//    @Mapping(target = "balanceAfter", source = "balanceBefore") // Баланс не меняется
//    @Mapping(target = "status", constant = "FAILED")
//    Operation createFailedOperation(CreateOperationRequest request,
//                                    Account account,
//                                    BigDecimal balanceBefore,
//                                    String errorMessage);
//
//    // Обновление статуса операции
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "account", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    void updateStatus(@MappingTarget Operation operation, String status);

    // Кастомный метод после маппинга
    @AfterMapping
    default void setAdditionalFields(@MappingTarget Operation operation) {
        // Можно добавить дополнительную логику если нужно
    }
}