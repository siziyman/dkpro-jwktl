/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.jwktl;

import de.tudarmstadt.ukp.jwktl.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Offers a command line interface to Wiktionary. You can type a word and 
 * after pressing &lt;enter&gt; the information of corresponding entries will 
 * be printed. In order to quit the interface just hit enter;
 */
public class WiktionaryCli {

	/**
	 * @param args path to parsed Wiktionary data
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1)
			throw new IllegalArgumentException("Too few arguments. "
						+ "Required arguments: <PARSED-WIKTIONARY>");
		
		final String PROMPT = "> ";
		final String END = "";
		String wktPath = args[0];
		WiktionaryFormatter formatter = WiktionaryFormatter.instance();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		System.out.print(PROMPT);
			try (IWiktionaryEdition wkt = JWKTL.openEdition(new File(wktPath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equals(END))
					break;

				IWiktionaryPage page = wkt.getPageForWord(line);
				if (page == null || page.getEntryCount() == 0)
					System.out.println(line + " is not in Wiktionary");
				else
					System.out.println(formatter.formatPage(page));

				System.out.print(PROMPT);
			}
		}
		System.out.println("exit");
	}

}
