import org.junit.jupiter.api.*;
import ru.accumulator.MerkleTree;

import java.util.*;

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
        List<byte[]> ree = new ArrayList<>();
        assertTrue(MerkleTree.verify(tree.getRoot(), 0, test, tree.proof(0)));
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
            assertTrue(MerkleTree.verify(tree.getRoot(), i, elems.get(i), tree.proof(i)));
        }
    }

    @Test
    @Order(3)
    public void BigRandomTest() {
        Set<byte[]> strings = new HashSet<>();
        byte[] test = Util.generateRandomByte(Util.generateInRange(2, 10));
        for (int i = 0; i < 10000; i++) {
            while (strings.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 10));
            }
            tree.set(i, test);
            strings.add(test);
        }

        for (int i = 0; i < 10000; i++) {
            assertTrue(MerkleTree.verify(tree.getRoot(), i, tree.getLeaf(i), tree.proof(i)));
        }
    }
}
