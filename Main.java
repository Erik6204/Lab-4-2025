import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== ТЕСТИРОВАНИЕ ВСЕХ ФУНКЦИЙ ===");
            
            // Часть 1: Тестирование Sin и Cos
            System.out.println("\n1. ТЕСТИРОВАНИЕ SIN И COS");
            Function sin = new Sin();
            Function cos = new Cos();
            
            System.out.println("Sin на отрезке [0, 3.14]:");
            printFunctionValues(sin, 0, Math.PI, 0.1, "sin");
            
            System.out.println("\nCos на отрезке [0, 3.14]:");
            printFunctionValues(cos, 0, Math.PI, 0.1, "cos");

            // Часть 2: Табулированные аналоги
            System.out.println("\n\n2. ТАБУЛИРОВАННЫЕ АНАЛОГИ");
            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
            TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
            
            System.out.println("Сравнение исходного и табулированного Sin:");
            printComparisonValues(sin, tabulatedSin, 0, Math.PI, 0.1);
            System.out.println("Сравнение исходного и табулированного Cos:");
            printComparisonValues(cos, tabulatedSin, 0, Math.PI, 0.1);

            // Часть 3: Сумма квадратов
            System.out.println("\n\n3. СУММА КВАДРАТОВ SIN И COS");
            Function adaptedSin = new TabulatedFunctionAdapter(tabulatedSin);
            Function adaptedCos = new TabulatedFunctionAdapter(tabulatedCos);
            
            Function sinSquared = Functions.power(adaptedSin, 2);
            Function cosSquared = Functions.power(adaptedCos, 2);
            Function sumSquares = Functions.sum(sinSquared, cosSquared);
            
            System.out.println("(sin(x))**2 + (cos(x))**2 на отрезке [0, 3.14]:");
            printFunctionValues(sumSquares, 0, Math.PI, 0.1, "sum");

            // Часть 4: Влияние количества точек
            System.out.println("\n\n4. ВЛИЯНИЕ КОЛИЧЕСТВА ТОЧЕК НА ТОЧНОСТЬ");
            int[] pointCounts = {5, 10, 20, 50};
            for (int count : pointCounts) {
                TabulatedFunction sinTab = TabulatedFunctions.tabulate(sin, 0, Math.PI, count);
                TabulatedFunction cosTab = TabulatedFunctions.tabulate(cos, 0, Math.PI, count);
                
                Function sinAdapted = new TabulatedFunctionAdapter(sinTab);
                Function cosAdapted = new TabulatedFunctionAdapter(cosTab);
                
                Function sum = Functions.sum(Functions.power(sinAdapted, 2), Functions.power(cosAdapted, 2));
                
                double maxError = 0;
                for (double x = 0; x <= Math.PI; x += 0.1) {
                    double error = Math.abs(sum.getFunctionValue(x) - 1.0);
                    if (error > maxError) maxError = error;
                }
                System.out.printf("Точек: %d, максимальная ошибка: %.6f\n", count, maxError);
            }

            // Часть 5: Тестирование с экспонентой (текстовый файл)
            System.out.println("\n\n5. ТЕСТИРОВАНИЕ С ЭКСПОНЕНТОЙ (текстовый файл)");
            Function exp = new Exp();
            TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);
            
            // Запись в текстовый файл
            try (FileWriter writer = new FileWriter("exp.txt")) {
                TabulatedFunctions.writeTabulatedFunction(tabulatedExp, writer);
                System.out.println("Экспонента записана в exp.txt");
            }
            
            // Чтение из текстового файла
            TabulatedFunction readExp;
            try (FileReader reader = new FileReader("exp.txt")) {
                readExp = TabulatedFunctions.readTabulatedFunction(reader);
                System.out.println("Экспонента прочитана из exp.txt");
            }
            
            System.out.println("Сравнение исходной и считанной экспоненты:");
            printFileComparisonValues(tabulatedExp, readExp, 0, 10, 1.0);

            // Часть 6: Тестирование с логарифмом (бинарный файл)
            System.out.println("\n\n6. ТЕСТИРОВАНИЕ С ЛОГАРИФМОМ (бинарный файл)");
            Log log = new Log(Math.E);
            TabulatedFunction tabulatedLog = TabulatedFunctions.tabulate(log, 0.1, 10, 11);
            
            // Запись в бинарный файл
            try (FileOutputStream out = new FileOutputStream("log.bin")) {
                TabulatedFunctions.outputTabulatedFunction(tabulatedLog, out);
                System.out.println("Логарифм записан в log.bin");
            }
            
            // Чтение из бинарного файла
            TabulatedFunction readLog;
            try (FileInputStream in = new FileInputStream("log.bin")) {
                readLog = TabulatedFunctions.inputTabulatedFunction(in);
                System.out.println("Логарифм прочитан из log.bin");
            }
            
            System.out.println("Сравнение исходной и считанной логарифма:");
            printFileComparisonValues(tabulatedLog, readLog, 0.1, 10, 1.0);

            // Часть 7: Анализ файлов разных форматов
            System.out.println("\n\n7. АНАЛИЗ ФАЙЛОВ РАЗНЫХ ФОРМАТОВ");
            TabulatedFunctions.outputTabulatedFunction(tabulatedExp, new FileOutputStream("exp.bin"));
            analyzeFile("exp.txt", "Текстовый файл экспоненты");
            analyzeFile("exp.bin", "Бинарный файл экспоненты");
            TabulatedFunctions.writeTabulatedFunction(tabulatedLog, new FileWriter("log.txt"));
            analyzeFile("log.txt", "Текстовый файл логарифма");
            analyzeFile("log.bin", "Бинарный файл логарифма");
            // Часть 8: Сравнение текстового и бинарного форматов
            System.out.println("\n\n8. СРАВНЕНИЕ ТЕКСТОВОГО И БИНАРНОГО ФОРМАТОВ");
            compareFileFormats();

            // Часть 9: Тестирование сериализации
            System.out.println("\n\n9. ТЕСТИРОВАНИЕ СЕРИАЛИЗАЦИИ");
            
            Function logFunc = new Log(Math.E);
            Function expFunc = new Exp();
            Function composition = Functions.composition(logFunc, expFunc);
            TabulatedFunction tabulatedComp = TabulatedFunctions.tabulate(composition, 0.1, 10, 11);

            // Сериализация
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("serializable_function.ser"))) {
                out.writeObject(tabulatedComp);
                System.out.println("Функция сериализована в serializable_function.ser");
            }

            // Десериализация
            TabulatedFunction deserializedComp;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("serializable_function.ser"))) {
                deserializedComp = (TabulatedFunction) in.readObject();
                System.out.println("Функция десериализована из serializable_function.ser");
            }

            System.out.println("Сравнение исходной и десериализованной функции:");
            printSerializationComparisonValues(tabulatedComp, deserializedComp, 0.1, 10, 1.0);

            // Анализ файла сериализации
            analyzeFile("serializable_function.ser", "Файл сериализации (Serializable)");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Вспомогательные методы 
     */
    private static void printFunctionValues(Function function, double start, double end, double step, String funcName) {
        int count = 0;
        for (double x = start; x <= end; x += step) {
            if (count % 5 == 0 && count != 0) {
                System.out.println();
            }
            System.out.printf("%s(%.1f)=%.6f\t", funcName, x, function.getFunctionValue(x));
            count++;
        }
        System.out.println();
    }
    
    /**
     * Вывод сравнения исходной и табулированной функции
     */
    private static void printComparisonValues(Function original, TabulatedFunction tabulated,double start, double end, double step) {
        int count = 0;
        for (double x = start; x <= end; x += step) {
            if (count % 3 == 0 && count != 0) {
                System.out.println();
            }
            double origVal = original.getFunctionValue(x);
            double tabVal = tabulated.getFunctionValue(x);
            System.out.printf("x=%.1f: Orig=%.6f Tabulated=%.6f differense=%.6f\t", x, origVal, tabVal,origVal-tabVal);
            count++;
        }
        System.out.println();
    }
    
    /**
     * Вывод сравнения для файловых операций
     */
    private static void printFileComparisonValues(TabulatedFunction original, TabulatedFunction read, double start, double end, double step) {
        int count = 0;
        for (double x = start; x <= end; x += step) {
            double origVal = original.getFunctionValue(x);
            double readVal = read.getFunctionValue(x);
            boolean matches = Math.abs(origVal - readVal) < 1e-10;
            System.out.printf("x=%.1f: Orig=%.6f C=%.6f %s\t", x, origVal, readVal, matches ? "Совпадают" : "Не совпадают");
            System.out.println();
        }
    }
    
    /**
     * Вывод сравнения для сериализации
     */
    private static void printSerializationComparisonValues(TabulatedFunction original, TabulatedFunction deserialized,double start, double end, double step) {
        int count = 0;
        for (double x = start; x <= end; x += step) {
            if (count % 5 == 0 && count != 0) {
                System.out.println();
            }
            double origVal = original.getFunctionValue(x);
            double deserVal = deserialized.getFunctionValue(x);
            boolean matches = Math.abs(origVal - deserVal) < 1e-10;
            System.out.printf("x=%.1f: Orig=%.6f D=%.6f %s\t", x, origVal, deserVal, matches ? "Совпадает" : "Не совподает");
            count++;
        }
        System.out.println();
    }
    
    /**
     * Адаптер для использования TabulatedFunction как Function
     */
    private static class TabulatedFunctionAdapter implements Function {
        private final TabulatedFunction function;
        
        public TabulatedFunctionAdapter(TabulatedFunction function) {
            this.function = function;
        }
        
        public double getLeftDomainBorder() {
            return function.getLeftDomainBorder();
        }
        
        public double getRightDomainBorder() {
            return function.getRightDomainBorder();
        }
        
        public double getFunctionValue(double x) {
            return function.getFunctionValue(x);
        }
    }
    
    /**
     * Анализ файлов разных форматов
     */
    private static void analyzeFile(String filename, String description) {
        try {
            File file = new File(filename);
            System.out.println("\n" + description + ":");
            System.out.println("  Размер: " + file.length() + " байт");
            
            if (filename.endsWith(".txt")) {
                System.out.println("  Формат: текстовый");
                System.out.println("  Содержимое:");
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("    " + line);
                    }
                }
            } else if (filename.endsWith(".bin")) {
                System.out.println("  Формат: бинарный");
                System.out.println("  Первые 50 байт (hex):");
                try (FileInputStream in = new FileInputStream(file)) {
                    byte[] buffer = new byte[50];
                    int bytesRead = in.read(buffer);
                    System.out.print("    ");
                    for (int i = 0; i < bytesRead; i++) {
                        System.out.printf("%02X ", buffer[i]);
                    }
                    System.out.println();
                }
            } else if (filename.endsWith(".ser")) {
                System.out.println("  Формат: сериализованный объект");
                System.out.println("  Размер файла: " + file.length() + " байт");
            }
            
            System.out.println("  Преимущества: " + getFileFormatAdvantages(filename));
            System.out.println("  Недостатки: " + getFileFormatDisadvantages(filename));
            
        } catch (IOException e) {
            System.out.println("Ошибка при анализе файла " + filename + ": " + e.getMessage());
        }
    }
    
    /**
     * Сравнение форматов файлов
     */
    private static void compareFileFormats() {
        System.out.println("СРАВНЕНИЕ ФОРМАТОВ ХРАНЕНИЯ:");
        System.out.println("┌─────────────────┬──────────────────┬──────────────────┬─────────────────┐");
        System.out.println("│     Формат      │     Размер       │  Человекочтаемый.│    Скорость     │");
        System.out.println("├─────────────────┼──────────────────┼──────────────────┼─────────────────┤");
        
        File textFile = new File("exp.txt");
        File binFile = new File("exp.bin");
        File serFile = new File("serializable_function.ser");
        
        System.out.printf("│   Текстовый     │ %8d байт    │       Да         │     Медленно    │\n", textFile.length());
        System.out.printf("│   Бинарный      │ %8d байт    │       Нет        │     Быстро      │\n", binFile.length());
        System.out.printf("│   Serializable  │ %8d байт    │       Нет        │     Средне      │\n", serFile.length());
        System.out.println("└─────────────────┴──────────────────┴──────────────────┴─────────────────┘");
        
        System.out.println("\nВЫВОДЫ:");
        System.out.println("Текстовый формат - лучший для отладки и совместимости");
        System.out.println("Бинарный формат - лучший для производительности и размера");
        System.out.println("Serializable - удобен для сложных объектов, но зависит от версии Java");
    }
    
    /**
     * Преимущества форматов файлов
     */
    private static String getFileFormatAdvantages(String filename) {
        if (filename.endsWith(".txt")) {
            return "человекочитаемый, легко отлаживать, совместим между системами";
        } else if (filename.endsWith(".bin")) {
            return "компактный размер, быстрая запись/чтение, эффективное использование памяти";
        } else if (filename.endsWith(".ser")) {
            return "сохраняет структуру объектов, удобен для сложных объектов, автоматическая сериализация";
        }
        return "";
    }
    
    /**
     * Недостатки форматов файлов
     */
    private static String getFileFormatDisadvantages(String filename) {
        if (filename.endsWith(".txt")) {
            return "больший размер, медленнее парсинг, нет типизации данных";
        } else if (filename.endsWith(".bin")) {
            return "не человекочитаемый, зависит от архитектуры, сложнее отлаживать";
        } else if (filename.endsWith(".ser")) {
            return "не человекочитаемый, зависит от версии Java, больший размер чем бинарный";
        }
        return "";
    }
}