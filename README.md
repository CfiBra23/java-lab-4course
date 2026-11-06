### Лабы

Братцев А.М. М-Б23, 19-ый вариант

# 1-я
Для истории оставлю <<
По поводу первой, чё-то Я с коммитами перемудрил, Хотел сделать - удалил файлик, который недоделал, мол ок, а потом такой, а не удалил из кода и ТД, поэтому оно там и ТД,
Далее чё-то туда-сюда мудрил, с версиями и ТД, поэтому оно всё так криво косо>>

## Условия:
PriorityQueue — топ-3 по длине имени

Считать персонажей в PriorityQueue, где приоритет = длина имени. Извлечь 3 самых длинных имени и записать их в файл.

## Реализация

**entity**
- `Character` — POJO с полями: id, name, status, species, type, gender, origin, location, created. Реализованы `equals`, `hashCode`, `toString`.

**reader**
- `CharacterReader` — интерфейс чтения списка персонажей.
- `CharacterCsvReader` — читает CSV из classpath, пропускает заголовок, парсит строки через `split(",", -1)` (сохраняет пустые поля).

**dao**
- `CharacterDao` — интерфейс: `findAll`, `findById`, `save`, `update`, `deleteById`.
- `CharacterCsvDao` — реализация через CSV: читает файл в список, манипулирует в памяти, перезаписывает файл целиком.

**service**
- `CharacterService` — интерфейс: построить очередь, извлечь топ-N, записать отчёт.
- `CharacterPriorityQueueService` — `PriorityQueue` с компаратором по длине имени (Max-Heap). Извлечение через `poll()` (итератор кучи не гарантирует порядок). Запись отчёта в CSV: `id, name, name_length, species, status`.

**Main**
1. Загружает CSV (фолбэк: `lab-1/src/main/resources/...` → `src/main/resources/...`).
2. Читает всех персонажей через DAO.
3. Строит очередь, достаёт топ-3.
4. Пишет в `lab-1/top_3_characters_by_name_length.csv`.
5. CRUD-демо: create → read → update → delete (файл возвращается в исходное состояние)

# 2-я

## Условия:
Вариант 19: `Company: id (int), name (String), employeesCount (int)`

Поднять PostgreSQL в Docker Compose (без дефолтного `postgres` и `public`). Реализовать CRUD-репозиторий на чистом JDBC с соблюдением идемпотентности всех методов кроме `save`. Подключить пул соединений HikariCP (+5 баллов) и версионирование миграций через Liquibase (+5 баллов).

## Реализация

**infra**
- `docker-compose.yml` — запуск PostgreSQL 16 в Docker с изолированным пользователем `company_user` и БД `company_db`.

**config**
- `DatabaseConfig` — парсит `application.yml` через SnakeYAML и инициализирует пул соединений `HikariDataSource` (без `static`-блоков).

**migration**
- `DatabaseMigrationService` — запуск миграций Liquibase через Java API. В ресурсах лежит `db.changelog-master.xml`, подключающий отдельный ченджсет на создание таблицы `company` в схеме `company_schema`.

**entity**
- `CompanyEntity` — сущность компании (`id`, `name`, `employeesCount`). Приватные поля, конструкторы, геттеры/сеттеры, `equals`, `hashCode`, `toString`.

**repository**
- `CompanyRepository` — интерфейс CRUD: `save`, `findById`, `findByName` (поиск по второму полю), `findAll`, `update`, `deleteById`.
- `CompanyRepositoryJdbc` — JDBC-реализация. Все ресурсы в `try-with-resources`, ошибки логируются через SLF4J и пробрасываются в `RepositoryException` (без дефолтных значений). Все методы кроме `save` строго идемпотентны.

**Main**
1. Инициализирует конфиг и поднимает пул HikariCP.
2. Прогоняет миграции Liquibase (создание схемы и таблицы).
3. Создаёт компании через `save()`.
4. Демонстрирует чтение: `findById()` и `findByName()` (включая проверку на `null` при отсутствии записи).
5. Выводит полный список через `findAll()`.
6. Проверяет `update()` и его идемпотентность (повторный вызов не меняет состояние).
7. Проверяет `deleteById()` и его идемпотентность (повторное удаление не бросает ошибок).
8. Закрывает пул `HikariDataSource`.

# 3-я
