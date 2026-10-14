package FriendlyCodingBrata.service;

import FriendlyCodingBrata.entity.Character;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class CharacterPriorityQueueService implements CharacterService {

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    @Override
    public PriorityQueue<Character> createPriorityQueueByLength(List<Character> characterList) {
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
        // TODO: [Вторая половина лабораторной]
        // Извлечь ровно `topCount` персонажей через метод characterQueue.poll().
        // Важно: нельзя итерироваться по PriorityQueue через for-each,
        // так как порядок итератора не гарантирует соблюдение приоритетов кучи.
        throw new UnsupportedOperationException("Extraction of top characters will be implemented in the next part");
    }

    @Override
    public void writeCharactersToFile(List<Character> characters, String destinationPath) {
        // TODO: [Вторая половина лабораторной]
        // Записать полученные имена и их длины в файл по пути destinationPath
        // используя try-with-resources и BufferedWriter/Files.newBufferedWriter.
        throw new UnsupportedOperationException("File writer will be implemented in the next part");
    }
}