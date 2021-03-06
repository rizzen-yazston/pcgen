/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.editcontext.pcclass.level;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import util.TestURI;

public abstract class AbstractPCClassLevelTokenTestCase extends TestCase
{
	protected LoadContext primaryContext;
	protected LoadContext secondaryContext;
	protected PCClassLevel primaryProf1;
	protected PCClassLevel secondaryProf1;
	protected PCClassLevel primaryProf2;
	protected PCClassLevel secondaryProf2;
	protected PCClassLevel primaryProf3;
	protected PCClassLevel secondaryProf3;
	protected static CDOMTokenLoader<PCClassLevel> loader = new CDOMTokenLoader<>();

	private static boolean classSetUpFired = false;
	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static void classSetUp()
	{
		testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
		classSetUpFired = true;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		if (!classSetUpFired)
		{
			classSetUp();
		}
		// Yea, this causes warnings...
		TokenRegistration.register(getToken());
		primaryContext = new EditorLoadContext();
		secondaryContext = new EditorLoadContext();
		primaryProf1 = primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"TestObj1");
		secondaryProf1 = secondaryContext.getReferenceContext().constructCDOMObject(
				getCDOMClass(), "TestObj1");
		primaryProf2 = primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"TestObj2");
		secondaryProf2 = secondaryContext.getReferenceContext().constructCDOMObject(
				getCDOMClass(), "TestObj2");
		primaryProf3 = primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"TestObj3");
		secondaryProf3 = secondaryContext.getReferenceContext().constructCDOMObject(
				getCDOMClass(), "TestObj3");
	}

	public Class<PCClassLevel> getCDOMClass()
	{
		return PCClassLevel.class;
	}

	public static void addToken(LstToken tok)
	{
		TokenStore.inst().addToTokenMap(tok);
	}

	public void runRoundRobin(String... str) throws PersistenceLayerException
	{
		// Default is not to write out anything
		assertNull(getToken().unparse(primaryContext, primaryProf1));
		assertNull(getToken().unparse(primaryContext, primaryProf2));
		assertNull(getToken().unparse(primaryContext, primaryProf3));
		// Ensure the graphs are the same at the start
		assertTrue(primaryContext.getListContext().masterListsEqual(
				secondaryContext.getListContext()));

		// Set value
		for (String s : str)
		{
			assertTrue(getToken().parseToken(primaryContext, primaryProf2, s).passed());
		}
		// Doesn't pollute other levels
		assertNull(getToken().unparse(primaryContext, primaryProf1));
		assertNull(getToken().unparse(primaryContext, primaryProf3));
		// Get back the appropriate token:
		String[] unparsed = getToken().unparse(primaryContext, primaryProf2);

		assertEquals(str.length, unparsed.length);

		for (int i = 0; i < str.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", str[i],
					unparsed[i]);
		}

		// Do round Robin
		StringBuilder unparsedBuilt = new StringBuilder();
		for (String s : unparsed)
		{
			unparsedBuilt.append(getToken().getTokenName()).append(':').append(
					s).append('\t');
		}
		loader.parseLine(secondaryContext, secondaryProf2, unparsedBuilt
				.toString(), testCampaign.getURI());

		// Ensure the objects are the same
		assertEquals(primaryProf1, secondaryProf1);
		assertEquals(primaryProf2, secondaryProf2);
		assertEquals(primaryProf3, secondaryProf3);

		// Ensure the graphs are the same
		assertTrue(primaryContext.getListContext().masterListsEqual(
				secondaryContext.getListContext()));

		// And that it comes back out the same again
		// Doesn't pollute other levels
		assertNull(getToken().unparse(secondaryContext, secondaryProf1));
		assertNull(getToken().unparse(secondaryContext, secondaryProf3));
		String[] sUnparsed = getToken().unparse(secondaryContext,
				secondaryProf2);
		assertEquals(unparsed.length, sUnparsed.length);

		for (int i = 0; i < unparsed.length; i++)
		{
			assertEquals("Expected " + i + " item to be equal", unparsed[i],
					sUnparsed[i]);
		}
	}

	public abstract CDOMPrimaryToken<PCClassLevel> getToken();

}
