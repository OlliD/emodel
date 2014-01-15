/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.agai.emodel.emotionmimicry.xcf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import net.sf.xcf.ActiveMemory;
import net.sf.xcf.InitializeException;
import net.sf.xcf.XcfManager;
import net.sf.xcf.event.MemoryEvent;
import net.sf.xcf.event.QueueAdapter;
import net.sf.xcf.event.XcfEvent;
import net.sf.xcf.memory.Condition;
import net.sf.xcf.memory.MemoryAction;
import net.sf.xcf.memory.MemoryException;
import net.sf.xcf.naming.NameNotFoundException;
import net.sf.xcf.transport.XOPData;
import net.sf.xcf.util.SynchronizedQueue;
import net.sf.xcf.xml.XPath;
import nu.xom.Attribute;

/**
 *
 * @author odamm
 */
public class XcfUtils {
    private static final long DEFAULT_TIMEOUT = 20000;
	private static XcfManager manager;
	private static Map<String, ActiveMemory> openMemories = new HashMap<String, ActiveMemory>();
	private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis() % 1000);

	public static String getUniquePrefix() {
		return String.valueOf(counter.incrementAndGet());
	}
	
	/**
	 * Gets an {@link XcfManager}, creating it if necessary and ensures that
	 * only one such manager is created in the whole program.
	 * 
	 * @return The XcfManager, never null.
	 * @throws InitializeException
	 *             If the manager can't be created.
	 */
	public static synchronized XcfManager getXcfManager()
			throws InitializeException {
		
		if (manager == null) {
			manager = XcfManager.createXcfManager();
		}

		assert (manager != null);

		return manager;
	}
	
	public static synchronized ActiveMemory getMemory(String memoryName) throws InitializeException, NameNotFoundException {
		if (!openMemories.containsKey(memoryName)) {
			openMemories.put(memoryName, getXcfManager().createActiveMemory(memoryName));
		}
		
		assert (openMemories.containsKey(memoryName));
		
		return openMemories.get(memoryName);
	}

	/**
	 * Inserts an element into the active memory and waits for it to be
	 * replaced.
	 * 
	 * @param memory
	 *            The {@link ActiveMemory} to use, should not be null.
	 * @param request
	 *            The XML that is inserted into the memory, should not be null.
	 * @return The XML that replaced the XML that was inserted.
	 * @throws MemoryException
	 *             If there was a problem communicating with the memory.
	 */
	public static XOPData memoryRequest(ActiveMemory memory, XOPData request) throws MemoryException {
		return memoryRequest(memory, request, DEFAULT_TIMEOUT);
	}

	public static XOPData memoryRequest(ActiveMemory memory, XOPData request, long timeout)
			throws MemoryException {

		SynchronizedQueue<MemoryEvent> replaces = new SynchronizedQueue<MemoryEvent>() {
			@Override
			public MemoryEvent convertFromXcfEvent(XcfEvent e) {
				return (MemoryEvent) e;
			}

		};

		QueueAdapter queueAdapter = new QueueAdapter(replaces, new Condition(
				MemoryAction.REPLACE, new XPath("/")));
		memory.addListener(queueAdapter);

		XOPData insertedRequest = memory.insert(request);

		// System.out.println("Inserted request, got id: "
		// + insertedRequest.getXcfId());

		long startTime = System.currentTimeMillis();
		long waitTime = timeout;

		MemoryEvent replace;

		while ((replace = replaces.next(waitTime)) != null) {
			// System.out.println("Got replace, id: "
			// + replace.getData().getXcfId());
			if (replace.getData().getXcfId().equals(insertedRequest.getXcfId())) {
				memory.removeListener(queueAdapter);
				return replace.getData();
			}

			long endTime = System.currentTimeMillis();
			
			// Decrease the wait time by the time spent waiting
			waitTime = Math.max(1, waitTime - (endTime - startTime));
		}

		throw new IllegalStateException("Timeout waiting for answer from memory!");
	}
	
	public static XOPData memoryRequest2(ActiveMemory memory, XOPData request, String answerRoot, String idAttribute) throws MemoryException {
		return memoryRequest2(memory, request, answerRoot, idAttribute, DEFAULT_TIMEOUT);
	}
	
	public static XOPData memoryRequest2(ActiveMemory memory, XOPData request, String answerRoot, String idAttribute, long timeout) throws MemoryException {
		SynchronizedQueue<MemoryEvent> inserts = new SynchronizedQueue<MemoryEvent>() {
			@Override
			public MemoryEvent convertFromXcfEvent(XcfEvent e) {
				return (MemoryEvent) e;
			}
		};

		QueueAdapter queueAdapter = new QueueAdapter(inserts, new Condition(
				MemoryAction.INSERT, new XPath("/" + answerRoot)));
		memory.addListener(queueAdapter);

		XOPData insertedRequest = memory.insert(request);

		// Save the time so we can decrease the wait time for the timeout
		long startTime = System.currentTimeMillis();
		long waitTime = timeout;
		
		MemoryEvent insert;
		while ((insert = inserts.next(waitTime)) != null) {
			Attribute id = insert.getData().getDocument().getRootElement().getAttribute(idAttribute);
			
			if (id != null && id.getValue().equals(insertedRequest.getXcfId())) {
				memory.removeListener(queueAdapter);
				return insert.getData();
			}
			
			long endTime = System.currentTimeMillis();
			
			// Decrease the wait time by the time spent waiting
			waitTime = Math.max(1, waitTime - (endTime - startTime));
		}

		throw new IllegalStateException("Timeout waiting for answer from memory!");
	}
}
