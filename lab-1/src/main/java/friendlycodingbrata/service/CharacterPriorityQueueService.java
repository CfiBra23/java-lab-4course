package friendlycodingbrata.service;

import friendlycodingbrata.entity.Character;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class CharacterPriorityQueueService implements CharacterService {

    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    private static final String REPORT_HEADER = "id,name,name_length,species,status";

    @Override
    public PriorityQueue<Character> createPriorityQueueByLength(List<Character> characterList) {
        // Компаратор для Max-Heap: чем больше длина имени, тем выше приоритет в куче
        Comparator<Character> nameLengthComparator = (firstCharacter, secondCharacter) -> {
            int firstLength = firstCharacter.getName().length();
            int secondLength = secondCharacter.getName().length();
            return Integer.compare(secondLength, firstLength);
        };

        int queueCapacity = Math.max(characterList.size(), DEFAULT_INITIAL_CAPACITY);
        PriorityQueue<Character> priorityQueue = new PriorityQueue<>(queueCapacity, nameLengthComparator);

        for (Character currentCharacter : characterList) {
            priorityQueue.offer(currentCharacter);
        }

        return priorityQueue;
    }

    @Override
    public List<Character> extractTopCharacters(PriorityQueue<Character> characterQueue, int topCount) {
        List<Character> topCharacters = new ArrayList<>();
        int elementsToExtractCount = Math.min(topCount, characterQueue.size());

        // Извлекаем элементы строго через poll(), так как итератор кучи не гарантирует порядок
        for (int step = 0; step < elementsToExtractCount; step++) {
            Character polledCharacter = characterQueue.poll();
            if (polledCharacter != null) {
                topCharacters.add(polledCharacter);
            }
        }

        return topCharacters;
    }

    @Override
    public void writeCharactersReportToFile(List<Character> characters, Path destinationPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(destinationPath, StandardCharsets.UTF_8)) {
            writer.write(REPORT_HEADER);
            writer.newLine();

            for (Character character : characters) {
                String reportLine = formatReportLine(character);
                writer.write(reportLine);
                writer.newLine();
            }
        } catch (IOException fileWritingException) {
            throw new IllegalStateException("Failed to write report to file: " + destinationPath, fileWritingException);
        }
    }

    private String formatReportLine(Character character) {
        return character.getId() + ","
                + character.getName() + ","
                + character.getName().length() + ","
                + character.getSpecies() + ","
                + character.getStatus();
    }
}