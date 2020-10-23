import org.junit.jupiter.api.*;
import ru.accumulator.Accumulator;
import ru.accumulator.MerkleAccumulator;
import ru.accumulator.SmartBackLinesAccumulator;
import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void oneElementTest() {
        String test = Util.generateRandomString(10);
        for (Accumulator accumulator : accumulators) {
            accumulator.add(AccumulatorUtils.toByteArray(test));
        }
    }

    @Test
    @Order(2)
    public void moreElementTest() {
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
}
