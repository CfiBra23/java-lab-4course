package friendlycodingbrata.dao;

import friendlycodingbrata.entity.Character;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CharacterCsvDao implements CharacterDao {

    private static final String CSV_DELIMITER = ",";
    private static final String CSV_HEADER = "id,name,status,species,type,gender,origin/name,location/name,created";
    private static final int EXPECTED_COLUMN_COUNT = 9;

    private static final int ID_COLUMN_INDEX = 0;
    private static final int NAME_COLUMN_INDEX = 1;
    private static final int STATUS_COLUMN_INDEX = 2;
    private static final int SPECIES_COLUMN_INDEX = 3;
    private static final int TYPE_COLUMN_INDEX = 4;
    private static final int GENDER_COLUMN_INDEX = 5;
    private static final int ORIGIN_NAME_COLUMN_INDEX = 6;
    private static final int LOCATION_NAME_COLUMN_INDEX = 7;
    private static final int CREATED_COLUMN_INDEX = 8;

    private final Path csvFilePath;

    public CharacterCsvDao(Path csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    @Override
    public List<Character> findAll() {
        List<Character> characterList = new ArrayList<>();
        if (!Files.exists(csvFilePath)) {
            return characterList;
        }

        try (BufferedReader reader = Files.newBufferedReader(csvFilePath, StandardCharsets.UTF_8)) {
            String currentLine = reader.readLine(); // Пропуск заголовка
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.isBlank()) {
                    continue;
                }
                Character parsedCharacter = parseCharacterLine(currentLine);
                characterList.add(parsedCharacter);
            }
        } catch (IOException fileReadingException) {
            throw new IllegalStateException("Failed to read characters from: " + csvFilePath, fileReadingException);
        }

        return characterList;
    }

    @Override
    public Optional<Character> findById(Long characterId) {
        return findAll().stream()
                .filter(character -> character.getId().equals(characterId))
                .findFirst();
    }

    @Override
    public Character save(Character character) {
        List<Character> currentCharacters = findAll();
        Long newId = calculateNextId(currentCharacters);
        character.setId(newId);

        currentCharacters.add(character);
        writeAllCharacters(currentCharacters);
        return character;
    }

    @Override
    public Character update(Character characterToUpdate) {
        List<Character> currentCharacters = findAll();
        boolean isCharacterFound = false;

        for (int index = 0; index < currentCharacters.size(); index++) {
            if (currentCharacters.get(index).getId().equals(characterToUpdate.getId())) {
                currentCharacters.set(index, characterToUpdate);
                isCharacterFound = true;
                break;
            }
        }

        if (!isCharacterFound) {
            throw new IllegalArgumentException("Character with ID " + characterToUpdate.getId() + " not found for update.");
        }

        writeAllCharacters(currentCharacters);
        return characterToUpdate;
    }

    @Override
    public boolean deleteById(Long characterId) {
        List<Character> currentCharacters = findAll();
        boolean isRemoved = currentCharacters.removeIf(character -> character.getId().equals(characterId));

        if (isRemoved) {
            writeAllCharacters(currentCharacters);
        }
        return isRemoved;
    }

    private Character parseCharacterLine(String line) {
        String[] lineParts = line.split(CSV_DELIMITER, -1);
        if (lineParts.length < EXPECTED_COLUMN_COUNT) {
            throw new IllegalArgumentException("Malformed CSV line: " + line);
        }

        return new Character(
                Long.parseLong(lineParts[ID_COLUMN_INDEX].trim()),
                lineParts[NAME_COLUMN_INDEX].trim(),
                lineParts[STATUS_COLUMN_INDEX].trim(),
                lineParts[SPECIES_COLUMN_INDEX].trim(),
                lineParts[TYPE_COLUMN_INDEX].trim(),
                lineParts[GENDER_COLUMN_INDEX].trim(),
                lineParts[ORIGIN_NAME_COLUMN_INDEX].trim(),
                lineParts[LOCATION_NAME_COLUMN_INDEX].trim(),
                lineParts[CREATED_COLUMN_INDEX].trim()
        );
    }

    private Long calculateNextId(List<Character> currentCharacters) {
        return currentCharacters.stream()
                .mapToLong(Character::getId)
                .max()
                .orElse(0L) + 1L;
    }

    private void writeAllCharacters(List<Character> characters) {
        try (BufferedWriter writer = Files.newBufferedWriter(csvFilePath, StandardCharsets.UTF_8)) {
            writer.write(CSV_HEADER);
            writer.newLine();

            for (Character character : characters) {
                writer.write(formatCharacterToCsvLine(character));
                writer.newLine();
            }
        } catch (IOException fileWritingException) {
            throw new IllegalStateException("Failed to write characters to: " + csvFilePath, fileWritingException);
        }
    }

    private String formatCharacterToCsvLine(Character character) {
        return String.join(CSV_DELIMITER,
                String.valueOf(character.getId()),
                character.getName(),
                character.getStatus(),
                character.getSpecies(),
                character.getType() == null ? "" : character.getType(),
                character.getGender(),
                character.getOriginName(),
                character.getLocationName(),
                character.getCreated()
        );
    }
}