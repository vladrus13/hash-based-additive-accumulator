import org.junit.jupiter.api.*;
import ru.accumulator.MerkleTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TreeTest {

    public static MerkleTree tree;

    @BeforeAll
    public static void beforeAll() {
        tree = new MerkleTree();
    }

    @BeforeEach
    public void beforeEach() {
        tree.clear();
    }

    @Test
    @Order(1)
    public void addingOneElementTest() {
        byte[] test = "smth i would test".getBytes();
        tree.set(0, test);
        assertTrue(tree.verify(test, 0, tree.proof(0)));
    }

    @Test
    @Order(2)
    public void addingFourElementsTest() {
        String test = "smth i would test number ";
        List<byte[]> elems = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            elems.add((test + i).getBytes());
        }
        for (int i = 0; i < 4; i++) {
            tree.set(i, elems.get(i));
        }

        for (int i = 0; i < 4; i++) {
            assertTrue(tree.verify(elems.get(i), i, tree.proof(i)));
        }
    }

    @Test
    @Order(3)
    public void BigRandomTest() {
        Set<byte[]> strings = new HashSet<>();
        List<byte[]> used = new ArrayList<>();
        List<byte[]> notUsed = new ArrayList<>();
        byte[] test = Util.generateRandomByte(Util.generateInRange(2, 10));
        for (int i = 0; i < 1000; i++) {
            while (strings.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 10));
            }
            tree.set(i, test);
            strings.add(test);
            used.add(test);
        }

        for (int i = 0; i < 1000; i++) {
            while (strings.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 10));
            }
            strings.add(test);
            notUsed.add(test);
        }

        for (int i = 0; i < 1000; i++) {
            assertTrue(tree.verify(used.get(i), i, tree.proof(i)));
        }
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                assertFalse(tree.verify(notUsed.get(i), j, tree.proof(j)));
            }
        }
    }
}
