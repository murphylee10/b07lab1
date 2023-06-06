import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class Polynomial {
    double[] coefficients;
    int[] exponents;

    public Polynomial(double[] coefficients, int[] exponents) {
        this.coefficients = coefficients;
        this.exponents = exponents;
    }

    public Polynomial() {
        this.coefficients = new double[] { 0 };
        this.exponents = new int[] { 0 };
    }

    public Polynomial(File f) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(f.getPath()));
            String expr = bf.readLine();
            int start = (expr.charAt(0) == '-') ? 1 : 0;
            String[] terms = expr.substring(start).split("[+-]");
            String[] sign = new String[terms.length];
            sign[0] = (expr.charAt(0) == '-') ? "-" : "+";
            int count = 1;
            for (int i = 1; i < expr.length(); i++) {
                if (expr.charAt(i) == '+' || expr.charAt(i) == '-') {
                    sign[count] = String.valueOf(expr.charAt(i));
                    count++;
                }
            }
            coefficients = new double[terms.length];
            exponents = new int[terms.length];
            for (int i = 0; i < terms.length; i++) {
                String[] pieces = terms[i].split("x");
                switch (pieces.length) {
                    case 0:
                        coefficients[i] = 1;
                        exponents[i] = 1;
                        break;
                    case 1:
                        if (terms[i].contains("x")) {
                            coefficients[i] = (terms[i].charAt(0) == 'x') ? 1 : Double.parseDouble(sign[i] + pieces[0]);
                            exponents[i] = (terms[i].charAt(0) == 'x') ? Integer.parseInt(pieces[0]) : 1;
                        } else {
                            coefficients[i] = Double.parseDouble(sign[i] + pieces[0]);
                            exponents[i] = 0;
                        }
                        break;
                    case 2:
                        coefficients[i] = Double.parseDouble(sign[i] + pieces[0]);
                        exponents[i] = Integer.parseInt(pieces[1]);
                        break;
                }
            }
            bf.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error occured. Creating zero polynomial...");
            this.coefficients = new double[] { 0 };
            this.exponents = new int[] { 0 };
        } catch (IOException e) {
            System.out.println("Error occured. Creating zero polynomial...");
            this.coefficients = new double[] { 0 };
            this.exponents = new int[] { 0 };
        }
    }

    public boolean validate(Polynomial poly) {
        if (poly == null || poly.coefficients.length != poly.exponents.length) {
            System.out.println("invalid due to mismatch sizes or null pointer. returning null...");
            return false;
        }
        return true;
    }

    public Polynomial add(Polynomial poly) {
        // validation
        if (!validate(poly))
            return null;

        // get sorted array of exponents
        String exps = "";
        int maxlen = Math.max(poly.coefficients.length, coefficients.length);
        for (int i = 0; i < maxlen; i++) {
            if (i < exponents.length && !exps.contains(Integer.toString(exponents[i]) + ","))
                exps += Integer.toString(exponents[i]) + ",";
            if (i < poly.exponents.length && !exps.contains(Integer.toString(poly.exponents[i]) + ","))
                exps += Integer.toString(poly.exponents[i]) + ",";
        }
        String[] temp = exps.substring(0, exps.length() - 1).split(",");
        double[] newCoeffs = new double[temp.length];
        int[] newExps = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            newExps[i] = Integer.parseInt(temp[i]);
        }
        Arrays.sort(newExps);

        // populate new coefficients
        for (int i = 0; i < newExps.length; i++) {
            for (int j = 0; j < maxlen; j++) {
                if (j < exponents.length && exponents[j] == newExps[i])
                    newCoeffs[i] += coefficients[j];
                if (j < poly.exponents.length && poly.exponents[j] == newExps[i])
                    newCoeffs[i] += poly.coefficients[j];
            }
        }

        // strip zero-coefficient entries
        int new_size = 0;
        for (int i = 0; i < newCoeffs.length; i++) {
            if (newCoeffs[i] != 0)
                new_size++;
        }
        if (new_size == 0)
            return new Polynomial();
        double[] newCoeffsStripped = new double[new_size];
        int[] newExpsStripped = new int[new_size];
        int index = 0;
        for (int i = 0; i < newCoeffs.length; i++) {
            if (newCoeffs[i] != 0) {
                newCoeffsStripped[index] = newCoeffs[i];
                newExpsStripped[index] = newExps[i];
                index++;
            }
        }

        return new Polynomial(newCoeffsStripped, newExpsStripped);
    }

    public double evaluate(double x) {
        double ans = 0;
        for (int i = 0; i < coefficients.length; i++) {
            ans += coefficients[i] * Math.pow(x, exponents[i]);
        }
        return ans;
    }

    public boolean hasRoot(double x) {
        return evaluate(x) == 0;
    }

    public Polynomial multiply(Polynomial poly) {
        if (!validate(poly))
            return null;
        Polynomial result = new Polynomial();
        for (int i = 0; i < poly.coefficients.length; i++) {
            double[] newCoeffs = new double[coefficients.length];
            int[] newExps = new int[coefficients.length];
            for (int j = 0; j < coefficients.length; j++) {
                newCoeffs[j] = poly.coefficients[i] * coefficients[j];
                newExps[j] = poly.exponents[i] + exponents[j];
            }
            result = result.add(new Polynomial(newCoeffs, newExps));
        }
        return result;
    }

    public void saveToFile(String path) {
        try {
            PrintStream ps = new PrintStream(path);
            for (int i = 0; i < coefficients.length; i++) {
                if (coefficients[i] == 0 && coefficients.length != 1)
                    continue;
                String term = "";
                if (i != 0 && coefficients[i] > 0)
                    term += "+";
                if (coefficients[i] > 0) {
                    term = (exponents[i] != 0 && coefficients[i] == 1.0) ? term
                            : term + String.valueOf(coefficients[i]);
                }
                if (coefficients[i] < 0) {
                    term = (exponents[i] != 0 && coefficients[i] == -1.0) ? term + "-"
                            : term + String.valueOf(coefficients[i]);
                }
                term = (exponents[i] == 0) ? term : term + "x";
                term = (exponents[i] == 1) ? term : term + String.valueOf(exponents[i]);
                ps.print(term);
            }
            ps.close();
        } catch (Exception e) {
            System.out.println("Couldn't save data");
        }

    }

}
