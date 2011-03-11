package com.spam.mctool.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedSplitQueue<E> implements Iterable<E> {
	private Node head;
	private volatile Node tail;
	private volatile int size;
	private int iteratorStepSize = 1;
	
	private class Node {
		volatile Node next;
		E data;
	}
	
	private class LSQIterator implements Iterator<E> {
		Node pos = head;

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

		public E next() {
			Node p = pos;
			try {
				for(int i=0; i<iteratorStepSize; i++) {
					p = p.next;
				}
				pos = p;
				return p.data;
			} catch(NullPointerException e) {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
		}
	}
	
	public LinkedSplitQueue() {
		head = new Node();
		tail = head;
		size = 0;
	}
	
	private LinkedSplitQueue(Node head, Node tail, int size) {
		this();
		this.head.next = head;
		this.tail = tail;
		this.size = size;
	}
	
	public synchronized void enqueue(E el) {
		Node n = new Node();
		n.data = el;
		
		tail.next = n;
		tail = n;
		size++;
	}
	
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
	
	public int size() {
		return this.size;
	}
	
	public int getIteratorStepSize() {
		return this.iteratorStepSize;
	}
	
	public void setIteratorStepSize(int step) {
		this.iteratorStepSize = step;
	}
	
}
