public class Polynomial {
    double[] coefficients;

    public Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public Polynomial() {
        this.coefficients = new double[] { 0 };
    }

    public Polynomial add(Polynomial poly) {
        int sumLength = Math.max(coefficients.length, poly.coefficients.length);
        double[] sum = new double[sumLength];

        for (int i = 0; i < sumLength; i++) {
            if (i < coefficients.length)
                sum[i] += coefficients[i];
            if (i < poly.coefficients.length)
                sum[i] += poly.coefficients[i];
        }

        return new Polynomial(sum);
    }

    public double evaluate(double x) {
        double ans = 0;
        for (int i = 0; i < coefficients.length; i++) {
            ans += coefficients[i] * Math.pow(x, i);
        }
        return ans;
    }

    public boolean hasRoot(double x) {
        return evaluate(x) == 0;
    }

}