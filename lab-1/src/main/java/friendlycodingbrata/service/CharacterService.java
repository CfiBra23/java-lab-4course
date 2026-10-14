package friendlycodingbrata.service;

import friendlycodingbrata.entity.Character;

import java.nio.file.Path;
import java.util.List;
import java.util.PriorityQueue;

public interface CharacterService {

    PriorityQueue<Character> createPriorityQueueByLength(List<Character> characterList);

    List<Character> extractTopCharacters(PriorityQueue<Character> characterQueue, int topCount);

    void writeCharactersReportToFile(List<Character> characters, Path destinationPath);
}