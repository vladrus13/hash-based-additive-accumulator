package ru.draw;

import ru.accumulator.Accumulator;
import ru.accumulator.MerkleAccumulator;
import ru.accumulator.SmartBackLinesAccumulator;
import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerformanceLauncher {

    private static Random random;

    private static final int countElements = 1000000;
    private static final int step = 20000;

    public static byte[] generateRandomByte(long size) {
        byte[] returned = new byte[(int) size];
        random.nextBytes(returned);
        return returned;
    }

    public static void main(String[] args) {
        random = new Random(System.currentTimeMillis());
        ArrayList<Accumulator<?>> accumulators = new ArrayList<>(List.of(new SmartBackLinesAccumulator(), new MerkleAccumulator()));
        for (Accumulator accumulator : accumulators) {
            String accumulatorName = accumulator.getClass().getName();
            ArrayList<Point> resultAdding = new ArrayList<>();
            ArrayList<Point> resultFind = new ArrayList<>();
            int pointStep = 800 / (countElements / step);
            int it = 0;
            for (int elements = 1; elements < countElements; elements += step) {
                accumulator.clear();
                for (int i = 0; i < elements; i++) {
                    accumulator.add(generateRandomByte(10));
                }
                long time = System.nanoTime();
                byte[] end = generateRandomByte(10);
                accumulator.add(end);
                time = System.nanoTime() - time;
                resultAdding.add(new Point(it  * pointStep, (int) time));
                time = System.nanoTime();
                accumulator.verify(accumulator.size(), accumulator.size(), accumulator.prove(accumulator.size()), end);
                time = System.nanoTime() - time;
                resultFind.add(new Point(it  * pointStep, (int) time));
                it++;
            }
            int max = resultAdding.stream().mapToInt(element -> element.y).max().orElse(0);
            for (Point point : resultAdding) {
                point.y = point.y * 800 / max;
            }
            max = resultFind.stream().mapToInt(element -> element.y).max().orElse(0);
            for (Point point : resultFind) {
                point.y = point.y * 800 / max;
            }
            ImageSaver.save(ImageCreator.draw(accumulatorName, accumulatorName, resultAdding, resultFind), accumulatorName);
        }
    }
}
