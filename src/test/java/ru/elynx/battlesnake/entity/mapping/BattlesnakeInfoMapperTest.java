package ru.elynx.battlesnake.entity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.api.BattlesnakeInfoDto;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;

@SpringBootTest
class BattlesnakeInfoMapperTest {
    @Test
    void test_entity_to_dto(@Autowired BattlesnakeInfoMapper tested) {
        BattlesnakeInfo entity = new BattlesnakeInfo("SomeAuthor", "#001122", "some-head", "some-tail", "4.321");
        BattlesnakeInfoDto dto = tested.toDto(entity);

        assertEquals("1", dto.getApiversion());
        assertEquals(entity.getAuthor(), dto.getAuthor());
        assertEquals(entity.getColor(), dto.getColor());
        assertEquals(entity.getHead(), dto.getHead());
        assertEquals(entity.getTail(), dto.getTail());
        assertEquals(entity.getVersion(), dto.getVersion());
    }
}
