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


# 3-я
