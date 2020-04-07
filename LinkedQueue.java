
public class LinkedQueue<T> implements Queue<T> {
	private Node<T> head, tail;
	private int size;
	
	public LinkedQueue() {
		head = tail = null;
		size = 0;
	}
	public int length() {
		return size;
	}
	public boolean full() {
		return false;
	}

	public void enqueue(T e) {
		if(size == 0) {
			head = tail = new Node<T> (e);
			size++;
			return;
		}
		tail.next= new Node<T>(e);
		tail = tail.next;
		size++;
	}
	public Node<T> peek(){
		if(size>0)
		return head;
		return null;
	}

	public T serve() {
		if(size!=0) {
		Node<T> temp = head;
		head = head.next;
		size--;
		return temp.data;
		}
		return null;
	}


}
