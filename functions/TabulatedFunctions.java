package functions;

import java.io.*;
import java.util.StringTokenizer;

public final class TabulatedFunctions {
    // Приватный конструктор чтобы запретить создание экземпляров
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создать экземпляр класса TabulatedFunctions");
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // Создаем массив точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            points[i] = new FunctionPoint(x, y);
        }

        // Возвращаем табулированную функцию (пока используем ArrayTabulatedFunction)
        return new ArrayTabulatedFunction(points);
    }
    public static double getFunctionValue(TabulatedFunction function, double x) {
        return function.getFunctionValue(x);
    }

    public static double getLeftDomainBorder(TabulatedFunction function) {
        return function.getLeftDomainBorder();
    }

    public static double getRightDomainBorder(TabulatedFunction function) {
        return function.getRightDomainBorder();
    }

    // Метод вывода в байтовый поток
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeInt(function.getPointsCount());
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                dos.writeDouble(point.getX());
                dos.writeDouble(point.getY());
            }
        } catch (IOException e) {
            // Пробрасываем RuntimeException, так как IOException - проверяемое исключение
            throw new RuntimeException("Ошибка при выводе функции в поток", e);
        }
    }

    // Метод ввода из байтового потока
    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointsCount = dis.readInt();
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            
            for (int i = 0; i < pointsCount; i++) {
                double x = dis.readDouble();
                double y = dis.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            
            return new ArrayTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении функции из потока", e);
        }
    }

    // Метод записи в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.print(function.getPointsCount() + " ");
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                writer.print(point.getX() + " " + point.getY() + " ");
            }
        }
        // PrintWriter не бросает IOException в методах print/println
    }

    // Метод чтения из символьного потока
    public static TabulatedFunction readTabulatedFunction(Reader in) {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(in);
            tokenizer.parseNumbers();
            
            // Читаем количество точек
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалось количество точек");
            }
            int pointsCount = (int) tokenizer.nval;
            
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            
            for (int i = 0; i < pointsCount; i++) {
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата x");
                }
                double x = tokenizer.nval;
                
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата y");
                }
                double y = tokenizer.nval;
                
                points[i] = new FunctionPoint(x, y);
            }
            
            return new ArrayTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении функции из потока", e);
        }
    }
}
