import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

// ===================================================
// Matrix Class - Matrix Functions, EdgeMatrix
// ===================================================

/* Structure:
 * [x  [x   ... x - x position
 *  y   y   ... y - y position
 *  z   z   ... z - z position
 *  a], a], ... a - alpha channel
 */

public class Matrix {
    protected ArrayList<double[]> matrix;
    protected ArrayList<Pixel> colors;
    protected int rows;
    protected int columns;
    protected int size;
    
    // Constructors
    public Matrix() { // EdgeMatrix
	matrix = new ArrayList<double[]>();
	colors = new ArrayList<Pixel>();
	rows = 4;
	columns = 0;
	size = 0;
    }
    public Matrix(int n, int m) {
	matrix = new ArrayList<double[]>();
	colors = new ArrayList<Pixel>();
	for (int i = 0; i < m; i++) {
	    matrix.add(new double[n]);
	}
	rows = n;
	columns = m;
    }
    public Matrix(double[][] darr) {
	matrix = new ArrayList<double[]>();
	colors = new ArrayList<Pixel>();
	for (double[] d : darr)
	    matrix.add(Arrays.copyOf(d, d.length));
	rows = darr[0].length;
	columns = darr.length;
    }
    
    // EdgeMatrix Methods
    public boolean add_point(double[] p) {
	columns++;
	return matrix.add(p);
    }
    public boolean add_point(double x, double y, double z, double a) {
	return add_point(new double[]{x, y, z, a});
    }
    public boolean add_point(double x, double y, double z) {
	return add_point(new double[]{x, y, z, 1.0});
    }
    public boolean add_point(double x, double y) {
	return add_point(new double[]{x, y, 0, 1.0});
    }

    public boolean add_edge(double[] p1, double[] p2) {
	return add_edge(p1, p2, new Pixel(0,0,0));
    }
    public boolean add_edge(double[] p1, double[] p2, Pixel p) {
	add_point(p1);
	add_point(p2);
	size++;
	colors.add(p);
	return true;
    }
    public boolean add_edge(double x1, double y1, double x2, double y2) {
	return add_edge(x1, y1, x2, y2, new Pixel(0,0,0));
    }
    public boolean add_edge(double x1, double y1, double x2, double y2, Pixel p) {
	return add_edge(x1, y1, 0, x2, y2, 0, p);
    }
    public boolean add_edge(double x1, double y1, double z1, 
			    double x2, double y2, double z2, Pixel p) {
	add_point(x1, y1, z1);
	add_point(x2, y2, z2);
	size++;
	colors.add(p);
	return true;
    }

    public boolean add_triangle(double x1, double y1, double z1,
				double x2, double y2, double z2,
				double x3, double y3, double z3, Pixel p) {
	// /* Color Debugging
	p = new Pixel(1);
	// */

	/* Line Mesh
	add_point(x1, y1, z1);
	add_point(x2, y2, z2);
	add_point(x3, y3, z3);
	size++;
	colors.add(p);
	// */

	// /* Filled Triangle
	int yi1 = (int)y1; int yi2 = (int)y2; int yi3 = (int)y3; 
	int ty, my, by;
	double tx, mx, bx; // tz, mz, bz;
	// Determining Triangle Order
	if (yi1 > yi2) { // y3? > y1 > y3? > y2 > y3?
	    if (yi1 > yi3) { // y1 > [ y2 >? y3 ]
		ty = yi1; tx = x1; // tz = z1;
		if (yi3 > yi2) { // y1 > y3 > y2
		    my = yi3; mx = x3; // mz = z3;
		    by = yi2; bx = x2; // bz = z2;
		} else { // y1 > y2 > y3
		    my = yi2; mx = x2; // mz = z2;
		    by = yi3; bx = x3; // bz = z3;
		}
	    } else { // y3 > y1 > y2 
		ty = yi3; tx = x3; // tz = z3;
		my = yi1; mx = x1; // mz = z1;
		by = yi2; bx = x2; // bz = z2;
	    }
	} else { // y3? > y2 > y3? > y1 > y3?
	    if (yi2 > yi3) { // y2 > [ y1 >? y3 ]
		ty = yi2; tx = x2; // tz = z2;
		if (yi3 > yi1) { // y2 > y3 > y1
		    my = yi3; mx = x3; // mz = z3;
		    by = yi1; bx = x1; // bz = z1;
		} else { // y2 > y1 > y3
		    my = yi1; mx = x1; // mz = z1;
		    by = yi3; bx = x3; // bz = z3;
		}
	    } else { // y3 > y2 > y1
		ty = yi3; tx = x3; // tz = z3;
		my = yi2; mx = x2; // mz = z2;
		by = yi1; bx = x1; // bz = z1;
	    }
	}

	System.out.println("top: " + tx + "," + ty + "\n" + 
			   "mid: " + mx + "," + my + "\n" + 
			   "bot: " + bx + "," + by); // */ Debugging 

	double xL = bx; // bx -> tx
	double xR = bx; // bx -> mx -> tx
	double dx0 = (ty == by) ? 0 : (tx - bx) / (ty - by); // Slope of Longer Side
	double dx1 = (my == by) ? 0 : (mx - bx) / (my - by); // Slope of Shorter Side - Changes
	// System.out.println("dx0:\t" + dx0 + "\tdx1:\t" + dx1); // Debugging
	// Bottom Up Traversal
	for (int y = by; y <= ty; y++) {
	    add_edge(xL, y, xR, y, p);
	    // System.out.println("y: " + y + "\txL:\t" + xL + "\txR:\t" + xR); // Debugging
	    if (y == my) // Switch Slope of Shorter Side
		dx1 = (ty == my) ? 0 : (tx - mx) / (ty - my);
	    xL += dx0; xR += dx1; 
	}
	// */	
	return true;
    }
    public boolean add_triangle(double x1, double y1, double z1,
				double x2, double y2, double z2,
				double x3, double y3, double z3) {
	return add_triangle(x1,y1,z1,x2,y2,z2,x3,y3,z3,new Pixel(0,0,0));
    }
    
    // Passes Copies, Not References
    public int append(Matrix m) {
	int l = m.getColumns();
	ArrayList<Pixel> ps = m.getColors();
	for (int i = 0; i < l; i++)
	    add_point(m.getColumn(i));
	for (Pixel p : ps)
	    colors.add(p);
	// System.out.println(this); // Debugging
	// System.out.println(ps); // Debugging
	return l;
    }

    // Accessors + Mutators
    public double get(int r, int c) {
	return matrix.get(c)[r];
    }
    public double set(int r, int c, double d) {
	double ret = matrix.get(c)[r];
	matrix.get(c)[r] = d;
	return ret;
    }
    public int getRows() {
	return rows;
    }
    public int getColumns() {
	return columns;
    }
    public double[] getRow(int r) {
	double[] ret = new double[columns];
	for (int i = 0; i < columns; i++) 
	    ret[i] = matrix.get(i)[r];
	return ret;
    }
    public double[] getColumn(int c) {
	double[] ret = new double[rows];
	for (int i = 0; i < rows; i++)
	    ret[i] = matrix.get(c)[i];
	return ret;
    }
    public ArrayList<double[]> getEdges() {
	return matrix;
    }

    public Pixel getColor(int i) {
	return colors.get(i);
    }
    public ArrayList<Pixel> getColors() {
	return colors;
    }

    // Matrix Functions
    protected boolean copy(Matrix m) {
	rows = m.getRows();
	columns = m.getColumns();
	matrix.clear();
	for (int i = 0; i < columns; i++)
	    matrix.add(m.getColumn(i));
	return true;
    }
    protected boolean scalar(double d) {
	for (double[] a : matrix) {
	    int l = a.length;
	    for (int i = 0; i < l; i++) 
		a[i] *= d;
	}
	return true;
    }
    protected boolean add(Matrix m) {
	if (this.check_dimensions(m)) {
	    for (int i = 0; i < rows; i++)
		for (int j = 0; j < columns; j++)
		    matrix.get(j)[i] += m.get(i, j);
	    return true;
	}
	return false;
    }

    protected Matrix multiply(Matrix m) { // Changes Matrix on Left
	if (this.check_multiply(m)) {
	    int r = rows;
	    int c = m.getColumns();

	    // Populating New Matrix
	    double[][] tmp = new double[c][r];
	    for (int row = 0; row < r; row++) 
		for (int column = 0; column < c; column++) 
		    tmp[column][row] = dot(this.getRow(row),
					   m.getColumn(column));

	    // Repopulating Data
	    matrix.clear();
	    for (double[] darr : tmp) 
		matrix.add(darr);
	    
	    columns = c;
	}
	return this;
    }

    // Helper Functions
    protected boolean check_dimensions(Matrix m) {
	return (columns == m.getColumns() &&
		rows == m.getRows());
    }
    protected boolean check_multiply(Matrix m) {
	return (this.columns == m.getRows());
    }
    public double dot(double[] u, double[] v) {
	double sum = 0;
	for (int i = 0; i < u.length; i++) 
	    sum += u[i] * v[i];
	return sum;
    }
    public static Matrix identity(int n) {
	Matrix m = new Matrix(n, n);
	for (int i = 0; i < n; i++) {
	    m.set(i, i, 1);
	}
	return m;
    }

    // ToString Utility
    public String toString() {
	String retStr = "|";
	for (int i = 0; i < rows; i++) {
	    double[] tmp = this.getRow(i);
	    for (int j = 0; j < tmp.length; j++) {
		retStr += " " + tmp[j] + "\t";
	    }
	    retStr += "|\n|";
	}
	return retStr.substring(0, retStr.length() - 3) + "|";
    }

    // Iterators
    public Iterator<double[]> iterator() {
	return matrix.iterator();
    }
    public Iterator<Pixel> colorIterator() {
	return colors.iterator();
    }

    // Testing
    public static void main(String[] args) {
	Matrix pm = new Matrix(4,4);
	System.out.println("Creating new Matrix...\n" + pm + "\n");
	
	Matrix pm2 = new Matrix(new double[][]
	    { {1, 2, 3, 4},
	      {5, 6, 7, 8}, 
	      {9, 10, 11, 12},
	      {13, 14, 15, 16} } );
	System.out.println("Creating second Matrix...\n" + pm2 + "\n");

	pm.add(pm2);
	System.out.println("Adding Matrix 2 to Matrix 1...\n" + pm + "\n");

	pm.multiply(pm2);
	System.out.println("Taking the product of Matrix 1 and Matrix 2...\n" + pm + "\n");

	Matrix i = Matrix.identity(4);
	System.out.println("Creating 4 by 4 identity Matrix...\n" + i + "\n");

	pm.multiply(i);
	System.out.println("Multiplying Matrix 1 by identity Matrix...\n" + pm + "\n");

	Matrix pm3 = new Matrix(new double[][]
	    { {1, 2, 3, 4}, {5, 6, 7, 8} } );
	System.out.println("Creating third Matrix, 4 by 2 this time...\n" + pm3 + "\n");

	pm.copy(pm2);
	System.out.println("Copying contents of Matrix 2 to Matrix 1...\n" + pm + "\n");

	pm.multiply(pm3);
	System.out.println("Multiplying Matrix 1 by Matrix 3...\n" + pm + "\n");

	pm.scalar(2.5);
	System.out.println("Scaling contents of Matrix 1 by 2.5...\n" + pm + "\n");

	System.out.println("Ensuring Matrix 2 is preserved...\n" + pm2 + "\n");
    }
}
