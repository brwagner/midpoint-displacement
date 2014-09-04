import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Draws a random GoogleEarth type terrain image using a midpoint displacement algorithm
 */
public class MidApplet extends Applet {
	private static final long serialVersionUID = -7446133259378651999L;
	float[][] heightMap; // matrix of heights
	Random rand; // random number gen
	BufferedImage image; // final image

	// Creates the image
	public void init() {
		this.rand = new Random();
		this.heightMap = this.generateMap(5, 100);
		this.subdivide(8);
		this.setSize(this.heightMap.length, this.heightMap.length);
		this.image = this.getPicture();
	}

	// Creates a new random 2d array of a given size and a given max value
	public float[][] generateMap(int size, int max) {
		float[][] dest = new float[size][size];

		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = this.rand.nextInt(max);
			}
		}

		return dest;
	}

	// Performs a midpoint displacement a given number of times
	// The midpoint generated is the average between two points plus or minus a random number
	public void subdivide(int n) {
		for (int i = 0; i < n; i++) {
			float[][] dest = new float[this.heightMap.length * 2 - 1][this.heightMap.length * 2 - 1];
			for (int x = 0; x < dest.length - 1; x += 2) {
				for (int y = 0; y < dest.length - 1; y += 2) {
					
					// Get the previous values of the matrix and place them in the new matrix with midpoints in between them
					dest[x][y] = this.heightMap[x / 2][y / 2];
					dest[x + 2][y] = this.heightMap[x / 2 + 1][y / 2];
					dest[x][y + 2] = this.heightMap[x / 2][y / 2 + 1];
					dest[x + 2][y + 2] = this.heightMap[x / 2 + 1][y / 2 + 1];

					// calculate averages for midpoints
					float avg;

					avg = (dest[x][y] + dest[x + 2][y]) / 2f;
					dest[x + 1][y] = avg + this.getDelta(-avg / 2f, avg / 2f);

					avg = (dest[x][y] + dest[x][y + 2]) / 2f;
					dest[x][y + 1] = avg + this.getDelta(-avg / 2f, avg / 2f);

					avg = (dest[x + 2][y] + dest[x + 2][y + 2]) / 2f;
					dest[x + 2][y + 1] = avg
							+ this.getDelta(-avg / 2f, avg / 2f);

					avg = (dest[x][y + 2] + dest[x + 2][y + 2]) / 2f;
					dest[x + 1][y + 2] = avg
							+ this.getDelta(-avg / 2f, avg / 2f);

					avg = (dest[x][y] + dest[x + 2][y] + dest[x][y + 2] + dest[x + 2][y + 2]) / 4f;
					dest[x + 1][y + 1] = avg
							+ this.getDelta(-avg / 2f, avg / 2f);
				}
			}
			this.heightMap = dest;
		}
	}

	// Render a picture based on the heightMap 
	// Heights of different sizes get diffrent colors 
	public BufferedImage getPicture() {
		BufferedImage im = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = im.createGraphics();
		int height;
		float factor;
		Color color;
		for (int x = 0; x < this.heightMap.length; x++) {
			for (int y = 0; y < this.heightMap.length; y++) {
				height = (int) this.heightMap[x][y];
				factor = (float) height / 255f;

				if (height > 255) {
					color = new Color(254, 254, 254);
				} else if (height > 150) {
					color = new Color((int) (150 * factor),
							(int) (150 * factor), (int) (150 * factor));
				} else if (height > 90) {
					color = new Color((int) (153 * factor),
							(int) (100 * factor), 0);
				} else if (height > 30) {
					color = new Color(0, (int) (150 * factor), 0);
				} else {
					color = new Color(0, 0, (int) (300 * factor));
				}

				g.setColor(color);
				g.drawLine(x, y, x, y);
			}
		}

		return im;
	}

	// Gets the random displacement between two points
	public float getDelta(float min, float max) {
		float range = max - min;
		float scaled = this.rand.nextFloat() * range;
		float shifted = scaled + min;
		return shifted;
	}

	// Draw it!
	public void paint(Graphics g) {
		g.drawImage(this.image, 0, 0, this);
	}
}
