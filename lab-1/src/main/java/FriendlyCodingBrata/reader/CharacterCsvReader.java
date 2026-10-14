package FriendlyCodingBrata.reader;

import FriendlyCodingBrata.entity.Character;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CharacterCsvReader implements CharacterReader {

    private static final String CSV_DELIMITER = ",";
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

    @Override
    public List<Character> readCharacters(String resourcePath) {
        List<Character> loadedCharacters = new ArrayList<>();

        try (InputStream inputStream = openResourceStream(resourcePath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            skipHeaderLine(bufferedReader);

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.isBlank()) {
                    continue;
                }
                Character parsedCharacter = parseCharacterLine(currentLine);
                loadedCharacters.add(parsedCharacter);
            }
        } catch (IOException fileReadingException) {
            throw new IllegalStateException("Failed to read characters from resource: " + resourcePath, fileReadingException);
        }

        return loadedCharacters;
    }

    private InputStream openResourceStream(String resourcePath) {
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (resourceStream == null) {
            throw new IllegalArgumentException("Resource not found in classpath: " + resourcePath);
        }
        return resourceStream;
    }

    private void skipHeaderLine(BufferedReader bufferedReader) throws IOException {
        bufferedReader.readLine();
    }

    private Character parseCharacterLine(String line) {
        // limit = -1 гарантирует сохранение пустых ячеек (например, пустого поля type)
        String[] lineParts = line.split(CSV_DELIMITER, -1);
        if (lineParts.length < EXPECTED_COLUMN_COUNT) {
            throw new IllegalArgumentException("Malformed CSV line, expected at least "
                    + EXPECTED_COLUMN_COUNT + " columns but got " + lineParts.length + ": " + line);
        }

        Long characterId = Long.parseLong(lineParts[ID_COLUMN_INDEX].trim());
        String characterName = lineParts[NAME_COLUMN_INDEX].trim();
        String characterStatus = lineParts[STATUS_COLUMN_INDEX].trim();
        String characterSpecies = lineParts[SPECIES_COLUMN_INDEX].trim();
        String characterType = lineParts[TYPE_COLUMN_INDEX].trim();
        String characterGender = lineParts[GENDER_COLUMN_INDEX].trim();
        String originName = lineParts[ORIGIN_NAME_COLUMN_INDEX].trim();
        String locationName = lineParts[LOCATION_NAME_COLUMN_INDEX].trim();
        String created = lineParts[CREATED_COLUMN_INDEX].trim();

        return new Character(
                characterId,
                characterName,
                characterStatus,
                characterSpecies,
                characterType,
                characterGender,
                originName,
                locationName,
                created
        );
    }
}