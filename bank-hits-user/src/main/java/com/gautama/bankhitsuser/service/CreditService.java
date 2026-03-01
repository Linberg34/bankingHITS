package com.gautama.bankhitsuser.service;

import com.gautama.bankhitsuser.client.CreditServiceClient;
import com.gautama.bankhitsuser.dto.*;
import com.gautama.bankhitsuser.enums.Role;
import com.gautama.bankhitsuser.enums.Status;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final CreditServiceClient creditServiceClient;
    private final UserRepository userRepository;

    public TariffResponse createTariff(Long userId, CreateTariffRequest req) {
        User user = getUserOrThrow(userId);
        requireRole(user, Role.EMPLOYEE);
        return creditServiceClient.createTariff(req);
    }

    public List<TariffResponse> getAllTariffs(Long userId) {
        getUserOrThrow(userId);
        return creditServiceClient.getAllTariffs();
    }


    public List<CreditResponse> getAllCredits(Long userId) {
        User user = getUserOrThrow(userId);
        requireRole(user, Role.EMPLOYEE);
        return creditServiceClient.getAllCredits();
    }

    public CreditResponse takeCredit(Long userId, TakeCreditRequest req) {
        User user = getUserOrThrow(userId);
        requireRole(user, Role.CLIENT);

        return creditServiceClient.takeCredit(new TakeCreditInternalRequest(
                user.getId(),
                req.getAccountId(),
                req.getTariffId(),
                req.getAmount()
        ));
    }

    public List<CreditResponse> getClientCredits(Long userId, Long clientId) {
        User user = getUserOrThrow(userId);

        if (user.getRole() == Role.CLIENT && !user.getId().equals(clientId)) {
            throw new IllegalArgumentException("Нельзя просматривать кредиты другого клиента");
        }

        return creditServiceClient.getClientCredits(clientId);
    }

    public CreditResponse getCredit(Long userId, Long creditId) {
        User user = getUserOrThrow(userId);

        CreditResponse credit = creditServiceClient.getCredit(creditId);

        if (user.getRole() == Role.CLIENT && !user.getId().equals(credit.getClientId())) {
            throw new IllegalArgumentException("Нет доступа к этому кредиту");
        }

        return credit;
    }

    public CreditResponse repayCredit(Long userId, Long creditId) {
        User user = getUserOrThrow(userId);
        requireRole(user, Role.CLIENT);

        CreditResponse credit = creditServiceClient.getCredit(creditId);
        if (!user.getId().equals(credit.getClientId())) {
            throw new IllegalArgumentException("Нельзя погашать кредит другого клиента");
        }

        return creditServiceClient.repayCredit(creditId);
    }

    public CreditResponse repayPartial(Long userId, Long creditId, PartialRepayRequest req) {
        User user = getUserOrThrow(userId);
        requireRole(user, Role.CLIENT);

        CreditResponse credit = creditServiceClient.getCredit(creditId);
        if (!user.getId().equals(credit.getClientId())) {
            throw new IllegalArgumentException("Нельзя погашать кредит другого клиента");
        }

        return creditServiceClient.repayPartial(creditId, req);
    }

    private User getUserOrThrow(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь с id: " + userId + " не найден"));

        if (user.getStatus() == Status.BANNED) {
            throw new IllegalStateException("Пользователь заблокирован");
        }

        return user;
    }

    private void requireRole(User user, Role required) {
        if (user.getRole() != required) {
            throw new IllegalStateException("Доступно только для роли " + required.name() +
                    ", у вас: " + user.getRole().name());
        }
    }
}
