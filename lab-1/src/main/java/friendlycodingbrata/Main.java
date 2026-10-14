package friendlycodingbrata;

import friendlycodingbrata.dao.CharacterCsvDao;
import friendlycodingbrata.dao.CharacterDao;
import friendlycodingbrata.entity.Character;
import friendlycodingbrata.service.CharacterPriorityQueueService;
import friendlycodingbrata.service.CharacterService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public class Main {

    private static final int TOP_CHARACTERS_COUNT = 3;
    private static final String RESOURCE_FILE_RELATIVE_PATH = "lab-1/src/main/resources/characters.csv";
    private static final String FALLBACK_RESOURCE_PATH = "src/main/resources/characters.csv";
    private static final String OUTPUT_REPORT_FILE_PATH = "lab-1/top_3_characters_by_name_length.csv";

    public static void main(String[] args) {
        Path csvPath = resolveFilePath(RESOURCE_FILE_RELATIVE_PATH, FALLBACK_RESOURCE_PATH);
        CharacterDao characterDao = new CharacterCsvDao(csvPath);
        CharacterService characterService = new CharacterPriorityQueueService();

        System.out.println("1: Чтение данных");
        List<Character> allCharacters = characterDao.findAll();
        System.out.println("Загружено персонажей из CSV: " + allCharacters.size());

        System.out.println("\n2: Загрузка в PriorityQueue и выборка ТОП-3");
        PriorityQueue<Character> priorityQueue = characterService.createPriorityQueueByLength(allCharacters);
        List<Character> topThreeCharacters = characterService.extractTopCharacters(priorityQueue, TOP_CHARACTERS_COUNT);

        System.out.println("Топ-3 персонажа по длине имени:");
        for (int index = 0; index < topThreeCharacters.size(); index++) {
            Character currentCharacter = topThreeCharacters.get(index);
            System.out.printf("%d. %s (длина имени: %d)%n",
                    index + 1,
                    currentCharacter.getName(),
                    currentCharacter.getName().length());
        }

        System.out.println("\n 3: Запись результата в файл");
        Path reportFilePath = Path.of(OUTPUT_REPORT_FILE_PATH);
        characterService.writeCharactersReportToFile(topThreeCharacters, reportFilePath);
        System.out.println("Результат успешно сохранен в файл: " + reportFilePath.toAbsolutePath());

        System.out.println("\n ДОП-КА (CRUD)");
        demonstrateCrudOperations(characterDao);
    }

    private static void demonstrateCrudOperations(CharacterDao characterDao) {
        // 1. Create (Создание)
        Character newCharacter = new Character(
                null,
                "Birdperson",
                "Alive",
                "Bird-Person",
                "",
                "Male",
                "Bird World",
                "Earth (Replacement Dimension)",
                "2024-01-01T00:00:00.000Z"
        );
        Character createdCharacter = characterDao.save(newCharacter);
        System.out.println("[CRUD: CREATE] Успешно добавлен персонаж с ID=" + createdCharacter.getId());

        // 2. Read (Чтение по ID)
        Optional<Character> foundCharacterOptional = characterDao.findById(createdCharacter.getId());
        foundCharacterOptional.ifPresent(character ->
                System.out.println("[CRUD: READ] Найден персонаж по ID: " + character.getName()));

        // 3. Update (Обновление)
        createdCharacter.setStatus("Dead");
        createdCharacter.setName("Phoenixperson");
        Character updatedCharacter = characterDao.update(createdCharacter);
        System.out.println("[CRUD: UPDATE] Обновлен персонаж: " + updatedCharacter.getName()
                + ", новый статус: " + updatedCharacter.getStatus());

        // 4. Delete (Удаление, чтобы вернуть исходный файл в исходное состояние)
        boolean isDeleted = characterDao.deleteById(updatedCharacter.getId());
        System.out.println("[CRUD: DELETE] Персонаж с ID=" + updatedCharacter.getId() + " удален: " + isDeleted);
    }

    private static Path resolveFilePath(String primaryPath, String fallbackPath) {
        Path path = Path.of(primaryPath);
        if (Files.exists(path)) {
            return path;
        }
        return Path.of(fallbackPath);
    }
}