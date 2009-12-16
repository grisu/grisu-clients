package org.vpac.grisu.plugins.fluent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

public class FluentJob {

	private XYSeries continuity = new XYSeries("Continuity", true, false);
	private XYSeries xVelocity = new XYSeries("x-velocity", true, false);
	private XYSeries yVelocity = new XYSeries("y-velocity", true, false);
	private XYSeries zVelocity = new XYSeries("z-velocity", true, false);

	private File stdout = null;
	int lastIteration = 1;

	public FluentJob(File stdout) {
		this.stdout = stdout;
		lastIteration = parse(lastIteration);
	}

	public XYSeries getContinuity() {
		return continuity;
	}

	public XYSeries getXVelocity() {
		return xVelocity;
	}

	public XYSeries getYVelocity() {
		return yVelocity;
	}

	public XYSeries getZVelocity() {
		return zVelocity;
	}

	private int parse(int firstIteration) {
		List lines = null;
		try {
			lines = FileUtils.readLines(stdout);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		Integer iteration = -1;
		for (Object lineObject : lines) {
			String line = ((String) lineObject).trim();

			String[] lineParts = line.split(" ");
			if (lineParts.length == 8) {
				try {
					iteration = Integer.parseInt(lineParts[0]);
					if (iteration < firstIteration) {
						// do nothing for this line
						continue;
					}
					Double contin = Double.parseDouble(lineParts[1]);
					Double xVelo = Double.parseDouble(lineParts[2]);
					Double yVelo = Double.parseDouble(lineParts[3]);
					Double zVelo = Double.parseDouble(lineParts[4]);
					try {
						continuity.add(new XYDataItem(iteration, contin));
					} catch (Exception e) {
						// do nothing
						// e.printStackTrace();
					}
					try {
						xVelocity.add(new XYDataItem(iteration, xVelo));
					} catch (Exception e) {
						// do nothing
						// e.printStackTrace();
					}
					try {
						yVelocity.add(new XYDataItem(iteration, yVelo));
					} catch (Exception e) {
						// do nothing
						// e.printStackTrace();
					}
					try {
						zVelocity.add(new XYDataItem(iteration, zVelo));
					} catch (Exception e) {
						// do nothing
						// e.printStackTrace();
					}

				} catch (NumberFormatException e) {
					// do nothing for this line
					continue;
				}

			}

		}
		return iteration;

	}

	public void refresh() {
		lastIteration = parse(lastIteration + 1);
	}

}
