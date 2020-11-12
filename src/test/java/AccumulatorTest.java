import org.junit.jupiter.api.*;
import ru.accumulator.Accumulator;
import ru.accumulator.MerkleAccumulator;
import ru.accumulator.SmartBackLinesAccumulator;
import ru.util.AccumulatorUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccumulatorTest {

    public static ArrayList<Accumulator<?>> accumulators;

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
        for (Accumulator<?> accumulator : accumulators) {
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
            for (Accumulator<?> accumulator : accumulators) {
                accumulator.add(test);
            }
            strings.add(test);
        }
    }

    public void addAndTestAll(Accumulator accumulator, ArrayList<byte[]> input) {
        for (byte[] in : input) {
            accumulator.add(in);
        }
        for (int i = 0; i < input.size(); i++) {
            assertTrue(accumulator.verify(accumulator.size(), i + 1, accumulator.prove(i + 1), input.get(i)));
        }
    }

    @Test
    @Order(3)
    public void oneElementTest() {
        byte[] hello = Util.generateRandomByte(5);
        for (Accumulator<?> accumulator : accumulators) {
            addAndTestAll(accumulator, new ArrayList<>(Collections.singleton(hello)));
        }
    }

    @Test
    @Order(4)
    public void twoElementsTest() {
        ArrayList<byte[]> input = new ArrayList<>(List.of(Util.generateRandomByte(10), Util.generateRandomByte(9)));
        for (Accumulator<?> accumulator : accumulators) {
            addAndTestAll(accumulator, input);
        }
    }

    @Test
    @Order(5)
    public void tenElementsTest() {
        ArrayList<byte[]> input = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            input.add(Util.generateRandomByte(i));
        }
        for (Accumulator<?> accumulator : accumulators) {
            addAndTestAll(accumulator, input);
        }
    }

    @Test
    @Order(6)
    public void oneHundredElementTest() {
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
        for (Accumulator<?> accumulator : accumulators) {
            addAndTestAll(accumulator, tests);
        }
    }

    public void addAndTestMissed(Accumulator accumulator, ArrayList<byte[]> elements,
                                 ArrayList<byte[]> falseElements) {
        for (byte[] in : elements) {
            accumulator.add(in);
        }
        for (byte[] falseElement : falseElements) {
            for (int j = 0; j < elements.size(); j++) {
                assertFalse(accumulator.verify(accumulator.size(), j + 1, accumulator.prove(j + 1), falseElement));
            }
        }
    }

    @Test
    @Order(7)
    public void missedElementsVerifying() {
        Set<byte[]> setTests = new HashSet<>();
        ArrayList<byte[]> tests = new ArrayList<>();
        ArrayList<byte[]> falseTests = new ArrayList<>();
        byte[] test = Util.generateRandomByte(Util.generateInRange(2, 10));
        for (int i = 0; i < 100; i++) {
            while (setTests.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 10));
            }
            setTests.add(test);
            tests.add(test);
        }

        for (int i = 0; i < 100; i++) {
            while (setTests.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 10));
            }
            setTests.add(test);
            falseTests.add(test);
        }


        for (Accumulator<?> accumulator : accumulators) {
            addAndTestMissed(accumulator, tests, falseTests);
        }
    }


    @Test
    @Order(8)
    public void BigRandomTest() {
        Set<byte[]> setTests = new HashSet<>();
        ArrayList<byte[]> tests = new ArrayList<>();
        byte[] test = Util.generateRandomByte(Util.generateInRange(2, 50));
        for (int i = 0; i < 100000; i++) {
            while (setTests.contains(test)) {
                test = Util.generateRandomByte(Util.generateInRange(2, 50));
            }
            setTests.add(test);
            tests.add(test);
        }
        for (Accumulator<?> accumulator : accumulators) {
            addAndTestAll(accumulator, tests);
        }
    }
}
