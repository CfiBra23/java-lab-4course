package friendlycodingbrata.reader;

import friendlycodingbrata.entity.Character;

import java.util.List;

public interface CharacterReader {

    List<Character> readCharacters(String resourcePath);
}