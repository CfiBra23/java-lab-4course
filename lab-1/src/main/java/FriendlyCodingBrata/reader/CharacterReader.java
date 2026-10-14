package FriendlyCodingBrata.reader;

import FriendlyCodingBrata.entity.Character;

import java.util.List;

public interface CharacterReader {

    List<Character> readCharacters(String resourcePath);
}