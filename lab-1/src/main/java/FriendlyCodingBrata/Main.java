package FriendlyCodingBrata;

import FriendlyCodingBrata.entity.Character;
import FriendlyCodingBrata.reader.CharacterCsvReader;
import FriendlyCodingBrata.reader.CharacterReader;
import FriendlyCodingBrata.service.CharacterPriorityQueueService;
import FriendlyCodingBrata.service.CharacterService;

import java.util.List;
import java.util.PriorityQueue;

public class Main {

    private static final String CHARACTERS_RESOURCE_PATH = "characters.csv";

    public static void main(String[] args) {
        CharacterReader characterReader = new CharacterCsvReader();
        CharacterService characterService = new CharacterPriorityQueueService();

        List<Character> loadedCharacters = characterReader.readCharacters(CHARACTERS_RESOURCE_PATH);
        System.out.println("Total characters loaded from CSV: " + loadedCharacters.size());

        PriorityQueue<Character> priorityQueue = characterService.createPriorityQueueByLength(loadedCharacters);
        System.out.println("PriorityQueue successfully created with elements count: " + priorityQueue.size());

        Character characterWithHighestPriority = priorityQueue.peek();
        if (characterWithHighestPriority != null) {
            System.out.println("Current character with longest name (heap head): "
                    + characterWithHighestPriority.getName()
                    + " (length: " + characterWithHighestPriority.getName().length() + ")");
        }
    }
}