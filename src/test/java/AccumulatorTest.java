import org.junit.jupiter.api.*;
import ru.accumulator.Accumulator;
import ru.accumulator.MerkleAccumulator;
import ru.accumulator.SmartBackLinesAccumulator;
import ru.util.AccumulatorUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccumulatorTest {

    public static ArrayList<Accumulator> accumulators;

    @BeforeAll
    public static void beforeAll() {
        accumulators = new ArrayList<>(List.of(new MerkleAccumulator(), new SmartBackLinesAccumulator()));
    }

    @BeforeEach
    public void beforeEach() {
        accumulators.forEach(Accumulator::clear);
    }

    @Test
    @Order(1)
    public void addingOneElementTest() {
        String test = Util.generateRandomString(10);
        for (Accumulator accumulator : accumulators) {
            accumulator.add(AccumulatorUtils.toByteArray(test));
        }
    }

    @Test
    @Order(2)
    public void addingMoreElementTest() {
        Set<byte[]> strings = new HashSet<>();
        byte[] test = Util.generateRandomByte(Util.generateInRange(2, 10));
        for (int i = 0; i < 10000; i++) {
            while (strings.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 10));
            }
            for (Accumulator accumulator : accumulators) {
                accumulator.add(test);
            }
            strings.add(test);
        }
    }

    @Test
    @Order(3)
    public void oneElementTest() {
        Set<byte[]> setTests = new HashSet<>();
        ArrayList<byte[]> tests = new ArrayList<>();
        byte[] test = Util.generateRandomByte(Util.generateInRange(2, 10));
        for (int i = 0; i < 100; i++) {
            while (setTests.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 10));
            }
            setTests.add(test);
            tests.add(test);
        }
        for (Accumulator accumulator : accumulators) {
            for (byte[] s : tests) {
                accumulator.add(s);
            }
            for (int j = 1; j < tests.size(); j++) {
                LinkedList<byte[]> prove = accumulator.prove(j);
                assertTrue(accumulator.verify(accumulator.get(accumulator.size()), accumulator.size(), j, prove, tests.get(j - 1)));
            }
        }
    }
}
