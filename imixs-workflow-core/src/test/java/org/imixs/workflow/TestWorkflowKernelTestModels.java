package org.imixs.workflow;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.ejb.SessionContext;
import javax.xml.parsers.ParserConfigurationException;

import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

/**
 * Test class for Imixs WorkflowKernel using the test models. The test class
 * verifies complex model situations based on the test models.
 * 
 * @author rsoika
 * 
 */
public class TestWorkflowKernelTestModels {

	WorkflowKernel kernel = null;
	protected SessionContext ctx;
	protected WorkflowContext workflowContext;
	private static Logger logger = Logger.getLogger(TestWorkflowKernelTestModels.class.getName());

	@Before
	public void setup() throws PluginException, ModelException, ParseException, ParserConfigurationException,
			SAXException, IOException {

		ctx = Mockito.mock(SessionContext.class);
		// simulate SessionContext ctx.getCallerPrincipal().getName()
		Principal principal = Mockito.mock(Principal.class);
		when(principal.getName()).thenReturn("manfred");
		when(ctx.getCallerPrincipal()).thenReturn(principal);

		workflowContext = Mockito.mock(WorkflowContext.class);

		// MokWorkflowContext ctx = new MokWorkflowContext();
		kernel = new WorkflowKernel(workflowContext);

		MokPlugin mokPlugin = new MokPlugin();
		kernel.registerPlugin(mokPlugin);

		logger.fine("init mocks completed");
	}

	/**
	 * Simple test
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ParseException
	 * @throws ModelException
	 */
	@Test
	@Category(org.imixs.workflow.WorkflowKernel.class)
	public void testSimpleModel() {
		try {
			// provide a mock modelManger class
			when(workflowContext.getModelManager()).thenReturn(new MokModelManager("/bpmn/simple.bpmn"));

			ItemCollection itemCollection = new ItemCollection();
			itemCollection.replaceItemValue("txtTitel", "Hello");
			itemCollection.replaceItemValue("$processid", 1000);
			itemCollection.replaceItemValue("$activityid", 10);

			itemCollection.replaceItemValue("$modelversion", MokModel.DEFAULT_MODEL_VERSION);

			itemCollection = kernel.process(itemCollection);
			Assert.assertEquals("Hello", itemCollection.getItemValueString("txttitel"));

			Assert.assertEquals(1100, itemCollection.getProcessID());

		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}

	}

	/**
	 * ticket.bpmn test
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ParseException
	 * @throws ModelException
	 */
	@Test
	@Category(org.imixs.workflow.WorkflowKernel.class)
	public void testTicketModel() {
		try {
			// provide a mock modelManger class
			when(workflowContext.getModelManager()).thenReturn(new MokModelManager("/bpmn/ticket.bpmn"));

			ItemCollection itemCollection = new ItemCollection();
			itemCollection.replaceItemValue("txtTitel", "Hello");
			itemCollection.replaceItemValue("$processid", 1100);
			itemCollection.replaceItemValue("$activityid", 20);

			itemCollection.replaceItemValue("$modelversion", MokModel.DEFAULT_MODEL_VERSION);

			itemCollection = kernel.process(itemCollection);
			Assert.assertEquals("Hello", itemCollection.getItemValueString("txttitel"));

			Assert.assertEquals(1200, itemCollection.getProcessID());

		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}

	}

	/**
	 * Test conditional-event models.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ParseException
	 * @throws ModelException
	 */
	@Test
	@Category(org.imixs.workflow.WorkflowKernel.class)
	public void testConditionalEventModel() {
		try {
			// provide a mock modelManger class
			when(workflowContext.getModelManager()).thenReturn(new MokModelManager("/bpmn/conditional_event1.bpmn"));

			// test Condition 1
			ItemCollection itemCollection = new ItemCollection();
			itemCollection.replaceItemValue("txtTitel", "Hello");
			itemCollection.replaceItemValue("$processid", 1000);
			itemCollection.replaceItemValue("$activityid", 10);
			itemCollection.replaceItemValue("$modelversion", MokModel.DEFAULT_MODEL_VERSION);

			itemCollection.replaceItemValue("_budget", 99);

			itemCollection = kernel.process(itemCollection);
			Assert.assertEquals("Hello", itemCollection.getItemValueString("txttitel"));
			Assert.assertEquals(1200, itemCollection.getProcessID());

			// test Condition 2
			itemCollection = new ItemCollection();
			itemCollection.replaceItemValue("txtTitel", "Hello");
			itemCollection.replaceItemValue("$processid", 1000);
			itemCollection.replaceItemValue("$activityid", 10);
			itemCollection.replaceItemValue("$modelversion", MokModel.DEFAULT_MODEL_VERSION);

			itemCollection.replaceItemValue("_budget", 9999);

			itemCollection = kernel.process(itemCollection);
			Assert.assertEquals("Hello", itemCollection.getItemValueString("txttitel"));

			Assert.assertEquals(1100, itemCollection.getProcessID());

		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();

		}

	}

}