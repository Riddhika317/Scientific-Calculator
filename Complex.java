package calc;

public class Complex {
    double real, imag;

    public Complex(double r, double i) {
        this.real = r;
        this.imag = i;
    }

    public Complex add(Complex other) {
        return new Complex(this.real + other.real, this.imag + other.imag);
    }

    public Complex subtract(Complex other) {
        return new Complex(this.real - other.real, this.imag - other.imag);
    }

    public Complex multiply(Complex other) {
        double r = this.real * other.real - this.imag * other.imag;
        double i = this.real * other.imag + this.imag * other.real;
        return new Complex(r, i);
    }

    public Complex divide(Complex other) {
        double denom = other.real * other.real + other.imag * other.imag;
        double r = (this.real * other.real + this.imag * other.imag) / denom;
        double i = (this.imag * other.real - this.real * other.imag) / denom;
        return new Complex(r, i);
    }

    @Override
    public String toString() {
        if (real == 0 && imag == 0) return "0.00";
        if (real == 0) return String.format("%.2fi", imag);
        if (imag == 0) return String.format("%.2f", real);
        return String.format("%.2f %s %.2fi", real, (imag >= 0 ? "+" : "-"), Math.abs(imag));
    }

    public static Complex parse(String s) {
        s = s.replaceAll(" ", "").replace("i", "");
        if (s.isEmpty()) return new Complex(0, 0);

        String[] parts;
        if (s.contains("+")) {
            parts = s.split("\\+");
            return new Complex(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        } else if (s.lastIndexOf('-') > 0) {
            int idx = s.lastIndexOf('-');
            return new Complex(Double.parseDouble(s.substring(0, idx)), Double.parseDouble(s.substring(idx)));
        } else {
            try {
                return new Complex(0, Double.parseDouble(s));
            } catch (NumberFormatException e) {
                return new Complex(Double.parseDouble(s), 0);
            }
        }
    }
}
