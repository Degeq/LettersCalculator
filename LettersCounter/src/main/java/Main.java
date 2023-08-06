import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static int counterA = 0;
    public static int counterB = 0;
    public static int counterC = 0;

    public static BlockingQueue<String> textsQueueForA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsQueueForB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsQueueForC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {


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

        Thread counter1 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                int counter = 0;
                try {
                    for (char j : textsQueueForA.take().toCharArray()) {
                        if (j == 'a') {
                            counter++;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (counter > counterA) {
                    counterA = counter;
                }
            }
            Thread.currentThread().interrupt();
        });

        Thread counter2 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                int counter = 0;
                try {
                    for (char j : textsQueueForB.take().toCharArray()) {
                        if (j == 'b') {
                            counter++;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (counter > counterB) {
                    counterB = counter;
                }
            }
            Thread.currentThread().interrupt();
        });

        Thread counter3 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                int counter = 0;
                try {
                    for (char j : textsQueueForC.take().toCharArray()) {
                        if (j == 'c') {
                            counter++;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (counter > counterC) {
                    counterC = counter;
                }
            }
            Thread.currentThread().interrupt();
        });

        creator.start();
        counter1.start();
        counter2.start();
        counter3.start();
        creator.join();
        counter1.join();
        counter2.join();
        counter3.join();

        System.out.println("В тексте с наибольшим количеством 'a' их: " + counterA);
        System.out.println("В тексте с наибольшим количеством 'b' их: " + counterB);
        System.out.println("В тексте с наибольшим количеством 'c' их: " + counterC);

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
