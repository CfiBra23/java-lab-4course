package FriendlyCodingBrata.service;

import FriendlyCodingBrata.entity.Character;

import java.util.List;
import java.util.PriorityQueue;

public interface CharacterService {

    PriorityQueue<Character> createPriorityQueueByLength(List<Character> characterList);

    List<Character> extractTopCharacters(PriorityQueue<Character> characterQueue, int topCount);

    void writeCharactersToFile(List<Character> characters, String destinationPath);
}