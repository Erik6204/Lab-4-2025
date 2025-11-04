package functions;

import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable, Externalizable {
    private static final long serialVersionUID = 1L;
    private FunctionPoint[] points;
    private int pointsCount;

    //  9
    public ArrayTabulatedFunction() {
        // Пустой конструктор требуется для Externalizable
    }

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10]; // Добавляем запас для добавления точек
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10]; // Добавляем запас для добавления точек
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }
    
    // ЗАДАНИE 1
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        // Проверяем упорядоченность точек
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию x");
            }
        }
        
        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 10];
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }
    
    // Методы интерфейса Function (наследуются через TabulatedFunction)
    public double getLeftDomainBorder()  {
        return points[0].getX();
    }

    public double getRightDomainBorder()  {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x)  {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;//Если x не в диапазоне ,то возвращаем Nan
        }
        for (int i = 0; i < pointsCount - 1; i++) {//Берем каждый минимальный отрезок между обьектами,берем их X
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            if (x >= x1 && x <= x2) {//Если исходный x между ними ,то находим для него y по формуле
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();

                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    public int getPointsCount(){
        return pointsCount;
    }
    
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
    }
        return new FunctionPoint(points[index]);
    }
    
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException{
        // Проверка выхода за границы
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        
        // Проверка упорядоченности и выброс исключения при нарушении
        if (index > 0 && point.getX() <= points[index - 1].getX()) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность с предыдущей точкой");
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX()) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность со следующей точкой");
        }

        points[index] = new FunctionPoint(point);
    }
    
    public double getPointX(int index) {
        // Проверка выхода за границы
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        return points[index].getX();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException{
        // Проверка выхода за границы
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        
        // Проверка упорядоченности и выброс исключения при нарушении
        if (index > 0 && x <= points[index - 1].getX()) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность с предыдущей точкой");
        }
        if (index < pointsCount - 1 && x >= points[index + 1].getX()) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность со следующей точкой");
        }

        points[index].setX(x);
    }
    
    public double getPointY(int index) {
        // Проверка выхода за границы
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        return points[index].getY();
    }
    
    public void setPointY(int index, double y) {
        // Проверка выхода за границы
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(" Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        points[index].setY(y);
    }
    
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        
        // Проверка на минимальное колчество точек
        if (pointsCount < 3) {
            throw new IllegalStateException("Невозможно удалить точку: количество точек должно быть не менее 3");
        }
        for (int i = index; i < pointsCount - 1; i++) {
            points[i] = points[i + 1];
        }
        points[pointsCount - 1] = null;
        --pointsCount;
    }
    
    public void addPoint(FunctionPoint point)throws InappropriateFunctionPointException{
        for (int i = 0; i < pointsCount; i++) {// Проверка на дубликат X и выходит исключения
            if (Math.abs(point.getX() - points[i].getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
            }
        }
        int count=0;
        while(count < pointsCount && point.getX()>points[count].getX()){//проверка текущего X обьекта с X обьекта point
            count++;
        }
        // Проверяем необходимость увеличения массива
        if (pointsCount >= points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            for (int i = 0; i < pointsCount; i++) {
                newPoints[i] = points[i];
            }
            points = newPoints;
        }
        // Сдвигаем элементы ,Выходит так что на месте  point[count] и points[count+1] стоят два одиннаковых элемента
        for (int i = pointsCount; i > count; i--) {
            points[i] = points[i - 1];
        }
        // Вставляем новую точку
        points[count] = new FunctionPoint(point);

        //  увеличиваем счетчик
        pointsCount++;
    }
    
    public void printTabulatedFunction() {
        for (int i = 0; i < pointsCount; i++) {
            System.out.println("x = " + getPointX(i) + ", y = " + getPointY(i));
        }
    }
    
    // Методы Externalizable (ЗАДАНИЕ 9)
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount =  in.readInt();
        points = new FunctionPoint[pointsCount + 10];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
}