package com.spam.mctool.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A implementation of a queue based on a linked list.
 * Optimized for fast splitting (for concurrent statistics analysis) and sequential access.
 * @author Jeffrey Jedele
 * @param Object to store in the queue
 */
public class LinkedSplitQueue<E> implements Iterable<E> {
	
	private Node head;
	private volatile Node tail;
	private volatile int size;
	private int iteratorStepSize = 1;
	
	private class Node {
		private volatile Node next;
		private E data;
	}
	
	private class LSQIterator implements Iterator<E> {
		private Node pos = head;

		/**
		 * @return true if an element exists in range of the set step width
		 */
		public boolean hasNext() {
			Node p = pos;
			try {
				for(int i=0; i<=iteratorStepSize; i++) {
					p = p.next;
				}
				return true;
			} catch(Exception e) {
				return false;
			}
		}

		/**
		 * @return next element regarding set step width if existent
		 * @throws NoSuchElementException if no more objects are reachable
		 */
		public E next() {
			Node p = pos;
			try {
				for(int i=0; i<iteratorStepSize; i++) {
					p = p.next;
				}
				pos = p;
				return p.data;
			} catch(Exception e) {
				throw new NoSuchElementException();
			}
		}

		/**
		 * removing elements is not supported in this implementation
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Default constructor
	 */
	public LinkedSplitQueue() {
		head = new Node();
		tail = head;
		size = 0;
	}
	
	// only used for splitting
	private LinkedSplitQueue(Node head, Node tail, int size) {
		this();
		this.head.next = head;
		this.tail = tail;
		this.size = size;
	}
	
	/**
	 * Enqueues a new element
	 * @param el
	 */
	public synchronized void enqueue(E el) {
		Node n = new Node();
		n.data = el;
		
		tail.next = n;
		tail = n;
		size++;
	}
	
	/**
	 * Returns a new queue with the content of the old and clears the old.
	 * @return content of queue
	 */
	public synchronized LinkedSplitQueue<E> split() {
		LinkedSplitQueue<E> lsq = new LinkedSplitQueue<E>(head.next, tail, size);
		head.next = null;
		tail = head;
		size = 0;
		return lsq;
	}

	public Iterator<E> iterator() {
		return new LSQIterator();
	}
	
	/**
	 * @return number of enqueued elements
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * @return step width of the iterator
	 */
	public int getIteratorStepSize() {
		return this.iteratorStepSize;
	}
	
	/**
	 * This can be used to adjust the step width of the queue iterator for custom traversal
	 * @param step stepwidth to set
	 */
	public void setIteratorStepSize(int step) {
		int escapedStep = (step<=0) ? 1 : step;
		this.iteratorStepSize = escapedStep;
	}
	
}
