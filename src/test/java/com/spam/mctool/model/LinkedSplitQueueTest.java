package com.spam.mctool.model;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class LinkedSplitQueueTest {
	LinkedSplitQueue<Integer> lsq;

	@Before
	public void setUp() throws Exception {
		lsq = new LinkedSplitQueue<Integer>();
	}

	@Test(expected=NoSuchElementException.class)
	public void testIfIteratorWorksCorrectForEmptyQueue() {
		assertEquals(lsq.size(), 0);
		assertEquals(false, lsq.iterator().hasNext());
		lsq.iterator().next();
	}
	
	@Test
	public void testEnqueue() {
		lsq.enqueue(1);
		lsq.enqueue(2);
		lsq.enqueue(3);
		
		assertEquals(lsq.size(), 3);
		
		Integer ie = 1;
		for(Integer i : lsq) {
			assertEquals(i, ie);
			ie++;
		}
	}

	@Test
	public void testSplit() {
		lsq.enqueue(1);
		lsq.enqueue(2);
		lsq.enqueue(3);
		
		LinkedSplitQueue<Integer> lsq2 = lsq.split();
		assertEquals(0, lsq.size());
		assertEquals(3, lsq2.size());
		
		Integer ie = 1;
		for(Integer i : lsq2) {
			assertEquals(ie, i);
			ie++;
		}
	}
	
	@Test
	public void testIterator() {
		// test correct iteration
		lsq.enqueue(1);
		lsq.enqueue(2);
		lsq.enqueue(3);
		lsq.enqueue(4);
		lsq.enqueue(5);
		lsq.enqueue(6);
		lsq.enqueue(7);
		lsq.enqueue(8);
		lsq.enqueue(9);
		lsq.enqueue(10);
		lsq.enqueue(11);
		
		lsq.setIteratorStepSize(3);
		
		Integer ie = 3;
		for(Integer i : lsq) {
			assertEquals("unexpected element in iteration", ie, i);
			ie+=3;
		}
		
		// setters and getters
		lsq.setIteratorStepSize(-1);
		assertEquals("illegal step width not catched correctly", 1, lsq.getIteratorStepSize());
		lsq.setIteratorStepSize(13);
		assertEquals("legal step width not returned correctly", 13, lsq.getIteratorStepSize());
		
		
		Iterator<Integer> i = lsq.iterator();
		// next function
		lsq.setIteratorStepSize(4);
		assertEquals("next() did not mention available elements", true, lsq.iterator().hasNext());
		lsq.setIteratorStepSize(20);
		assertEquals("next() did not return false with no remaining elements", false, lsq.iterator().hasNext());
		
		// exceptions
		block1:{
			try{
				i.next();
			} catch(NoSuchElementException e) {
				break block1;
			}
			fail("no such element exception not thrown correctly");
		}
		block2:{
			try{
				i.remove();
			} catch(UnsupportedOperationException e) {
				break block2;
			}
			fail("remove() did not throw excpetion");
		}
	}

}
