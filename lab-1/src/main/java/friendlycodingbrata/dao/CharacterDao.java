package friendlycodingbrata.dao;

import friendlycodingbrata.entity.Character;

import java.util.List;
import java.util.Optional;

public interface CharacterDao {

    List<Character> findAll();

    Optional<Character> findById(Long characterId);

    Character save(Character character);

    Character update(Character character);

    boolean deleteById(Long characterId);
}