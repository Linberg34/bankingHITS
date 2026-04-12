#!/bin/bash
# ═══════════════════════════════════════════════════════════════
#  Bank Hits — финальный тест бекенда (Stage 3)
#  Req 1 (FCM) пропущен — требует ручной настройки Firebase.
#
#  Использование:
#    1. Заполни JWT и ACCOUNT_NUMBER
#    2. chmod +x test_backend.sh && ./test_backend.sh
# ═══════════════════════════════════════════════════════════════

BASE_CLIENT=http://localhost:8084
BASE_MONITORING=http://localhost:8087
BASE_ACCOUNT=http://localhost:8082
JWT="eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxMjdkYjJiMy0zMjYxLTQ0MGItYmUwMS0zNGIyYjgxOTU2ZGEiLCJpc3MiOiJodHRwOi8vc2VydmljZS1zc286ODA4NiIsImlhdCI6MTc3NTk3MjIxMiwiZXhwIjoxNzc1OTc1ODEyLCJyb2xlcyI6WyJDTElFTlQiXSwidXNlcm5hbWUiOiJjbGllbnRAYmFuay5sb2NhbCJ9.g7-hVU8s75Jr7cHAEO5yHtpwuZC0Y01tVYQAiw6edxSmwEoC82J9BAQijrauBpJnrsgryFgV0GJy6i70D49q83-viMszp9I_69cWmoGdVVkVCfoHQhuKB9EkDsTBctKgwYGePlMSsgzmDPrdGn5qnFvH5JJ70o3TdYq-0QtNGv_hlG_Biq8Fdvi5YYFEvRUUilFly9-uo9QUSzky1VCVbcN6xpPNbXWncouLWCrrwUY9iFzu-kKjJNVIfkGbWPz3oiqCVaRsEUas1fQSQFvheqCbGv9eF-_Nb6okhKLuu9uR-k4F21pMyRzAXv9X7riyT2IAYMUGMZIULAScg7Paqw"
ACCOUNT_NUMBER="4081742719662980191"

# ─── Цвета ────────────────────────────────────────────────────
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

PASS() { echo -e "  ${GREEN}✓ PASS${NC} — $1"; }
FAIL() { echo -e "  ${RED}✗ FAIL${NC} — $1"; FAILURES=$((FAILURES + 1)); }
SKIP() { echo -e "  ${YELLOW}⊘ SKIP${NC} — $1"; }
INFO() { echo -e "  $1"; }

FAILURES=0

require_jwt() {
  if [ -z "$JWT" ]; then
    SKIP "JWT не задан — установи переменную JWT в начале скрипта"
    return 1
  fi
  return 0
}

require_account() {
  if [ -z "$ACCOUNT_NUMBER" ]; then
    SKIP "ACCOUNT_NUMBER не задан — установи переменную ACCOUNT_NUMBER в начале скрипта"
    return 1
  fi
  return 0
}

json_field() {
  # json_field '<json>' '<field>'
  echo "$1" | python3 -c "import json,sys; data=json.load(sys.stdin); print(data$2)" 2>/dev/null
}

# ═══════════════════════════════════════════════════════════════
echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║         Bank Hits — Финальное тестирование           ║"
echo "╚══════════════════════════════════════════════════════╝"

# ═══════════════════════════════════════════════════════════════
echo ""
echo "=== Req 2: Нестабильность 30%/70% ==="
echo "  Отправляем 20 запросов к account-service (без авторизации)..."
echo "  Instability-фильтр срабатывает до Spring Security — авторизация не нужна."

COUNT_500=0
TOTAL=20
for i in $(seq 1 $TOTAL); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_ACCOUNT/api/accounts")
  [ "$CODE" = "500" ] && COUNT_500=$((COUNT_500 + 1))
done

INFO "Получено 500-ошибок: $COUNT_500 из $TOTAL"

CURRENT_MINUTE=$(date +%M | sed 's/^0//')  # убираем ведущий ноль
CURRENT_MINUTE=${CURRENT_MINUTE:-0}

if [ $((CURRENT_MINUTE % 2)) -eq 0 ]; then
  INFO "Чётная минута — ожидается ~70% ошибок (≥10 из 20)"
  if [ "$COUNT_500" -ge 8 ]; then
    PASS "$COUNT_500/20 ошибок (70%-режим подтверждён)"
  else
    FAIL "$COUNT_500/20 ошибок — слишком мало для чётной минуты (ожидалось ≥8)"
  fi
else
  INFO "Нечётная минута — ожидается ~30% ошибок (≥2 из 20)"
  if [ "$COUNT_500" -ge 2 ] && [ "$COUNT_500" -le 12 ]; then
    PASS "$COUNT_500/20 ошибок (30%-режим подтверждён)"
  else
    FAIL "$COUNT_500/20 ошибок — значение вне диапазона для нечётной минуты (2–12)"
  fi
fi

# ═══════════════════════════════════════════════════════════════
echo ""
echo "=== Req 3: Идемпотентность ==="

if ! require_jwt || ! require_account; then
  : # SKIP уже напечатан
else
  IDEM_KEY="idem-test-$(date +%s)"
  INFO "Ключ идемпотентности: $IDEM_KEY"

  # Баланс до
  ACCOUNTS_BEFORE=$(curl -s "$BASE_CLIENT/bff/client/accounts" \
    -H "Authorization: Bearer $JWT")
  BALANCE_BEFORE=$(echo "$ACCOUNTS_BEFORE" | python3 -c "
import json, sys
data = json.load(sys.stdin)
accounts = data.get('accounts', data) if isinstance(data, dict) else data
accts = [a for a in accounts if a.get('accountNumber') == '$ACCOUNT_NUMBER']
print(accts[0]['balance'] if accts else 'NOT_FOUND')
" 2>/dev/null)
  INFO "Баланс до: $BALANCE_BEFORE"

  if [ "$BALANCE_BEFORE" = "NOT_FOUND" ]; then
    FAIL "Счёт $ACCOUNT_NUMBER не найден — проверь ACCOUNT_NUMBER"
  else
    # Первый запрос
    R1=$(curl -s -o /dev/null -w "%{http_code}" \
      -X POST "$BASE_CLIENT/bff/client/operations/deposit" \
      -H "Authorization: Bearer $JWT" \
      -H "Idempotency-Key: $IDEM_KEY" \
      -H "Content-Type: application/json" \
      -d "{\"accountNumber\": \"$ACCOUNT_NUMBER\", \"amount\": 77}")
    INFO "Первый запрос: HTTP $R1"

    # Второй запрос — тот же ключ
    R2=$(curl -s -o /dev/null -w "%{http_code}" \
      -X POST "$BASE_CLIENT/bff/client/operations/deposit" \
      -H "Authorization: Bearer $JWT" \
      -H "Idempotency-Key: $IDEM_KEY" \
      -H "Content-Type: application/json" \
      -d "{\"accountNumber\": \"$ACCOUNT_NUMBER\", \"amount\": 77}")
    INFO "Второй запрос (тот же ключ): HTTP $R2"

    # Пауза — операция асинхронная (Kafka)
    INFO "Ждём 3 секунды (асинхронная обработка Kafka)..."
    sleep 3

    # Баланс после
    ACCOUNTS_AFTER=$(curl -s "$BASE_CLIENT/bff/client/accounts" \
      -H "Authorization: Bearer $JWT")
    BALANCE_AFTER=$(echo "$ACCOUNTS_AFTER" | python3 -c "
import json, sys
data = json.load(sys.stdin)
accounts = data.get('accounts', data) if isinstance(data, dict) else data
accts = [a for a in accounts if a.get('accountNumber') == '$ACCOUNT_NUMBER']
print(accts[0]['balance'] if accts else 'NOT_FOUND')
" 2>/dev/null)
    INFO "Баланс после: $BALANCE_AFTER"

    DIFF=$(python3 -c "
try:
    b = float('$BALANCE_BEFORE')
    a = float('$BALANCE_AFTER')
    print(round(a - b, 2))
except:
    print('ERROR')
" 2>/dev/null)
    INFO "Разница баланса: $DIFF (ожидается 77.0)"

    if [ "$DIFF" = "77.0" ] || [ "$DIFF" = "77" ]; then
      PASS "Баланс вырос ровно на 77 — повторный запрос не задублировал операцию"
    elif [ "$DIFF" = "154.0" ] || [ "$DIFF" = "154" ]; then
      FAIL "Баланс вырос на 154 — идемпотентность не работает, операция выполнилась дважды"
    else
      FAIL "Неожиданная разница баланса: $DIFF (возможно, нестабильность помешала тесту — повтори)"
    fi
  fi
fi

# ═══════════════════════════════════════════════════════════════
echo ""
echo "=== Req 4: Трассировка (Trace-Id сквозь сервисы) ==="

if ! require_jwt; then
  :
else
  TRACE_ID="trace-test-$(date +%s)"
  INFO "Отправляем запрос с X-Trace-Id: $TRACE_ID"

  RESPONSE_HEADERS=$(curl -s -D - -o /dev/null \
    "$BASE_CLIENT/bff/client/accounts" \
    -H "Authorization: Bearer $JWT" \
    -H "X-Trace-Id: $TRACE_ID")

  RETURNED_TRACE=$(echo "$RESPONSE_HEADERS" | grep -i "x-trace-id" | tr -d '\r' | awk '{print $2}')
  INFO "X-Trace-Id в ответе: $RETURNED_TRACE"

  if [ "$RETURNED_TRACE" = "$TRACE_ID" ]; then
    PASS "X-Trace-Id корректно вернулся в response headers"
  else
    FAIL "X-Trace-Id в ответе не совпадает (получили: '$RETURNED_TRACE', ожидали: '$TRACE_ID')"
  fi

  # Проверить что traceId попал в monitoring-service
  sleep 1
  MONITORING_LOGS=$(curl -s "$BASE_MONITORING/api/monitoring/logs")
  TRACE_IN_MONITORING=$(echo "$MONITORING_LOGS" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
found = any(l.get('traceId', '').startswith('$TRACE_ID') for l in logs)
print('YES' if found else 'NO')
" 2>/dev/null)
  INFO "traceId найден в monitoring-service: $TRACE_IN_MONITORING"

  if [ "$TRACE_IN_MONITORING" = "YES" ]; then
    PASS "traceId зафиксирован в monitoring-service"
  else
    FAIL "traceId не найден в monitoring-service (логи могли не успеть — это не критично если Req 5 проходит)"
  fi
fi

# ═══════════════════════════════════════════════════════════════
echo ""
echo "=== Req 5: Мониторинг — логи попадают в monitoring-service ==="

# Очищаем логи
curl -s -X DELETE "$BASE_MONITORING/api/monitoring/logs" > /dev/null
INFO "Логи очищены"

# Нужен запрос через BFF (он использует DownstreamCallExecutor → публикует в мониторинг)
if require_jwt; then
  curl -s -o /dev/null "$BASE_CLIENT/bff/client/accounts" \
    -H "Authorization: Bearer $JWT"
  INFO "Запрос через client-bff выполнен"
else
  # Без JWT пробуем несколько запросов — нестабильность создаст трафик
  INFO "JWT не задан — делаем несколько запросов к account-service (только для Req 5)"
  INFO "Внимание: без JWT monitoring-service может не получить логи (BFF их не вызывается)"
  for i in $(seq 1 3); do curl -s -o /dev/null "$BASE_CLIENT/bff/client/accounts"; done
fi

sleep 1
LOGS=$(curl -s "$BASE_MONITORING/api/monitoring/logs")
LOG_COUNT=$(echo "$LOGS" | python3 -c "import json,sys; print(len(json.load(sys.stdin)))" 2>/dev/null)
INFO "Записей в мониторинге: $LOG_COUNT"

if [ "$LOG_COUNT" -gt 0 ] 2>/dev/null; then
  # Проверяем структуру первой записи
  FIRST=$(echo "$LOGS" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
if logs:
    l = logs[0]
    missing = [f for f in ['app','service','method','path','status','latencyMs','traceId','level'] if f not in l]
    print('MISSING:' + ','.join(missing) if missing else 'OK')
" 2>/dev/null)

  if [ "$FIRST" = "OK" ]; then
    PASS "$LOG_COUNT записей, структура корректна (все обязательные поля присутствуют)"
  else
    FAIL "Записей $LOG_COUNT, но в структуре отсутствуют поля: $FIRST"
  fi
else
  FAIL "Мониторинг пуст после запроса — DownstreamCallExecutor не публикует логи"
fi

# Дополнительно: проверяем что есть хотя бы один лог с retries или ошибкой
ERROR_LOGS=$(echo "$LOGS" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
errors = [l for l in logs if l.get('status', 0) >= 500 or l.get('retries', 0) > 0]
print(len(errors))
" 2>/dev/null)
INFO "Записей с ошибками/retry в мониторинге: $ERROR_LOGS"
if [ "$ERROR_LOGS" -gt 0 ] 2>/dev/null; then
  PASS "Ошибки и retry фиксируются в мониторинге"
else
  INFO "  (нет записей с ошибками — попробуй в чётную минуту для более показательного результата)"
fi

# ═══════════════════════════════════════════════════════════════
echo ""
echo "=== Req 6a: Retry — повторные попытки при ошибке ==="

if ! require_jwt; then
  :
else
  INFO "Очищаем мониторинг и отправляем 5 запросов (лучше запускать в чётную минуту)..."
  curl -s -X DELETE "$BASE_MONITORING/api/monitoring/logs" > /dev/null

  for i in $(seq 1 5); do
    curl -s -o /dev/null "$BASE_CLIENT/bff/client/accounts" \
      -H "Authorization: Bearer $JWT"
  done

  sleep 1
  LOGS=$(curl -s "$BASE_MONITORING/api/monitoring/logs")
  MAX_RETRIES=$(echo "$LOGS" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
if not logs:
    print(0)
else:
    print(max(l.get('retries', 0) for l in logs))
" 2>/dev/null)
  TOTAL_RETRIES=$(echo "$LOGS" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
print(sum(l.get('retries', 0) for l in logs))
" 2>/dev/null)

  INFO "Максимум retry в одном запросе: $MAX_RETRIES"
  INFO "Всего retry суммарно: $TOTAL_RETRIES"

  if [ "$TOTAL_RETRIES" -gt 0 ] 2>/dev/null; then
    PASS "Retry зафиксированы в мониторинге (суммарно: $TOTAL_RETRIES)"
  else
    FAIL "Retry не обнаружены — возможно нечётная минута (мало ошибок). Повтори в чётную минуту."
  fi
fi

# ═══════════════════════════════════════════════════════════════
echo ""
echo "=== Req 6b: Circuit Breaker — срабатывание при высоком % ошибок ==="

if ! require_jwt; then
  :
else
  CURRENT_MINUTE_CB=$(date +%M | sed 's/^0//')
  CURRENT_MINUTE_CB=${CURRENT_MINUTE_CB:-0}
  if [ $((CURRENT_MINUTE_CB % 2)) -ne 0 ]; then
    INFO "⚠ Сейчас нечётная минута (30% ошибок) — circuit breaker может не открыться."
    INFO "  Для надёжного теста запусти скрипт в чётную минуту (70% ошибок)."
  fi

  INFO "Очищаем мониторинг и отправляем 20 запросов подряд..."
  curl -s -X DELETE "$BASE_MONITORING/api/monitoring/logs" > /dev/null

  GOT_503=0
  for i in $(seq 1 20); do
    CODE=$(curl -s -o /dev/null -w "%{http_code}" \
      "$BASE_CLIENT/bff/client/accounts" \
      -H "Authorization: Bearer $JWT")
    if [ "$CODE" = "503" ]; then
      GOT_503=$((GOT_503 + 1))
      INFO "  Запрос $i: $CODE ← circuit breaker"
    fi
  done

  INFO "Получено 503 (circuit breaker): $GOT_503"

  if [ "$GOT_503" -gt 0 ]; then
    PASS "Circuit breaker сработал — получено $GOT_503 ответов 503"
  else
    FAIL "Circuit breaker не сработал за 20 запросов. Запусти в чётную минуту или увеличь число запросов."
  fi

  # Проверяем circuitState в мониторинге
  LOGS=$(curl -s "$BASE_MONITORING/api/monitoring/logs")
  CB_STATES=$(echo "$LOGS" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
states = {}
for l in logs:
    s = l.get('circuitState', 'UNKNOWN')
    states[s] = states.get(s, 0) + 1
for k, v in states.items():
    print(f'  {k}: {v} записей')
" 2>/dev/null)
  INFO "Circuit states в мониторинге:"
  echo "$CB_STATES"

  OPEN_COUNT=$(echo "$LOGS" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
print(sum(1 for l in logs if l.get('circuitState') == 'OPEN' or l.get('blockedByCircuit')))
" 2>/dev/null)

  if [ "$OPEN_COUNT" -gt 0 ] 2>/dev/null; then
    PASS "В мониторинге зафиксировано $OPEN_COUNT записей с состоянием OPEN/blocked"
  else
    INFO "  (OPEN-состояние не зафиксировано в мониторинге — нормально если 503 не было)"
  fi

  # Ждём восстановления (HALF_OPEN)
  if [ "$GOT_503" -gt 0 ]; then
    INFO "Ждём 21 секунду для перехода в HALF_OPEN..."
    sleep 21
    CODE_AFTER=$(curl -s -o /dev/null -w "%{http_code}" \
      "$BASE_CLIENT/bff/client/accounts" \
      -H "Authorization: Bearer $JWT")
    INFO "Запрос после паузы: HTTP $CODE_AFTER"

    HALF_OPEN=$(curl -s "$BASE_MONITORING/api/monitoring/logs" | python3 -c "
import json, sys
logs = json.load(sys.stdin)
found = any(l.get('circuitState') == 'HALF_OPEN' for l in logs)
print('YES' if found else 'NO')
" 2>/dev/null)
    INFO "HALF_OPEN зафиксирован в мониторинге: $HALF_OPEN"

    if [ "$HALF_OPEN" = "YES" ]; then
      PASS "Переход OPEN → HALF_OPEN подтверждён"
    else
      INFO "  (HALF_OPEN не обнаружен — возможно запрос сразу прошёл или упал снова)"
    fi
  fi
fi

# ═══════════════════════════════════════════════════════════════
echo ""
echo "════════════════════════════════════════"
if [ "$FAILURES" -eq 0 ]; then
  echo -e "${GREEN}Все проверенные тесты прошли успешно!${NC}"
else
  echo -e "${RED}Провалено тестов: $FAILURES${NC}"
fi
echo "════════════════════════════════════════"
echo ""
