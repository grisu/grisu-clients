package org.vpac.grisu.plugins.underworld;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

public class UnderworldJob {

	static final Logger myLogger = Logger.getLogger(UnderworldJob.class
			.getName());

	private GrisuJobMonitoringObject job = null;

	private GrisuFileObject frequentOutput = null;

	int timesteps_total = -1;
	int current_timestep = 0;

	ArrayList<String> rows = new ArrayList<String>();
	Map<String, ArrayList<Double>> data = new TreeMap<String, ArrayList<Double>>();

	public UnderworldJob(GrisuJobMonitoringObject job) {

		this.job = job;
		timesteps_total = Integer.parseInt(job.getOtherProperties().get(
				"maxTimesteps"));

		String frequentOutput_uri = job.getJobDirectory()
				+ "/"
				+ job.getOtherProperties().get("underworldOutputDirectory")
						.substring(2) + "/" + "FrequentOutput.dat";
		try {
			frequentOutput = job.getJobDirectoryObject().getFileSystemBackend()
					.getFileObject(new URI(frequentOutput_uri));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		frequentOutput.getLocalRepresentation(true);

	}

	public int getCurrent_timestep() {
		return current_timestep;
	}

	public Map<String, ArrayList<Double>> getData() {
		return data;
	}

	public GrisuFileObject getFrequentOutput() {
		return frequentOutput;
	}

	public GrisuJobMonitoringObject getJob() {
		return job;
	}

	public ArrayList<String> getRowNames() {
		if (rows == null || rows.size() == 0) {
			parse();
		}
		return rows;
	}

	public int getTimesteps_total() {
		return timesteps_total;
	}

	private void parse() {

		File frequentOutput_cache = frequentOutput
				.getLocalRepresentation(false);

		List lines = null;
		try {
			lines = FileUtils.readLines(frequentOutput_cache);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (rows != null && rows.size() > 0) {
			for (String name : data.keySet()) {
				data.put(name, new ArrayList<Double>());
			}
		}

		for (Object lineObject : lines) {

			String line = (String) lineObject;
			String[] parts = line.trim().split("\\s++");

			if (rows.size() == 0) {
				System.out.println();
				if (line.startsWith("#")) {
					line = line.replaceAll("CPU Time", "CPUTime");
					parts = line.trim().split("\\s++");

					for (int i = 0; i < parts.length; i++) {
						if (parts[i].startsWith("#")) {
							continue;
						}

						myLogger.debug("Adding row: " + parts[i]);
						rows.add(parts[i]);
						data.put(parts[i], new ArrayList<Double>());
					}
					continue;
				}
			}

			if (parts.length != rows.size()) {
				// not able to parse this
				continue;
			}

			for (int i = 0; i < parts.length; i++) {

				if ("Timestep".equals(rows.get(i))) {
					current_timestep = Integer.parseInt(parts[i]);
				}

				Double value = Double.parseDouble(parts[i]);
				data.get(rows.get(i)).add(value);
			}

		}
	}

	public void refresh() {
		parse();
	}

}
