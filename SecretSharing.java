import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SecretSharing {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read number of points (n) and minimum number of roots (k)
        System.out.print("Enter the number of roots (n): ");
        int n = scanner.nextInt();
        System.out.print("Enter the minimum number of roots required (k): ");
        int k = scanner.nextInt();

        if (k > n) {
            System.out.println("Not enough points to determine the polynomial.");
            return;
        }

        List<Point> points = new ArrayList<>();

        // Read points
        for (int i = 0; i < n; i++) {
            System.out.print("Enter base and value for root " + (i + 1) + ": ");
            int base = scanner.nextInt();
            String value = scanner.next();
            int x = i + 1; // Use i+1 as the x-coordinate based on the order of input
            int y = decode(value, base);
            points.add(new Point(x, y));
        }

        // Extract the required points for interpolation
        List<Point> selectedPoints = points.subList(0, k);

        // Solve for polynomial coefficients
        double[] coefficients = solvePolynomial(selectedPoints, k);

        // The constant term is the coefficient of x^0
        System.out.println("The constant term c of the polynomial is: " + coefficients[0]);
    }

    // Method to decode a string from a given base to a decimal integer
    private static int decode(String value, int base) {
        return Integer.parseInt(value, base);
    }

    // Method to solve the polynomial system and return coefficients
    private static double[] solvePolynomial(List<Point> points, int k) {
        double[][] matrix = new double[k][k];
        double[] results = new double[k];

        // Fill the matrix and results
        for (int i = 0; i < k; i++) {
            Point point = points.get(i);
            for (int j = 0; j < k; j++) {
                matrix[i][j] = Math.pow(point.x, j);
            }
            results[i] = point.y;
        }

        // Solve the linear system using Gaussian elimination
        return gaussianElimination(matrix, results);
    }

    // Method for Gaussian elimination to solve the system of linear equations
    private static double[] gaussianElimination(double[][] matrix, double[] results) {
        int n = results.length;

        // Forward elimination
        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(matrix[k][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = k;
                }
            }
            // Swap rows
            double[] temp = matrix[i];
            matrix[i] = matrix[maxRow];
            matrix[maxRow] = temp;
            double tempResult = results[i];
            results[i] = results[maxRow];
            results[maxRow] = tempResult;

            // Make the current row's pivot element 1
            for (int k = i + 1; k < n; k++) {
                double factor = matrix[k][i] / matrix[i][i];
                for (int j = i; j < n; j++) {
                    matrix[k][j] -= factor * matrix[i][j];
                }
                results[k] -= factor * results[i];
            }
        }

        // Back substitution
        double[] solution = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            solution[i] = results[i];
            for (int j = i + 1; j < n; j++) {
                solution[i] -= matrix[i][j] * solution[j];
            }
            solution[i] /= matrix[i][i];
        }
        return solution;
    }

    // Helper class to represent a point (x, y)
    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
