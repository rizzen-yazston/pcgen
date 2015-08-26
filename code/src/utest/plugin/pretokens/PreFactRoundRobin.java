/*
 * PreFactRoundRobin.java
 * Copyright James Dempsey, 2015
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 27 Aug 2015 9:11:34 am
 */
package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.cdom.facet.FacetInitialization;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreFactParser;
import plugin.pretokens.writer.PreFactWriter;

/**
 * The Class <code>PreFactRoundRobin</code> tests the parsing and unparsing of 
 * PREFACTs. 

 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class PreFactRoundRobin extends AbstractPreRoundRobin
{
	private static boolean initialised = false;
	public static void main(String args[])
	{
		TestRunner.run(PreFactRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreFactRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreFactParser());
		TokenRegistration.register(new PreFactWriter());
		if (!initialised)
		{
			FacetInitialization.initialize();
			initialised = true;
		}
	}

	public void testBoolean()
	{
		runPositiveRoundRobin("PREFACT:1,RACE,Foo=true");
	}

	public void testString()
	{
		runPositiveRoundRobin("PREFACT:1,RACE,Foo=Bar");
	}
	
	public void testMultipleBoolean()
	{
		runPositiveRoundRobin("PREFACT:1,RACE,"
			+ "Bard_Archetype_BardicKnowledge=True,"
			+ "Bard_Archetype_Countersong=True,"
			+ "Bard_Archetype_BardicPerformance=True");
	}

}
