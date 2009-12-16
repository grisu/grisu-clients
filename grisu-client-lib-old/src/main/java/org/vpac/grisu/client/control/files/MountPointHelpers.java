package org.vpac.grisu.client.control.files;

import org.vpac.grisu.model.MountPoint;

/**
 * Methods to help with {@link MountPoint}s.
 * 
 * @author Markus Binsteiner
 * 
 */
public class MountPointHelpers {

	/**
	 * Tries to figure out what the name of the site is by parsing the url part
	 * of a {@link MountPoint}. That works ok if the names of the data gateways
	 * follow a know schema.
	 * 
	 * @param rootUrl
	 *            the url of the {@link MountPoint}
	 * @return the name of the site (something like vpac, sapac or ac3)
	 */
	public static String getSiteFromMountPointUrl(String rootUrl) {

		final String[] possibleTlds = new String[] { ".com.au", ".com", ".org",
				".edu.au", ".au", ".ac.nz", ".nz" };

		int index_tld = -1;
		for (String tld : possibleTlds) {
			index_tld = rootUrl.indexOf(tld);
			if (index_tld != -1)
				break;
		}

		String substring = rootUrl.substring(0, index_tld);
		int index_site = substring.lastIndexOf(".");

		return rootUrl.substring(index_site + 1, index_tld);
	}

	/**
	 * Tries to figure out the name of the site by parsing the url of the
	 * submission location. That works ok if the names of the submission
	 * gateways follow a known schema.
	 * 
	 * @param submissionLocation
	 *            the url of the submission locations
	 * @return the name of the site (something like vpac, sapac or ac3) or null
	 *         if it can't be parsed
	 */
	public static String getSiteFromSubmissionLocation(String submissionLocation) {

		if (submissionLocation != null && !"".equals(submissionLocation)) {

			final String[] possibleTlds = new String[] { ".com.au", ".com",
					".org", ".edu.au", ".au", ".ac.nz", ".nz" };

			int index_tld = -1;
			for (String tld : possibleTlds) {
				index_tld = submissionLocation.indexOf(tld);
				if (index_tld != -1)
					break;
			}

			String substring = submissionLocation.substring(0, index_tld);
			int index_site = substring.lastIndexOf(".");

			return submissionLocation.substring(index_site + 1, index_tld);
		} else
			return null;
	}

}
