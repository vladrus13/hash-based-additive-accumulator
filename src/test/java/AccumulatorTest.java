import org.junit.jupiter.api.*;
import ru.accumulator.Accumulator;
import ru.accumulator.MerkleAccumulator;
import ru.accumulator.SmartBackLinesAccumulator;
import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccumulatorTest {

    public static ArrayList<Accumulator> accumulators;

    @BeforeAll
    public static void beforeAll() {
        accumulators = new ArrayList<>(List.of(new MerkleAccumulator(), new SmartBackLinesAccumulator()));
    }

    @BeforeEach
    public static void beforeEach() {
        accumulators.forEach(Accumulator::clear);
    }

    @Test
    @Order(1)
    public static void oneElementTest() {
        String test = Util.generateRandomString(10);
        for (Accumulator accumulator : accumulators) {
            accumulator.add(AccumulatorUtils.toByteArray(test));
        }
    }
}
