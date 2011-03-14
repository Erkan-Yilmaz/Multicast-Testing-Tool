package com.spam.mctool.model;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class LinkedSplitQueueTestsuite {
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
	public void testIfIteratorWithStepWidthWorksCorrectly() {
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
			assertEquals(ie, i);
			ie+=3;
		}
	}

}
