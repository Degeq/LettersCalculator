import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static BlockingQueue<String> textsQueueForA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsQueueForB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsQueueForC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException, ExecutionException {


        Thread creator = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    textsQueueForA.put(text);
                    textsQueueForB.put(text);
                    textsQueueForC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Thread.currentThread().interrupt();
        });

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        Future task1 = threadForCount(threadPool, textsQueueForA, 'a');
        Future task2 = threadForCount(threadPool, textsQueueForB, 'b');
        Future task3 = threadForCount(threadPool, textsQueueForC, 'c');

        creator.start();

        System.out.println("В тексте с наибольшим количеством 'a' их: "
                + task1.get());
        System.out.println("В тексте с наибольшим количеством 'b' их: "
                + task2.get());
        System.out.println("В тексте с наибольшим количеством 'c' их: "
                + task3.get());
        threadPool.shutdown();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Future threadForCount(ExecutorService threadPool, BlockingQueue<String> textsQueue, char letterForCount) {
        Future task = threadPool.submit(() -> {
            int counterLetters = 0;
            for (int i = 0; i < 10_000; i++) {
                int counter = 0;
                try {
                    for (char j : textsQueue.take().toCharArray()) {
                        if (j == letterForCount) {
                            counter++;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (counter > counterLetters) {
                    counterLetters = counter;
                }
            }
            return counterLetters;
        });

        return task;
    }
}
